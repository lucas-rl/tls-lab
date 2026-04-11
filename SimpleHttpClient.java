import javax.xml.crypto.Data;
import java.io.*;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;

public class SimpleHttpClient {
    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("localhost", 9090);

        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();

        DataManager dataManager = new DataManager(outputStream, inputStream);

        //Send hello
        dataManager.sendData("HELLO".getBytes());

        //Receive certificate, validate, generate symmetrickey and send
        Certificate certificate = Certificate.deserialize(new String(dataManager.getData()));
        verifyCert(certificate);

        String RSAPublicKey = certificate.publicKeyBase64;
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

    private static void verifyCert(Certificate certificate) throws Exception {
        String data = certificate.publicKeyBase64 + "|" + certificate.identity;
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] expectedHash = digest.digest(data.getBytes());

        byte[] signatureBytes = CryptoUtils.decodeBase64(certificate.signatureBase64);

        // "decrypt" signature using CA public key
        byte[] decrypted = CryptoUtils.rsaDecrypt(
                signatureBytes,
                SimpleCA.CA_PUBLIC_KEY
        );


        if (!Arrays.equals(expectedHash, decrypted)) {
            throw new RuntimeException("Invalid certificate! Possible MITM!");
        }
    }
}