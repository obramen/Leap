package com.antrixgaming.leap.Models;

import java.util.Date;



public class CircleMember {

    private String phoneNumber;
    private String Admin;
    private long JoinDate;
    private String LeapStatus;
    private String MemberStatus;

    public CircleMember(String phoneNumber, String Admin, String LeapStatus) {
        this.phoneNumber = phoneNumber;
        this.Admin = Admin;
        this.LeapStatus = LeapStatus;
        this.MemberStatus = MemberStatus;

        // Initialize to current time
        JoinDate = new Date().getTime();
    }

    public CircleMember(String phoneNumber, String Admin, String LeapStatus, String MemberStatus) {
        this.phoneNumber = phoneNumber;
        this.Admin = Admin;
        this.LeapStatus = LeapStatus;
        this.MemberStatus = MemberStatus;

        // Initialize to current time
        JoinDate = new Date().getTime();
    }

    public CircleMember() {

    }

    public void setAdmin(String admin) {
        Admin = admin;
    }

    public void setJoinDate(long joinDate) {
        JoinDate = joinDate;
    }

    public void setLeapStatus(String leapStatus) {
        LeapStatus = leapStatus;
    }

    public void setMemberStatus(String memberStatus) {
        MemberStatus = memberStatus;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public long getJoinDate() {
        return JoinDate;
    }

    public String getAdmin() {
        return Admin;
    }

    public String getLeapStatus() {
        return LeapStatus;
    }

    public String getMemberStatus() {
        return MemberStatus;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

}






