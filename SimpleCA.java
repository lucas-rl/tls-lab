import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class SimpleCA {
    public static PublicKey CA_PUBLIC_KEY;
    public static PrivateKey CA_PRIVATE_KEY;

    static {
        try {
            byte[] pub = CryptoUtils.decodeBase64("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxAllr4sYzXwUeJFBpbIu7RKy9YU1PU3xy8yzzDP2NgHsB1kF0XGi/Kew02jS1a7fPOdZOPuFIMyBo/oE1gmgiDRVV50L9IxFR9HyVfEwlrpg+nA5ItUBI6oGq8dsvE10Lkl5jO+JLJmeLlx/0MPdW98xed5cfRp8Zrnh6Q8Z8pSPSFJ4zdlsCwsyqK4NMuwur5wiVSC9BVNOYSNTVPQD+3ekjBEp9z8uNTaL62Wk3xUB5YOd88gNKGUcaGDQda1uN4TdWb+mmVxXDTkAEahx7KXluuaNlaQZwtuKZIoH9J+LXEQgcvGMDInob2btpTY4V4M3SX7FlP/Q1GnV7ef6IwIDAQAB");
            byte[] priv = CryptoUtils.decodeBase64("MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQDECWWvixjNfBR4kUGlsi7tErL1hTU9TfHLzLPMM/Y2AewHWQXRcaL8p7DTaNLVrt8851k4+4UgzIGj+gTWCaCINFVXnQv0jEVH0fJV8TCWumD6cDki1QEjqgarx2y8TXQuSXmM74ksmZ4uXH/Qw91b3zF53lx9GnxmueHpDxnylI9IUnjN2WwLCzKorg0y7C6vnCJVIL0FU05hI1NU9AP7d6SMESn3Py41NovrZaTfFQHlg53zyA0oZRxoYNB1rW43hN1Zv6aZXFcNOQARqHHspeW65o2VpBnC24pkigf0n4tcRCBy8YwMiehvZu2lNjhXgzdJfsWU/9DUadXt5/ojAgMBAAECggEAWLqkIstDg6Y4ocWlP/khUj6Xb5dGXKQN4B+b9b5DH3jJOx4pCv5zNgY0e8vDOeJ3ulpAQRFe1VSbDPwZ+kLLPTrqscgsZBilNveoxwtu4ZokUNZAlipw5JKNiGywI56vClBJSugUX3J8bSGC3Jv6ch3Yh/+EPz+gtOJHhTRHm0qk0iqr2CoUfkAzVU79XQZR0/Wll/SPjrqryBNrB4iCz3K5ZY3Q2lh1Q3K9Hmeo2haMCFWFEmPZzkyhLFSOwjiexsXZoYkhyucDVh+NXSl/JHf45lyBDPfJdlniac1AX+OJe3Iu2UXwmuWJWhpYCDuTTSztlZJfCXcTFbt3SsC1wQKBgQDMOS6eCSqGfKpFlWdljYTNY3BBPHAWh4SUjVDVQKEsIHuTVF4e/P7Sw1Jo19lnP6wl7fDN6gOdDGNGMBR7qWJnXajfnDjWBPfe5FAbB8CaN6lVWemtLUuASoqs0VmAoDbi8+c+YmThsAzzv04Z0Irj8o4xO7ABtyZ6goNxc2h1IQKBgQD1vN85+p1NcdLbpXhcBwhEW5kmr5JnyjoHxz8Gp2wMnNeNyxOsMJW/onZdFEiGe1B4EpcSj6ZIM6IUay9DgT2qx6B0vWzTfxPM0llX0LSRRos8kTx4NczJBYnuVDwMbLiY0BoHxmiXikDVy2oY/1lw4pPXEzO1Twi9Et5HolyCwwKBgGt2s5cpj50RQLA4/B2uKHHDn7BUU08npJZe/mgZCDamKSDPxm9cBeN2MIlG+/mewcF7wbk5KQmdGSg36K9h5Iwr9s8j76x/FMfPMG/o1mrQW2S0WYhtS0uI7gweZZCPvhIS8l4bfKZd85LX2gsvd8aXPked9vIQJElXID+00QTBAoGAMd6mnqmRisnytMib6HE06Ep+hi9TUuTjafixfbQ0ZUrfI9N2ppcljMJ3quQW49PavMNpDUdBw51W4zXtXZG45hOlKIvYrgM4DXUS1JNyUZkzYJNI5kxkxtbs6wUFOrBFth1lavaE+1v/VGP3oCMSD6qrvhYThFQJvfU6TudQRukCgYAOFXi0ruA1KOV9Xq55MAf0EZYNPZcq3Gb4cl1lC8si2G/wLaMDeWi3/AJpvPDfZGKPPwtdbN7nVqoeLF8HADUPwywAwMNZ8x+ekATsWVN1j3BW21zJ6dpYpIXm+ZQZu4m+tmZpW1VY/8YMO+gB7mem/AINfy9FPJFjpJDOBx8d6w==");

            KeyFactory kf = KeyFactory.getInstance("RSA");

            CA_PUBLIC_KEY = kf.generatePublic(
                    new X509EncodedKeySpec(pub)
            );

            CA_PRIVATE_KEY = kf.generatePrivate(
                    new PKCS8EncodedKeySpec(priv)
            );

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}