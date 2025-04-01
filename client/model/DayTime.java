package model;

import java.time.LocalTime;

public class DayTime {
    private DateEnum date;  // Enum for day of the week
    private LocalTime time; // Stores the exact time

    // Constructor
    public DayTime(DateEnum date, LocalTime time) {
        this.date = date;
        this.time = time;
    }

    // Getters and Setters
    public DateEnum getDate() {
        return date;
    }

    public void setDate(DateEnum date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }
}
