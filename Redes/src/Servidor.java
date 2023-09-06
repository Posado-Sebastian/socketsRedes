import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
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
            System.out.println("hola");
            topics.add(s);
        }
        return topics;
    }

    public String agregarSuscripcion(String topic, Socket clienteSocket, String nickname) {
        HashSet<Socket> suscriptores = canales.getOrDefault(topic, new HashSet<>());
        if (suscriptores.contains(clienteSocket)) {
            if(nickname==null) {
                return clienteSocket.getInetAddress() + " YA ESTÁ SUSCRIPTO";
            }else{
                return nickname + " YA ESTÁ SUSCRIPTO";
            }
        } else {
            suscriptores.add(clienteSocket);
            canales.put(topic, suscriptores);
            if(nickname==null) {
                return clienteSocket.getInetAddress() + " SUSCRIPTO A: " + topic;
            }
            else{
                return nickname + " SUSCRIPTO A: " + topic;
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
    public String recibirMensaje(String mensaje1, String mensaje2, KeyPair keypair, PublicKey publicKey) throws Exception {
        String firma;
        String firmaRecibida=mensaje1;
        String mensaje=mensaje2;
        //Mensaje
        byte[] aux = Criptografia.base64ToByte(mensaje);
        System.out.println(aux.length);
        mensaje = Criptografia.desencriptar(aux, keypair.getPrivate());
        //Firma
        firma=Integer.toString(mensaje.hashCode());
        byte[] aux2 = Criptografia.base64ToByte(firmaRecibida);
        System.out.println(aux2.length);
        String aux3 = Criptografia.desencriptarFirma(aux2, publicKey);

        if(firma.equals(aux3)){
            return mensaje;
        }
        else{
            System.out.println("Error");
            return null;
        }
    }

    public void enviarMensaje(String topic, String mensaje, PublicKey llave) {//envia mensaje a todos los suscriptos al topico
        String m;
        HashSet<Socket> suscriptores = canales.get(topic);
        if (suscriptores != null && suscriptores.size() > 0) {
            for (Socket suscriptor : suscriptores) {
                try {
                    PrintWriter output = new PrintWriter(suscriptor.getOutputStream(), true);
                    m=(topic + ":" + mensaje);
                    byte[] concat=m.getBytes(StandardCharsets.UTF_8);
                    byte[] aux=Criptografia.encriptar(concat, llave);
                    output.println(Criptografia.byteTobase64(aux));
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
                clientThread = new Thread(new ClientHandler(clientSocket1, servidor));
                clientThread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //handler de hilos
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
                KeyPair keypair = Criptografia.generarLLaves();
                PublicKey llaveCliente;
                BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);
                LoggerPro l=new LoggerPro();
                l.escribir("CLIENTE " + clientSocket.getInetAddress() + " CONECTADO");//logger sout
                String topic = "";
                byte[] aux;
                String mensaje="";
                String mensajep2="";
                String mensaje2 = "";
                String nickname=null;
                boolean si=true;
                llaveCliente=Criptografia.stringBase64ToKey(input.readLine());
                output.println(Criptografia.keyToStringBase64(keypair.getPublic()));
                System.out.println("LLaves compartidas");
                while (si && (mensaje = input.readLine()) != null) {//input.readLine() lee
                    mensajep2 = input.readLine();
                    mensaje=servidor.recibirMensaje(mensaje, mensajep2, keypair, llaveCliente);
                    if(nickname==null) {
                        l.escribir(clientSocket.getInetAddress() + " DICE: " + mensaje);
                    }
                    else{
                        l.escribir(nickname + " DICE: " + mensaje);
                    }
                    if (mensaje.startsWith("s:")) { // suscribirse
                        topic = mensaje.substring(2);
                        l.escribir(servidor.agregarSuscripcion(topic, clientSocket, nickname));
                    } else if (mensaje.startsWith("u:")) { // desuscribir
                        topic = mensaje.substring(2);
                        l.escribir(servidor.eliminarSuscripcion(topic, clientSocket, nickname));
                    } else if (mensaje.startsWith("m:")) { // mensaje
                        String[] parts = mensaje.split(":", 3);
                        topic = parts[1];
                        mensaje2 = parts[2];
                        servidor.enviarMensaje(topic, mensaje2, llaveCliente);
                        if(nickname==null) {
                            l.escribir(clientSocket.getInetAddress()+" ENVÍO AL TEMA " + topic + ": " + mensaje2);
                        }
                        else{
                            l.escribir(nickname+" ENVÍO AL TEMA " + topic + ": " + mensaje2);
                        }
                    } else if (mensaje.startsWith("Topics")) {
                        for (String s : servidor.mostrarTopicos()) {
                            byte[] concat=s.getBytes(StandardCharsets.UTF_8);
                            byte[] ayuda=Criptografia.encriptar(concat, llaveCliente);
                            output.println(Criptografia.byteTobase64(ayuda));
                        }
                    } else if (mensaje.startsWith("nickname:")) {
                        String aux2 = mensaje.substring(9);
                        if(nickname==null) {
                            l.escribir(clientSocket.getInetAddress()+" AHORA SERÁ " + aux2);
                        }
                        else{
                            l.escribir(nickname + " AHORA SERÁ: " + aux2);
                        }
                        nickname = aux2;
                    } else if (mensaje.startsWith("ack/")) {  //recibio el mensaje
                        if(nickname==null) {
                            l.escribir(clientSocket.getInetAddress()+" RECIBÍO EL MENSAJE: "+ mensaje.substring(4) +" DE FORMA EXITOSA");
                        }
                        else{
                            l.escribir(nickname +" RECIBÍO EL MENSAJE: "+ mensaje +" DE FROMA EXITOSA");
                        }
                    } else if(mensaje.startsWith("END")){
                        clientSocket.close();
                        si=false;
                    }
                    else if(mensaje.startsWith("f:")){
                        System.out.println(mensaje.substring(2));
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