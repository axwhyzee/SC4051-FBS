import java.net.InetAddress;
import java.net.UnknownHostException;
import middleware.network.RUDP;
import middleware.protos.*;


public class ClientTest {
    public static void main(String args[]) {
        // try {
        //     // configure RUDP client
        //     InetAddress localhost = InetAddress.getLocalHost();
        //     RUDP rudp = new RUDP();
        //     int port = 5432;
        //     TestServiceStub stub = new TestServiceStub(localhost, port, rudp);   

        //     // make RPC request
        //     Day[] days = {Day.MONDAY, Day.FRIDAY, Day.SUNDAY};
        //     DayTime[] result = stub.generate_noon_daytimes(days);

        //     for (DayTime daytime : result) {
        //         System.out.println("Day: " + daytime.day());
        //         System.out.println("Hour: " + daytime.hour());
        //         System.out.println("Minute: " + daytime.minute());
        //     }
        // } catch (UnknownHostException e) {
        //     System.out.println("Localhost could not be resolved");
        // }

        // Initialize availability array
        Interval[] availability = new Interval[]{
            new Interval(new DayTime(Day.MONDAY, 8, 0), new DayTime(Day.MONDAY, 10, 0)), // Before Alice's booking
            new Interval(new DayTime(Day.MONDAY, 12, 0), new DayTime(Day.MONDAY, 20, 0)), // After Alice's booking
            new Interval(new DayTime(Day.WEDNESDAY, 8, 0), new DayTime(Day.WEDNESDAY, 14, 30)), // Before Bob's booking
            new Interval(new DayTime(Day.WEDNESDAY, 16, 0), new DayTime(Day.WEDNESDAY, 20, 0))  // After Bob's booking
        };

        try {
            // configure RUDP client
            InetAddress localhost = InetAddress.getLocalHost();
            RUDP rudp = new RUDP();
            int port = 5432;
            FacilityBookingClientStub stub = new FacilityBookingClientStub(localhost, port, rudp); 
            Response result = stub.publish(availability);

        } catch (UnknownHostException e) {
            System.out.println("Localhost could not be resolved");
        }
    }
}