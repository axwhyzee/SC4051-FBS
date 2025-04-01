# RPC 

Language-agnostic command line tool to compile interface file into language specific header files to faciliate socket programm via generated stubs. Stubs also ensure reliable network protocol built on top of UDP.

## How It Works

A parser generates logical models of interface components from the interface file. Next, language-specific compilers, build these components part by part to generate RPC stubs.


# C++ Demo

_Note: Run all commands from the `SC4051-FBS/middleware/rpc` directory_

## Install RPC tools
```
# setup python env
python -m venv env
source env/bin/activate
pip install .  # installs rpc package
```

## Compile Interface File into Stubs

Paste the following into `proto.idl`
```
enum Day {
    MONDAY;
    TUESDAY;
    WEDNESDAY;
    THURSDAY;
    FRIDAY;
    SATURDAY;
    SUNDAY;
};

struct DayTime {
    Day day;
    int hour;
    int minute;
};

interface TestService {
    sequence<DayTime> generate_noon_daytimes(sequence<Day> days);
};
```

Compile interface file
```
mkdir ../../server
source env/bin/activate
rpc_tools --infile=proto.idl --outdir=../../server --lang=cpp
```

After compilation, the C++ project structure will look like this:
```
|
|-- server/
|   |
|   |-- middleware
|   |   |-- protos
|   |   |   |-- marshalling.cpp
|   |   |   |-- marshalling.h
|   |   |   |-- ...
|   |   |
|   |   |-- network
|   |       |-- protocol.cpp
|   |       |-- protocol.h
|   |       |-- ...
|   |   
|   |-- server.cpp // create yourself
|   |-- ...
|
|-- middleware/
|-- client/
```

## Implement Server

Copy the demo server code below into `server/server.cpp`
```
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
```

Start server
```
// from the server/ directory, run ...
g++ -std=c++11 server.cpp middleware/network/*.cpp middleware/protos/*.cpp -o server
```

## Implement Client
Copy the demo client code below into `server/client.cpp`

##### `client.cpp` (demo client)
```
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
```

Start client
```
// from the server/ directory, run ...
g++ -std=c++11 client.cpp middleware/network/*.cpp middleware/protos/*.cpp -o client
```

# Java Demo

## Compile Interface File into Stubs

Paste the following into `proto.idl`
```
enum Day {
    MONDAY;
    TUESDAY;
    WEDNESDAY;
    THURSDAY;
    FRIDAY;
    SATURDAY;
    SUNDAY;
};

struct DayTime {
    Day day;
    int hour;
    int minute;
};

interface TestService {
    sequence<DayTime> generate_noon_daytimes(sequence<Day> days);
};
```

Compile interface file
```
mkdir ../../client
source env/bin/activate
rpc_tools --infile=proto.idl --outdir=../../client --lang=java
```

After compilation, the Java project structure will look like this:
```
|
|-- client/
|   |
|   |-- middleware
|   |   |-- protos
|   |   |   |-- Day.java
|   |   |   |-- DayTime.java
|   |   |   |-- ...
|   |   |
|   |   |-- network
|   |       |-- Protocol.java
|   |       |-- RUDP.java
|   |       |-- ...
|   |   
|   |-- TestClient.java // create yourself
|   |-- ...
|
|-- middleware/
|-- server/
```

## Implement Client

Paste the client demo code below into `client/TestClient.java`

```
// Example Java client (TestClient.java)
import java.net.InetAddress;
import java.net.UnknownHostException;
import middleware.network.RUDP;
import middleware.protos.AvailabilityResponse;
import middleware.protos.FacilityBookingServiceStub;
import middleware.protos.Day;
import middleware.protos.Interval;


public class TestServer {
    public static void main(String args[]) {
        try {
            // configure client
            InetAddress localhost = InetAddress.getLocalHost();
            RUDP rudp = new RUDP();
            int port = 5432;
            FacilityBookingServiceStub stub = new FacilityBookingServiceStub(localhost, port, rudp);   

            // make RPC request
            Day[] days = {Day.FRIDAY, Day.MONDAY};     
            AvailabilityResponse resp = stub.queryFacility("Facility A", days);
            System.out.println(resp.error());
            for (Interval itv : resp.availability()) {
                System.out.println("Start: " + itv.start());
                System.out.println("End: " + itv.end());
            }
        } catch (UnknownHostException e) {
            System.out.println("Localhost could not be resolved");
        }
    }
}
```
