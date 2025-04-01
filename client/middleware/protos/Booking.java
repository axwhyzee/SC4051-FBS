package middleware.protos;

public record Booking(
	String user,
	DayTime start,
	DayTime end
) {};