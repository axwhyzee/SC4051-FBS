#include <string>
#include <vector>
#include <ctime>
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


struct Response {
    std::string error;
};

struct AvailabilityResponse {
    std::string error;
    std::vector<Interval> availability;
};

struct BookResponse {
    std::string error;
    int bookingId;
};

struct FacilitiesResponse {
    std::string error;
    std::vector<Facility> facilities;
};

