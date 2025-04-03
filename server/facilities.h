#include <string>
#include <iostream>
#include <vector>
#include <unordered_map>
#include <algorithm>
#include <ctime>
#include "middleware/protos/proto_types.h"
#include "middleware/protos/stubs.h"
#include "middleware/network/RUDP.h"
#include "middleware/protos/marshalling.h"
#include "middleware/protos/unmarshalling.h"
#include "middleware/network/servicer.h"

using namespace std;

class Monitor {
private:
    time_t start_time;
    int minutes;
public:
    sockaddr_in client_addr;
    Monitor(sockaddr_in client_addr, time_t start_time, int minutes) {
        client_addr = client_addr;
        start_time = start_time;
        minutes = minutes;
    }
    bool expired() {
        time_t cur_time;
        time(&cur_time);
        
        return difftime(cur_time, start_time) >= minutes * 60;
    }
};
class Facility_class {
public:
    Facility_class(string name, string type) : facilityName(name), type(type) {}
    std::string facilityName;
    std::string type;
    std::vector<Booking> bookings;
    std::vector<Monitor*> monitors;
    void checkMonitors();
    Facility get_facility_struct() {
        Facility res;
        res.bookings = bookings;
        res.name = facilityName;
        res.type = type;
        return res;
    }
};
class Facilities: public FacilityBookingService {
private:
    unordered_map<string, Facility_class*> facilities;
    int cur_booking_id = 1;
    unordered_map<int, Booking*> allBookings;

public:
    Facilities() {
        // init 3 facility
        facilities["classroom1"] = new Facility_class("classroom1", "clasroom");
        facilities["classroom2"] = new Facility_class("classroom2", "clasroom");
        facilities["classroom3"] = new Facility_class("classroom3", "clasroom");
    }

    AvailabilityResponse queryFacility(string facilityName, vector<Day> days, sockaddr_in client_addr);
    BookResponse bookFacility(string facilityName, string user, DayTime start, DayTime end, sockaddr_in client_addr);
    Response changeBooking(int bookingId, int offset, sockaddr_in client_addr);
    Response extendBooking(int bookingId, int minutes, sockaddr_in client_addr);
    Response subscribe(string facilityName, int minutes, sockaddr_in client_addr);
    FacilitiesResponse viewFacilities(sockaddr_in client_addr);
};