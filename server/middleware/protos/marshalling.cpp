#include "marshalling.h"

#define INT_SIZE 4
#define ONES 255  // 1111 1111 = 255


void marshall_len_header(char* message, int& i, int len) {
    marshall_int(message, i, len);
}

void marshall_int(char* message, int& i, int val) {
    for (int j=0; j<INT_SIZE; j++)
        message[i++] = ONES & (val >> (8 * (INT_SIZE - j - 1)));
}

void marshall_string(char* message, int& i, std::string val) {
    marshall_len_header(message, i, val.length());
    std::memcpy(&message[i], val.c_str(), val.length());
    i += val.length();
}

void marshall_Day(char* message, int& i, Day val) {
	marshall_int(message, i, (int)val+1);
}

void marshall_DayTime(char* message, int& i, DayTime val) {
	marshall_Day(message, i, val.day);
	marshall_int(message, i, val.hour);
	marshall_int(message, i, val.minute);
}

void marshall_Interval(char* message, int& i, Interval val) {
	marshall_DayTime(message, i, val.start);
	marshall_DayTime(message, i, val.end);
}

void marshall_Booking(char* message, int& i, Booking val) {
	marshall_int(message, i, val.bookingId);
	marshall_string(message, i, val.user);
	marshall_string(message, i, val.facilityName);
	marshall_DayTime(message, i, val.start);
	marshall_DayTime(message, i, val.end);
}

void marshall_Facility(char* message, int& i, Facility val) {
	marshall_string(message, i, val.name);
	marshall_string(message, i, val.type);
	marshall_len_header(message, i, val.bookings.size());
	for (int j=0; j<val.bookings.size(); j++)
		marshall_Booking(message, i, val.bookings[j]);
}

void marshall_Response(char* message, int& i, Response val) {
	marshall_string(message, i, val.error);
}

void marshall_AvailabilityResponse(char* message, int& i, AvailabilityResponse val) {
	marshall_string(message, i, val.error);
	marshall_len_header(message, i, val.availability.size());
	for (int j=0; j<val.availability.size(); j++)
		marshall_Interval(message, i, val.availability[j]);
}

void marshall_BookResponse(char* message, int& i, BookResponse val) {
	marshall_string(message, i, val.error);
	marshall_int(message, i, val.bookingId);
}

void marshall_FacilitiesResponse(char* message, int& i, FacilitiesResponse val) {
	marshall_string(message, i, val.error);
	marshall_len_header(message, i, val.facilities.size());
	for (int j=0; j<val.facilities.size(); j++)
		marshall_Facility(message, i, val.facilities[j]);
}

