import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
public class Mensajero {
    public static String enviarFirma(String me, PrivateKey llave){
        try {
            int m2=(me.hashCode());                                //hashea
            String m=Integer.toString(m2);                         //Convierte el hash a String
            byte[] concat=m.getBytes(StandardCharsets.UTF_8);      //Lo pasa a bytes
            byte[] aux=Criptografia.firma(concat,llave);           //Lo firma
            return (Criptografia.byteTobase64(aux));               //Lo pasa a base 64 y lo devuelve
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void enviarMensaje(String m, PublicKey llave, KeyPair keypair, Socket socket){
        try {
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            byte[] concat=m.getBytes(StandardCharsets.UTF_8);                           //Lo pasa a bytes
            byte[] aux=Criptografia.encriptar(concat,llave);                            //Lo encripta
            output.println(enviarFirma(m, keypair.getPrivate())+":"+Criptografia.byteTobase64(aux));  //Concatena la firma con lo anterior en base 64. Y lo envia
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String recibirMensaje(String mensajeR, KeyPair keypair, PublicKey publicKey) throws Exception {
        String firma;
        String[] parts = mensajeR.split(":", 2);   //Divide la firma y el mensaje
        String firmaRecibida=parts[0];                  //
        String mensaje=parts[1];                        //
        //Mensaje
        byte[] aux = Criptografia.base64ToByte(mensaje);                    //Lo pase de b64 a bytes
        mensaje = Criptografia.desencriptar(aux, keypair.getPrivate());     //Lo desencripta
        System.out.println(mensaje);
        //Firma
        firma=Integer.toString(mensaje.hashCode());                         //Hace una copia del mesaje hasheado
        byte[] aux2 = Criptografia.base64ToByte(firmaRecibida);             //Pasa de b64 a bytes
        String aux3 = Criptografia.desencriptarFirma(aux2, publicKey);      //Lo "desfirma"
        System.out.println(aux3);
        if(firma.equals(aux3)){                                             //Compara la firma con el mensaje hasheado
            return mensaje;
        }
        else{
            System.out.println("Error");
            return null;
        }
    }
}
