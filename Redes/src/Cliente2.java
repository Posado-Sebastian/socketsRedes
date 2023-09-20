import javax.crypto.SecretKey;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.HashSet;
import java.util.Scanner;
public class Cliente2 {
    public Socket socket;
    public Cliente2() {}
    public static void main(String[] args) throws Exception {
        KeyPair keypair = Criptografia.generarLLaves();
        PublicKey llaveServidor;
        SecretKey llaveSimetrica;
     //    String serverAddress = "172.16.255.190";
        String serverAddress = "localhost";
        Scanner s=new Scanner(System.in);
        int serverPort = 4001;
        boolean si=true;
        Thread clientThread = null ;
        Cliente2 cliente=new Cliente2();
        try {
            Socket socket = new Socket(serverAddress, serverPort);
            HashSet<String>topicsSuscriptos=new HashSet<>();
            cliente.socket=socket;
            System.out.println("Conectado al servidor: " + socket.getInetAddress());
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            output.println(Criptografia.keyToStringBase64(keypair.getPublic()));// Intercambio de llaves
            llaveServidor = Criptografia.stringBase64ToKey(input.readLine());   //      =           =
            llaveSimetrica = Criptografia.base64ToSecretKey(Mensajero.recibirMensajeAsimetrico(input.readLine(), keypair, llaveServidor));
            System.out.println("llaves compartidas");
            clientThread = new Thread(new ClientHandler2(input, socket, llaveServidor, keypair, llaveSimetrica));
            clientThread.start();
            while(si){
                System.out.println("1=Suscribirse a un topic");
                System.out.println("2=Desuscribirese de un topic");
                System.out.println("3=Enviar mensaje");
                System.out.println("4=Cambiar nombre");
                System.out.println("5=Borrar nombre");
                System.out.println("6=Recibir topicos");
                System.out.println("7=Mostrar suscripciones");
                System.out.println("8=Terminar conexion");
                switch(s.nextInt()){
                    case 1:
                        System.out.println("Topic:");
                        s.nextLine();
                        String topic=s.nextLine();
                        Mensajero.enviarMensajeSimetrico("s:"+topic, llaveSimetrica, socket, keypair);
                        topicsSuscriptos.add(topic);
                        break;
                    case 2:
                        System.out.println("Topic:");
                        s.nextLine();
                        String topic2=s.nextLine();
                        Mensajero.enviarMensajeSimetrico("u:"+topic2, llaveSimetrica, socket, keypair);
                        topicsSuscriptos.remove(topic2);
                        break;
                    case 3:
                        System.out.println("Escribir mensaje");
                        s.nextLine(); // Se come el salto de linea
                        String m=s.nextLine();
                        System.out.println("Topic:");
                        Mensajero.enviarMensajeSimetrico("m:"+s.nextLine()+":"+m, llaveSimetrica, socket, keypair);
                        Thread.sleep(1000);
                    break;
                    case 4:
                        System.out.println("Elegir nombre");
                        s.nextLine();
                        Mensajero.enviarMensajeSimetrico("nickname:"+s.nextLine(), llaveSimetrica,socket, keypair);
                    break;
                    case 5:
                        Mensajero.enviarMensajeSimetrico("nickname:default", llaveSimetrica,socket, keypair);
                    break;
                    case 6:
                        Mensajero.enviarMensajeSimetrico("Topics", llaveSimetrica, socket, keypair);
                        Thread.sleep(1000);
                    break;
                    case 7:
                        for(String str:topicsSuscriptos){
                            System.err.println(str);
                        }
                        if(topicsSuscriptos.size()<1){
                            System.err.println("No se ha suscripto a ninigun topico");
                        }
                    break;
                    case 8:
                        Mensajero.enviarMensajeSimetrico("END", llaveSimetrica , socket, keypair);
                        si=false;
                    break;
                }
            }
            System.out.println("Conexion terminada, gracias por usar nuestro codigo");
        } catch (Exception e) {
            e.getMessage();
            e.printStackTrace();
        }
    }
    private static class ClientHandler2 implements Runnable {
        private BufferedReader input;
        private Socket socket;
        private PublicKey llave;
        private KeyPair keyPair;
        private SecretKey llaveSecreta;
        public ClientHandler2( BufferedReader input2, Socket socket, PublicKey llave, KeyPair keyPair, SecretKey llaveSecreta) {
            this.input = input2;
            this.socket=socket;
            this.llave=llave;
            this.keyPair=keyPair;
            this.llaveSecreta=llaveSecreta;
        }
        @Override
        public void run() {
            try {
                String mensaje;
                while ((mensaje = input.readLine()) != null) {
                    mensaje=Mensajero.recibirMensajeSimetrico(mensaje,llaveSecreta, llave);
                    System.err.println(mensaje);
                    Mensajero.enviarMensajeSimetrico("ack/"+mensaje, llaveSecreta, socket, keyPair);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
