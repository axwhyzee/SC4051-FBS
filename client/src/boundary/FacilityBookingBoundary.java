package boundary; 

import java.util.List;

import middleware.protos.Day; 
import middleware.protos.DayTime;
import middleware.protos.Interval;
import middleware.protos.Facility;

public class FacilityBookingBoundary {

    public void displayMainMenu(){
        System.out.println("\nFacility Booking System");
        System.out.println("1. View Facilities");
        System.out.println("2. Check Facility Availability");
        System.out.println("3. Book Facility");
        System.out.println("4. Change Booking");
        System.out.println("5. Extend Booking");
        System.out.println("6. Subscribe to Monitor Facility Availability");
        System.out.println("7. Exit");
        System.out.print("Choose an option: ");
    }

    public void selectDays(){
        System.out.println("1. Monday");
        System.out.println("2. Tuesday");
        System.out.println("3. Wednesday");
        System.out.println("4. Thursday");
        System.out.println("5. Friday");
        System.out.println("6. Saturday");
        System.out.println("7. Sunday");
        System.out.println("Enter day's number one after another (press Enter without input to stop):");
    }

    public void displayFacilityDetails(Facility[] facilities) {
        // Print header
        System.out.println(String.format("%-20s %-20s", "Facility Name", "Facility Type"));
        System.out.println("-------------------- --------------------");
        
        // Loop through each facility
        for (Facility facility : facilities) {
            // Ensure the name and type are displayed with exactly 20 characters, left-aligned
            System.out.println(String.format("%-20s %-20s", facility.name(), facility.type()));
        }
    }

    public void displayAvailability(String facilityName, Interval[] availability, List<Integer> days) {
        // Print header
        System.out.println("Showing Availability of " + facilityName); 
        System.out.println(String.format("%9s %-10s %9s %-10s", "Start", "Time", "End", "Time"));
        System.out.println("-------------------- --------------------");

        // Loop through each interval
        for (Interval interval : availability) {
            // Extract start and end times
            DayTime start = interval.start();
            DayTime end = interval.end();
            
            // Check if the start and end day are in the 'days' list
            if (days.contains(start.day().ordinal() + 1) && days.contains(end.day().ordinal() + 1)) {
                // Format start and end times
                String startTime = String.format("%9s %02d:%02d", start.day(), start.hour(), start.minute());
                String endTime = String.format("%9s %02d:%02d", end.day(), end.hour(), end.minute());

                // Print formatted interval
                System.out.println(String.format("%-20s %-20s", startTime, endTime));
            }
        }
    }

    


}