class {__SERVICE_NAME__}Servicer {

/**
 * Servicer listens to a specified port for RPC requests.
 * Requests are unmarshalled and routed to the respective 
 * methods of the underlying service class.
 */

public:
    {__SERVICE_NAME__}Servicer(int port, {__SERVICE_NAME__}& service) : service(service) {
        // create socket
        if ( (server_socket = socket(AF_INET, SOCK_DGRAM, 0)) < 0 ) { 
            perror("Socket creation failed"); 
            RAISE;
        }

        // bind socket to addr and port
        struct sockaddr_in server_addr; 
        memset(&server_addr, 0, sizeof(server_addr));
        server_addr.sin_family = AF_INET;
        server_addr.sin_addr.s_addr = INADDR_ANY; 
        server_addr.sin_port = htons(port);

        if (bind(
            server_socket, 
            (const struct sockaddr *)&server_addr,  
            sizeof(server_addr)) < 0 
        ) { 
            perror("bind failed"); 
            RAISE;
        }
        std::cout << "Server listening on port " << port << std::endl;
    };

    /**
     * Unmarshall raw RPC message bytes and call the corresponding 
     * method of the underlying service with the unmarshalled args.
     */
    int _dispatch(char* message, int n, char response[BUFFER_SIZE]) {
        int i, j = 0;
        char arg_id;
        int method_id = unmarshall_int(message, i);
        
        switch (method_id) {{__DISPATCH_CODE__}
        }
    }

    void _listen() {
        struct sockaddr_in client_addr; 
        int client_socket;
        socklen_t client_len = sizeof(client_addr);

        while (true) {
            // accept incoming connections by creating new client socket
            client_socket = accept(
                server_socket, 
                (struct sockaddr*)&client_addr, 
                &client_len
            );

            if (client_socket < 0) {
                perror("Accept failed");
                close(client_socket);
                continue;
            }

            // read message
            char buffer[BUFFER_SIZE];
            int bytes_received = recv(
                client_socket, buffer, sizeof(buffer), 0
            );

            if (bytes_received < 0) {
                perror("Receive failed");
                close(client_socket);
                continue;
            }

            char response[BUFFER_SIZE];
            int response_bytes = _dispatch(buffer, bytes_received, response);
            send(client_socket, response, sizeof(char) * response_bytes, 0);
            close(client_socket);
        }
    }

private:
    int server_socket;
    {__SERVICE_NAME__}& service;
};

