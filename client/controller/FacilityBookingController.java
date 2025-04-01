package controller;

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

public class FacilityBookingController {
    
    // client stub to communicate with server
    private String user; 
    private FacilityBookingBoundary boundary = new FacilityBookingBoundary();
    private FacilityBookingServiceStub stub; 

    private Interval[] subscribedAvailability ={
        new Interval(new DayTime(Day.MONDAY, 8, 0), new DayTime(Day.MONDAY, 10, 0)),  // Before Alice's booking
        new Interval(new DayTime(Day.MONDAY, 12, 0), new DayTime(Day.MONDAY, 20, 0)), // After Alice's booking
        new Interval(new DayTime(Day.WEDNESDAY, 8, 0), new DayTime(Day.WEDNESDAY, 14, 30)), // Before Bob's booking
        new Interval(new DayTime(Day.WEDNESDAY, 16, 0), new DayTime(Day.WEDNESDAY, 20, 0))  // After Bob's booking
    };
    private String subscribedFacility = "Hall 2"; 

    // Constructor
    public FacilityBookingController(String user) {
        this.user = user;
        try {
            // configure client
            InetAddress localhost = InetAddress.getLocalHost();
            RUDP rudp = new RUDP();
            int port = 5432;
            FacilityBookingServiceStub stub = new FacilityBookingServiceStub(localhost, port, rudp); 
            this.stub = stub;  
        } catch (UnknownHostException e) {
            System.out.println("Localhost could not be resolved");
        }
        
    }

    public void viewFacilities() {
        
        FacilitiesResponse resp = null;
        
        try {
            resp = stub.viewFacilities();
            boundary.displayFacilityDetails(resp.facilities());
        } catch (Exception e) {
            System.out.println("An error occurred during the request: " + e.getMessage());
        }

        // boundary.displayFacilityDetails(facilities); // for testing

    }

    
    public void queryFacility(String facilityName, List<Integer> daysList) {
        
        AvailabilityResponse resp = null;
        
        try {
            // convert to Day structure
            Day[] days = convertListToDayArray(daysList);
            System.out.println(Arrays.toString(days));
            resp = stub.queryFacility(facilityName,days);
            boundary.displayAvailability(facilityName,resp.availability(),daysList);
        } catch (Exception e) {
            System.out.println("An error occurred during the request: " + e.getMessage());
        }
        
        // boundary.displayAvailability(facilityName,exampleAvailability,daysList); // for testing


    }

    
    public void bookFacility(String user, String start, String end) {
        
        BookResponse resp = null;
        
        try {
            resp = stub.bookFacility(user,convertToDayTime(start),convertToDayTime(end));
            System.out.print(resp.error());
            if (resp.bookingId() > 0) {
                System.out.println("Booking successfully made! Your booking confirmation ID: " + resp.bookingId());
            } else {
                System.out.println("Booking failed. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred during the request: " + e.getMessage());
        }
 
        // Testing
        DayTime startTime = convertToDayTime(start); 
        DayTime endTime = convertToDayTime(end); 
        System.out.println(String.format("%s %02d:%02d, %s %02d:%02d", startTime.day(), startTime.hour(), startTime.minute(), endTime.day(), endTime.hour(), endTime.minute()));
    }

    
    public void changeBooking(int bookingId, int offset) {
        Response resp = null;
        
        // try {
        //     resp = stub.changeBooking(bookingId,offset);
        // } catch (UnknownHostException e) {
        //     System.out.println("Localhost could not be resolved");
        // } catch (Exception e) {
        //     System.out.println("An error occurred during the request: " + e.getMessage());
        // }

    }
    
    public void extendBooking(int bookingId, int minutes) {
        Response resp = null;
        
        // try {
        //     resp = stub.extendBooking(bookingId,minutes);
        // } catch (UnknownHostException e) {
        //     System.out.println("Localhost could not be resolved");
        // } catch (Exception e) {
        //     System.out.println("An error occurred during the request: " + e.getMessage());
        // }
 
    }

    
    public void subscribe(String facilityName, int minutes) {
        Response resp = null;
        
        // try {
        //     resp = stub.subscribe(facilityName,minutes);
        // } catch (UnknownHostException e) {
        //     System.out.println("Localhost could not be resolved");
        // } catch (Exception e) {
        //     System.out.println("An error occurred during the request: " + e.getMessage());
        // }

       
    }

    
    public void viewSubscribedAvailability() {
        
        
        if (subscribedFacility.isEmpty()){
            System.out.println("You are not subscribed to any facility");
        } else {
            List<Integer> days = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7));
            boundary.displayAvailability(subscribedFacility,subscribedAvailability,days); 
        }
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
