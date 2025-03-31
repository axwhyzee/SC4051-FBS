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

}