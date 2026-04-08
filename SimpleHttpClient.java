import java.io.*;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

public class SimpleHttpClient {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 9090);

        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();

        //Send hello
        outputStream.write("HELLO".getBytes());
        outputStream.flush();

        //Receive public key
        String RSAPublicKey = new String(getData(inputStream));
        byte[] pubKeyBytes = CryptoUtils.decodeBase64(RSAPublicKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(
                new X509EncodedKeySpec(pubKeyBytes)
        );

        //Send symmetric key (RSA encrypted)
        byte symmetricKey = 0x0F;
        outputStream.write(CryptoUtils.rsaEncrypt(new byte[]{symmetricKey}, publicKey));
        outputStream.flush();

        //Receive ok
        byte[] ok = getData(inputStream);
        if(!(new String(ok)).equals("OK")) socket.close();

        //SendRequest
        String request =
                "POST /login HTTP/1.1\r\n" +
                        "Host: localhost\r\n" +
                        "Content-Type: application/json\r\n" +
                        "Content-Length: 18\r\n" +
                        "\r\n" +
                        "{\"user\":\"admin\"}";

        outputStream.write(CryptoUtils.encrypt(request.getBytes(), symmetricKey));
        outputStream.flush();

        //Get response
        byte[] responseEncrypted = getData(inputStream);
        String response = new String(CryptoUtils.decrypt(responseEncrypted, symmetricKey));

        System.out.println("RESPONSE:");
        System.out.println(response);


        System.out.println("Connection Finished");

        socket.close();
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