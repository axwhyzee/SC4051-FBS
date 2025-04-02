#include <iostream>
#include <stdio.h>
#include <sys/time.h>
#include <sys/types.h>
#include <unistd.h>
#include <netinet/in.h> 
#include <random>
#include "servicer.h"
#include "RUDP.h"


RUDP::RUDP(int port) {
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
}

RUDP::~RUDP() {
    if (sockfd >= 0)
        close(sockfd);
}


int RUDP::get_buffer_size() {
    return BUFFER_SIZE;
}


void RUDP::_send_once(sockaddr_in addr, char* rudp_payload, int rudp_payload_len) {
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
            std::cout << "Socket recv timed out" << std::endl;
        else
            std::cerr << "Error receiving data" << std::endl;
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
    int request_id = _get_rudp_request_id(rudp_payload);

    for (int i=1; i<=MAX_RETRIES; i++) {
        std::cout << "Sending message with ID " << request_id << std::endl;
        _send_once(addr, rudp_payload, rudp_payload_len);
        
        // recv successfully, check if request ID is correct
        if ((recv_len = _recv(rudp_response)) >= 0) {
            int recv_request_id = _get_rudp_request_id(rudp_response);
            std::cout << "Received response with ID " << recv_request_id << std::endl;
            std::cout << "Expect response with ID " << request_id << std::endl;

            if (recv_request_id == request_id) {
                _strip_rudp_header(rudp_response);
                return recv_len - 4;
            }
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
    int recv_len, resp_len, request_id;

    while (true) {     
        // socket has timeout so keep listening  
        if ((recv_len = _recv(recv_buffer, client_addr)) < 0)
            continue;  
        
        // handle message w/o RUDP headers
        request_id = _get_rudp_request_id(recv_buffer);
        std::cout << "Received message with request ID " << request_id << std::endl;
        std::cout << "Received message with length " << recv_len << std::endl;
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
            _add_rudp_header(resp_buffer, request_id);
            _send_with_retry(
                client_addr,
                resp_buffer,
                resp_len + 4,
                recv_buffer,
                BUFFER_SIZE
            );
        } catch (const std::exception& e) {
            // no ACK received after sending response to client
            // server should ignore and continue running
        }
    }
}


int RUDP::_get_rudp_request_id(char* rudp_payload) {
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
    std::random_device rd;
    std::mt19937 gen(rd());
    int request_id = gen();

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
    _add_rudp_header(ack_payload, request_id);
    _send_once(addr, ack_payload, 4);
    return result_len - 4;
}


void RUDP::_add_rudp_header(char* payload, int request_id) {
    // shift back by 4 bytes
    std::memcpy(payload+4, payload, BUFFER_SIZE-4);

    for (int j=0; j<4; j++)
       payload[j] = 0xFF & (request_id >> (8 * (4 - j - 1)));
}