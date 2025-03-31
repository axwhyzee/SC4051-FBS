import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


/**
 * RUDP implements a reliable UDP-based protocol using
 * transmissions with exponential backoffs, up till a 
 * specified max retry limit.
 */
public class RUDP implements Protocol {

    private static final int BUFFER_SIZE = 2048;

    @Override
    public int get_buffer_size() {
        return BUFFER_SIZE;
    }

    @Override
    public void listen(int port, Servicer servicer) {
        DatagramSocket socket = null;
        System.out.println("Listening on port " + port + " ...");

        try {
            socket = new DatagramSocket(port);
            byte[] buffer = new byte[BUFFER_SIZE];

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                Bytes received_bytes = new Bytes(packet.getData(), packet.getLength());

                try {
                    // run callback and send response
                    Bytes result_bytes = servicer.callback(received_bytes);
                    send(
                        packet.getAddress(), 
                        packet.getPort(), 
                        result_bytes
                    );
                } catch (Exception e) {
                    break;
                }   
            }
        } catch (IOException e) {
            // socket-related exceptions
            System.out.println("IO exception when receiving via RUDP");
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }

    @Override
    public Bytes send(InetAddress addr, int port, Bytes data) {
        DatagramSocket socket = null;

        try {
            socket = new DatagramSocket();
            DatagramPacket packet;

            // send UDP packet
            packet = new DatagramPacket(data.bytes(), data.length(), addr, port);
            socket.send(packet);

            // receive UDP packet as response
            packet = new DatagramPacket(data.bytes(), data.length(), addr, port);
            socket.receive(packet);
            return new Bytes(packet.getData(), packet.getLength());
        } catch (IOException e) {
            System.out.println("IO exception when sending via RUDP");
            return new Bytes(new byte[0], 0);
        }
         finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }
}
