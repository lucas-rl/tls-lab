import javax.xml.crypto.Data;
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

        DataManager dataManager = new DataManager(outputStream, inputStream);

        //Send hello
        dataManager.sendData("HELLO".getBytes());

        //Receive public key, generate and send
        String RSAPublicKey = new String(dataManager.getData());
        byte[] pubKeyBytes = CryptoUtils.decodeBase64(RSAPublicKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(
                new X509EncodedKeySpec(pubKeyBytes)
        );
        byte symmetricKey = 0x0F;
        dataManager.sendData(CryptoUtils.rsaEncrypt(new byte[]{symmetricKey}, publicKey));

        //Receive ok
        byte[] ok = dataManager.getData();
        if(!(new String(ok)).equals("OK")) socket.close();

        //SendRequest
        String request =
                "POST /login HTTP/1.1\r\n" +
                        "Host: localhost\r\n" +
                        "Content-Type: application/json\r\n" +
                        "Content-Length: 16\r\n" +
                        "\r\n" +
                        "{\"user\":\"admin\"}";

        dataManager.sendData(CryptoUtils.encrypt(request.getBytes(), symmetricKey));

        //Get response
        byte[] responseEncrypted = dataManager.getData();
        String response = new String(CryptoUtils.decrypt(responseEncrypted, symmetricKey));

        System.out.println("RESPONSE:");
        System.out.println(response);


        System.out.println("Connection Finished");

        socket.close();
    }
}