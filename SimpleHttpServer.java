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

            byte[] hello = getData(inputStream);

            String helloMessage = new String(hello);
            if(!helloMessage.equals("HELLO")) clientSocket.close();

            //Send public key
            KeyPair keyPair = CryptoUtils.generateRSAKeyPair();
            String publicKeyBase64 = CryptoUtils.encodeBase64(keyPair.getPublic().getEncoded());
            outputStream.write(publicKeyBase64.getBytes());
            outputStream.flush();

            byte[] symetricKeyRSAEncryptedData = getData(inputStream);
            byte symmetricKey = CryptoUtils.rsaDecrypt(symetricKeyRSAEncryptedData, keyPair.getPrivate())[0];

            //Send ok
            outputStream.write("OK".getBytes());
            outputStream.flush();

            //Read symmetric encrypted request
            byte[] encryptedRequestData = getData(inputStream);
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
            outputStream.write(encryptedResponse);
            outputStream.flush();

            System.out.println("\n=== Connection Finished ====");

            clientSocket.close();
        }
    }

    private static byte[] getData(InputStream input) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] chunk = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(chunk)) != -1) {
            buffer.write(chunk, 0, bytesRead);

            if (buffer.size() > 0) break; // simple for now
        }
        return buffer.toByteArray();
    }
}
