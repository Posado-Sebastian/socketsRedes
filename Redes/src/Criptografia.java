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
    public static byte[] firma(byte[] aux, PrivateKey privateKey) throws Exception{
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(aux);
    }
    public static String desencriptarFirma(byte[] cipherText, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] result= cipher.doFinal(cipherText);
        return new String(result);
    }
    public static byte[] encriptar(byte[] plainText, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(plainText);
    }
    public static String desencriptar(byte[] cipherText, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] result= cipher.doFinal(cipherText);
        return new String(result);
    }
    public static byte[] base64ToByte(String base64){
        Base64.Decoder dec = Base64.getDecoder();
        return dec.decode(base64);

    }
    public static String byteTobase64(byte [] mensaje){
        Base64.Encoder enc = Base64.getEncoder();
        String encoded = enc.encodeToString(mensaje);
        return encoded;
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
