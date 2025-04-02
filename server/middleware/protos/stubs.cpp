#include <iostream>
#include <stdio.h>
#include <sys/time.h>
#include <sys/types.h>
#include <unistd.h>
#include <netinet/in.h> 
#include "stubs.h"
#include "proto_types.h"
#include "unmarshalling.h"
#include "../network/servicer.h"
#include "../network/protocol.h"
#include "marshalling.h"


AvailabilityResponse FacilityBookingServiceStub::queryFacility(std::string facilityName, std::vector<Day> days) {
	int i = 0;
	int buffer_size = proto.get_buffer_size();
	char response_data[buffer_size];
	char request_data[buffer_size];
	marshall_int(request_data, i, 1);
	marshall_string(request_data, i, facilityName);
	marshall_int(request_data, i, days.size());
	for (Day days__item : days)
		marshall_Day(request_data, i, days__item);
	int response_len = proto.send(server_addr, request_data, i, response_data, buffer_size);
	i = 0;
	unmarshall_int(response_data, i);  // strip method_id
	return unmarshall_AvailabilityResponse(response_data, i);
}

BookResponse FacilityBookingServiceStub::bookFacility(std::string facility, std::string user, DayTime start, DayTime end) {
	int i = 0;
	int buffer_size = proto.get_buffer_size();
	char response_data[buffer_size];
	char request_data[buffer_size];
	marshall_int(request_data, i, 2);
	marshall_string(request_data, i, facility);
	marshall_string(request_data, i, user);
	marshall_DayTime(request_data, i, start);
	marshall_DayTime(request_data, i, end);
	int response_len = proto.send(server_addr, request_data, i, response_data, buffer_size);
	i = 0;
	unmarshall_int(response_data, i);  // strip method_id
	return unmarshall_BookResponse(response_data, i);
}

Response FacilityBookingServiceStub::changeBooking(int bookingId, int offset) {
	int i = 0;
	int buffer_size = proto.get_buffer_size();
	char response_data[buffer_size];
	char request_data[buffer_size];
	marshall_int(request_data, i, 3);
	marshall_int(request_data, i, bookingId);
	marshall_int(request_data, i, offset);
	int response_len = proto.send(server_addr, request_data, i, response_data, buffer_size);
	i = 0;
	unmarshall_int(response_data, i);  // strip method_id
	return unmarshall_Response(response_data, i);
}

Response FacilityBookingServiceStub::subscribe(std::string facilityName, int minutes) {
	int i = 0;
	int buffer_size = proto.get_buffer_size();
	char response_data[buffer_size];
	char request_data[buffer_size];
	marshall_int(request_data, i, 4);
	marshall_string(request_data, i, facilityName);
	marshall_int(request_data, i, minutes);
	int response_len = proto.send(server_addr, request_data, i, response_data, buffer_size);
	i = 0;
	unmarshall_int(response_data, i);  // strip method_id
	return unmarshall_Response(response_data, i);
}

Response FacilityBookingServiceStub::extendBooking(int bookingId, int minutes) {
	int i = 0;
	int buffer_size = proto.get_buffer_size();
	char response_data[buffer_size];
	char request_data[buffer_size];
	marshall_int(request_data, i, 5);
	marshall_int(request_data, i, bookingId);
	marshall_int(request_data, i, minutes);
	int response_len = proto.send(server_addr, request_data, i, response_data, buffer_size);
	i = 0;
	unmarshall_int(response_data, i);  // strip method_id
	return unmarshall_Response(response_data, i);
}

FacilitiesResponse FacilityBookingServiceStub::viewFacilities() {
	int i = 0;
	int buffer_size = proto.get_buffer_size();
	char response_data[buffer_size];
	char request_data[buffer_size];
	marshall_int(request_data, i, 6);
	int response_len = proto.send(server_addr, request_data, i, response_data, buffer_size);
	i = 0;
	unmarshall_int(response_data, i);  // strip method_id
	return unmarshall_FacilitiesResponse(response_data, i);
}

int FacilityBookingServiceServicer::callback(char* request_data, int request_len, char* response_data, sockaddr_in client_addr) {
    int i, j;
    i = j = 0;
    char arg_id;
    int method_id = unmarshall_int(request_data, i);
    
    switch (method_id) {
		case 1: {
			std::string facilityName__arg = unmarshall_string(request_data, i);
			std::vector<Day> days__arg = std::vector<Day>();
			int days__arg__len = unmarshall_int(request_data, i);
			for (int j=0; j<days__arg__len; j++)
				days__arg.push_back(unmarshall_Day(request_data, i));
			AvailabilityResponse queryFacility__result = service.queryFacility(facilityName__arg, days__arg, client_addr);
			marshall_int(response_data, j, 1);
			marshall_AvailabilityResponse(response_data, j, queryFacility__result);
			return j;
		}
		case 2: {
			std::string facility__arg = unmarshall_string(request_data, i);
			std::string user__arg = unmarshall_string(request_data, i);
			DayTime start__arg = unmarshall_DayTime(request_data, i);
			DayTime end__arg = unmarshall_DayTime(request_data, i);
			BookResponse bookFacility__result = service.bookFacility(facility__arg, user__arg, start__arg, end__arg, client_addr);
			marshall_int(response_data, j, 2);
			marshall_BookResponse(response_data, j, bookFacility__result);
			return j;
		}
		case 3: {
			int bookingId__arg = unmarshall_int(request_data, i);
			int offset__arg = unmarshall_int(request_data, i);
			Response changeBooking__result = service.changeBooking(bookingId__arg, offset__arg, client_addr);
			marshall_int(response_data, j, 3);
			marshall_Response(response_data, j, changeBooking__result);
			return j;
		}
		case 4: {
			std::string facilityName__arg = unmarshall_string(request_data, i);
			int minutes__arg = unmarshall_int(request_data, i);
			Response subscribe__result = service.subscribe(facilityName__arg, minutes__arg, client_addr);
			marshall_int(response_data, j, 4);
			marshall_Response(response_data, j, subscribe__result);
			return j;
		}
		case 5: {
			int bookingId__arg = unmarshall_int(request_data, i);
			int minutes__arg = unmarshall_int(request_data, i);
			Response extendBooking__result = service.extendBooking(bookingId__arg, minutes__arg, client_addr);
			marshall_int(response_data, j, 5);
			marshall_Response(response_data, j, extendBooking__result);
			return j;
		}
		case 6: {
			FacilitiesResponse viewFacilities__result = service.viewFacilities(client_addr);
			marshall_int(response_data, j, 6);
			marshall_FacilitiesResponse(response_data, j, viewFacilities__result);
			return j;
		}
		default:
			throw std::runtime_error("Invalid method id" + std::to_string(method_id));
    }
}
Response FacilityBookingClientStub::terminate() {
	int i = 0;
	int buffer_size = proto.get_buffer_size();
	char response_data[buffer_size];
	char request_data[buffer_size];
	marshall_int(request_data, i, 7);
	int response_len = proto.send(server_addr, request_data, i, response_data, buffer_size);
	i = 0;
	unmarshall_int(response_data, i);  // strip method_id
	return unmarshall_Response(response_data, i);
}

Response FacilityBookingClientStub::publish(std::vector<Interval> availability) {
	int i = 0;
	int buffer_size = proto.get_buffer_size();
	char response_data[buffer_size];
	char request_data[buffer_size];
	marshall_int(request_data, i, 8);
	marshall_int(request_data, i, availability.size());
	for (Interval availability__item : availability)
		marshall_Interval(request_data, i, availability__item);
	int response_len = proto.send(server_addr, request_data, i, response_data, buffer_size);
	i = 0;
	unmarshall_int(response_data, i);  // strip method_id
	return unmarshall_Response(response_data, i);
}

int FacilityBookingClientServicer::callback(char* request_data, int request_len, char* response_data, sockaddr_in client_addr) {
    int i, j;
    i = j = 0;
    char arg_id;
    int method_id = unmarshall_int(request_data, i);
    
    switch (method_id) {
		case 7: {
			Response terminate__result = service.terminate(client_addr);
			marshall_int(response_data, j, 7);
			marshall_Response(response_data, j, terminate__result);
			return j;
		}
		case 8: {
			std::vector<Interval> availability__arg = std::vector<Interval>();
			int availability__arg__len = unmarshall_int(request_data, i);
			for (int j=0; j<availability__arg__len; j++)
				availability__arg.push_back(unmarshall_Interval(request_data, i));
			Response publish__result = service.publish(availability__arg, client_addr);
			marshall_int(response_data, j, 8);
			marshall_Response(response_data, j, publish__result);
			return j;
		}
		default:
			throw std::runtime_error("Invalid method id" + std::to_string(method_id));
    }
}
