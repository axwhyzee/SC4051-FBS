package middleware\protos;

public record FacilitiesResponse(
	String error,
	Facility[] facilities
) {};