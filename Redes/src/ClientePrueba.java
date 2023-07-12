import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;


public class ClientePrueba {
    public static void main(String[] args) {
        int aux=1;
        String topic="";
        String message=null;
        String serverAddress = "172.16.255.190";
        int serverPort = 4001;

        try {
            Socket socket = new Socket(serverAddress, serverPort);
            System.out.println("Conectado al servidor: " + socket.getInetAddress());

            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            output.println("nombre:BOT_FECHA");
            output.println("s:Fecha");
            while(true){
                while (input.ready()) {
                    message = input.readLine();
                    System.out.println("Mensaje: " + message);
                    if(message.startsWith("Fecha:hora")){
                        output.println("m:Fecha:"+LocalDateTime.now());
                    }
                }
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}