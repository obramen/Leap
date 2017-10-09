package com.antrixgaming.leap.Models;


import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class UserLeap {

    private String gameType;
    public String gameFormat;
    public String leaperOneScore;
    public String leaperOne;
    public String leaperTwoScore;
    public String leaperTwo;
    private long leapSetupTime;
    private String leapDay;
    private String leapTime;
    public String leapStatus;
    private String leapID;
    private long leapStatusChangeTime;
    private String circleID;



    public UserLeap(String leapID, String gameType, String gameFormat, String leaperOne, String leaperTwo, String leapDay, String leapTime, String leapStatus,
                    @Nullable String circleID) {
        this.leapID = leapID;
        this.gameType = gameType;
        this.gameFormat = gameFormat;
        this.leaperOne = leaperOne;
        this.leaperTwo = leaperTwo;
        this.leapDay = leapDay;
        this.leapTime = leapTime;
        this.leapStatus = leapStatus;
        this.circleID = circleID;

        // Initialize to current time
        leapSetupTime = new Date().getTime();

        leapStatusChangeTime = new Date().getTime();

    }

    public UserLeap(String leapID, String gameType, String gameFormat, String leaperOne, String leaperTwo, String leapDay, String leapTime, String leapStatus,
                    @Nullable String circleID, @Nullable String leaperOneScore, @Nullable String leaperTwoScore) {
        this.leapID = leapID;
        this.gameType = gameType;
        this.gameFormat = gameFormat;
        this.leaperOne = leaperOne;
        this.leaperOneScore = leaperOneScore;
        this.leaperTwo = leaperTwo;
        this.leaperTwoScore = leaperTwoScore;
        this.leapDay = leapDay;
        this.leapTime = leapTime;
        this.leapStatus = leapStatus;
        this.circleID = circleID;

        // Initialize to current time
        leapSetupTime = new Date().getTime();

        leapStatusChangeTime = new Date().getTime();

    }

    public UserLeap(){

    }

    public String getleapID() {
        return leapID;
    }
    public void setleapID(String leapID) {this.leapID = leapID;}


    public String getgameType() {return gameType;}
    public void setgameType(String gameType) {this.gameType = gameType;}


    public String getgameFormat() {
        return gameFormat;
    }
    public void setgameFormat(String gameFormat) {
        this.gameFormat = gameFormat;
    }


    public String getleaperOne() {
        return leaperOne;
    }
    public void setleaperOne(String leaperOne) {
        this.leaperOne = leaperOne;
    }


    public String getleaperTwo(){return leaperTwo;}
    public void setleaperTwo(String leaperTwo){this.leaperTwo = leaperTwo;}


    public Long getleapSetupTime(){return leapSetupTime;}
    public void setleapSetupTime(Long leapSetupTime){this.leapSetupTime = leapSetupTime;}


    public String getleapDay(){return leapDay;}
    public void setleapDay  (String leapDay) {this.leapDay = leapDay;}


    public String getleapTime(){return leapTime;}
    public void setleapTime(String leapTime){this.leapTime = leapTime;}

    public String getleapStatus(){return leapStatus;}
    public void setleapStatus(String leapStatus){this.leapStatus = leapStatus;}

    public long getleapStatusChangeTime(){return leapStatusChangeTime;}
    public void setleapStatusChangeTime(long leapStatusChangeTime){this.leapStatusChangeTime = leapStatusChangeTime;}

    public void setCircleID(String circleID) {
        this.circleID = circleID;
    }

    public String getCircleID() {
        return circleID;
    }

    public String getLeaperTwoScore() {
        return leaperTwoScore;
    }

    public String getLeaperOneScore() {
        return leaperOneScore;
    }

    public void setLeaperOneScore(String leaperOneScore) {
        this.leaperOneScore = leaperOneScore;
    }

    public void setLeaperTwoScore(String leaperTwoScore) {
        this.leaperTwoScore = leaperTwoScore;
    }
}
