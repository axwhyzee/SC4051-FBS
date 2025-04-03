#include "proto_types.h"

#define BYTE 8
#define LEN_SIZE 4
#define INT_SIZE 4

#pragma once

int unmarshall_int(char* message, int& i);

std::string unmarshall_string(char* message, int& i);

Day unmarshall_Day(char* message, int& i);

DayTime unmarshall_DayTime(char* message, int& i);

Interval unmarshall_Interval(char* message, int& i);

Booking unmarshall_Booking(char* message, int& i);

Facility unmarshall_Facility(char* message, int& i);

Response unmarshall_Response(char* message, int& i);

AvailabilityResponse unmarshall_AvailabilityResponse(char* message, int& i);

BookResponse unmarshall_BookResponse(char* message, int& i);

FacilitiesResponse unmarshall_FacilitiesResponse(char* message, int& i);

