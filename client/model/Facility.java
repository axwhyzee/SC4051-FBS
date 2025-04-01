package model;

import java.util.List;

public class Facility {

    private String facilityName;
    private String facilityType;
    private List<Availability> availabilityList; 
    private List<Booking> bookingList;

    // Constructor
    public Facility(String facilityName, String facilityType, List<Availability> availabilityList, List<Booking> bookingList) {
        this.facilityName = facilityName;
        this.facilityType = facilityType;
        this.availabilityList = availabilityList;
        this.bookingList = bookingList;
    }

    // Getters and Setters
    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getFacilityType() {
        return facilityType;
    }

    public void setFacilityType(String facilityType) {
        this.facilityType = facilityType;
    }

    public List<Availability> getAvailabilityList() {
        return availabilityList;
    }

    public void setAvailabilityList(List<Availability> availabilityList) {
        this.availabilityList = availabilityList;
    }

    public List<Booking> getBookingList() {
        return bookingList;
    }

    public void setBookingList(List<Booking> bookingList) {
        this.bookingList = bookingList;
    }
}
