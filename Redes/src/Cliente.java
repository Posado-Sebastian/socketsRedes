import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        int aux=1;
        int akc=1;
        String topic="";
        String message=null;
        String serverAddress = "172.16.255.190";
        Scanner s=new Scanner(System.in);
        int serverPort = 4001;
        boolean si=true;

        try {
            Socket socket = new Socket(serverAddress, serverPort);
            System.out.println("Conectado al servidor: " + socket.getInetAddress());

            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);


            while(si){
                System.out.println("Que queres hacer:");
                System.out.println("1=Suscribirse a un topic");
                System.out.println("2=Desuscribirese de un topic");
                System.out.println("3=Enviar mensaje");
                System.out.println("4=Leer mensajes recibidos");
                System.out.println("5=Cambiar nombre");
                System.out.println("6=Recibir topicos");
                System.out.println("7=Terminar conexion");
                switch(s.nextInt()){
                    case 1:
                        System.out.println("Que topic:");
                        System.out.println("1=Clima");
                        System.out.println("2=Fecha");
                        switch (s.nextInt()){
                            case 1:
                                output.println("s:Clima");
                                break;
                            case 2:
                                output.println("s:Fecha");
                                break;
                        }
                        break;
                    case 2:
                        System.out.println("Que topic:");
                        System.out.println("1=Clima");
                        System.out.println("2=Fecha");
                        switch (s.nextInt()){
                            case 1:
                                output.println("u:Clima");
                                break;
                            case 2:
                                output.println("u:Fecha");
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
                                output.println("m:Clima:"+m);
                                break;
                            case 2:
                                output.println("m:Fecha:"+m);
                                break;
                        }
                    break;
                    case 4:
                        System.out.println("Mensajes recibidos:");
                        while (input.ready()) {
                            message = input.readLine();
                            System.out.println(message);
                            output.println("ack/"+message);
                        }
                    break;
                    case 5:
                        System.out.println("Elegir nombre");
                        s.nextLine();
                        output.println("nombre:"+s.nextLine());
                    break;
                    case 6:
                        output.println("Topics");
                    break;
                    case 7:
                        output.println("END");
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
}
