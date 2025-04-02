import middleware.protos.*;


/**
 * Concrete TestService implementation.
 *
 * Whenever a UDP packet is received by RUDP, it calls the Servicer 
 * via callback to first unmarshall the RPC request, then dispatch the
 * unmarshalled arguments to the corresponding method implemented by
 * this ConcreteTestService class.
 *
 * Return values of this ConcreteTestService are mashalled by the
 * Servicer, and sent as response back to the client via RUDP.
 */
public class ConcreteTestService implements TestService {
    @Override
    public DayTime[] generate_noon_daytimes(Day[] days) {
        DayTime[] result = new DayTime[days.length];
        for (int i=0; i<days.length; i++) {
            DayTime daytime = new DayTime(days[i], 12, 0);
            result[i] = daytime;
        }
        return result;
    }
}