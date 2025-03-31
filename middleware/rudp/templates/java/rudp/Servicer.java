package rudp;

import java.net.InetAddress;

public interface Servicer {
    void callback(byte[] data, InetAddress client_addr, int client_port);
}
