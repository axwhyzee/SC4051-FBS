package service; 

import boundary.*;
import middleware.protos.*;

public class FacilityBookingClientImpl implements FacilityBookingClient {
    

    @Override
    public Response terminate() {
        return new Response("Service terminated successfully");
    }

    @Override
    public Response publish(Interval[] availability) {
        if (availability.length > 0) {
            FacilityBookingBoundary boundary = new FacilityBookingBoundary();
            boundary.displayMonitoredFacility(availability);
            return new Response("Successful");
        }
        return new Response("No availability provided");
    }

}
