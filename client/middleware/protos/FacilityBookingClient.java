package middleware.protos;

public interface FacilityBookingClient {
	Response terminate() throws Exception;
	Response publish(Interval[] availability) throws Exception;
}