package com.antrixgaming.leap.Models;

import java.util.Date;


public class LeapScore {

    private String matchID;
    private String leaperOne;
    private String leaperTwo;
    private String leaperOneScore;
    private String leaperTwoScore;
    private String scoreStatus;
    private String entryBy;
    private String modifiedBy;
    private long lastModifiedTime;
    private long firstEntryTime;



    public LeapScore(String matchID, String leaperOne, String leaperTwo, String leaperOneScore, String leaperTwoScore,
                     String scoreStatus, String entryBy, String modifiedBy, long lastModifiedTime) {
        this.matchID = matchID;
        this.leaperOne = leaperOne;
        this.leaperTwo = leaperTwo;
        this.leaperOneScore = leaperOneScore;
        this.leaperTwoScore = leaperTwoScore;
        this.scoreStatus = scoreStatus;
        this.entryBy = entryBy;
        this.modifiedBy = modifiedBy;
        this.lastModifiedTime = lastModifiedTime;

        // Initialize to current time
        firstEntryTime = new Date().getTime();
    }

    public LeapScore(){

    }

    public void setEntryBy(String entryBy) {
        this.entryBy = entryBy;
    }

    public void setFirstEntryTime(long firstEntryTime) {
        this.firstEntryTime = firstEntryTime;
    }

    public void setLastModifiedTime(long lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public void setLeaperOne(String leaperOne) {
        this.leaperOne = leaperOne;
    }

    public void setLeaperOneScore(String leaperOneScore) {
        this.leaperOneScore = leaperOneScore;
    }

    public void setLeaperTwo(String leaperTwo) {
        this.leaperTwo = leaperTwo;
    }

    public void setLeaperTwoScore(String leaperTwoScore) {
        this.leaperTwoScore = leaperTwoScore;
    }

    public void setMatchID(String matchID) {
        this.matchID = matchID;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public void setScoreStatus(String scoreStatus) {
        this.scoreStatus = scoreStatus;
    }

    public long getFirstEntryTime() {
        return firstEntryTime;
    }

    public long getLastModifiedTime() {
        return lastModifiedTime;
    }

    public String getEntryBy() {
        return entryBy;
    }

    public String getLeaperOne() {
        return leaperOne;
    }

    public String getLeaperOneScore() {
        return leaperOneScore;
    }

    public String getLeaperTwo() {
        return leaperTwo;
    }

    public String getLeaperTwoScore() {
        return leaperTwoScore;
    }

    public String getMatchID() {
        return matchID;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public String getScoreStatus() {
        return scoreStatus;
    }
}

