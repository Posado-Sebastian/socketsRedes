import sun.net.InetAddressCachePolicy;

import java.net.InetAddress;
import java.net.Socket;
import java.util.HashSet;

public class ClienteSusc {
    private HashSet<String> suscriptos;
    private InetAddress ip;

    public HashSet<String> getSuscriptos() {
        return suscriptos;
    }

    public void setSuscriptos(HashSet<String> suscriptos) {
        this.suscriptos = suscriptos;
    }
    public InetAddress getIp() {
        return ip;
    }
    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

    public ClienteSusc(HashSet<String> suscriptos, InetAddress ip) {
        this.suscriptos = suscriptos;
        this.ip = ip;
    }
    public ClienteSusc() {
        this.suscriptos = new HashSet<>();
        this.ip = null;
    }
}
