#include <string>
#include <vector>

#pragma once

enum Day {
	MONDAY=1,
	TUESDAY=2,
	WEDNESDAY=3,
	THURSDAY=4,
	FRIDAY=5,
	SATURDAY=6,
	SUNDAY=7,
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
	std::string user;
	std::string facilityName;
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

