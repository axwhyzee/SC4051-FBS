#include "proto_types.h"

class FacilityBookingService {
public:
    virtual ~FacilityBookingService() {};
    virtual AvailabilityResponse queryFacility(std::string facilityName, std::vector<Day> days) = 0;
    virtual BookResponse bookFacility(std::string facilityName, std::string user, DayTime start, DayTime end) = 0;
    virtual Response changeBooking(int bookingId, int offset) = 0;
    virtual Response subscribe(std::string facilityName, int minutes) = 0;
    virtual Response extendBooking(int bookingId, int minutes) = 0;
    virtual FacilitiesResponse viewFacilities() = 0;
};

class FacilityBookingServiceStub {
public:
    FacilityBookingServiceStub();
    ~FacilityBookingServiceStub() {};
    AvailabilityResponse queryFacility(std::string facilityName, std::vector<Day> days) {/* TODO: marshall and send to server via UDP */};
    BookResponse bookFacility(std::string user, DayTime start, DayTime end) {/* TODO: marshall and send to server via UDP */};
    Response changeBooking(int bookingId, int offset) {/* TODO: marshall and send to server via UDP */};
    Response subscribe(std::string facilityName, int minutes) {/* TODO: marshall and send to server via UDP */};
    Response extendBooking(int bookingId, int minutes) {/* TODO: marshall and send to server via UDP */};
    FacilitiesResponse viewFacilities() {/* TODO: marshall and send to server via UDP */};
};

class FacilityBookingClient {
public:
    virtual ~FacilityBookingClient() {};
    virtual Response terminate() = 0;
    virtual Response publish(std::vector<Interval> availability) = 0;
};

class FacilityBookingClientStub {
public:
    FacilityBookingClientStub();
    ~FacilityBookingClientStub() {};
    Response terminate() {/* TODO: marshall and send to server via UDP */};
    Response publish(std::vector<Interval> availability) {/* TODO: marshall and send to server via UDP */};
};

