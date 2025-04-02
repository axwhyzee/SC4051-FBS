import middleware.network.RUDP;
import middleware.protos.*;

import service.FacilityBookingServiceImpl;

public class Server {
    public static void main(String args[]) {
        // configure RUDP client
        RUDP rudp = new RUDP();
        int server_port = 5432;
        FacilityBookingService service = new FacilityBookingServiceImpl();
        FacilityBookingServiceServicer servicer = new FacilityBookingServiceServicer(service);
        rudp.listen(server_port, servicer);
    }
}
