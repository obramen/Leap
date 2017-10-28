package com.antrixgaming.leap.Models;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class createGroupCircle {

    private String createdBy;
    private String groupName;
    private String groupid;
    private String publicGroup;
    private long createdOn;
    private String defaultGame;


    public createGroupCircle(String createdBy, String groupName, String groupid) {
        this.createdBy = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.groupName = groupName;
        this.groupid = groupid;

        // Initialize to current time
        createdOn = new Date().getTime();
    }

    public createGroupCircle(String createdBy, String groupName, String groupid, String publicGroup, String defaultGame) {
        this.createdBy = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.groupName = groupName;
        this.groupid = groupid;
        this.publicGroup = publicGroup;
        this.defaultGame = defaultGame;

        // Initialize to current time
        createdOn = new Date().getTime();
    }

    public createGroupCircle(){

    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupid() {
        return groupid;
    }

    public void seGroupid(String groupid) {
        this.groupid = groupid;
    }

    public long getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(long createdOn) {
        this.createdOn = createdOn;
    }

    public String getPublicGroup() {
        return publicGroup;
    }

    public void setPublicGroup(String publicGroup) {
        this.publicGroup = publicGroup;
    }

    public String getDefaultGame() {
        return defaultGame;
    }

    public void setDefaultGame(String defaultGame) {
        this.defaultGame = defaultGame;
    }
}
