package com.antrixgaming.leap.Models;


import android.support.annotation.RequiresPermission;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class ChatMessage {

    private String messageText;
    private String messageUser;
    private long messageTime;
    private String senderPhoneNumber;
    private String senderuid;
    private String onecircleid;
    private String receiverPhoneNumber;
    private String loadname;
    private String ReadFlag;



    public ChatMessage(String messageText, String onecircleid, String senderPhoneNumber,
                       String receiverPhoneNumber, String senderuid, String loadname, String ReadFlag) {
        this.senderuid = senderuid;
        this.messageText = messageText;
        //this.messageUser = FirebaseDatabase.getInstance().getReference().child("uid").child(uid).child("name").toString();
        this.onecircleid = onecircleid;
        this.senderPhoneNumber = senderPhoneNumber;
        this.receiverPhoneNumber = receiverPhoneNumber;
        this.loadname = loadname;
        this.ReadFlag = ReadFlag;

        // Initialize to current time
        messageTime = new Date().getTime();
    }

    public ChatMessage(){

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

    public void setsenderPhoneNumber(String senderPhoneNumber){this.senderPhoneNumber = senderPhoneNumber;}

    public String getReceiverPhoneNumber(){return receiverPhoneNumber;}

    public void setReceiverPhoneNumber(String receiverPhoneNumber){this.receiverPhoneNumber = receiverPhoneNumber;}

    public String getSenderUid(){return senderuid;}

    public void setSenderUid  (String uid) {this.senderuid = uid;}

    public String getOnecircleid(){return onecircleid;}

    public void setOnecircleid(String onecircleid){this.onecircleid = onecircleid;}

    public String getloadname(){return loadname;}

    public void setLoadname(String loadname){this.loadname = loadname;}

    public void setReadFlag(String readFlag) {
        ReadFlag = readFlag;
    }

    public String getReadFlag() {
        return ReadFlag;
    }

}
