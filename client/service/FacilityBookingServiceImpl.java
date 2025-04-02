package service;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.net.InetAddress;
import java.net.UnknownHostException;
import middleware.network.RUDP;

import middleware.protos.*;

import boundary.FacilityBookingBoundary;

import java.util.List;

public class FacilityBookingServiceImpl implements FacilityBookingService {
    // Note that all functions sends dummy data

    private Facility[] facilities; // An array of available facilities
    private Interval[] availability; // An array of available intervals

    public FacilityBookingServiceImpl() {
        // Initialize facilities array
        facilities = new Facility[] {
            new Facility(
                "Hall A",
                "Sports Hall",
                new Booking[] {
                    new Booking("Alice", new DayTime(Day.MONDAY, 10, 0), new DayTime(Day.MONDAY, 12, 0)),
                    new Booking("Bob", new DayTime(Day.WEDNESDAY, 14, 30), new DayTime(Day.WEDNESDAY, 16, 0))
                }
            ),
            new Facility(
                "Conference Room 1",
                "Conference Room",
                new Booking[] {
                    new Booking("Charlie", new DayTime(Day.TUESDAY, 9, 0), new DayTime(Day.TUESDAY, 11, 0)),
                    new Booking("Dave", new DayTime(Day.THURSDAY, 13, 30), new DayTime(Day.THURSDAY, 15, 30)),
                    new Booking("Eva", new DayTime(Day.FRIDAY, 10, 30), new DayTime(Day.FRIDAY, 12, 0))
                }
            ),
            new Facility(
                "Hall B",
                "Sports Hall",
                new Booking[] {
                    new Booking("Frank", new DayTime(Day.MONDAY, 7, 0), new DayTime(Day.MONDAY, 8, 0)),
                    new Booking("Grace", new DayTime(Day.FRIDAY, 18, 0), new DayTime(Day.FRIDAY, 19, 0)),
                    new Booking("Hannah", new DayTime(Day.SUNDAY, 15, 0), new DayTime(Day.SUNDAY, 16, 0))
                }
            )
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
        System.out.println("query facility");
        
        return new AvailabilityResponse("", availability);
    }

    @Override
    public BookResponse bookFacility(String facilityName, String user, DayTime start, DayTime end) {
        System.out.println("book facility");
        int bookingId = 100; 
        BookResponse resp; 

        // successful
        resp = new BookResponse("", bookingId);

        // Fail
        // resp = new BookResponse("Booking Failed", -1);

        return resp ;
    }

    @Override
    public Response changeBooking(int bookingId, int offset) {
        System.out.println("change booking");
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
        System.out.println("extend booking");
        Response resp;

        // successful
        resp = new Response("");
        // Failed
        // resp =  new Response("Failed to extend booking");

        return resp; 
    }

    
}
