import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Servidor {
    private HashMap<String, HashSet<Socket>> canales;

    public Servidor() {
        canales = new HashMap<>();
    }

    public Servidor(HashMap<String, HashSet<Socket>> canales) {
        this.canales = canales;
    }

    public HashMap<String, HashSet<Socket>> getCanales() {
        return canales;
    }

    public void setCanales(HashMap<String, HashSet<Socket>> canales) {
        this.canales = canales;
    }

    public String agregarSuscripcion(String topic, Socket clienteSocket) {
        HashSet<Socket> suscriptores = canales.getOrDefault(topic, new HashSet<>());
        if(suscriptores.contains(clienteSocket)) {
            return "Ya esta suscripto";
        }
        else{
            suscriptores.add(clienteSocket);
            canales.put(topic, suscriptores);
            return "Suscripto a: "+topic;
        }
    }

    public String eliminarSuscripcion(String topic, Socket clienteSocket) {
        HashSet<Socket> suscriptores = canales.get(topic);
        if(suscriptores!=null) {
            if(suscriptores.size()>0) {
                suscriptores.remove(clienteSocket);
                return "Desuscripto a: "+topic;
            }
            else{
                return "No esta suscripto";
            }
        }
        else {
            return "No esta suscripto";
        }
    }

    public void enviarMensaje(String topic, String mensaje) {
        HashSet<Socket> suscriptores = canales.get(topic);
        if(suscriptores.size()>0) {
            for (Socket suscriptor : suscriptores) {
                try {
                    PrintWriter output = new PrintWriter(suscriptor.getOutputStream(), true);
                    output.println(topic + ":" + mensaje);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        Servidor servidor = new Servidor();
        try {
            ServerSocket serverSocket = new ServerSocket(4001);
            System.out.println("Esperando conexión...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado: " + clientSocket.getInetAddress());

                BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);

                String topic = "";
                String mensaje = "";
                String mensaje2 = "";
                while ((mensaje = input.readLine()) != null) {
                    System.out.println("Cliente dice: " + mensaje);
                    if (mensaje.startsWith("s:")) { //suscribirse
                        topic = mensaje.substring(2);
                        System.out.println( servidor.agregarSuscripcion(topic, clientSocket));
                    } else if (mensaje.startsWith("u:")) { // desuscribir
                        topic = mensaje.substring(2);
                        System.out.println(servidor.eliminarSuscripcion(topic, clientSocket));
                    } else if (mensaje.startsWith("m:")) { // mensaje
                        String[] parts = mensaje.split(":", 3);
                        topic = parts[1];
                        mensaje2 = parts[2];
                        servidor.enviarMensaje(topic, mensaje2);
                        System.out.println("Mensaje enviado al tema: " + topic + ": " + mensaje2);
                        servidor.enviarMensaje(topic, mensaje2);
                    }
                }
                clientSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
