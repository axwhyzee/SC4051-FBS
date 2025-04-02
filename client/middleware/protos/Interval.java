package middleware\protos;

public record Interval(
	DayTime start,
	DayTime end
) {};