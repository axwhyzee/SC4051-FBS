#include <netinet/in.h> 
#include "servicer.h"

#pragma once


/**
 * Protocol defines the interface for all network protocols.
 * All protocols must implement a listen and send method.
 */
class Protocol {
public:
    /**
     * Listen for incoming RPC requests, and handle using Servicer.
     * 
     * @param port Server's local port to listen to
     * @param servicer Servicer provides a callback to handle 
     *   received messages
     */
    virtual void listen(int port, Servicer& servicer) = 0;

    /**
     * @param server_addr Address of server to send to
     * @param request_data Byte array of request data
     * @param request_len Length of request data in bytes
     * @param response_data Byte array to write response into
     * @return Length of response data
     */
    virtual int send(
        sockaddr_in server_addr, 
        char* request_data, 
        int request_len, 
        char* response_data
    ) = 0;

    virtual int get_buffer_size() = 0;

    virtual ~Protocol() {}
};


class RUDP : public Protocol {
public:
    ~RUDP() {};
    void listen(int port, Servicer& servicer);
    int send(
        sockaddr_in server_addr, 
        char* request_data, 
        int request_len, 
        char* response_data
    );
    int get_buffer_size();

private:
    static constexpr int BUFFER_SIZE = 2048;
};
