import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
public class Mensajero {
    public Mensajero() {
    }
    public static String enviarFirma(String me, PrivateKey llave, Socket socket){
        try {
            int m2=(me.hashCode());
            String m=Integer.toString(m2);
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            byte[] concat=m.getBytes(StandardCharsets.UTF_8);
            byte[] aux=Criptografia.firma(concat,llave);
            return (Criptografia.byteTobase64(aux));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void enviarMensaje(String m, PublicKey llave, KeyPair keypair, Socket socket){
        try {
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            byte[] concat=m.getBytes(StandardCharsets.UTF_8);
            byte[] aux=Criptografia.encriptar(concat,llave);
            output.println(enviarFirma(m, keypair.getPrivate(), socket)+":"+Criptografia.byteTobase64(aux));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String recibirMensaje(String mensajeR, KeyPair keypair, PublicKey publicKey) throws Exception {
        String firma;
        String[] parts = mensajeR.split(":", 2);
        String firmaRecibida=parts[0];
        String mensaje=parts[1];
        //Mensaje
        byte[] aux = Criptografia.base64ToByte(mensaje);
        System.out.println(aux.length);
        mensaje = Criptografia.desencriptar(aux, keypair.getPrivate());
        //Firma
        firma=Integer.toString(mensaje.hashCode());
        byte[] aux2 = Criptografia.base64ToByte(firmaRecibida);
        System.out.println(aux2.length);
        String aux3 = Criptografia.desencriptarFirma(aux2, publicKey);
        if(firma.equals(aux3)){
            return mensaje;
        }
        else{
            System.out.println("Error");
            return null;
        }
    }
}
