package middleware.protos;

public record Booking(
	int bookingId,
	String facilityName,
	String user,
	DayTime start,
	DayTime end
) {};