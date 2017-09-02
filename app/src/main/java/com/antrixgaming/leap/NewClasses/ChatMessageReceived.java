package com.antrixgaming.leap.NewClasses;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class ChatMessageReceived {

    private String messageText;
    private String messageUser;
    private long messageTime;
    private String senderPhoneNumber;
    private String senderuid;
    private String onecircleid;


    public ChatMessageReceived(String messageText, String onecircleid, String senderPhoneNumber, String senderuid) {
        this.senderuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.messageText = messageText;
        //this.messageUser = FirebaseDatabase.getInstance().getReference().child("uid").child(uid).child("name").toString();
        this.onecircleid = onecircleid;
        this.senderPhoneNumber = senderPhoneNumber;

        // Initialize to current time
        messageTime = new Date().getTime();
    }

    public ChatMessageReceived(){

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

    public String getSenderPhoneNumber(){return senderPhoneNumber;}

    public void setSenderPhoneNumber(String phoneNumber){this.senderPhoneNumber = phoneNumber;}

    public String getSenderUid(){return senderuid;}

    public void setSenderUid  (String uid) {this.senderuid = uid;}

    public String getOnecircleid(){return onecircleid;}

    public void setOnecircleid(String onecircleid){this.onecircleid = onecircleid;}


}
