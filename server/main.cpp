#include <string>
#include <iostream>
#include <vector>
#include <unordered_map>
#include <algorithm>
#include "protos/proto_types.h"
#include "protos/stubs.h"

using namespace std;


int port;

int main(int argc, char** argv) {
    cout << "Usage: server <port>";
    if (argc < 2) port = 80;
    else port = stoi(argv[1]);


    // TODO start server listening on PORT

    cout << "Server listening on PORT " << port << '\n';


}

