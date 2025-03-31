package middleware\protos;

public record BookResponse(
	String error,
	int bookingId
) {};