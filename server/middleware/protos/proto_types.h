#include <string>
#include <vector>

#pragma once

enum Day {
	MONDAY=0,
	TUESDAY=1,
	WEDNESDAY=2,
	THURSDAY=3,
	FRIDAY=4,
	SATURDAY=5,
	SUNDAY=6,
};

struct DayTime {
	Day day;
	int hour;
	int minute;
};

struct Interval {
	DayTime start;
	DayTime end;
};

struct Booking {
	int bookingId;
	std::string facilityName;
	std::string user;
	DayTime start;
	DayTime end;
};

struct Facility {
	std::string name;
	std::string type;
	std::vector<Booking> bookings;
};

struct Response {
	std::string error;
};

struct AvailabilityResponse {
	std::string error;
	std::vector<Interval> availability;
};

struct BookResponse {
	std::string error;
	int bookingId;
};

struct FacilitiesResponse {
	std::string error;
	std::vector<Facility> facilities;
};

