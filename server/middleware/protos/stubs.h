#include <netinet/in.h>
#include "proto_types.h"
#include "unmarshalling.h"
#include "../network/RUDP.h"

#pragma once

class FacilityBookingService {
public:
	virtual ~FacilityBookingService() {};
	virtual AvailabilityResponse queryFacility(std::string facilityName, std::vector<Day> days, sockaddr_in client_addr) = 0;
	virtual BookResponse bookFacility(std::string facilityName, std::string user, DayTime start, DayTime end, sockaddr_in client_addr) = 0;
	virtual Response changeBooking(int bookingId, int offset, sockaddr_in client_addr) = 0;
	virtual Response subscribe(std::string facilityName, int minutes, sockaddr_in client_addr) = 0;
	virtual Response extendBooking(int bookingId, int minutes, sockaddr_in client_addr) = 0;
	virtual FacilitiesResponse viewFacilities(sockaddr_in client_addr) = 0;
};

class FacilityBookingServiceStub {
public:
	FacilityBookingServiceStub(sockaddr_in server_addr, RUDP& proto) : server_addr(server_addr), proto(proto) {};
	~FacilityBookingServiceStub() {};
	AvailabilityResponse queryFacility(std::string facilityName, std::vector<Day> days);
	BookResponse bookFacility(std::string facilityName, std::string user, DayTime start, DayTime end);
	Response changeBooking(int bookingId, int offset);
	Response subscribe(std::string facilityName, int minutes);
	Response extendBooking(int bookingId, int minutes);
	FacilitiesResponse viewFacilities();
private:
	sockaddr_in server_addr;
	RUDP& proto;
};

class FacilityBookingServiceServicer : public Servicer {

/**
 * Servicer listens to a specified port for RPC requests.
 * Requests are unmarshalled and routed to the respective 
 * methods of the underlying service class.
 */

public:
    FacilityBookingServiceServicer(FacilityBookingService& service) : service(service) {};

    /**
     * Unmarshall raw RPC bytestream and call the corresponding 
     * method of the underlying service with the unmarshalled args.
     */
    int callback(char* request_data, int request_len, char* response_data, sockaddr_in client_addr);

private:
    int server_socket;
    FacilityBookingService& service;
};

class FacilityBookingClient {
public:
	virtual ~FacilityBookingClient() {};
	virtual Response terminate(sockaddr_in client_addr) = 0;
	virtual Response publish(std::vector<Interval> availability, sockaddr_in client_addr) = 0;
};

class FacilityBookingClientStub {
public:
	FacilityBookingClientStub(sockaddr_in server_addr, RUDP& proto) : server_addr(server_addr), proto(proto) {};
	~FacilityBookingClientStub() {};
	Response terminate();
	Response publish(std::vector<Interval> availability);
private:
	sockaddr_in server_addr;
	RUDP& proto;
};

class FacilityBookingClientServicer : public Servicer {

/**
 * Servicer listens to a specified port for RPC requests.
 * Requests are unmarshalled and routed to the respective 
 * methods of the underlying service class.
 */

public:
    FacilityBookingClientServicer(FacilityBookingClient& service) : service(service) {};

    /**
     * Unmarshall raw RPC bytestream and call the corresponding 
     * method of the underlying service with the unmarshalled args.
     */
    int callback(char* request_data, int request_len, char* response_data, sockaddr_in client_addr);

private:
    int server_socket;
    FacilityBookingClient& service;
};

