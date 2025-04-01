package middleware.protos;

public record AvailabilityResponse(
	String error,
	Interval[] availability
) {};