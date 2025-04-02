#include "proto_types.h"

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

