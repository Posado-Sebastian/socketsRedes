import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;


public class Cliente {
    public Socket socket;
    public void enviarMensaje(String m, PublicKey llave, KeyPair keypair){
        try {
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            byte[] concat=m.getBytes(StandardCharsets.UTF_8);
            byte[] aux=Criptografia.encriptar(concat,llave);
            output.println(enviarFirma(m, keypair.getPrivate()));
            output.println(Criptografia.byteTobase64(aux));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void enviarAck(String m, PublicKey llave, Socket socket){
        try {
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            byte[] concat=m.getBytes(StandardCharsets.UTF_8);
            byte[] aux=Criptografia.encriptar(concat,llave);
            output.println(Criptografia.byteTobase64(aux));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String enviarFirma(String me,PrivateKey llave){
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
    public Cliente() {}
    public static void main(String[] args) throws Exception {
                KeyPair keypair = Criptografia.generarLLaves();
                PublicKey llaveServidor;
                int aux=1;
                int akc=1;
                String topic="";
                String message=null;
                // String serverAddress = "172.16.255.190";
                String serverAddress = "localhost";
                Scanner s=new Scanner(System.in);
                int serverPort = 4001;
                boolean si=true;
                Thread clientThread = null;
                Cliente cliente=new Cliente();
                try {
                    Socket socket = new Socket(serverAddress, serverPort);
                    cliente.socket=socket;
                    System.out.println("Conectado al servidor: " + socket.getInetAddress());

                    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
                    output.println(Criptografia.keyToStringBase64(keypair.getPublic()));
                    llaveServidor = Criptografia.stringBase64ToKey(input.readLine());
                    clientThread = new Thread(new ClientHandler2(input, output, cliente, socket, llaveServidor, keypair));

                    clientThread.start();

                    while(si){
                        System.out.println("Que queres hacer:");
                        System.out.println("1=Suscribirse a un topic");
                        System.out.println("2=Desuscribirese de un topic");
                        System.out.println("3=Enviar mensaje");
                        System.out.println("4=Cambiar nombre");
                        System.out.println("5=Recibir topicos");
                        System.out.println("6=Terminar conexion");
                        System.out.println("7=Enviar firma");
                        switch(s.nextInt()){
                            case 1:
                                System.out.println("Que topic:");
                                System.out.println("1=Clima");
                                System.out.println("2=Fecha");
                                switch (s.nextInt()){
                                    case 1:
                                        cliente.enviarMensaje("s:Clima", llaveServidor, keypair);
                                        break;
                                    case 2:
                                        cliente.enviarMensaje("s:Fecha", llaveServidor, keypair);
                                        break;
                                }
                                break;
                            case 2:
                                System.out.println("Que topic:");
                                System.out.println("1=Clima");
                                System.out.println("2=Fecha");
                                switch (s.nextInt()){
                                    case 1:
                                        cliente.enviarMensaje("u:Clima", llaveServidor, keypair);
                                        break;
                                    case 2:
                                        cliente.enviarMensaje("u:Fecha", llaveServidor, keypair);
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
                                        cliente.enviarMensaje("m:Clima:"+m, llaveServidor, keypair);
                                        break;
                                    case 2:
                                        cliente.enviarMensaje("m:Fecha:"+m, llaveServidor, keypair);
                                        break;
                                }
                                break;
                            case 4:
                                System.out.println("Elegir nombre");
                                s.nextLine();
                                cliente.enviarMensaje("nickname:"+s.nextLine(), llaveServidor, keypair);
                                break;
                            case 5:
                                cliente.enviarMensaje("Topics", llaveServidor, keypair);
                                break;
                            case 6:
                                cliente.enviarMensaje("END", llaveServidor, keypair);
                                si=false;
                                break;
                            case 7:
                                cliente.enviarFirma("firma", keypair.getPrivate());
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
                private PrintWriter output;
                private Cliente cliente;
                private Socket socket;
                private PublicKey llave;
                private KeyPair keyPair;

                public ClientHandler2( BufferedReader input2, PrintWriter output2, Cliente cliente, Socket socket, PublicKey llave, KeyPair keyPair) {
                    this.input = input2;
                    this.output = output2;
                    this.cliente=cliente;
                    this.socket=socket;
                    this.llave=llave;
                    this.keyPair=keyPair;
                }

                @Override
                public void run() {
                    try {
                        byte[] aux;
                        String mensaje="";
                        while ((mensaje = input.readLine()) != null) {
                            aux = Criptografia.base64ToByte(mensaje);
                            mensaje=Criptografia.desencriptar(aux, keyPair.getPrivate());
                            System.err.println(mensaje);
                            cliente.enviarAck("ack/"+mensaje, llave, socket);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
    }
}
