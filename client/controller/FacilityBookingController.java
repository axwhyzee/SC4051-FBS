package controller;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.net.InetAddress;
import java.net.UnknownHostException;
import middleware.network.RUDP;

import middleware.protos.*;

import boundary.FacilityBookingBoundary;


import java.util.List;

public class FacilityBookingController {
    
    // client stub to communicate with server
    private String user; 
    private FacilityBookingBoundary boundary = new FacilityBookingBoundary();
    private FacilityBookingServiceStub stub; 
    private RUDP rudp;
    private InetAddress localhost;  
    private int port; 
    // FacilityBookingClient service;
    // FacilityBookingClientServicer servicer;


    // Constructor
    public FacilityBookingController(String user) {
        this.user = user;
        try {
            // configure client
            this.localhost = InetAddress.getLocalHost();
            this.rudp = new RUDP();
            this.port = 5432;
            this.stub = new FacilityBookingServiceStub(this.localhost, this.port, this.rudp);   
        } catch (UnknownHostException e) {
            System.out.println("Localhost could not be resolved");
        }
        
    }

    public void viewFacilities() {

        try {
            FacilitiesResponse resp = stub.viewFacilities();
            boundary.displayFacilityDetails(resp.facilities());
        } catch (Exception e) {
            System.out.println("An error occurred during the request: " + e.getMessage());
        }
    }

    
    public void queryFacility(String facilityName, List<Integer> daysList) {
        
        try {
            // convert to Day structure
            Day[] days = convertListToDayArray(daysList);
            AvailabilityResponse resp = stub.queryFacility(facilityName,days);
            boundary.displayAvailability(facilityName,resp.availability(),daysList);
        } catch (Exception e) {
            System.out.println("An error occurred during the request: " + e.getMessage());
        }

    }

    
    public void bookFacility(String facilityName, String user, String start, String end) {
       
       try {
            BookResponse resp = stub.bookFacility(facilityName,user,convertToDayTime(start),convertToDayTime(end));
            if (resp.bookingId() > 0) {
                System.out.println("Booking successfully made! Your booking confirmation ID: " + resp.bookingId());
            } else {
                System.out.println("Booking failed. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred during the request: " + e.getMessage());
        }
 
    }

    
    public void changeBooking(int bookingId, int offset) {
        
        try {
            Response resp = stub.changeBooking(bookingId,offset);
        } catch (Exception e) {
            System.out.println("An error occurred during the request: " + e.getMessage());
        }

    }
    
    public void extendBooking(int bookingId, int minutes) {
        Response resp = null;
        
        try {
            resp = stub.extendBooking(bookingId,minutes);
        } catch (Exception e) {
            System.out.println("An error occurred during the request: " + e.getMessage());
        }
 
    }

    
    public void subscribe(String facilityName, int minutes) {

        List<Integer> daysList = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7));
        Day[] days = convertListToDayArray(daysList);

        Response resp = null;
        
        try {
            AvailabilityResponse availResp = stub.queryFacility(facilityName,days);
            boundary.displayAvailability(facilityName,availResp.availability(),daysList); 
        } catch (Exception e) {
            System.out.println("An error occurred during the request: " + e.getMessage());
        }

        // try {
        //     this.service = new FacilityBookingClientImpl();
        //     this.servicer = new FacilityBookingClientServicer(service);
        //     rudp.listen(this.port, this.servicer);
        // } catch (Exception e) {
        //     System.out.println("Subscription has expired");
        // }       
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
