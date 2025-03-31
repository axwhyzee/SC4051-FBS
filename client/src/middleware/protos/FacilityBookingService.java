package middleware\protos;

public interface FacilityBookingService {
	AvailabilityResponse queryFacility(String facilityName, Day[] days);
	BookResponse bookFacility(String user, DayTime start, DayTime end);
	Response changeBooking(int bookingId, int offset);
	Response subscribe(String facilityName, int minutes);
	Response extendBooking(int bookingId, int minutes);
	FacilitiesResponse viewFacilities();
}