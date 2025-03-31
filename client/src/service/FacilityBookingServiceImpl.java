package service;

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

// import middleware.protos.FacilityBookingServiceStub;

import java.util.List;

public class FacilityBookingServiceImpl {
    
    // client stub to communicate with server
    private String user; 
    private FacilityBookingBoundary boundary = new FacilityBookingBoundary();
    // private FacilityBookingServiceStub stub; 

    private Facility exampleFacility = new Facility(
        "Hall A",
        "Sports Hall",
        new Booking[] {
            new Booking("Alice", new DayTime(Day.MONDAY, 10, 0), new DayTime(Day.MONDAY, 12, 0)),
            new Booking("Bob", new DayTime(Day.WEDNESDAY, 14, 30), new DayTime(Day.WEDNESDAY, 16, 0))
        }
    );

    private Facility[] facilities = new Facility[] {
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
            "Gym A",
            "Gym Facility",
            new Booking[] {
                new Booking("Frank", new DayTime(Day.MONDAY, 7, 0), new DayTime(Day.MONDAY, 8, 0)),
                new Booking("Grace", new DayTime(Day.FRIDAY, 18, 0), new DayTime(Day.FRIDAY, 19, 0)),
                new Booking("Hannah", new DayTime(Day.SUNDAY, 15, 0), new DayTime(Day.SUNDAY, 16, 0))
            }
        )
    };

    private Interval[] exampleAvailability = {
        new Interval(new DayTime(Day.MONDAY, 8, 0), new DayTime(Day.MONDAY, 10, 0)),  // Before Alice's booking
        new Interval(new DayTime(Day.MONDAY, 12, 0), new DayTime(Day.MONDAY, 20, 0)), // After Alice's booking
        new Interval(new DayTime(Day.WEDNESDAY, 8, 0), new DayTime(Day.WEDNESDAY, 14, 30)), // Before Bob's booking
        new Interval(new DayTime(Day.WEDNESDAY, 16, 0), new DayTime(Day.WEDNESDAY, 20, 0))  // After Bob's booking
    };

    // Constructor
    public FacilityBookingServiceImpl(String user) {
        this.user = user;
        // try {
        //     // configure client
        //     InetAddress localhost = InetAddress.getLocalHost();
        //     RUDP rudp = new RUDP();
        //     int port = 5432;
        //     FacilityBookingServiceStub stub = new FacilityBookingServiceStub(localhost, port, rudp);   
        // } catch (UnknownHostException e) {
        //     System.out.println("Localhost could not be resolved");
        // }
        // this.stub = stub;
    }

    
    public AvailabilityResponse queryFacility(String facilityName, Day[] days) {
        
        AvailabilityResponse resp = null;
        
        // try {
        //     resp = stub.queryFacility(facilityName,days);
        // } catch (UnknownHostException e) {
        //     System.out.println("Localhost could not be resolved");
        // } catch (Exception e) {
        //     System.out.println("An error occurred during the request: " + e.getMessage());
        // }

        return  resp; 
    }

    
    public BookResponse bookFacility(String user, DayTime start, DayTime end) {
        
        BookResponse resp = null;
        
        // try {
        //     resp = stub.bookFacility(user,start,end);
        // } catch (UnknownHostException e) {
        //     System.out.println("Localhost could not be resolved");
        // } catch (Exception e) {
        //     System.out.println("An error occurred during the request: " + e.getMessage());
        // }

        return  resp; 

    }

    
    public Response changeBooking(int bookingId, int offset) {
        Response resp = null;
        
        // try {
        //     resp = stub.changeBooking(bookingId,offset);
        // } catch (UnknownHostException e) {
        //     System.out.println("Localhost could not be resolved");
        // } catch (Exception e) {
        //     System.out.println("An error occurred during the request: " + e.getMessage());
        // }

        return  resp; 
    }

    
    public Response subscribe(String facilityName, int minutes) {
        Response resp = null;
        
        // try {
        //     resp = stub.subscribe(facilityName,minutes);
        // } catch (UnknownHostException e) {
        //     System.out.println("Localhost could not be resolved");
        // } catch (Exception e) {
        //     System.out.println("An error occurred during the request: " + e.getMessage());
        // }

        return  resp; 
    }

    
    public Response extendBooking(int bookingId, int minutes) {
        Response resp = null;
        
        // try {
        //     resp = stub.extendBooking(bookingId,minutes);
        // } catch (UnknownHostException e) {
        //     System.out.println("Localhost could not be resolved");
        // } catch (Exception e) {
        //     System.out.println("An error occurred during the request: " + e.getMessage());
        // }

        return  resp; 
    }

    
    public FacilitiesResponse viewFacilities() {
        
        FacilitiesResponse resp = null;
        
        // try {
        //     resp = stub.viewFacilities();
        // } catch (UnknownHostException e) {
        //     System.out.println("Localhost could not be resolved");
        // } catch (Exception e) {
        //     System.out.println("An error occurred during the request: " + e.getMessage());
        // }
        boundary.displayFacilityDetails(facilities);

        return resp;
    }
}
