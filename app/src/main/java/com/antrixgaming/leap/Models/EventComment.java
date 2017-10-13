package com.antrixgaming.leap.Models;

import java.util.Date;



public class EventComment {

    private String eventID;
    private String commentID;
    private String commentBy;
    private String comment;
    private long commentDate;

    public EventComment(String eventID, String commentID, String commentBy, String comment) {
        this.eventID = eventID;
        this.commentID = commentID;
        this.commentBy = commentBy;
        this.comment = comment;

        // Initialize to current time
        commentDate = new Date().getTime();
    }



    public EventComment() {

    }

    public long getCommentDate() {
        return commentDate;
    }

    public String getComment() {
        return comment;
    }

    public String getCommentBy() {
        return commentBy;
    }

    public String getEventID() {
        return eventID;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setCommentBy(String commentBy) {
        this.commentBy = commentBy;
    }

    public void setCommentDate(long commentDate) {
        this.commentDate = commentDate;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }
}






