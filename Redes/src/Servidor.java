import javax.crypto.SecretKey;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static jdk.nashorn.internal.objects.NativeString.substring;

public class Servidor {
    private  static HashMap<Socket, HashSet<String>> mensajesSinACK=new HashMap<>();
    private HashMap<String, HashMap<Socket, PublicKey>> canales;
    private HashMap<String, HashMap<Socket, SecretKey>> canalesV2;
    public Servidor() {
        canales = new HashMap<>();
    }
    public Servidor(HashMap<String, HashMap<Socket, PublicKey>> canales) {
        this.canales = canales;
    }
    public HashMap<String, HashMap<Socket, PublicKey>> getCanales() {return canales;}
    public HashMap<String, HashMap<Socket, SecretKey>> getCanalesV2() {return canalesV2;}
    public void setCanales(HashMap<String, HashMap<Socket, PublicKey>> canales) {
        this.canales = canales;
    }
    public HashSet<String> mostrarTopicos() {
        HashSet<String> topics = new HashSet<>();
        for (String s : canales.keySet()) {
            topics.add(s);
        }
        return topics;
    }
    public String agregarSuscripcion(String topic, Socket clienteSocket, String nickname, PublicKey llave) {
        HashMap<Socket, PublicKey> suscriptores = canales.getOrDefault(topic, new HashMap<>());
        if (suscriptores.containsKey(clienteSocket)) {
            if(nickname==null) {
                return clienteSocket.getInetAddress() + " YA ESTÁ SUSCRIPTO";
            }else{
                return nickname + " YA ESTÁ SUSCRIPTO";
            }
        } else {
            suscriptores.put(clienteSocket,llave);
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
        HashMap<Socket, PublicKey> suscriptores = canales.get(topic);
        if (suscriptores != null) {
            if(suscriptores.containsKey(clienteSocket)) {
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
    public void enviarMensaje(String topic, String mensaje, PublicKey llave, KeyPair keyPair) {//envia mensaje a todos los suscriptos al topico
        String m;
        HashMap<Socket, PublicKey> suscriptores = canales.get(topic);
        HashMap<Socket, SecretKey> suscriptoresV2 = canalesV2.get(topic);
        HashSet<String> msj;
        if (suscriptores != null && suscriptores.size() > 0) {
            for (Socket suscriptor : suscriptores.keySet()) {
                try {
                    m=(topic + ":" + mensaje);
                    Mensajero.enviarMensajeSimetrico(m,suscriptoresV2.get(suscriptor), suscriptor, keyPair);
                    if(Servidor.mensajesSinACK.size()<=0){
                        msj=new HashSet<>();
                        msj.add(m);
                        Servidor.mensajesSinACK.put(suscriptor, msj);
                    }
                    else {
                        msj = Servidor.mensajesSinACK.get(suscriptor);
                        msj.add(m);
                        Servidor.mensajesSinACK.put(suscriptor, msj);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static void main(String[] args) throws Exception {
        KeyPair keypair = Criptografia.generarLLaves();
        int aux=0;
        Servidor servidor = new Servidor();
        HashMap <String, HashMap<Socket, PublicKey>> auxC=new HashMap<>();
        servidor.setCanales(auxC);
        try {
            ServerSocket serverSocket1 = new ServerSocket(4001);
            System.out.println("Esperando conexión...");
            while (true) {
                Thread clientThread = null;
                Socket clientSocket1 = serverSocket1.accept();
                clientThread = new Thread(new ClientHandler(clientSocket1, servidor, keypair));
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
        private KeyPair keypair;

        public ClientHandler(Socket clientSocket, Servidor servidor, KeyPair keypair) {
            this.clientSocket = clientSocket;
            this.servidor = servidor;
            this.keypair = keypair;
        }

        @Override
        public void run() {
            try {
                PublicKey llaveCliente;
                SecretKey llaveSectreta=Criptografia.generateSecretKey();
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
                Mensajero.enviarMensajeAsimetrico(Criptografia.secretKeyToBase64(llaveSectreta), llaveCliente,keypair, clientSocket);
                System.out.println("LLaves compartidas");
                while (si && (mensaje = input.readLine()) != null) {//input.readLine() lee
                    mensaje=Mensajero.recibirMensajeSimetrico(mensaje, llaveSectreta, llaveCliente);
                    if(nickname==null) {
                        l.escribir(clientSocket.getInetAddress() + " DICE: " + mensaje);
                    }
                    else{
                        l.escribir(nickname + " DICE: " + mensaje);
                    }
                    if (mensaje.startsWith("s:")) { // suscribirse
                        topic = mensaje.substring(2);
                        l.escribir(servidor.agregarSuscripcion(topic, clientSocket, nickname, llaveCliente));
                    } else if (mensaje.startsWith("u:")) { // desuscribir
                        topic = mensaje.substring(2);
                        l.escribir(servidor.eliminarSuscripcion(topic, clientSocket, nickname));
                    } else if (mensaje.startsWith("m:")) { // mensaje
                        String[] parts = mensaje.split(":", 3);
                        topic = parts[1];
                        mensaje2 = parts[2];
                        servidor.enviarMensaje(topic, mensaje2, llaveCliente,keypair);
                        if(nickname==null) {
                            l.escribir(clientSocket.getInetAddress()+" ENVÍO AL TEMA " + topic + ": " + mensaje2);
                        }
                        else{
                            l.escribir(nickname+" ENVÍO AL TEMA " + topic + ": " + mensaje2);
                        }
                    } else if (mensaje.startsWith("Topics")) {
                        for (String s : servidor.mostrarTopicos()) {
                            Mensajero.enviarMensajeSimetrico(s,llaveSectreta,clientSocket, keypair);
                        }
                    } else if (mensaje.startsWith("nickname:")) {
                        String aux2 = mensaje.substring(9);
                        if(!aux2.equals("default")) {
                            if (nickname == null) {
                                l.escribir(clientSocket.getInetAddress() + " AHORA SERÁ: " + aux2);
                            } else {
                                l.escribir(nickname + " AHORA SERÁ: " + aux2);
                            }
                            nickname = aux2;
                        }
                        else{
                            if(nickname==null){
                                l.escribir(clientSocket.getInetAddress()+ " AHORA SERÁ: " + clientSocket.getInetAddress());
                            }
                            else{
                                l.escribir(nickname + " AHORA SERÁ: "+clientSocket.getInetAddress());
                            }
                            nickname=null;
                        }
                    } else if (mensaje.startsWith("ack/")) {  //recibio el mensaje
                        Servidor.mensajesSinACK.get(clientSocket).remove(mensaje.substring(4));
                        if(nickname==null) {
                            l.escribir(clientSocket.getInetAddress()+" RECIBÍO EL MENSAJE: "+ mensaje.substring(4) +" DE FORMA EXITOSA");
                        }
                        else{
                            l.escribir(nickname +" RECIBÍO EL MENSAJE: "+ substring(mensaje, 4) +" DE FROMA EXITOSA");
                        }
                    } else if(mensaje.startsWith("END")){
                        clientSocket.close();
                        si=false;
                    } else if (mensaje.startsWith("SIN")) {
                        for(Map.Entry<Socket, HashSet<String>> entry:Servidor.mensajesSinACK.entrySet()){
                            System.out.println(entry.getKey().getInetAddress());
                            for(String s:entry.getValue()){
                                System.out.println(s);
                            }
                        }
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