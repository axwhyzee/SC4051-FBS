class {__SERVICE_NAME__}Servicer : public Servicer {

/**
 * Servicer listens to a specified port for RPC requests.
 * Requests are unmarshalled and routed to the respective 
 * methods of the underlying service class.
 */

public:
    {__SERVICE_NAME__}Servicer({__SERVICE_NAME__}& service) : service(service) {};

    /**
     * Unmarshall raw RPC bytestream and call the corresponding 
     * method of the underlying service with the unmarshalled args.
     */
    int callback(char* request_data, int request_len, char* response_data);

private:
    int server_socket;
    {__SERVICE_NAME__}& service;
};

