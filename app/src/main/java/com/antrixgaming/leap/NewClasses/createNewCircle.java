package com.antrixgaming.leap.NewClasses;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class createNewCircle {

    private String messageText;
    private String messageUser;
    private long messageTime;
    private String phoneNumber;

    public createNewCircle(String messageText, String messageUser, String phoneNumber) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.phoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().toString();

        // Initialize to current time
        messageTime = new Date().getTime();
    }

    public createNewCircle(){

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
}
