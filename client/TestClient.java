// Example Java client (TestClient.java)
import java.net.InetAddress;
import java.net.UnknownHostException;
import middleware.network.RUDP;
import middleware.protos.AvailabilityResponse;
import middleware.protos.FacilityBookingServiceStub;
import middleware.protos.Day;
import middleware.protos.Interval;

public class TestClient {
    public static void main(String args[]) {
        try {
            // configure client
            InetAddress localhost = InetAddress.getLocalHost();
            RUDP rudp = new RUDP();
            int port = 5432;
            FacilityBookingServiceStub stub = new FacilityBookingServiceStub(localhost, port, rudp);   

            // make RPC request
            Day[] days = {Day.FRIDAY, Day.MONDAY};     
            AvailabilityResponse resp = stub.queryFacility("Facility A", days);
            System.out.println(resp.error());
            for (Interval itv : resp.availability()) {
                System.out.println("Start: " + itv.start());
                System.out.println("End: " + itv.end());
            }
        } catch (UnknownHostException e) {
            System.out.println("Localhost could not be resolved");
        }
    }
}