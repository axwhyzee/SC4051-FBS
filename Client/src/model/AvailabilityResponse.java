package model;

import java.util.Map;

public class AvailabilityResponse {

    private int respCode;  // -1 for not exists, 1 for OK
    private Map<DateEnum, Availability> availability; // Map of DAY to availability times (String array to hold times)

}
