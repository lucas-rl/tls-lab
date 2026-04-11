import java.security.KeyPair;

public class Main {
    public static void main(String... args) throws Exception {
        KeyPair caKeyPair = CryptoUtils.generateRSAKeyPair();

        System.out.println(
                CryptoUtils.encodeBase64(caKeyPair.getPublic().getEncoded())
        );
        System.out.println(
                CryptoUtils.encodeBase64(caKeyPair.getPrivate().getEncoded())
        );
    }
}
