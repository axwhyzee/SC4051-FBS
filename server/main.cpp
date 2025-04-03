#include <string>
#include <iostream>
#include <vector>
#include <unordered_map>
#include <algorithm>
#include "middleware/protos/proto_types.h"
#include "middleware/protos/stubs.h"
#include "middleware/network/RUDP.h"
#include "facilities.h"

using namespace std;


int port;

int main(int argc, char** argv) {
    cout << "Usage: server <port>\n";
    if (argc < 2) port = 8888;
    else port = stoi(argv[1]);

    RUDP rudp = RUDP(port);
    Facilities facilitiesBookingService = Facilities();
    FacilityBookingServiceServicer servicer = FacilityBookingServiceServicer(facilitiesBookingService);
    rudp.listen(servicer);
}

