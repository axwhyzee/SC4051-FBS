package middleware.protos;

public interface FacilityBookingService {
	AvailabilityResponse queryFacility(String facilityName, Day[] days) throws Exception;
	BookResponse bookFacility(String facilityName, String user, DayTime start, DayTime end) throws Exception;
	Response changeBooking(int bookingId, int offset) throws Exception;
	Response subscribe(String facilityName, int minutes) throws Exception;
	Response extendBooking(int bookingId, int minutes) throws Exception;
	FacilitiesResponse viewFacilities() throws Exception;
}