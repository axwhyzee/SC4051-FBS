package model;

public class Booking {

    private int bookingId;

    private String userName;

    private DayTime startDate; 

    private DayTime endDate; 

    // Constructor
    public Booking(int bookingId, String userName, DayTime startDate, DayTime endDate) {
        this.bookingId = bookingId;
        this.userName = userName;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and Setters
    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public DayTime getStartDate() {
        return startDate;
    }

    public void setStartDate(DayTime startDate) {
        this.startDate = startDate;
    }

    public DayTime getEndDate() {
        return endDate;
    }

    public void setEndDate(DayTime endDate) {
        this.endDate = endDate;
    }
}
