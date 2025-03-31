#include <string>
#include <iostream>
#include <vector>
#include <unordered_map>
#include <algorithm>
#include <ctime>
#include "protos/proto_types.h"
#include "protos/stubs.h"

using namespace std;

class Monitor {
    private:
        string client_ip;
        int client_port;
        time_t start_time;
        int minutes;
    public:
        Monitor(string client_ip, int client_port, time_t start_time, int minutes) {
            client_ip = client_ip;
            client_port = client_port;
            start_time = start_time;
            minutes = minutes;
        }
        bool expired() {
            time_t cur_time;
            time(&cur_time);
            
            return difftime(cur_time, start_time) >= minutes * 60;
        }
    };
    class Facility {
    public:
        Facility(string name, string type) {
            facilityName = name;
            type = type;
        }
        std::string facilityName;
        std::string type;
        std::vector<Booking> bookings;
        std::vector<Monitor*> monitors;
        void checkMonitors();
    };
class Facilities: public FacilityBookingService {
private:
    unordered_map<string, Facility*> facilities;
    int cur_booking_id = 1;
    unordered_map<int, Booking*> allBookings;

public:
    Facilities() {
        // init 3 facility
        facilities["classroom1"] = new Facility("classroom1", "clasroom");
        facilities["classroom2"] = new Facility("classroom2", "clasroom");
        facilities["classroom3"] = new Facility("classroom3", "clasroom");
    }

    AvailabilityResponse queryFacility(string facilityName, vector<Day> days );
    BookResponse bookFacility(string facilityName, string user, DayTime start, DayTime end);
    Response changeBooking(int bookingId, int offset);
    Response extendBooking(int bookingId, int minutes);
    Response subscribe(string facilityName, int minutes, string client_ip, int client_port);
    FacilitiesResponse viewFacilities();

};