# Distributed Facility Booking System
Distributed facility booking system composed of a Java client and C++ server communicating via a network middleware using custom serialization and RPC framework

## Experiments

### Experiment 1
Server response fails to reach client

#### Client
```
RUDP bound to random port 54056
Sending message with seq number 1  // send request with retries
Received message with seq number 2
Packet dropped by receiver
Backing off for 1500 ms ...

Sending message with seq number 1  // retry sending request
Received message with seq number 2
Sending message with seq number 0
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

### Experiemnt 2

Request packet fails to reach server

#### Client
```
Sending message with seq num 1
Socket recv timed out
Backing off for 1.5 seconds ...
Sending message with seq num 1
Packet dropped by receiver
Backing off for 3 seconds ...
Sending message with seq num 1
Received message with seq num 2
Sending message with seq num 0
```

#### Server
```
Packet dropped by receiver
Socket recv timed out
Received message with seq num 1
Sending message with seq num 2
Socket recv timed out
Backing off for 1.5 seconds ...
Sending message with seq num 2
Received message with seq num 1
Backing off for 3 seconds ...
Sending message with seq num 2
Packet dropped by receiver
Backing off for 4.5 seconds ...
Sending message with seq num 2
Socket recv timed out
Backing off for 6 seconds ...
Sending message with seq num 2
```

### Experiment 3

Response fails to reach client. Client retries but the retried request fails to reach server.

#### Client
```
Sending message with seq num 1
Packet dropped by receiver
Backing off for 1.5 seconds ...
Sending message with seq num 1
Packet dropped by receiver
Backing off for 3 seconds ...
Sending message with seq num 1
Received message with seq num 2
Sending message with seq num 0
```

#### Server
```
Received message with seq num 1
Sending message with seq num 2
Packet dropped by receiver
Backing off for 1.5 seconds ...
Sending message with seq num 2
Socket recv timed out
Backing off for 3 seconds ...
Sending message with seq num 2
Received message with seq num 1
Backing off for 4.5 seconds ...
Sending message with seq num 2
Received message with seq num 0
```