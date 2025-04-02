#pragma once


class Servicer {
public:
    /**
     * Servicer provides a callback method for Protocol object
     * to call each time a RPC message is received.
     * 
     * @param request_data RPC request data to act on
     * @param request_len Length of RPC request bytes
     * @param response_data Buffer to write response data into
     * @param client_addr Address of client making the request
     * @return Length of response data
     */
    virtual int callback(char* request_data, int request_len, char* response_data, sockaddr_in client_addr) = 0;
    virtual ~Servicer() {}
};
