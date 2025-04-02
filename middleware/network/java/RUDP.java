import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.InetAddress;
import java.util.Random;


/**
 * RUDP implements a reliable UDP-based protocol using
 * transmissions with exponential backoffs, up till a 
 * specified max retry limit.
 */
public class RUDP {

    private static final int BUFFER_SIZE = 2048;
    private static final int MAX_RETRIES = 5;
    private static final int SOCKET_TIMEOUT = 2000;
    private static final int BASE_BACKOFF = 1500;
    private static final float PACKET_DROP_PROBABILITY = 0.2f;
    private static final Random random = new Random();
    private final DatagramSocket socket;


    public RUDP() throws SocketException {
        socket = new DatagramSocket();
        socket.setSoTimeout(SOCKET_TIMEOUT);
        System.out.println("RUDP bound to random port " + socket.getLocalPort());
    }


    public RUDP(int port) throws SocketException {
        socket = new DatagramSocket(port);
        socket.setSoTimeout(SOCKET_TIMEOUT);
        System.out.println("RUDP bound to port " + port);
    }

    /**
     * Get max buffer size for RUDP payloads
     */
    public int get_buffer_size() {
        return BUFFER_SIZE;
    }


    /**
     * Get request ID from RUDP payload which contains a RUDP header
     * 
     * @param bytes Raw byte array of RUDP payload
     * @return Request ID
     */
    public int _get_rudp_request_id(byte[] bytes) {
        int res = 0;
        for (int i=0; i<4; i++) {
            res <<= 8;
            res |= bytes[i];
        }
        return res;
    }


    public Bytes _strip_rudp_header(Bytes data) {
        int new_length = data.length() - 4;
        byte[] data_without_headers = new byte[new_length];
        System.arraycopy(data.bytes(), 4, data_without_headers, 0, new_length);
        return new Bytes(data_without_headers, new_length);
    }


    /**
     * Asynchronously send a RUDP payload
     * 
     * @param addr Receiver addr
     * @param port Receiver port
     * @param request_id Request ID for RUDP header
     * @param data Data with RUDP header
     * @throws IOException
     */
    public void _send_once(InetAddress addr, int port, Bytes rudp_payload) throws IOException {
        // send packet
        socket.send(
            new DatagramPacket(
                rudp_payload.bytes(),
                rudp_payload.length(),
                addr,
                port
            )
        );
    }


    /**
     * Send with retry mechanism

     * @param addr Destination address
     * @param port Destination port
     * @param request_id Request ID of current request-response sequence
     * @param request Request data without header
     * @return Received data without header
     * @throws Exception
     */
    public Bytes _send_with_retry(InetAddress addr, int port, Bytes rudp_payload) throws Exception {
        int request_id = _get_rudp_request_id(rudp_payload.bytes());
        
        for (int i=1; i<=MAX_RETRIES; i++) {
            try {
                System.out.println("Sending request with ID " + request_id);
                _send_once(addr, port, rudp_payload);
                DatagramPacket resp = _recv();
                int recv_request_id = _get_rudp_request_id(resp.getData());
                System.out.println("Received response with ID " + recv_request_id);
                System.out.println("Expect response with ID " + request_id);

                if (recv_request_id == request_id) {
                    Bytes resp_bytes = new Bytes(resp.getData(), resp.getLength());
                    return _strip_rudp_header(resp_bytes);
                }
            }
            catch (SocketTimeoutException _) {}
            catch (IOException _) {}
            
            if (i >= MAX_RETRIES)
                break;            

            int backoff = BASE_BACKOFF * i;
            System.out.println("Backing off for " + backoff + " ms ...\n");
            Thread.sleep(backoff);
        }
        throw new Exception(
            "ACK not received after " + MAX_RETRIES + " retries"
        );
    }


    public Bytes _add_rudp_header(Bytes payload, int request_id) {
        byte[] rudp_payload = new byte[payload.length() + 4];
        // add request id as RUDP header
        for (int i=0; i<4; i++) {
            rudp_payload[i] = (byte) (request_id >> ((4-1-i)*8));
        }
        System.arraycopy(payload.bytes(), 0, rudp_payload, 4, payload.length());
        return new Bytes(rudp_payload, rudp_payload.length);
    }

    /**
     * Send request and retry until response is received, after which
     * an ACK is sent to terminate request-response sequence
     * 
     * @param addr Destination address
     * @param port Destination port
     * @param payload Payload without RUDP headers
     * @return Response without RUDP payload
     * @throws Exception
     */
    public Bytes send(InetAddress addr, int port, Bytes payload) throws Exception {
        int request_id = random.nextInt();   

        // send request and await for response
        Bytes response = _send_with_retry(
            addr, port, _add_rudp_header(payload, request_id)
        );

        // send ACK (empty payload, only request ID header) after receiving response
        Bytes ack_payload = new Bytes(new byte[0], 0);
        _send_once(addr, port, _add_rudp_header(ack_payload, request_id));
        return response;
    }


    /**
     * Receive a single packet
     * 
     * @return DatagramPacket received
     */
    public DatagramPacket _recv() throws IOException, SocketTimeoutException {
        if (random.nextFloat() < PACKET_DROP_PROBABILITY) {
            System.out.println("Packet dropped on purpose by receiver");
            throw new SocketTimeoutException();
        }
        byte[] buffer = new byte[BUFFER_SIZE];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        return packet;
    }


    /**
     * Listen for RPC requests on the port allocated in constructor. On receiving
     * requests, handle it with servicer and send the response with retries.
     * 
     * @param servicer
     */
    public void listen(Servicer servicer) {
        System.out.println("Listening on port " + socket.getLocalPort() + " ...");

        
        while (true) {
            try {
                // receive
                DatagramPacket recv_packet = _recv();
                Bytes recv_data_with_header = new Bytes(recv_packet.getData(), recv_packet.getLength());
                Bytes recv_data_without_header = _strip_rudp_header(recv_data_with_header);
                int recv_request_id = _get_rudp_request_id(recv_data_with_header.bytes());

                // execute request
                Bytes result_bytes = servicer.callback(
                    recv_data_without_header,
                    BUFFER_SIZE
                );

                // respond with request, resending until ACKed
                _send_with_retry(
                    recv_packet.getAddress(), 
                    recv_packet.getPort(),
                    _add_rudp_header(result_bytes, recv_request_id)
                );
            } catch (SocketTimeoutException _ ) {
            } catch (IOException e) {
                // socket-related exceptions
                System.out.println("IO exception when receiving via RUDP");
            } catch (Exception _) {
                break;
            }
        }
    }


    public void close() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}
