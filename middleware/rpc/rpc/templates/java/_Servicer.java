import java.net.InetAddress;
import middleware.rudp.Servicer;
import middleware.rudp.UDPClient;


public class {__SERVICE_NAME__}Servicer implements Servicer {
    public static final int BUFFER_SIZE = 1024;
    private {__SERVICE_NAME__} service;

    public {__SERVICE_NAME__}Servicer({__SERVICE_NAME__} service) {
        this.service = service;
    }

    public int _dispatch(byte[] message, byte[] response) throws Exception {
        int[] i = new int[]{0};
        int method_id = Unmarshaller.unmarshall_int(message, i);

        switch (method_id) {{__DISPATCH_CODE__}
        }
    }
    
    public void callback(byte[] data, InetAddress client_addr, int client_port) {
        byte[] response = new byte[BUFFER_SIZE];
        try {
            int response_len = _dispatch(data, response);
            UDPClient.send(client_addr, client_port, response, response_len);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
