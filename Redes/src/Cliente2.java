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
         String serverAddress = "172.16.255.190";
      //  String serverAddress = "localhost";
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
            clientThread = new Thread(new ClientHandler2(input, socket, llaveServidor, keypair));
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
                        Mensajero.enviarMensaje("s:"+topic, llaveServidor, keypair, socket);
                        topicsSuscriptos.add(topic);
                        break;
                    case 2:
                        System.out.println("Topic:");
                        s.nextLine();
                        String topic2=s.nextLine();
                        Mensajero.enviarMensaje("u:"+topic2, llaveServidor, keypair, socket);
                        topicsSuscriptos.remove(topic2);
                        break;
                    case 3:
                        System.out.println("Escribir mensaje");
                        s.nextLine(); // Se come el salto de linea
                        String m=s.nextLine();
                        System.out.println("Topic:");
                        Mensajero.enviarMensaje("m:"+s.nextLine()+":"+m, llaveServidor, keypair, socket);
                        Thread.sleep(1000);
                    break;
                    case 4:
                        System.out.println("Elegir nombre");
                        s.nextLine();
                        Mensajero.enviarMensaje("nickname:"+s.nextLine(), llaveServidor, keypair,socket);
                    break;
                    case 5:
                        Mensajero.enviarMensaje("nickname:default", llaveServidor, keypair,socket);
                    break;
                    case 6:
                        Mensajero.enviarMensaje("Topics", llaveServidor, keypair, socket);
                        Thread.sleep(1000);
                    break;
                    case 7:
                        for(String str:topicsSuscriptos){
                            System.err.println(str);
                        }
                    break;
                    case 8:
                        Mensajero.enviarMensaje("END", llaveServidor, keypair, socket);
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
        public ClientHandler2( BufferedReader input2, Socket socket, PublicKey llave, KeyPair keyPair) {
            this.input = input2;
            this.socket=socket;
            this.llave=llave;
            this.keyPair=keyPair;
        }
        @Override
        public void run() {
            try {
                String mensaje;
                while ((mensaje = input.readLine()) != null) {
                    System.out.println("Este");
                    System.out.println(mensaje);
                    mensaje=Mensajero.recibirMensaje(mensaje,keyPair,llave);
                    System.err.println(mensaje);
                    Mensajero.enviarMensaje("ack/"+mensaje, llave, keyPair, socket);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
