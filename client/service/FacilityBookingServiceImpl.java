package service;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.net.InetAddress;
import java.net.UnknownHostException;
import middleware.network.RUDP;

import middleware.protos.FacilityBookingService;
import middleware.protos.AvailabilityResponse;
import middleware.protos.BookResponse;
import middleware.protos.Response;
import middleware.protos.FacilitiesResponse;

import middleware.protos.Day; 
import middleware.protos.DayTime;
import middleware.protos.Interval;
import middleware.protos.Facility;
import middleware.protos.Booking;

import boundary.FacilityBookingBoundary;

import middleware.protos.FacilityBookingServiceStub;

import java.util.List;

public class FacilityBookingServiceImpl implements FacilityBookingService {

    private Facility[] facilities; // An array of available facilities
    private Interval[] availability; // An array of available intervals

    public FacilityBookingServiceImpl() {
        // Initialize facilities array
        facilities = new Facility[]{
            new Facility("Hall A", "Sports Hall", new Booking[] {})
        };

        // Initialize availability array
        availability = new Interval[]{
            new Interval(new DayTime(Day.MONDAY, 8, 0), new DayTime(Day.MONDAY, 10, 0)), // Before Alice's booking
            new Interval(new DayTime(Day.MONDAY, 12, 0), new DayTime(Day.MONDAY, 20, 0)), // After Alice's booking
            new Interval(new DayTime(Day.WEDNESDAY, 8, 0), new DayTime(Day.WEDNESDAY, 14, 30)), // Before Bob's booking
            new Interval(new DayTime(Day.WEDNESDAY, 16, 0), new DayTime(Day.WEDNESDAY, 20, 0))  // After Bob's booking
        };
    }

    @Override
    public FacilitiesResponse viewFacilities() {
        System.out.println("Getting facilities");
        return new FacilitiesResponse("", facilities);
    }

    @Override
    public AvailabilityResponse queryFacility(String facilityName, Day[] days) {
        System.out.println(facilityName + " " + days);
        return new AvailabilityResponse("hi", availability);
    }

    @Override
    public BookResponse bookFacility(String user, DayTime start, DayTime end) {
        int bookingId = 100; 
        BookResponse resp; 

        // successful
        resp = new BookResponse("sup", bookingId);

        // Fail
        // resp = new BookResponse("Booking Failed", -1);

        return resp ;
    }

    @Override
    public Response changeBooking(int bookingId, int offset) {
        
        Response resp;

        // successful
        resp = new Response("");
        // Failed
        // resp =  new Response("Failed to change Booking");

        return resp; 
    }

    @Override
    public Response subscribe(String facilityName, int minutes) {
        Response resp;

        // successful
        resp = new Response("");
        // Failed
        // resp =  new Response("Subscription Failed");

        return resp; 
    }

    @Override
    public Response extendBooking(int bookingId, int minutes) {
        Response resp;

        // successful
        resp = new Response("");
        // Failed
        // resp =  new Response("Failed to extend booking");

        return resp; 
    }

    
}
