package com.antrixgaming.leap.Models;

import java.util.Date;



public class EventAttenders {

    private String eventID;
    private String attenderName;
    private long leapInDate;

    public EventAttenders(String eventID, String attenderName) {
        this.eventID = eventID;
        this.attenderName = attenderName;

        // Initialize to current time
        leapInDate = new Date().getTime();
    }


    public EventAttenders() {

    }


    public String getEventID() {
        return eventID;
    }

    public long getLeapInDate() {
        return leapInDate;
    }

    public String getAttenderName() {
        return attenderName;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public void setAttenderName(String attenderName) {
        this.attenderName = attenderName;
    }

    public void setLeapInDate(long leapInDate) {
        this.leapInDate = leapInDate;
    }
}






