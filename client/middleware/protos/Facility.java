package middleware.protos;

public record Facility(
	String name,
	String type,
	Booking[] bookings
) {};