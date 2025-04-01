#include "marshalling.h"

#define INT_SIZE 4
#define ZEROES 255  // 1111 1111 = 255


void marshall_len_header(char* message, int& i, int len) {
    marshall_int(message, i, len);
}

void marshall_int(char* message, int& i, int val) {
    for (int j=0; j<INT_SIZE; j++)
        message[i++] = val & (ZEROES << (8 * (INT_SIZE - j - 1)));
}

void marshall_string(char* message, int& i, std::string val) {
    marshall_len_header(message, i, val.length());
    std::memcpy(&message[i], val.c_str(), val.length());
    i += val.length();
}

