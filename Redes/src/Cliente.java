import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("172.16.255.190", 1234); // Dirección IP y puerto del servidor
            System.out.println("Conectado al servidor.");

            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.print("Ingrese un mensaje: ");
                String message = scanner.nextLine(); // Lee la entrada del usuario

                output.println(message); // Envía el mensaje al servidor
                String response = input.readLine(); // Espera la respuesta del servidor
                System.out.println("Servidor dice: " + response);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
