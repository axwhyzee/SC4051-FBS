#include <iostream>
#include <sys/socket.h>
#include <cstring>
#include <iostream>
#include <netinet/in.h>
#include <unistd.h>


using namespace std;
class Server{
    private:
        int sockfd;
        int port;

        struct sockaddr_in serverAddress;
        
    public:
        Server(int port);
};