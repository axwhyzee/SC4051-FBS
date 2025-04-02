int {__SERVICE_NAME__}Servicer::callback(char* request_data, int request_len, char* response_data, sockaddr_in client_addr) {
    int i, j;
    i = j = 0;
    char arg_id;
    int method_id = unmarshall_int(request_data, i);
    
    switch (method_id) {{__DISPATCH_CODE__}
    }
}
