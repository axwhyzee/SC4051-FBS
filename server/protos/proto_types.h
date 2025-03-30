#include <string>
#include <vector>

enum Day {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY
};

struct DayTime {
    Day day;
    int hour;
    int minute;
};

struct Interval {
    DayTime start;
    DayTime end;
};

struct Booking {
    int bookingId;
    std::string facilityName;
    std::string user;
    DayTime start;
    DayTime end;
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
};

struct Response {
    std::string error;
};

struct AvailabilityResponse {
    std::string error;
    std::vector<std::vector<Interval>> availability;
};

struct BookResponse {
    std::string error;
    int bookingId;
};

struct FacilitiesResponse {
    std::string error;
    std::vector<Facility> facilities;
};

