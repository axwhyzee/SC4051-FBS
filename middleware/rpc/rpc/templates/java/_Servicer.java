import java.net.DatagramPacket;
import java.net.DatagramSocket;


public class {__SERVICE_NAME__}Servicer {
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
    
    public void listen(int port) {
        DatagramSocket socket = null;

        try {
            socket = new DatagramSocket(port);
            byte[] receive_data = new byte[BUFFER_SIZE];
            System.out.printf("Listening on port %d...\n", port);

            while (true) {
                DatagramPacket receive_packet = new DatagramPacket(receive_data, receive_data.length);
                socket.receive(receive_packet);
                byte[] message = receive_packet.getData();

                // Send response
                byte[] response = new byte[BUFFER_SIZE];
                int response_len = _dispatch(message, response);
                DatagramPacket response_packet = new DatagramPacket(
                    message,
                    response_len,
                    receive_packet.getAddress(), 
                    receive_packet.getPort()
                );
                socket.send(response_packet);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }
}
