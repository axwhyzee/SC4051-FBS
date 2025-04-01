#include "unmarshalling.h"


int unmarshall_int(char* message, int& i) {
    int res = 0;
    for (int j=0; j<INT_SIZE; j++) {
        res <<= BYTE;
        res |= message[i++];
    }
    return res;
}

std::string unmarshall_string(char* message, int& i) {
    std::string res;
    int len = unmarshall_int(message, i);
    res.assign(&message[i], len);
    i+=len;
    return res;
}

