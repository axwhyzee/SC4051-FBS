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

