package boundary; 

import middleware.protos.Facility;

public class FacilityBookingBoundary {

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
}