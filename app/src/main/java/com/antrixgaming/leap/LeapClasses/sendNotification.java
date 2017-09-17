package com.antrixgaming.leap.LeapClasses;

import java.util.Date;


public class sendNotification {

    private String notificationID;
    private String circleID;
    private String inviteBy;
    private String leaperInvited;
    private String inviteMessage;
    public String inviteType;
    private long inviteTime;



    public sendNotification(String notificationID, String inviteType, String circleID, String inviteBy, String leaperInvited, String inviteMessage) {
        this.notificationID = notificationID;
        this.circleID = circleID;
        this.inviteBy = inviteBy;
        this.leaperInvited = leaperInvited;
        this.inviteMessage = inviteMessage;
        this.inviteType = inviteType;

        // Initialize to current time
        inviteTime = new Date().getTime();
    }

    public sendNotification(){

    }

    public String getNotificationID() {
        return notificationID;
    }
    public void setNotificationID(String notificationID) {
        this.notificationID = notificationID;
    }

    public String geinviteType(){return inviteType;}
    public void setinviteType (String inviteType) {this.inviteType = inviteType;}

    public String getcircleID() {
        return circleID;
    }
    public void setcircleID(String circleID) {
        this.circleID = circleID;
    }

    public String getinviteBy(){return inviteBy;}
    public void setinviteBy(String inviteBy) {
        this.inviteBy = inviteBy;
    }

    public String getleaperInvited() {
        return leaperInvited;
    }
    public void setleaperInvited(String leaperInvited) {
        this.leaperInvited = leaperInvited;
    }

    public String getinviteMessage(){return inviteMessage;}
    public void setinviteMessage(String inviteMessage){this.inviteMessage = inviteMessage;}

    public Long getinviteTime(){return inviteTime;}
    public void setinviteTime(Long inviteTime){this.inviteTime = inviteTime;}


}
