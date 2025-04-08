package middleware.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.InetAddress;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;


class ProtocolException extends RuntimeException {
    public ProtocolException(String message) {
        super(message);
    }
}

class DuplicatePacketException extends ProtocolException {
    public DuplicatePacketException(String message) {
        super(message);
    }
}

/**
 * RUDP implements a reliable UDP-based protocol using
 * transmissions with exponential backoffs, up till a 
 * specified max retry limit.
 */
public class RUDP {

    private static final int BUFFER_SIZE = 2048;
    private static final int MAX_RETRIES = 5;
    private static final int SOCKET_TIMEOUT = 2000;
    private static final int BASE_BACKOFF = 400;
    private static final float PACKET_DROP_PROBABILITY = 0.25f;
    private final boolean deduplicate;
    private final DatagramSocket socket;
    private final Map<String, Integer> conn_seqs = new HashMap<>();  // track max seq num for each conn
    private final Map<String, Bytes> conn_resps = new HashMap<>();  // cache prev responses for duplicate packets


    public RUDP() throws SocketException {
        this.deduplicate = true;
        this.socket = new DatagramSocket();
        socket.setSoTimeout(SOCKET_TIMEOUT);
    }

    public RUDP(int port) throws SocketException {
        this.deduplicate = true;
        this.socket = new DatagramSocket(port);
        socket.setSoTimeout(SOCKET_TIMEOUT);
    }

    public RUDP(boolean deduplicate) throws SocketException {
        this.deduplicate = deduplicate;
        this.socket = new DatagramSocket();
        socket.setSoTimeout(SOCKET_TIMEOUT);
    }

    public RUDP(int port, boolean deduplicate) throws SocketException {
        this.deduplicate = deduplicate;
        this.socket = new DatagramSocket(port);
        socket.setSoTimeout(SOCKET_TIMEOUT);
    }

    private String _get_conn_str(InetAddress addr, int port) {
        return addr.getHostAddress() + ":" + port;
    }

    private int _get_next_seq(InetAddress addr, int port) {
        String conn = _get_conn_str(addr, port);
        return conn_seqs.getOrDefault(conn, 1);
    }

    private void _set_next_seq(InetAddress addr, int port, int seq) {
        String conn = _get_conn_str(addr, port);
        conn_seqs.put(conn, seq);
    }

    private void _cache_response(InetAddress addr, int port, Bytes response) {
        String conn = _get_conn_str(addr, port);
        conn_resps.put(conn, response);
    }

    private Bytes _get_cached_response(InetAddress addr, int port) {
        String conn = _get_conn_str(addr, port);
        return conn_resps.get(conn);
    }

    /**
     * Get max buffer size for RUDP payloads
     */
    public int get_buffer_size() {
        return BUFFER_SIZE;
    }

    /**
     * Get request sequence number from RUDP payload which contains a RUDP header
     * 
     * @param bytes Raw byte array of RUDP payload
     * @return Request ID
     */
    private int _get_rudp_seq_num(byte[] bytes) {
        int res = 0;
        for (int i=0; i<4; i++) {
            res <<= 8;
            res |= bytes[i];
        }
        return res;
    }

    private Bytes _strip_rudp_header(Bytes data) {
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
     * @param request_id RUDP sequence number for RUDP header
     * @param data Data with RUDP header
     * @throws IOException
     */
    private void _send_once(InetAddress addr, int port, Bytes rudp_payload) throws IOException {
        System.out.println("Sending message with seq number " + _get_rudp_seq_num(rudp_payload.bytes()));
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
     * @throws ProtocolException
     */
    private Bytes _send_with_retry(InetAddress addr, int port, Bytes payload) throws ProtocolException {
        int req_seq = _get_next_seq(addr, port);
        Bytes rudp_payload = _add_rudp_header(payload, req_seq);
        _set_next_seq(addr, port, req_seq+1);  // for next recv
        
        for (int i=1; i<=MAX_RETRIES; i++) {
            try {
                _send_once(addr, port, rudp_payload);
                DatagramPacket resp = _recv();
                Bytes resp_bytes = new Bytes(resp.getData(), resp.getLength());
                _set_next_seq(addr, port, req_seq + 2);  // for next send
                return _strip_rudp_header(resp_bytes);
            }
            catch (SocketTimeoutException err) {}
            catch (ProtocolException | IOException e) {
                System.out.println(e.getMessage());
            }
            
            if (i >= MAX_RETRIES)
                break;            

            int backoff = BASE_BACKOFF * i;
            System.out.println("Backing off for " + backoff + " ms ...\n");
            try {
                Thread.sleep(backoff);
            } catch (InterruptedException err) {}
        }
        throw new ProtocolException(
            "ACK not received after " + MAX_RETRIES + " retries"
        );
    }

    private Bytes _add_rudp_header(Bytes payload, int request_id) {
        byte[] rudp_payload = new byte[payload.length() + 4];
        // add request id as RUDP header
        for (int i=0; i<4; i++) {
            rudp_payload[i] = (byte) (request_id >> ((4-1-i)*8));
        }
        System.arraycopy(payload.bytes(), 0, rudp_payload, 4, payload.length());
        return new Bytes(rudp_payload, rudp_payload.length);
    }

    /**
     * Send request and retry until response is received.
     * 
     * @param addr Destination address
     * @param port Destination port
     * @param payload Payload without RUDP headers
     * @return Response without RUDP payload
     * @throws Exception
     */
    public Bytes send(InetAddress addr, int port, Bytes payload) throws Exception {
        // send request and await for response
        Bytes response = _send_with_retry(addr, port, payload);
        return response;
    }

    /**
     * Receive a single packet with deduplication logic
     * 
     * @return DatagramPacket received
     */
    private DatagramPacket _recv() throws IOException, SocketTimeoutException, DuplicatePacketException {
        Random random = new Random();
        byte[] buffer = new byte[BUFFER_SIZE];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        int recv_seq = _get_rudp_seq_num(packet.getData());

        System.out.println("Received message with seq number " + recv_seq);

        if (random.nextFloat() < PACKET_DROP_PROBABILITY) {
            System.out.println("Packet dropped by receiver");
            throw new SocketTimeoutException();
        }

        if (!deduplicate) return packet;
        
        // deduplicate
        int expected_recv_seq = _get_next_seq(packet.getAddress(), packet.getPort());
        if (expected_recv_seq != recv_seq) {
            // send cached response
            InetAddress sender_addr = packet.getAddress();
            int sender_port = packet.getPort();
            Bytes cached_response = _get_cached_response(sender_addr, sender_port);

            if (cached_response != null)
                _send_once(sender_addr, sender_port, cached_response);

            throw new DuplicatePacketException(
                "Duplicate packet received. " 
                + "Expected seq: " + expected_recv_seq + ". "
                + "Recevied seq: " + recv_seq
            );
        }
        return packet;
    }

    /**
     * Listen for RPC requests on the port allocated in constructor. On receiving
     * requests, handle it with servicer and send the response with retries.
     * 
     * @param servicer
     */
    public void listen(Servicer servicer, int minutes) {
        System.out.println("Listening on port " + socket.getLocalPort() + " ...");

        long startTime = System.currentTimeMillis();
        long endTime = startTime + minutes * 60 * 1000; // Convert minutes to milliseconds

        while (System.currentTimeMillis() < endTime) {
            // execute request
            try {
                // receive
                DatagramPacket recv_packet = _recv();
                InetAddress request_addr = recv_packet.getAddress();
                int request_port = recv_packet.getPort();
                Bytes recv_data_with_header = new Bytes(recv_packet.getData(), recv_packet.getLength());
                Bytes recv_data_without_header = _strip_rudp_header(recv_data_with_header);
                int recv_seq = _get_rudp_seq_num(recv_data_with_header.bytes());

                Bytes result_bytes = servicer.callback(
                    recv_data_without_header,
                    BUFFER_SIZE
                );
                result_bytes = _add_rudp_header(result_bytes, recv_seq + 1);

                // send response
                _send_once(
                    request_addr, 
                    request_port,
                    result_bytes
                );
                _set_next_seq(
                    request_addr, 
                    request_port,
                    recv_seq + 2
                );
                _cache_response(
                    request_addr, 
                    request_port,
                    result_bytes
                );
            } catch (SocketTimeoutException err ) {
            } catch (IOException e) {
                // socket-related exceptions
                System.out.println("IO exception when receiving via RUDP");
            } catch (DuplicatePacketException _) {
                // duplicate packet. Send cached response and continue.
            } catch (Exception err) {
                System.out.println("Exception raised by callback. Terminating listen ...");
                break;
            }
        }
        System.out.println("Subscription has expired.");
    }

    public void close() {
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }
}
