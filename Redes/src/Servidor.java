import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

public class Servidor {
    private HashSet<ClienteSusc> clientes;
    public HashSet<ClienteSusc> getClientes() {return clientes;}
    public void setClientes(HashSet<ClienteSusc> clientes) {this.clientes = clientes;}
    public Servidor(HashSet<ClienteSusc> clientes) {this.clientes = clientes;}
    public Servidor() {this.clientes = null;}
    public void agregarCliente(InetAddress ip){
        clientes.add(new ClienteSusc(null, ip));
    }
    public void agregarSus(InetAddress ip, String nuevo) {
        for (ClienteSusc c : clientes) {
            if (c.getIp().equals(ip)) {
                HashSet<String> aux = new HashSet<>(c.getSuscriptos());
                aux.add(nuevo);
                c.setSuscriptos(aux);
            }
        }
    }

    public static void main(String[] args) {
        Servidor s=new Servidor();
        boolean fallo=true;
        String topic1="Clima";
        String topic2="Aviso";
        String topic3="Fecha";
        try {
            ServerSocket serverSocket = new ServerSocket(4000); // Puerto del servidor
            System.out.println("Esperando conexión...");

            Socket clientSocket = serverSocket.accept(); // Espera a que un cliente se conecte
            System.out.println("Cliente conectado.");

            BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);

            String message;
            while ((message = input.readLine()) != null) {
                System.out.println("Cliente dice: " + message);
                if(message.substring(3, message.length())=="s:"){
                    System.out.println("s");
                    for(ClienteSusc c:s.getClientes()){
                        if(clientSocket.getInetAddress()==c.getIp()){
                            s.agregarSus(c.getIp(), message.substring(2));
                            fallo=false;
                        }
                    }
                    if(fallo){
                        s.agregarCliente(clientSocket.getInetAddress());
                    }
                } else if (message.contains("m:Clima:")) {
                    System.out.println("m,1");
                    output.println(message.substring(2));
                }
                else if (message.contains("m:Aviso:")) {
                    System.out.println("m,2");
                    output.println(message.substring(2));
                }
                else if (message.contains("m:Fecha")) {
                    System.out.println("m,3");
                    output.println(message.substring(2));
                }
            }
            clientSocket.close();
            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}