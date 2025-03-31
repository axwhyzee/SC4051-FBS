package rudp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


public class UDPClient {
    private static final int BUFFER_SIZE = 2048;

    public static void listen(int port, Servicer callback) {
        DatagramSocket socket = null;
        System.out.println("Listening on port " + port + " ...");
        try {
            socket = new DatagramSocket(port);
            byte[] buffer = new byte[BUFFER_SIZE];

            while (true) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                callback.callback(packet.getData(), packet.getAddress(), packet.getPort());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }

    public static void send(InetAddress addr, int port, byte[] data, int n) {
        DatagramSocket socket = null;

        try {
            socket = new DatagramSocket();
            DatagramPacket packet = new DatagramPacket(data, n, addr, port);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }
}
