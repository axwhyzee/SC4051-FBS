#include "facilities.h"
#include "helper.h"

int MN_TIME = convertDayTimeToInt({(Day)0, 0, 0});
int MX_TIME = convertDayTimeToInt({(Day)6, 23, 59});


vector<vector<Interval>> get_availability(Facility* facility) {
    vector<pair<int, int>> bookings;
    for (auto &booking : facility->bookings) {
        int start = convertDayTimeToInt(booking.start);
        int end  = convertDayTimeToInt(booking.end);
        bookings.push_back({start, end});
    }
    sort(bookings.begin(), bookings.end());

    
    vector<vector<Interval>> res(7, vector<Interval>());
    int booking_len = bookings.size();
    int cur = 0;
    for (int i = 0; i < 7; i++) {
        DayTime start_time = {(Day)i, 0, 0};
        int start = convertDayTimeToInt(start_time);
        DayTime end_time = {(Day)i, 23, 59};
        int end = convertDayTimeToInt(end_time);

        while(cur < booking_len && bookings[cur].first > start) {
            res[i].push_back({convertIntToDayTime(start), convertIntToDayTime(bookings[cur].first)});
            start = min(end, bookings[cur].second + 1);
            cur ++;
        }
        if (start < end) {
            res[i].push_back({convertIntToDayTime(start), convertIntToDayTime(end)});
        }
    }

    return res;
}

bool check_availability(Facility* facility, int start_time, int end_time) {
    vector<pair<int, int>> bookings;
    for (auto &booking : facility->bookings) {
        int start = convertDayTimeToInt(booking.start);
        int end  = convertDayTimeToInt(booking.end);
        bookings.push_back({start, end});
    }
    bookings.push_back({start_time, end_time});
    sort(bookings.begin(), bookings.end());

    int n = bookings.size();

    for (int i = 0; i < n-1; i++) {
        // check overlap
        if (bookings[i].second >= bookings[i+1].first) return false;
    }
    return true;
}

bool check_change_booking_availability(Facility* facility, int target_bookingId, int offset) {
    vector<pair<int, int>> bookings;
    for (auto &booking : facility->bookings) {

        int start = convertDayTimeToInt(booking.start);
        int end  = convertDayTimeToInt(booking.end);
        if (booking.bookingId == target_bookingId) {
            start += offset;
            end += offset;
            if (start < MN_TIME || end >= MX_TIME) return false;
        }
        bookings.push_back({start, end});
    }

    int n = bookings.size();

    for (int i = 0; i < n-1; i++) {
        // check overlap
        if (bookings[i].second >= bookings[i+1].first) return false;
    }
    return true;
}

bool check_extend_booking_availability(Facility* facility, int target_bookingId, int minutes) {
    vector<pair<int, int>> bookings;
    for (auto &booking : facility->bookings) {

        int start = convertDayTimeToInt(booking.start);
        int end  = convertDayTimeToInt(booking.end);
        if (booking.bookingId == target_bookingId) {
            end += minutes;
            if (end >= MX_TIME) return false;
        }
        bookings.push_back({start, end});
    }

    int n = bookings.size();

    for (int i = 0; i < n-1; i++) {
        // check overlap
        if (bookings[i].second >= bookings[i+1].first) return false;
    }
    return true;
}

AvailabilityResponse Facilities::queryFacility(string facilityName, vector<Day> days ) {
    AvailabilityResponse res = {};
    if (facilities.find(facilityName) == facilities.end()) {
        res.error = "No such facility name.";
        return res;
    } else {
        Facility* facility = facilities[facilityName];
        res.availability = get_availability(facility);
        res.error = "success";
        return res;
    }
}
BookResponse Facilities::bookFacility(string facilityName, string user, DayTime start, DayTime end) {
    int start_time = convertDayTimeToInt(start);
    int end_time = convertDayTimeToInt(end);
    BookResponse res;
    if (facilities.find(facilityName) == facilities.end()) {
        res.error = "No such facility name.";
        res.bookingId = -1;
        return res;
    }
    if (end_time <= start_time) {
        res.error = "End time must be later than start time";
        res.bookingId = -1;
        return res;
    }
    Facility* facility = facilities[facilityName];
    if (!check_availability(facility, start_time, end_time)) {
        res.error = facilityName + " not available at time";
        res.bookingId = -1;
        return res;
    } else {
        Booking newBooking = {cur_booking_id, facilityName, user, start, end};
        facility->bookings.push_back(newBooking);
        allBookings[cur_booking_id] = &newBooking;
        res.error = "Success";
        res.bookingId = cur_booking_id++;
        return res;
    }
}
Response Facilities::changeBooking(int bookingId, int offset) {
    Response res;
    if (allBookings.find(bookingId) == allBookings.end()) {
        res.error = "No such booking ID";
        return res;
    }
    Booking* booking = allBookings[bookingId];
    
    Facility* facility = facilities[booking->facilityName];
    
    if (!check_change_booking_availability(facility, bookingId, offset)) {
        res.error = "Timing not available";
    } else {
        for (auto &booking : facility->bookings) {
            if (booking.bookingId == bookingId) {
                int newStart = convertDayTimeToInt(booking.start) + offset; 
                int newEnd = convertDayTimeToInt(booking.end) + offset;
                booking.start = convertIntToDayTime(newStart);
                booking.end = convertIntToDayTime(newEnd);
            }
        }

        res.error = "success";
        return res;
    }
}
Response Facilities::subscribe(std::string facilityName, int minutes, string client_ip, int client_port) {
    Response res;
    if (facilities.find(facilityName) == facilities.end()) {
        res.error = "No such facility name.";
        return res;
    }

    time_t start_time;
    time(&start_time);

    Monitor* newMonitor = new Monitor(client_ip, client_port, start_time, minutes);

    Facility* facility = facilities[facilityName];

    facility->monitors.push_back(newMonitor);

    res.error = "success";
    return res;
}
Response Facilities::extendBooking(int bookingId, int minutes) {
    Response res;
    if (allBookings.find(bookingId) == allBookings.end()) {
        res.error = "No such booking ID";
        return res;
    }
    Booking* booking = allBookings[bookingId];
    
    Facility* facility = facilities[booking->facilityName];
    
    if (!check_extend_booking_availability(facility, bookingId, minutes)) {
        res.error = "Timing not available";
    } else {
        for (auto &booking : facility->bookings) {
            if (booking.bookingId == bookingId) { 
                int newEnd = convertDayTimeToInt(booking.end) + minutes;
                booking.end = convertIntToDayTime(newEnd);
            }
        }

        res.error = "success";
        return res;
    }
}
FacilitiesResponse Facilities::viewFacilities() {
    FacilitiesResponse res;

    res.error = "success";

    vector<Facility> facility_arr;

    for (auto &p : facilities) {
        facility_arr.push_back(*p.second);
    }
    res.facilities = facility_arr;

    return res;
}

void Facility::checkMonitors() {
    vector<int> to_remove;

    int monitors_len = monitors.size();
    vector<Monitor*> newMonitors;
    for (int i = 0; i < monitors_len; i++) {
        if (monitors[i]->expired()) {
            to_remove.push_back(i);
        } else {
            send_callback(facilityName, monitors[i]);
            newMonitors.push_back(monitors[i]);
        }
    }

    this->monitors = newMonitors;
}

bool send_callback(string facilityName, Monitor* monitor) {
    // TODO
}


