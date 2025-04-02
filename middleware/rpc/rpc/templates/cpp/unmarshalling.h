#include "proto_types.h"

#define BYTE 8
#define LEN_SIZE 4
#define INT_SIZE 4

#pragma once

int unmarshall_int(char* message, int& i);

std::string unmarshall_string(char* message, int& i);

