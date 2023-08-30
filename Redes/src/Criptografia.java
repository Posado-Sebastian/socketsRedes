import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.PublicKey;

public class Criptografia {
    private static final String RSA = "RSA";

    public static KeyPair generarLLaves() throws Exception {
        SecureRandom secureRandom = new SecureRandom();
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA);
        keyPairGenerator.initialize(2048, secureRandom);
        return keyPairGenerator.generateKeyPair();
    }
    public static byte[] encriptar(String plainText, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(plainText.getBytes());
    }
    public static String desencriptar(byte[] cipherText, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] result = cipher.doFinal(cipherText);
        return new String(result);
    }
    public static String convertStringToBinary(String input) {

        StringBuilder result = new StringBuilder();
        char[] chars = input.toCharArray();
        for (char aChar : chars) {
            result.append(
                    String.format("%8s", Integer.toBinaryString(aChar))
            );
        }
        return result.toString();

    }
    public static PublicKey stringBase64ToKey(String publicK) throws Exception {
        byte[] encodedPublicKey = Base64.getDecoder().decode(publicK);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(encodedPublicKey);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }
    public static String keyToStringBase64(PublicKey publicK){
        String publicKeyBase64 = Base64.getEncoder().encodeToString(publicK.getEncoded());
        return publicKeyBase64;
    }
    public static void main(String args[]) throws Exception {
        KeyPair keypair = generarLLaves();
        /*String plainText = "Hola";
        byte[] cipherText = encriptar(plainText, keypair.getPublic());
        System.out.println("The Public Key is: " + DatatypeConverter.printHexBinary(keypair.getPublic().getEncoded()));
        System.out.println("The Private Key is: " + DatatypeConverter.printHexBinary(keypair.getPrivate().getEncoded()));
        System.out.println("The Encrypted Text is: "+DatatypeConverter.printHexBinary(cipherText));
        String decryptedText = desencriptar(cipherText, keypair.getPrivate());
        System.out.println("The decrypted text is: " + decryptedText);*/





    }
}
