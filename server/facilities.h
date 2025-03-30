#include <string>
#include <iostream>
#include <vector>
#include <unordered_map>
#include <algorithm>
#include "protos/proto_types.h"
#include "protos/stubs.h"

using namespace std;
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
    FacilitiesResponse viewFacilities();
};