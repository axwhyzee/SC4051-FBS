#include <vector>
#include <iostream>
#include "middleware/network/protocol.h"
#include "middleware/protos/stubs.h"
#include "middleware/protos/proto_types.h"


/**
 * Concrete TestService implementation.
 *
 * Whenever a UDP packet is received by RUDP, it calls the Servicer 
 * via callback to first unmarshall the RPC request, then dispatch the
 * unmarshalled arguments to the corresponding method implemented by
 * this ConcreteTestService class.
 *
 * Return values of this ConcreteTestService are mashalled by the
 * Servicer, and sent as response back to the client via RUDP.
 */
class ConcreteTestService : public TestService {
public:
    std::vector<DayTime> generate_noon_daytimes(std::vector<Day> days) {
        std::vector<DayTime> result;
        for (Day day : days) {
            DayTime daytime;
            daytime.day = day;
            daytime.hour = 12;
            daytime.minute = 0;
            result.push_back(daytime);
        }
        return result;
    }
};


int main() {
    int server_port = 5432;
    ConcreteTestService service;
    TestServiceServicer servicer = TestServiceServicer(service);
    RUDP rudp = RUDP();

    rudp.listen(server_port, servicer);
}