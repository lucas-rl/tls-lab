import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.*;
import java.util.Base64;

public class CryptoUtils {

    public static byte[] encrypt(byte[] data, byte KEY){
        byte[] result = new byte[data.length];
        for (int i = 0 ; i < data.length ; i++){
            result[i] = (byte) (data[i] ^ KEY);
        }
        return result;
    }

    public static byte[] decrypt(byte[] data, byte KEY){
        return encrypt(data, KEY);
    }

    public static KeyPair generateRSAKeyPair() throws Exception{
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    public static byte[] rsaEncrypt(byte[] data, PublicKey publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    public static byte[] rsaDecrypt(byte[] data, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    public static String encodeBase64(byte[] data){
        return Base64.getEncoder().encodeToString(data);
    }

    public static byte[] decodeBase64(String data){
        return Base64.getDecoder().decode(data);
    }
}
