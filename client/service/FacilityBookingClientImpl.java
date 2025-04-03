package service; 

import boundary.*;
import middleware.protos.*;

public class FacilityBookingClientImpl implements FacilityBookingClient {
    

    @Override
    public Response terminate() throws Exception {
        throw new Exception("Terminate listening loop");
    }

    @Override
    public Response publish(Interval[] availability) {
        System.out.println("Received updated ... ");
        if (availability.length > 0) {
            FacilityBookingBoundary boundary = new FacilityBookingBoundary();
            boundary.displayMonitoredFacility(availability);
            return new Response("Successful");
        }
        return new Response("No availability provided");
    }

}
