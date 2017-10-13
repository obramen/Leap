package com.antrixgaming.leap.Models;



public class CreateEvent {

    String eventID;
    String eventType;
    String eventBy;
    String eventTitle;
    long eventStartDate;
    long eventEndDate;
    String eventDescription;
    String eventLocation;


    public CreateEvent(){


    }

    public CreateEvent (String eventID,String eventType, String eventBy, String eventTitle, long eventStartDate, long eventEndDate, String eventDescription, String eventLocation){
        this.eventID = eventID;
        this.eventType = eventType;
        this.eventBy = eventBy;
        this.eventTitle = eventTitle;
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
        this.eventDescription = eventDescription;
        this.eventLocation = eventLocation;
    }

    public String getEventType() {
        return eventType;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public long getEventEndDate() {
        return eventEndDate;
    }

    public String getEventID() {
        return eventID;
    }

    public long getEventStartDate() {
        return eventStartDate;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public String getEventBy() {
        return eventBy;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setEventEndDate(long eventEndDate) {
        this.eventEndDate = eventEndDate;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public void setEventStartDate(long eventStartDate) {
        this.eventStartDate = eventStartDate;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public void setEventBy(String eventBy) {
        this.eventBy = eventBy;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }
}


