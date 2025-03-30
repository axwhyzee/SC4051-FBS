#include "server.h"


Server::Server(int port) {
    port = port;
    sockfd = socket(AF_INET, SOCK_DGRAM, 0);

    if (sockfd == -1) {
        cout << "ERROR: Could not open socket.\n";
        return;
    }

    serverAddress.sin_family = AF_INET;
    serverAddress.sin_port = htons(port);
    serverAddress.sin_addr.s_addr = INADDR_ANY;

    if (bind(sockfd, (struct sockaddr*)&serverAddress, sizeof(serverAddress)) < 0) {
        cout << "ERROR: Could not bind to socket.\n";
    }

    
}