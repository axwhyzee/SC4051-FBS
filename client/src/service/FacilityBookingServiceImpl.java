package service;

import model.Facility;
import model.Booking;
import model.Availability;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;
import interfaces.FacilityBookingService;

public class FacilityBookingServiceImpl implements FacilityBookingService {

    private List<Facility> facilities; // A list of available facilities

    public FacilityBookingServiceImpl() {
        // Initialize the facilities list here or load it from a database
        facilities = new ArrayList<>();
    }

    @Override
    public List<Facility> getFacilityDetails() {
        return facilities;
    }

    @Override
    public Availability queryFacility(String facilityName, List<Integer> days) {
        
        
        return availability;
    }

    @Override
    public int bookFacility(int userName, DayTime startDate, DayTime endDate) {
        // read user input
        // query to server
        // receive response
        // return response
        return bookingId;
    }

    @Override
    public int changeBooking(int bookingId, int offset) {
        // read user input
        // send to server
        // receive response
        // return response 
        int result = 0;
        return result;
    }

    @Override
    public int extendBooking(int bookingId, int minutes) {
        // read user input
        // send to server
        // receive response
        // return response 
        int result = 0;
        return result;
    }
}
