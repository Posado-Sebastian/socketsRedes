import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
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
    public static byte[] encriptar(byte[] plainText, PublicKey publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(RSA);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);//encripta con la pub.key del otro
        return cipher.doFinal(plainText);
    }
    public static String desencriptar(byte[] cipherText, PrivateKey privateKey) throws Exception {
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
    public static SecretKey generateKey(int n) throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(n);
        SecretKey key = keyGenerator.generateKey();
        return key;
    }
    public static byte[] encriptar2(byte[] plainText, SecretKey secretKey) throws Exception, NoSuchPaddingException {
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(plainText);
    }
    public static String desencriptar2(byte[] cipherText, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] result= cipher.doFinal(cipherText);
        return new String(result);
    }

    public static void main(String[] args) {
        String msg="mensaje";
        byte[] msg2;
        System.out.println(msg);
        String msgE=null;
        try {
            SecretKey k = generateKey(256);
            msg2=encriptar2(msg.getBytes(),k);
            System.out.println(msg2);
            msgE=desencriptar2(msg2,k);
            System.out.println(msgE);
        }catch (NoSuchAlgorithmException e){
            System.out.println(e.getMessage());
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
