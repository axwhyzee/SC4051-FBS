#include <iostream>
#include <stdio.h>
#include <sys/time.h>
#include <sys/types.h>
#include <unistd.h>
#include <netinet/in.h> 
#include "servicer.h"
#include "protocol.h"


void RUDP::listen(int port, Servicer& servicer) {
    // create socket
    int sockfd = socket(AF_INET, SOCK_DGRAM, 0);
    if (sockfd < 0 ) { 
        throw std::runtime_error("Socket creation failed");
    }

    // bind socket to addr and port
    struct sockaddr_in server_addr; 
    memset(&server_addr, 0, sizeof(server_addr));
    server_addr.sin_family = AF_INET;
    server_addr.sin_addr.s_addr = INADDR_ANY; 
    server_addr.sin_port = htons(port);

    if (bind(
        sockfd, 
        (const struct sockaddr *)&server_addr,  
        sizeof(server_addr)) < 0 
    )
        throw std::runtime_error("Bind failed"); 

    std::cout << "Server listening on port " << port << std::endl;

    struct sockaddr_in client_addr; 
    socklen_t client_len = sizeof(client_addr);
    char request_data[BUFFER_SIZE];
    int request_len;

    while (true) {
        // accept incoming connections by creating new socket
        request_len = recvfrom(
            sockfd, 
            request_data, 
            BUFFER_SIZE, 
            0, 
            (struct sockaddr*)&client_addr, 
            &client_len
        );

        if (request_len < 0) {
            perror("Accept failed");
            continue;
        }

        // handle message
        try {
            char response_data[BUFFER_SIZE];
            int response_len = servicer.callback(
                request_data, 
                request_len, 
                response_data
            );

            // respond to client
            sendto(
                sockfd,
                response_data,
                response_len,
                0,
                (struct sockaddr*)&client_addr,
                sizeof(client_addr)
            );
        } catch (const std::exception& e) {
            break;
        }
    }
    if (sockfd >= 0)
        close(sockfd);
}


int RUDP::send(
    sockaddr_in server_addr, 
    char* request_data, 
    int request_len, 
    char* response_data,
    int response_len
) {
    // create socket
    int sockfd = socket(AF_INET, SOCK_DGRAM, 0);
    if (sockfd < 0 ) { 
        throw std::runtime_error("Socket creation failed");
    }

    sendto(
        sockfd, 
        request_data, 
        request_len, 
        0,
        (struct sockaddr*)&server_addr, 
        sizeof(server_addr)
    );

    // receive response
    struct sockaddr_in response_addr;
    socklen_t addr_len = sizeof(response_addr);

    int recv_response_len = recvfrom(
        sockfd, 
        response_data, 
        response_len,
        0,
        (struct sockaddr*)&response_addr, 
        &addr_len
    );

    if (recv_response_len < 0) {
        throw std::runtime_error("Failed to receive response");
    }
    return recv_response_len;
}


int RUDP::get_buffer_size() {
    return BUFFER_SIZE;
}