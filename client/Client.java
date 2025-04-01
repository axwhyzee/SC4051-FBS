import java.net.InetAddress;
import java.net.UnknownHostException;
import middleware.network.RUDP;
import middleware.protos.*;


public class Client {
    public static void main(String args[]) {
        try {
            // configure RUDP client
            InetAddress localhost = InetAddress.getLocalHost();
            RUDP rudp = new RUDP();
            int port = 5432;
            TestServiceStub stub = new TestServiceStub(localhost, port, rudp);   

            // make RPC request
            Day[] days = {Day.MONDAY, Day.FRIDAY, Day.SUNDAY};
            DayTime[] result = stub.generate_noon_daytimes(days);

            for (DayTime daytime : result) {
                System.out.println("Day: " + daytime.day());
                System.out.println("Hour: " + daytime.hour());
                System.out.println("Minute: " + daytime.minute());
            }
        } catch (UnknownHostException e) {
            System.out.println("Localhost could not be resolved");
        }
    }
}