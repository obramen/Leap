package com.antrixgaming.leap.LeapClasses;


import java.util.Date;

public class FavoriteLeaper {

    String leaperName;
    long index;
    long addTime;

    public FavoriteLeaper(){

    }

    public FavoriteLeaper(String leaperName){

        this.leaperName = leaperName;

        addTime = new Date().getTime();
        index = -1 * addTime;

    }

    public long getIndex() {
        return index;
    }

    public long getAddTime() {
        return addTime;
    }

    public String getLeaperName() {
        return leaperName;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    public void setLeaperName(String leaperName) {
        this.leaperName = leaperName;
    }
}
