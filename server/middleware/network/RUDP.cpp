#include <iostream>
#include <stdio.h>
#include <sys/time.h>
#include <sys/types.h>
#include <unistd.h>
#include <netinet/in.h> 
#include <arpa/inet.h>
#include <random>
#include "servicer.h"
#include "RUDP.h"

void print_buffer(char* buffer, int len) {
    for (int i = 0; i < len; i++) {
        printf("bytes %d: %x | ", i, buffer[i]);
    }
    std::cout << '\n';
}

float _random_probability() {
    std::random_device rd;
    std::mt19937 gen(rd());
    std::uniform_real_distribution<float> dist(0.0f, 1.0f); // 0 - 1, inclusive
    return dist(gen);
}


RUDP::RUDP(int port, bool deduplicate) : deduplicate(deduplicate) {
    sockfd = socket(AF_INET, SOCK_DGRAM, 0);
    if (sockfd < 0 ) { 
        throw std::runtime_error("Socket creation failed");
    }
    
    // bind socket to addr and port
    struct sockaddr_in server_addr; 
    memset(&server_addr, 0, sizeof(server_addr));
    server_addr.sin_family = AF_INET;
    server_addr.sin_addr.s_addr = INADDR_ANY; 
    server_addr.sin_port = htons(port);

    // Set receive timeout
    struct timeval timeout;
    timeout.tv_sec = SOCKET_TIMEOUT;
    timeout.tv_usec = ((int) (SOCKET_TIMEOUT * 100000)) % 100000;
    
    if (
        setsockopt(
            sockfd,
            SOL_SOCKET,
            SO_RCVTIMEO,
            (const char*)&timeout, 
            sizeof(timeout)
        ) < 0
    ) {
        throw std::runtime_error("Error setting socket receive timeout");
    }

    // bind socket
    if (
        bind(
            sockfd, 
            (const struct sockaddr *)&server_addr,  
            sizeof(server_addr)
        ) < 0 
    )
        throw std::runtime_error("Bind failed"); 

    conn_seqs = std::unordered_map<std::string, int>();
}

RUDP::~RUDP() {
    if (sockfd >= 0)
        close(sockfd);
}


int RUDP::get_buffer_size() {
    return BUFFER_SIZE;
}


void RUDP::_send_once(sockaddr_in addr, char* rudp_payload, int rudp_payload_len) {
    std::cout << "Sending message with seq num " << _get_rudp_seq_num(rudp_payload) << std::endl;
    if (_random_probability() < PACKET_DROP_PROBABILITY) {
        std::cout << "Packet dropped while sending" << std::endl;
        return;
    }
    sendto(
        sockfd, 
        rudp_payload, 
        rudp_payload_len, 
        0,
        (struct sockaddr*)&addr, 
        sizeof(addr)
    );
}


int RUDP::_recv(char* receive_buffer, sockaddr_in& client_addr) {
    socklen_t client_len = sizeof(client_addr);
    int request_len = recvfrom(
        sockfd, 
        receive_buffer, 
        BUFFER_SIZE,
        0, 
        (struct sockaddr*)&client_addr, 
        &client_len
    );

    if (request_len < 0) {
        if (errno == EAGAIN || errno == EWOULDBLOCK)
            // std::cout << "Socket recv timed out" << std::endl;
            ;
        else
            std::cerr << "Error receiving data errno: " << errno << std::endl;
        return request_len;
    }

    int recv_seq = _get_rudp_seq_num(receive_buffer);
    std::cout << "Received message with seq num " << recv_seq << std::endl;

    // random drop packets
    if (_random_probability() < PACKET_DROP_PROBABILITY) {
        std::cout << "Packet dropped by receiver" << std::endl;
        return -1;
    }

    if (!deduplicate) return request_len;

    // deduplicate
    std::string conn = std::string(inet_ntoa(client_addr.sin_addr)) + ":" + std::to_string(ntohs(client_addr.sin_port));    

    if (recv_seq == ACK_SEQ) {
        // ACK is sent at end of sequence
        conn_seqs.erase(conn);
    } else if (
        (conn_seqs.find(conn) == conn_seqs.end() && recv_seq <= 2) ||
        (conn_seqs[conn] + 2 == recv_seq)
    ) {
        // new sequence or correct next sequence 
        conn_seqs.emplace(conn, recv_seq);
    } else {
        // drop duplicate packet
        return -1;
    }
    return request_len;
}


int RUDP::_recv(char* receive_buffer) {
    sockaddr_in _;
    return _recv(receive_buffer, _);
}

void RUDP::_strip_rudp_header(char* rudp_payload) {
    std::memcpy(rudp_payload, rudp_payload+4, BUFFER_SIZE-4);
}

int RUDP::_send_with_retry(
    sockaddr_in addr, 
    char* rudp_payload, 
    int rudp_payload_len, 
    char* rudp_response, 
    int rudp_response_len
) {
    int recv_len;
    int send_seq = _get_rudp_seq_num(rudp_payload);

    for (int i=1; i<=MAX_RETRIES; i++) {
        _send_once(addr, rudp_payload, rudp_payload_len);
        
        // recv successfully, check if request ID is correct
        if ((recv_len = _recv(rudp_response)) >= 0) {
            int recv_seq = _get_rudp_seq_num(rudp_response);

            _strip_rudp_header(rudp_response);
            return recv_len - 4;
        }
        // recv failed, check if should retry or not
        if (i < MAX_RETRIES) {
            float backoff = BASE_BACKOFF * i;
            std::cout << "Backing off for " << backoff << " seconds ..." << std::endl;
            sleep(backoff);
        }
    }
    throw std::runtime_error("ACK not received");
    std::cout << "ACK not received after " + std::to_string(MAX_RETRIES) + " retries" << std::endl;
}

void RUDP::listen(Servicer& servicer) {
  
    struct sockaddr_in client_addr; 
    socklen_t client_len = sizeof(client_addr);
    char recv_buffer[BUFFER_SIZE], resp_buffer[BUFFER_SIZE];;
    int recv_len, resp_len, recv_seq;

    while (true) {     
        // socket has timeout so keep listening  
        if ((recv_len = _recv(recv_buffer, client_addr)) < 0)
            continue;  
        // for (int i = 0; i < recv_len; i++) {
        //     recv_buffer[i] = ntohl(recv_buffer[i]);
        // }
        print_buffer(recv_buffer, recv_len);
        // handle message w/o RUDP headers
        recv_seq = _get_rudp_seq_num(recv_buffer);
        try {
            resp_len = servicer.callback(
                recv_buffer + 4, recv_len - 4, resp_buffer, client_addr
            );
        } catch (const std::exception& e) {
            // servicer deliberately threw a callback to break listen loop
            break;
        }

        try {
            // respond to client
            _add_rudp_header(resp_buffer, recv_seq + 1);
            _send_once(client_addr, resp_buffer, resp_len+4);
        } catch (const std::exception& e) {
            // no ACK received after sending response to client
            // server should ignore and continue running
        }
    }
}


int RUDP::_get_rudp_seq_num(char* rudp_payload) {
    int res = 0;
    for (int j=0; j<4; j++) {
        res <<= 8;
        res |= rudp_payload[j];
    }
    return res;
}

int RUDP::send(
    sockaddr_in addr,
    char* request_data,
    int request_len,
    char* response_data,
    int response_len
) {
    // for every new RPC request, generate a random request ID
    int request_id = 1;

    // send request and await for response
    
    _add_rudp_header(request_data, request_id);
    int result_len = _send_with_retry(
        addr, 
        request_data, 
        request_len+4,
        response_data,
        response_len
    );

    // send ACK (empty payload, only request ID header) after receiving response
    char ack_payload[BUFFER_SIZE];
    _add_rudp_header(ack_payload, ACK_SEQ);
    _send_once(addr, ack_payload, 4);
    conn_seqs.clear();
    return result_len - 4;
}


void RUDP::_add_rudp_header(char* payload, int request_id) {
    // shift back by 4 bytes
    std::memcpy(payload+4, payload, BUFFER_SIZE-4);

    for (int j=0; j<4; j++)
       payload[j] = 0xFF & (request_id >> (8 * (4 - j - 1)));
}