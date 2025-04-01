package middleware.protos;

public interface TestService {
	DayTime[] generate_noon_daytimes(Day[] days);
}