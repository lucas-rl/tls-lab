import javax.xml.crypto.Data;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.MessageDigest;

public class SimpleHttpServer {
    public static void main(String[] args) throws Exception{
        int port = 8080;
        ServerSocket serverSocket = new ServerSocket(port);

        System.out.println("Server running on port " + port);

        KeyPair keyPair = CryptoUtils.generateRSAKeyPair();

        Certificate certificate = generateCetificate(keyPair);

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

            //Send cert
            dataManager.sendData(certificate.serialize().getBytes());

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

    private static Certificate generateCetificate(KeyPair keyPair) throws Exception {
        String pubKeyBase64 = CryptoUtils.encodeBase64(keyPair.getPublic().getEncoded());
        String identity = "localhost";

        // data to sign
        String data = pubKeyBase64 + "|" + identity;

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data.getBytes());

        // sign with CA private key
        byte[] signatureBytes = CryptoUtils.rsaEncrypt(
                hash,
                SimpleCA.CA_PRIVATE_KEY   // 🔥 signing
        );

        String signatureBase64 = CryptoUtils.encodeBase64(signatureBytes);

        // build certificate
        Certificate cert = new Certificate();
        cert.publicKeyBase64 = pubKeyBase64;
        cert.identity = identity;
        cert.signatureBase64 = signatureBase64;

        return cert;
    }
}
