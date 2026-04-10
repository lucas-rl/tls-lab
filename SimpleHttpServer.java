import javax.xml.crypto.Data;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;

public class SimpleHttpServer {
    public static void main(String[] args) throws Exception{
        int port = 8080;
        ServerSocket serverSocket = new ServerSocket(port);

        System.out.println("Server running on port " + port);

        while(true){
            Socket clientSocket = serverSocket.accept();
            System.out.println("\n==== New Connection ===");

            //Get request
            InputStream inputStream = clientSocket.getInputStream();
            OutputStream outputStream = clientSocket.getOutputStream();

            DataManager dataManager = new DataManager(outputStream, inputStream);

            byte[] hello = dataManager.getData();

            String helloMessage = new String(hello);
            if(!helloMessage.equals("HELLO")) clientSocket.close();

            //Send public key
            KeyPair keyPair = CryptoUtils.generateRSAKeyPair();
            String publicKeyBase64 = CryptoUtils.encodeBase64(keyPair.getPublic().getEncoded());
            dataManager.sendData(publicKeyBase64.getBytes());

            //get symmetric key
            byte[] symetricKeyRSAEncryptedData = dataManager.getData();
            byte symmetricKey = CryptoUtils.rsaDecrypt(symetricKeyRSAEncryptedData, keyPair.getPrivate())[0];

            //Send ok
            dataManager.sendData("OK".getBytes());

            //Read symmetric encrypted request
            byte[] encryptedRequestData = dataManager.getData();
            byte[] request = CryptoUtils.decrypt(encryptedRequestData, symmetricKey);


            System.out.println("REQUEST:");
            System.out.println(new String(request));

            //Send response
            String response =
                    "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: text/plain\r\n" +
                    "\r\n" +
                    "Hello from server";

            byte[] encryptedResponse = CryptoUtils.encrypt(response.getBytes(), symmetricKey);
            dataManager.sendData(encryptedResponse);


            System.out.println("\n=== Connection Finished ====");

            clientSocket.close();
        }
    }
}
