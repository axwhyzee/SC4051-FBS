#include "proto_types.h"

#define BYTE 8
#define LEN_SIZE 4
#define INT_SIZE 4
#define RAISE exit(EXIT_FAILURE)
#pragma once

int unmarshall_int(char* message, int& i) {
    int res = 0;
    for (int j=0; j<INT_SIZE; j++) {
        res <<= BYTE;
        res |= message[i+j];
    }
    i += 4;
    return res;
}

std::string unmarshall_string(char* message, int& i) {
    std::string res;
    int len = unmarshall_int(message, i);
    res.assign(&message[i], len);
    i+=len;
    return res;
}

