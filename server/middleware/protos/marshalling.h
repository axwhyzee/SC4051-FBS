#include "proto_types.h"
#include <cstring>
#include <stdexcept>

#pragma once


/**
 * Marshall messages, structs and primitives into byte stream.
 * 
 * The diagram below shows the structure of an RPC message. Numbers in
 * parenthesis represent length of field in bytes.
 * 
 * +-----------------+------------------+----------------+
 * |  METHOD_ID (4)  |  ARG1_LEN (0|4)  |  ARG1_VAL (N)  |
 * +-----------------+------------------+----------------+
 *                   |    ... repeat for other args ...  |
 *                   +------------------+----------------+
 * 
 * - Structs are flattened according to the order by which the attributes are
 *   defined in the interface file.
 * - Only variable-length types like strings and sequences have ARG_LEN 
 *   headers.
 * - Responses use the same METHOD_ID as that in the request.
 */

void marshall_len_header(char* message, int& i, int len);

void marshall_int(char* message, int& i, int val);

void marshall_string(char* message, int& i, std::string val);

void marshall_Day(char* message, int& i, Day val);

void marshall_DayTime(char* message, int& i, DayTime val);

void marshall_Interval(char* message, int& i, Interval val);

void marshall_Booking(char* message, int& i, Booking val);

void marshall_Facility(char* message, int& i, Facility val);

void marshall_Response(char* message, int& i, Response val);

void marshall_AvailabilityResponse(char* message, int& i, AvailabilityResponse val);

void marshall_BookResponse(char* message, int& i, BookResponse val);

void marshall_FacilitiesResponse(char* message, int& i, FacilitiesResponse val);

