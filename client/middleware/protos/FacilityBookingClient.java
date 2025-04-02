package middleware\protos;

public interface FacilityBookingClient {
	Response terminate();
	Response publish(Interval[] availability);
}