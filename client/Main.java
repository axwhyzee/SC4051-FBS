import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;

import boundary.FacilityBookingBoundary;
import controller.FacilityBookingController;
import java.net.SocketException;

public class Main {
    public static void main(String[] args) {

        try (Scanner scanner = new Scanner(System.in)) {
            FacilityBookingBoundary boundary = new FacilityBookingBoundary(); 
            String userName = "";
            String facilityName, input, startTime, endTime;
            int number, bookingID; 

            // Retrieve user
            while (userName.trim().isEmpty()) {
                System.out.print("Enter your username: ");
                userName = scanner.nextLine();
                
                // Check if the input is empty
                if (userName.trim().isEmpty()) {
                    System.out.println("Username cannot be empty. Please try again.");
                }
            }

            // Inititate Service
            FacilityBookingController service = new FacilityBookingController(userName); 

            while (true) {
                boundary.displayMainMenu();

                String choice = scanner.nextLine().trim();

                switch (choice) {
                    case "1":
                        System.out.println("\n[1] View Facilities");
                        service.viewFacilities();

                        System.out.print("\nPress Enter to continue...");
                        scanner.nextLine();
                        break;
                    case "2":
                        List<Integer> dayList = new ArrayList<>();

                        System.out.println("\n[2] Check Facility Availability");

                        while (true){ 
                            System.out.print("Enter facility name: ");  
                            facilityName = scanner.nextLine().trim();
                            if (facilityName.isEmpty()){
                                System.out.println("Facility name cannot be empty. Please try again.");
                            } else {
                                break;
                            }
                        }
                        
                        System.out.println("Select days to check availability: ");
                        boundary.selectDays(); 
                        while (true) {
                            input = scanner.nextLine().trim(); // Read full line and trim spaces
                            
                            if (input.isEmpty()) {
                                break; // Stop if input is empty (user presses Enter)
                            }
                            
                            try {
                                number = Integer.parseInt(input); // Convert to integer
                                if (number > 0 && number < 8) {
                                    dayList.add(number);
                                } else {
                                    System.out.println("Invalid input! Please enter a valid number.");
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid input! Please enter a valid number.");
                            }
                        }

                        service.queryFacility(facilityName, dayList);

                        System.out.print("\nPress Enter to continue...");
                        scanner.nextLine();
                        break;

                    case "3":

                        System.out.println("\n[3] Book Facility");

                        while (true){ 
                        System.out.print("Enter facility name: ");
                            facilityName = scanner.nextLine().trim();
                            if (facilityName.isEmpty()){
                                System.out.println("Facility name cannot be empty. Please try again.");
                            } else {
                                break;
                            }
                        }

                        // Get valid time input in "d hh:mm" format
                        while (true) {
                            System.out.print("Enter start time (format: d hh:mm, where d is 1 for Monday, 7 for Sunday, and hh:mm is in 24-hour format): ");
                            startTime = scanner.nextLine().trim();

                            if (isValidTimeFormat(startTime)) {
                                break; // Valid input, exit loop
                            } else {
                                System.out.println("Invalid format! Enter a day (1-7) and time in 24-hour format (e.g., '2 14:30').");
                            }
                        }

                        while (true) {
                            System.out.print("Enter end time (format: d hh:mm, where d is 1 for Monday, 7 for Sunday, and hh:mm is in 24-hour format): ");
                            endTime = scanner.nextLine().trim();

                            if (isValidTimeFormat(endTime)) {
                                break; // Valid input, exit loop
                            } else {
                                System.out.println("Invalid format! Enter a day (1-7) and time in 24-hour format (e.g., '2 14:30').");
                            }
                        }
                        
                        service.bookFacility(facilityName,userName, startTime, endTime);

                        System.out.print("\nPress Enter to continue...");
                        scanner.nextLine();
                        break;

                    case "4": 
                        System.out.println("[4] Change Facility Booking by Offset");

                        while (true) {
                            System.out.print("Enter booking ID: ");
                            input = scanner.nextLine().trim();
                            
                            try {
                                bookingID = Integer.parseInt(input); // Convert to integer
                        
                                break; 
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid input! Please enter a valid integer value for booking ID.");
                            }
                        }

                        while (true) {
                            System.out.print("Enter the offset value in minutes (positive for forward, negative for backward): ");
                            input = scanner.nextLine().trim();
                            
                            try {
                                number = Integer.parseInt(input); // Convert to integer (in minutes)
                        
                                break;
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid input! Please enter a valid integer value for the offset in minutes.");
                            }
                        }

                        service.changeBooking(bookingID,number);

                        System.out.print("\nPress Enter to continue...");
                        scanner.nextLine();
                        break; 

                    case "5": 
                        System.out.println("[5] Extend Facility Booking");

                        while (true) {
                            System.out.print("Enter booking ID: ");
                            input = scanner.nextLine().trim();
                            
                            try {
                                bookingID = Integer.parseInt(input); // Convert to integer
                        
                                break; 
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid input! Please enter a valid integer value for booking ID.");
                            }
                        }

                        while (true) {
                            System.out.print("Enter the number of minutes to extend booking: ");
                            input = scanner.nextLine().trim();
                            
                            try {
                                number = Integer.parseInt(input); // Convert to integer (in minutes)
                        
                                break;
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid input! Please enter a valid integer value in minutes.");
                            }
                        }
                        
                        service.extendBooking(bookingID,number);

                        System.out.print("\nPress Enter to continue...");
                        scanner.nextLine();
                        break; 

                    case "6": 
                        System.out.println("[6] Monitor Facility Availability");

                        while (true){ 
                        System.out.print("Enter facility name: ");
                            facilityName = scanner.nextLine().trim();
                            if (facilityName.isEmpty()){
                                System.out.println("Facility name cannot be empty. Please try again.");
                            } else {
                                break;
                            }
                        }

                        while (true) {
                            System.out.print("Enter the number of minutes to monitor facility's availability: ");
                            input = scanner.nextLine().trim();
                            
                            try {
                                number = Integer.parseInt(input); // Convert to integer (in minutes)
                        
                                break;
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid input! Please enter a valid integer value in minutes.");
                            }
                        }

                        service.subscribe(facilityName,number);

                        System.out.print("\nPress Enter to continue...");
                        scanner.nextLine();
                        break; 

                    case "7":
                        System.out.println("Exiting...");
                        return;
                    default:
                        System.out.println("Invalid option! Try again.");
                }
            }
        } catch (SocketException _) {
            System.out.println("Unable to create socket. Exiting ...");
        }
    }

    public static boolean isValidTimeFormat(String input) {
        // Regex breakdown: 
        // ^[1-7] - Starts with a number between 1-7 (inclusive)
        // \s+ - At least one space
        // (0\d|1\d|2[0-3]) - Matches 00-23 (valid 24-hour format)
        // : - Colon separator
        // [0-5]\d - Matches 00-59 (valid minutes)
        String regex = "^[1-7]\\s+(0\\d|1\\d|2[0-3]):[0-5]\\d$";
        return Pattern.matches(regex, input);
    }
}