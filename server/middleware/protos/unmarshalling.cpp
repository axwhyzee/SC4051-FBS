#include "unmarshalling.h"


int unmarshall_int(char* message, int& i) {
    int res = 0;
    for (int j=0; j<INT_SIZE; j++) {
        res <<= BYTE;
        res |= message[i++];
    }
    return res;
}

std::string unmarshall_string(char* message, int& i) {
    std::string res;
    int len = unmarshall_int(message, i);
    res.assign(&message[i], len);
    i+=len;
    return res;
}

Day unmarshall_Day(char* message, int& i) {
	char enum_id = unmarshall_int(message, i);
	switch (enum_id) {
		case 1:
			return (Day) MONDAY;
		case 2:
			return (Day) TUESDAY;
		case 3:
			return (Day) WEDNESDAY;
		case 4:
			return (Day) THURSDAY;
		case 5:
			return (Day) FRIDAY;
		case 6:
			return (Day) SATURDAY;
		case 7:
			return (Day) SUNDAY;
		default:
			throw std::runtime_error("Unrecognized enum_id" + std::to_string(enum_id));
	}
}

DayTime unmarshall_DayTime(char* message, int& i) {
	DayTime DayTime__struct;
	DayTime__struct.day = unmarshall_Day(message, i);
	DayTime__struct.hour = unmarshall_int(message, i);
	DayTime__struct.minute = unmarshall_int(message, i);
	return DayTime__struct;
}

Interval unmarshall_Interval(char* message, int& i) {
	Interval Interval__struct;
	Interval__struct.start = unmarshall_DayTime(message, i);
	Interval__struct.end = unmarshall_DayTime(message, i);
	return Interval__struct;
}

Booking unmarshall_Booking(char* message, int& i) {
	Booking Booking__struct;
	Booking__struct.bookingId = unmarshall_int(message, i);
	Booking__struct.facilityName = unmarshall_string(message, i);
	Booking__struct.user = unmarshall_string(message, i);
	Booking__struct.start = unmarshall_DayTime(message, i);
	Booking__struct.end = unmarshall_DayTime(message, i);
	return Booking__struct;
}

Facility unmarshall_Facility(char* message, int& i) {
	Facility Facility__struct;
	Facility__struct.name = unmarshall_string(message, i);
	Facility__struct.type = unmarshall_string(message, i);
	int bookings__len = unmarshall_int(message, i);
	std::vector<Booking> temp__bookings = std::vector<Booking>();
	for (int j=0; j<bookings__len; j++)
		temp__bookings.push_back(unmarshall_Booking(message, i));
	Facility__struct.bookings = temp__bookings;
	return Facility__struct;
}

Response unmarshall_Response(char* message, int& i) {
	Response Response__struct;
	Response__struct.error = unmarshall_string(message, i);
	return Response__struct;
}

AvailabilityResponse unmarshall_AvailabilityResponse(char* message, int& i) {
	AvailabilityResponse AvailabilityResponse__struct;
	AvailabilityResponse__struct.error = unmarshall_string(message, i);
	int availability__len = unmarshall_int(message, i);
	std::vector<Interval> temp__availability = std::vector<Interval>();
	for (int j=0; j<availability__len; j++)
		temp__availability.push_back(unmarshall_Interval(message, i));
	AvailabilityResponse__struct.availability = temp__availability;
	return AvailabilityResponse__struct;
}

BookResponse unmarshall_BookResponse(char* message, int& i) {
	BookResponse BookResponse__struct;
	BookResponse__struct.error = unmarshall_string(message, i);
	BookResponse__struct.bookingId = unmarshall_int(message, i);
	return BookResponse__struct;
}

FacilitiesResponse unmarshall_FacilitiesResponse(char* message, int& i) {
	FacilitiesResponse FacilitiesResponse__struct;
	FacilitiesResponse__struct.error = unmarshall_string(message, i);
	int facilities__len = unmarshall_int(message, i);
	std::vector<Facility> temp__facilities = std::vector<Facility>();
	for (int j=0; j<facilities__len; j++)
		temp__facilities.push_back(unmarshall_Facility(message, i));
	FacilitiesResponse__struct.facilities = temp__facilities;
	return FacilitiesResponse__struct;
}

