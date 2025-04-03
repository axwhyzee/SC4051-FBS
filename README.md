# Distributed Facility Booking System
Distributed facility booking system composed of a Java client and C++ server communicating via a network middleware using custom serialization and RPC framework

## Experiments

### Experiment 1

#### Client
```
RUDP bound to random port 54056
Sending message with seq number 1  // send request with retries
Received message with seq number 2
Packet dropped on purpose
Backing off for 1500 ms ...

Sending message with seq number 1  // retry sending request
Received message with seq number 2
Sending message with seq number 0

// successfully received results
```

#### Server
```
RUDP bound to port 5432
Listening on port 5432 ...
Received message with seq number 1
Sending request with seq number 2  // send response with retries
Received message with seq number 1
Duplicate packet received. Prev seq: 1. Recv seq: 1
Backing off for 1500 ms ...

Sending request with seq number 2  // retry sending response
Received message with seq number 0
```
