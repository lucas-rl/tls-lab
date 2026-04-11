public class Certificate {
    public String publicKeyBase64;
    public String identity;
    public String signatureBase64;

    public String serialize() {
        return publicKeyBase64 + "|" + identity + "|" + signatureBase64;
    }

    public static Certificate deserialize(String data) {
        String[] parts = data.split("\\|");
        Certificate cert = new Certificate();
        cert.publicKeyBase64 = parts[0];
        cert.identity = parts[1];
        cert.signatureBase64 = parts[2];
        return cert;
    }
}