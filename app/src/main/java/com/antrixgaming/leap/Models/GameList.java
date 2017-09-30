package com.antrixgaming.leap.Models;


import android.support.annotation.RequiresPermission;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class GameList {

    private String gameTitle;
    String addedDate;


    public GameList(String gameTitle) {
        this.gameTitle = gameTitle;


        // Initialize to current time
        String addedDate = String.valueOf(new Date().getTime());
    }

    public GameList(){

    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }

    public void setGameTitle(String gameTitle) {
        this.gameTitle = gameTitle;
    }

    public String getAddedDate() {
        return addedDate;
    }

    public String getGameTitle() {
        return gameTitle;
    }
}
