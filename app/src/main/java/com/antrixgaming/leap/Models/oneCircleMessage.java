package com.antrixgaming.leap.Models;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class oneCircleMessage {

    private String messageText;
    private String messageUser;
    private long messageTime;
    private String phoneNumber;
    private String uid;
    private String circleid;
    long index;


    public oneCircleMessage(String messageText, String circleid, String phoneNumber, String uid) {
        this.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.messageText = messageText;
        //this.messageUser = FirebaseDatabase.getInstance().getReference().child("uid").child(uid).child("name").toString();
        this.circleid = circleid;
        this.phoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

        // Initialize to current time
        messageTime = new Date().getTime();
        index = -1 * messageTime;
    }

    public oneCircleMessage(){

    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }

    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }

    public String getPhoneNumber(){return phoneNumber;}

    public void setPhoneNumber(String phoneNumber){this.phoneNumber = phoneNumber;}

    public String getUid(){return uid;}

    public void setUid  (String uid) {this.uid = uid;}

    public String getCircleid(){return circleid;}

    public void setCircleid(String circleid){this.circleid = circleid;}


}
