

public class Main {
    public static void main(String[] args) {

        // create stub, and test link to server

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nFacility Booking System");
            System.out.println("1. View Facilities");
            System.out.println("2. Check Facility Availability");
            System.out.println("3. Book Facility");
            System.out.println("4. Extend Facility");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (choice) {
                case 1:
                    System.out.println("\n[1] View Facilities");
                    // bookingService.viewFacilities();
                    break;
                case 2:
                    System.out.println("\n[2] Check Facility Availability");
                    System.out.print("Enter facility name: ");
                    String facilityName = scanner.nextLine();
                    // bookingService.queryFacility(facilityName);
                    break;
                case 3:
                    System.out.println("[3] Book Facility");
                    System.out.print("Enter facility name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter day: ");
                    String day = scanner.nextLine();
                    System.out.print("Enter time slot: ");
                    String timeSlot = scanner.nextLine();
                    // bookingService.bookFacility(name, day, timeSlot);
                    break;
                case 4: 
                    System.out.println("[4] Extend Facility");
                    break; 
                case 5:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid option! Try again.");
            }
        }
    }
}