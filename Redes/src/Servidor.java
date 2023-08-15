import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;

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

    public HashSet<String> mostrarTopicos() {
        HashSet<String> topics = new HashSet<>();
        for (String s : canales.keySet()) {
            topics.add(s);
        }
        return topics;
    }

    public String agregarSuscripcion(String topic, Socket clienteSocket, String nombre) {
        HashSet<Socket> suscriptores = canales.getOrDefault(topic, new HashSet<>());
        if (suscriptores.contains(clienteSocket)) {
            if(nombre==null) {
                return clienteSocket.getInetAddress() + " YA ESTÁ SUSCRIPTO";
            }else{
                return nombre + " YA ESTÁ SUSCRIPTO";
            }
        } else {
            suscriptores.add(clienteSocket);
            canales.put(topic, suscriptores);
            if(nombre==null) {
                return clienteSocket.getInetAddress() + " SUSCRIPTO A: " + topic;
            }
            else{
                return nombre + " SUSCRIPTO A: " + topic;
            }
        }
    }

    public String eliminarSuscripcion(String topic, Socket clienteSocket, String nombre) {
        HashSet<Socket> suscriptores = canales.get(topic);
        if (suscriptores != null) {
            if(suscriptores.contains(clienteSocket)) {
                suscriptores.remove(clienteSocket);
                if(nombre==null) {
                    return clienteSocket.getInetAddress() + " DESUSCRIPTO A: " + topic;
                }
                else{
                    return nombre + " DESUSCRIPTO A: " + topic;
                }
            }
            else{
                return "NO ESTÁ SUSCRIPTO";
            }

        } else {
            return "NO ESTÁ SUSCRIPTO";
        }
    }

    public void enviarMensaje(String topic, String mensaje) {
        HashSet<Socket> suscriptores = canales.get(topic);
        if (suscriptores != null && suscriptores.size() > 0) {
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
        int aux=0;
        Servidor servidor = new Servidor();
        try {
            ServerSocket serverSocket1 = new ServerSocket(4001);
            System.out.println("Esperando conexión...");

            while (true) {
                Thread clientThread = null;
                Socket clientSocket1 = serverSocket1.accept();
                System.out.println("CLIENTE " + clientSocket1.getInetAddress() + " CONECTADO");
                clientThread = new Thread(new ClientHandler(clientSocket1, servidor));
                clientThread.start();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private Servidor servidor;

        public ClientHandler(Socket clientSocket, Servidor servidor) {
            this.clientSocket = clientSocket;
            this.servidor = servidor;
        }

        @Override
        public void run() {
            try {
                BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);
                LoggerPro l=new LoggerPro();
                int ack=1;
                String topic = "";
                String mensaje = "";
                String mensaje2 = "";
                String nombre=null;
                boolean si=true;
                while (si && (mensaje = input.readLine()) != null) {
                    if(nombre==null) {
                        l.escribir(clientSocket.getInetAddress() + " DICE: " + mensaje);
                    }
                    else{
                        l.escribir(nombre + " DICE: " + mensaje);
                    }
                    if (mensaje.startsWith("s:")) { // suscribirse
                        topic = mensaje.substring(2);
                        l.escribir(servidor.agregarSuscripcion(topic, clientSocket, nombre));
                    } else if (mensaje.startsWith("u:")) { // desuscribir
                        topic = mensaje.substring(2);
                        l.escribir(servidor.eliminarSuscripcion(topic, clientSocket, nombre));
                    } else if (mensaje.startsWith("m:")) { // mensaje
                        String[] parts = mensaje.split(":", 3);
                        topic = parts[1];
                        mensaje2 = parts[2];
                        servidor.enviarMensaje(topic, mensaje2);
                        if(nombre==null) {
                            l.escribir(clientSocket.getInetAddress()+" ENVÍO AL TEMA " + topic + ": " + mensaje2);
                        }
                        else{
                            l.escribir(nombre+" ENVÍO AL TEMA " + topic + ": " + mensaje2);
                        }
                    } else if (mensaje.startsWith("Topics")) {
                        for (String s : servidor.mostrarTopicos()) {
                            output.println(s);
                        }
                    } else if (mensaje.startsWith("nombre:")) {
                        String aux = mensaje.substring(7);
                        if(nombre==null) {
                            l.escribir(clientSocket.getInetAddress()+" AHORA SERÁ " + aux);
                        }
                        else{
                            l.escribir(nombre + " AHORA SERÁ: " + aux);
                        }
                        nombre = aux;
                    } else if (mensaje.startsWith("ack/")) {
                        if(nombre==null) {
                            l.escribir(clientSocket.getInetAddress()+" RECIBÍO EL MENSAJE: "+ mensaje.substring(4) +" DE FORMA EXITOSA");
                        }
                        else{
                            l.escribir(nombre +" RECIBÍO EL MENSAJE: "+ mensaje +" DE FROMA EXITOSA");
                        }
                    } else if(mensaje.startsWith("END")){
                        clientSocket.close();
                        si=false;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}