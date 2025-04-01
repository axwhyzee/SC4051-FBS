package model;


import java.time.LocalTime;

public class Availability {

    private DateEnum day; 

    private LocalTime startDate; 

    private LocalTime endDate; 

    // Constructor
    public Availability(DateEnum day, LocalTime startDate, LocalTime endDate) {
        this.day = day
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and Setters
    public DateEnum getDay() {
        return day;
    }

    public void setDay(DateEnum day) {
        this.day = day;
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
