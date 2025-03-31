package middleware.rudp;

import java.net.InetAddress;

/**
 * Protocol defines the interface for a network protocol.
 * All protocols must implement the send and listen methods.
 */
public interface Protocol {

    /**
     * In a blocking infinite loop, isten on local port for incoming 
     * RPC requests, then handle using the servicer via callbacks.
     * Raise an error using the servicer callback to break out of the
     * loop.
     * 
     * @param port Local port to listen on
     * @param servicer Servicer instance to handle incoming requests
     */
    void listen(int port, Servicer servicer);

    /**
     * Send data to specified address and port
     * 
     * @param addr Receiver address to send to
     * @param port Receiver port to send to
     * @param data Data to send as Bytes
     * @param n Length of data in bytes
     * @return Server response as Bytes
     */
    Bytes send(InetAddress addr, int port, Bytes data);

    /**
     * Get max buffer size of a packet
     * @return Buffer size in bytes
     */
    int get_buffer_size();
}
