import javax.crypto.SecretKey;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Scanner;
public class Cliente {
    public Socket socket;
    public Cliente() {}
    public static void main(String[] args) throws Exception {
                KeyPair keypair = Criptografia.generarLLaves();
                PublicKey llaveServidor;
                SecretKey llaveSimetrica;
                 String serverAddress = "172.16.255.190";
              //  String serverAddress = "localhost";
                Scanner s=new Scanner(System.in);
                int serverPort = 4001;
                boolean si=true;
                Thread clientThread = null ;
                Cliente cliente=new Cliente();
                try {
                    Socket socket = new Socket(serverAddress, serverPort);
                    cliente.socket=socket;
                    System.out.println("Conectado al servidor: " + socket.getInetAddress());
                    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
                    output.println(Criptografia.keyToStringBase64(keypair.getPublic()));// Intercambio de llaves
                    llaveServidor = Criptografia.stringBase64ToKey(input.readLine());   //      =           =
                    clientThread = new Thread(new ClientHandler2(input, output, cliente, socket, llaveServidor, keypair));
                    clientThread.start();
                    while(si){
                        System.out.println("1=Suscribirse a un topic");
                        System.out.println("2=Desuscribirese de un topic");
                        System.out.println("3=Enviar mensaje");
                        System.out.println("4=Cambiar nombre");
                        System.out.println("5=Borrar nombre");
                        System.out.println("6=Recibir topicos");
                        System.out.println("7=Terminar conexion");
                        switch(s.nextInt()){
                            case 1:
                                System.out.println("Que topic:");
                                System.out.println("1=Clima");
                                System.out.println("2=Fecha");
                                switch (s.nextInt()){
                                    case 1:
                                        Mensajero.enviarMensajeAsimetrico("s:Clima", llaveServidor, keypair, socket);
                                        break;
                                    case 2:
                                        Mensajero.enviarMensajeAsimetrico("s:Fecha", llaveServidor, keypair, socket);
                                        break;
                                }
                                break;
                            case 2:
                                System.out.println("Que topic:");
                                System.out.println("1=Clima");
                                System.out.println("2=Fecha");
                                switch (s.nextInt()){
                                    case 1:
                                        Mensajero.enviarMensajeAsimetrico("u:Clima", llaveServidor, keypair, socket);
                                        break;
                                    case 2:
                                        Mensajero.enviarMensajeAsimetrico("u:Fecha", llaveServidor, keypair, socket);
                                        break;
                                }
                                break;
                            case 3:
                                System.out.println("Escribir mensaje");
                                s.nextLine(); // Se come el salto de linea
                                String m=s.nextLine();
                                System.out.println("Que topic:");
                                System.out.println("1=Clima");
                                System.out.println("2=Fecha");
                                int t=s.nextInt();
                                switch (t){
                                    case 1:
                                        Mensajero.enviarMensajeAsimetrico("m:Clima:"+m, llaveServidor, keypair,socket);
                                        break;
                                    case 2:
                                        Mensajero.enviarMensajeAsimetrico("m:Fecha:"+m, llaveServidor, keypair, socket);
                                        break;
                                }
                                break;
                            case 4:
                                System.out.println("Elegir nombre");
                                s.nextLine();
                                Mensajero.enviarMensajeAsimetrico("nickname:"+s.nextLine(), llaveServidor, keypair,socket);
                                break;
                            case 5:
                                Mensajero.enviarMensajeAsimetrico("nickname:default", llaveServidor, keypair,socket);
                                break;
                            case 6:
                                Mensajero.enviarMensajeAsimetrico("Topics", llaveServidor, keypair, socket);
                                break;
                            case 7:
                                Mensajero.enviarMensajeAsimetrico("END", llaveServidor, keypair, socket);
                                si=false;
                                break;
                        }
                        Thread.sleep(1000);
                    }
                    System.out.println("Conexion terminada, gracias por usar nuestro codigo");

                } catch (Exception e) {
                    e.printStackTrace();
                }
    }
    private static class ClientHandler2 implements Runnable {
        private BufferedReader input;
        private Socket socket;
        private PublicKey llave;
        private KeyPair keyPair;
        public ClientHandler2( BufferedReader input2, PrintWriter output2, Cliente cliente, Socket socket, PublicKey llave, KeyPair keyPair) {
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
                    mensaje=Mensajero.recibirMensajeAsimetrico(mensaje,keyPair,llave);
                    System.err.println(mensaje);
                    Mensajero.enviarMensajeAsimetrico("ack/"+mensaje, llave, keyPair, socket);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
