class Servicer {
public:
    /**
     * Servicer provides a callback method for Protocol object
     * to call each time a RPC message is received.
     * 
     * @param request_data RPC request data to act on
     * @param request_len Length of RPC request bytes
     * @param response_data Buffer to write response data into
     * @return Length of response data
     */
    virtual int callback(char* request_data, int request_len, char* response_data) = 0;
    virtual ~Servicer() {}
};
