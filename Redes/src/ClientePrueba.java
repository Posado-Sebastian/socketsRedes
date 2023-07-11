import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

public class ClientePrueba {
    public String Uno(Socket socket, BufferedReader input,  PrintWriter output){
        Scanner s=new Scanner(System.in);
        ArrayList<String> topics=new ArrayList<String>();
        int aux=1;
        String message="";
        output.println("Topics");
        try {
            while (input.ready()) {
                message = input.readLine();
                System.out.println(message);
                topics.add(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for(String st:topics){
            System.out.println(st);
        }
        System.out.println("Que topic:");
        String t=s.nextLine();
        if(topics.contains(t)) {
            for (int i = 0; i < topics.size(); i++) {
                System.out.println(i + " = " + topics.get(i));
            }
            return topics.get(s.nextInt());
        }
        else{
            System.out.println("No existe, creando...");
            return t;
        }

    }
    public static void main(String[] args) {
        int aux=1;
        String topic="";
        String message=null;
        String serverAddress = "172.16.255.190";
        Scanner s=new Scanner(System.in);
        int serverPort = 4001;
        ClientePrueba c=new ClientePrueba();




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
                        output.println("s:"+c.Uno(socket, input, output));
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
