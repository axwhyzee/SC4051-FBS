# RPC 

Language-agnostic command line tool to compile interface file into language specific header files to faciliate socket programm via generated stubs. Stubs also ensure reliable network protocol built on top of UDP.

## How It Works

A parser generates logical models of interface components from the interface file. Next, language-specific compilers, build these components part by part to generate RPC stubs.

## Quickstart

_Note: Run all commands from the `SC4051-FBS/middleware/rpc` directory_

### Install RPC tools
```
# setup python env
python -m venv env
source env/bin/activate
pip install .  # installs rpc package
```

### Compile Interface File into Stubs

#### C++

```
mkdir ../../client
mkdir ../../client/protos
source env/bin/activate
rpc_tools --infile=proto.idl --outdir=../../server --lang=cpp
```

#### Java

```
mkdir ../../client
source env/bin/activate
rpc_tools --infile=proto.idl --outdir=../../client --lang=java
```

After compilation, the project structure will look like this:
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

rpc_tools --infile=proto.idl --outdir=./client --lang=cpp
