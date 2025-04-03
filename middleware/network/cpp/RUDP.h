#include <netinet/in.h> 
#include "servicer.h"
#include <unordered_map>

#pragma once


class RUDP {
public:
    RUDP(int port = 0, bool deduplicate = true);
    ~RUDP();

    /**
     * Send RPC request and receive response
     */
    int send(
        sockaddr_in addr,
        char* request_data,
        int request_len,
        char* response_data,
        int response_len
    );

    /**
     * Get RUDP max buffer size for payloads
     */
    int get_buffer_size();

    /**
     * Read request sequence number from RUDP payload
     */
    int _get_rudp_seq_num(char* rudp_payload);

    /**
     * Remove RUDP headers from the RUDP payload
     * 
     * @data RUDP payload
     */
    void _strip_rudp_header(char* rudp_payload);

    /**
     * Send RUDP payload without awaiting for response
     * 
     * @param addr Destination address
     * @param rudp_payload RUDP payload to send
     * @param rudp_payload_len Length of RUDP payload in bytes
     */
    void _send_once(sockaddr_in addr, char* rudp_payload, int rudp_payload_len);

    /**
     * Repeatedly send a RUDP payload until response is 
     * received with request ID matching the sent RUDP payload
     * 
     * @param sockaddr_in Destination address
     * @param rudp_payload RUDP payload
     * @param rudp_payload_len Length of RUDP payload in bytes
     * @param rudp_response Buffer for receiving RUDP payload as response
     * @param rudp_response_len Len of received RUDP payload
     */
    int _send_with_retry(
        sockaddr_in addr, 
        char* rudp_payload, 
        int rudp_payload_len, 
        char* rudp_response, 
        int rudp_response_len
    );

    /**
     * Add request ID to RUDP payload as headers
     * 
     * @param payload Normal payload without RUDP headers
     * @param request_id Request ID to add as RUDP headers
     */
    void _add_rudp_header(char* payload, int request_id);

    /**
     * Receive without caring about client addr
     * 
     * * @return Len of received data in bytes
     */
    int _recv(char* receive_buffer);

    /**
     * Receive both data and client addr
     * 
     * @return Len of received data in bytes
     */
    int _recv(char* receive_buffer, sockaddr_in& client_addr);

    /**
     * Listen for incoming RPCs and handle using servicer callback
     * 
     * @param servicer Servicer with a callback interface
     */
    void listen(Servicer& servicer);

private:
    static const int ACK_SEQ = 0;
    static const int START_SEQ = 1;
    static const int BUFFER_SIZE = 2048;
    static const int MAX_RETRIES = 5;
    static constexpr float SOCKET_TIMEOUT = 2.0f;
    static constexpr float BASE_BACKOFF = 1.5f;
    static constexpr float PACKET_DROP_PROBABILITY = 0.0f;
    std::unordered_map<std::string, int> conn_seqs;
    int sockfd;
    bool deduplicate;
};
