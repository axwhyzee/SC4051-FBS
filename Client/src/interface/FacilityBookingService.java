package interfaces;

import model.Facility;
import model.Booking;
import model.Availability;
import java.util.List;

public interface FacilityBookingService {


    List<Facility> getFaciltyDetails(); 

    AvailabilityResponse queryFacility(String facilityName, List<DateEnum> days); queryFacility(String facilityName, List<int> days);

    int bookFacility(int userName, DayTime startDate, DayTime endDate);

    int changeBooking(int bookingId, int offset);

    int extendBooking(int bookingId, int minutes);

}
