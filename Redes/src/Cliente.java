import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        int aux=1;
        String topic="";
        String message=null;
        String serverAddress = "172.16.255.190";
        Scanner s=new Scanner(System.in);
        int serverPort = 4001;

        try {
            Socket socket = new Socket(serverAddress, serverPort);
            System.out.println("Conectado al servidor: " + socket.getInetAddress());

            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);


            while(true){
                System.out.println("Que queres hacer:");
                System.out.println("1=Suscribirse a un topic");
                System.out.println("2=Desuscribirese de un topic");
                System.out.println("3=Enviar mensaje");
                System.out.println("4=Leer mensajes recibidos");
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
                            if(aux==1) {
                                System.out.println(message);
                                aux=0;
                            }
                            else{
                                aux=1;
                            }
                        }
                    break;
                }
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
