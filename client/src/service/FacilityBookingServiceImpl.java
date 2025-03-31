package service;

import java.util.List;
import java.util.ArrayList;

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
            "Hall B",
            "Sports Hall",
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

    
    public AvailabilityResponse queryFacility(String facilityName, List<Integer> daysList) {
        
        AvailabilityResponse resp = null;
        
        // try {
        //     // convert to Day structure
        //     Day[] days = convertListToDayArray(daysList);
        //     resp = stub.queryFacility(facilityName,days);
        //     boundary.displayAvailability(facilityName,resp.availability(),daysList);
        // } catch (UnknownHostException e) {
        //     System.out.println("Localhost could not be resolved");
        // } catch (Exception e) {
        //     System.out.println("An error occurred during the request: " + e.getMessage());
        // }
        
        boundary.displayAvailability(facilityName,exampleAvailability,daysList); // for testing


        return  resp; 
    }

    
    public BookResponse bookFacility(String user, String start, String end) {
        
        BookResponse resp = null;
        
        try {
            resp = stub.bookFacility(user,convertToDayTime(start),convertToDayTime(end));
            if (resp.bookingId() > 0){
                System.out.println("Booking successfully made! Your booking confirmation ID: " + resp.bookingId());
            } else {
                System.out.println("Booking failed. Please try again.");
            }
        } catch (UnknownHostException e) {
            System.out.println("Localhost could not be resolved");
        } catch (Exception e) {
            System.out.println("An error occurred during the request: " + e.getMessage());
        }

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
        //     boundary.displayFacilityDetails(resp.facilities());
        // } catch (UnknownHostException e) {
        //     System.out.println("Localhost could not be resolved");
        // } catch (Exception e) {
        //     System.out.println("An error occurred during the request: " + e.getMessage());
        // }

        boundary.displayFacilityDetails(facilities); // for testing

        return resp;
    }

    public static Day[] convertListToDayArray(List<Integer> daysList) {
        // Create a new Day[] array with the size of the List
        Day[] daysArray = new Day[daysList.size()];

        // Populate the Day[] array by mapping integers to Day enum values
        for (int i = 0; i < daysList.size(); i++) {
            int dayIndex = daysList.get(i) - 1; // Convert to 0-based index
            daysArray[i] = Day.values()[dayIndex];
        }

        return daysArray;
    }

    public static DayTime convertToDayTime(String input) {
        // Split the input string by space
        String[] parts = input.split(" ");

        // Extract the day and time components
        int day = Integer.parseInt(parts[0]);
        String[] timeParts = parts[1].split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        // Create a DayTime object using the parsed values
        DayTime dayTime = new DayTime(Day.values()[day - 1], hour, minute);
        
        return dayTime;
    }
}
