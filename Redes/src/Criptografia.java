import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyFactory;
import java.security.PublicKey;
public class Criptografia {
    private static final String RSA = "RSA";
    private static final String AES = "AES";
    public static KeyPair generarLLaves() throws Exception {//Genera el par de llaves
        SecureRandom secureRandom = new SecureRandom();
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(RSA);
        keyPairGenerator.initialize(2048, secureRandom);
        return keyPairGenerator.generateKeyPair();
    }
    public static byte[] firma (byte[] aux, PrivateKey privateKey) throws Exception{
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);       //firma con la propia priv.key
        return cipher.doFinal(aux);
    }
    public static String desencriptarFirma(byte[] cipherText, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] result= cipher.doFinal(cipherText);
        return new String(result);
    }
    public static byte[] encriptarAsimetrico(byte[] plainText, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);//encripta con la pub.key del otro
        return cipher.doFinal(plainText);
    }
    public static String desencriptarAsimetrico(byte[] cipherText, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);//desencripta con la priv.key propia
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
    public static String secretKeyToBase64(SecretKey secretKey) {
        byte[] keyBytes = secretKey.getEncoded();
        return Base64.getEncoder().encodeToString(keyBytes);
    }
    public static SecretKey base64ToSecretKey(String base64Key) {
        byte[] keyBytes = Base64.getDecoder().decode(base64Key);
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, "AES"); // Cambia "AES" por el algoritmo de tu clave secreta
    }
//////////////////////////////////////////////////////
//////////////////////////////////////////////////////
//////////////////////////////////////////////////////
//////////////////////////////////////////////////////
//////////////////////////////////////////////////////
    public static SecretKey generateSecretKey() throws NoSuchAlgorithmException {
        int n=256;
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(n);
        SecretKey key = keyGenerator.generateKey();
        return key;
    }
    public static byte[] encriptarSimetrico(String plainText, SecretKey secretKey) throws Exception, NoSuchPaddingException {
        byte[] text=plainText.getBytes();
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(text);
    }
    public static String desencriptarSimetrico(byte[] cipherText, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] result= cipher.doFinal(cipherText);
        return new String(result);
    }

    public static void main(String[] args) throws Exception {
     /*  SecretKey secretKey= generateSecretKey();
        String base64Key = secretKeyToBase64(secretKey);
        SecretKey decodedSecretKey = base64ToSecretKey(base64Key);
        String msg="hola";
        byte[] msg2;
        System.out.println(msg);
        String msgE=null;
        msg2=encriptarSimetrico(msg,decodedSecretKey);
        System.out.println(msg2);
        msgE=desencriptarSimetrico(msg2,secretKey);
        System.out.println(msgE);
       */
    }
}
