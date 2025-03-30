#include "proto_types.h"

#pragma once

void _marshall_len_header(char* message, int& i, int len) {
    int32_t network_val = htonl(len);
    std::memcpy(&message[i], &network_val, sizeof(int32_t));
    i += sizeof(int32_t);
}

void marshall_int(char* message, int& i, int val) {
    int32_t network_val = htonl(val);
    std::memcpy(&message[i], &network_val, sizeof(int32_t));
    i += sizeof(int32_t);
}

void marshall_string(char* message, int i, std::string val) {
    _marshall_len_header(message, i, sizeof(char) * val.length());
    std::memcpy(&message[i], val.c_str(), val.length());
    i += val.length();
}

