public class CryptoUtils {
    private static final byte KEY = 0x0F;

    public static byte[] encrypt(byte[] data){
        byte[] result = new byte[data.length];
        for (int i = 0 ; i < data.length ; i++){
            result[i] = (byte) (data[i] ^ KEY);
        }
        return result;
    }

    public static byte[] decrypt(byte[] data){
        return encrypt(data);
    }
}
