#include <iostream>
#include <cstring>      
#include <netinet/in.h> 
#include <arpa/inet.h>
#include "middleware/network/protocol.h"
#include "middleware/protos/stubs.h"


# define LOG(msg) std::cout << msg << ' ';
# define LOG_LINE(msg) std::cout << msg << std::endl;


int main() {
    // configure server addr
    int server_port = 5432;
    const char* server_ip = "127.0.0.1";
    sockaddr_in server_addr;

    memset(&server_addr, 0, sizeof(server_addr));
    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(server_port);
    if (inet_pton(AF_INET, server_ip, &server_addr.sin_addr) <= 0) {
        std::cerr << "Invalid IP address" << std::endl;
        return 1;
    }

    // construct and send RPC request
    RUDP rudp = RUDP();
    TestServiceStub stub = TestServiceStub(server_addr, rudp);
    std::vector<Day> days = {MONDAY, FRIDAY, SUNDAY};
    std::vector<DayTime> result = stub.generate_noon_daytimes(days);

    LOG("Result length: ");
    LOG_LINE(result.size());

    // print response
    for (DayTime item : result) {
        LOG_LINE("-----------");
        LOG("Day:");
        LOG_LINE(item.day);
        LOG("HOUR:");
        LOG_LINE(item.hour);
        LOG("MINUTE:");
        LOG_LINE(item.minute);
    }
}