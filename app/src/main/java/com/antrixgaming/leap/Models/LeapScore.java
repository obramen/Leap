package com.antrixgaming.leap.Models;

import android.support.annotation.Nullable;

import java.util.Date;


public class LeapScore {

    private String leapID;
    private String leaperOneScore;
    private String leaperTwoScore;
    private String scoreStatus;
    private String entryBy;
    private String modifiedBy;
    private long lastModifiedTime;
    private long firstEntryTime;
    String winner;
    String looser;


    public LeapScore(String leapID, String leaperOneScore, String leaperTwoScore,
                     String scoreStatus, String entryBy, String winner, String looser) {
        this.leapID = leapID;
        this.leaperOneScore = leaperOneScore;
        this.leaperTwoScore = leaperTwoScore;
        this.scoreStatus = scoreStatus;
        this.entryBy = entryBy;
        this.winner = winner;
        this.looser = looser;
        this.modifiedBy = modifiedBy;
        this.lastModifiedTime = lastModifiedTime;

        // Initialize to current time
        firstEntryTime = new Date().getTime();
    }


    public LeapScore(String leapID, String leaperOneScore, String leaperTwoScore,
                     String scoreStatus, String entryBy, String winner, String looser, @Nullable String modifiedBy, @Nullable long lastModifiedTime) {
        this.leapID = leapID;
        this.leaperOneScore = leaperOneScore;
        this.leaperTwoScore = leaperTwoScore;
        this.scoreStatus = scoreStatus;
        this.entryBy = entryBy;
        this.modifiedBy = modifiedBy;
        this.lastModifiedTime = lastModifiedTime;
        this.winner = winner;
        this.looser = looser;

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

    public void setLeaperOneScore(String leaperOneScore) {
        this.leaperOneScore = leaperOneScore;
    }

    public void setLeaperTwoScore(String leaperTwoScore) {
        this.leaperTwoScore = leaperTwoScore;
    }

    public void setleapID(String leapID) {
        this.leapID = leapID;
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

    public String getLeaperOneScore() {
        return leaperOneScore;
    }

    public String getLeaperTwoScore() {
        return leaperTwoScore;
    }

    public String getleapID() {
        return leapID;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public String getScoreStatus() {
        return scoreStatus;
    }

    public String getWinner() {
        return winner;
    }

    public String getLooser() {
        return looser;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public void setLooser(String looser) {
        this.looser = looser;
    }
}

