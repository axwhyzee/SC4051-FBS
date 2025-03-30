#include "proto_types.h"

#pragma once


/**
 * Marshall structs and primitives into byte stream.
 * 
 * The diagram below shows the structure of an RPC message. Numbers in
 * parenthesis represent length of field in bytes.
 * 
 * 
 * +------------------+---------------+------------------+-----------------+
 * |  MESSAGE_ID (1)  |  ARG1_ID (1)  |  ARG1_LEN (0/4)  |  ARG_1_VAL (N)  |
 * +------------------+---------------+------------------+-----------------+ 
 *                    |           ... repeat for other args ...            |
 *                    +---------------+------------------+-----------------+
 * 
 * - Structs are flattened according to the order by which the attributes are
 *   defined in the interface file.
 * - Only variable-length types like strings and sequences have arg_len 
 *   headers.
 */

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

