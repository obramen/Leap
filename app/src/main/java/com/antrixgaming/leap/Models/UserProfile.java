package com.antrixgaming.leap.Models;

import java.util.Date;



public class UserProfile {


    private String phoneNumber;
    private String name;
    private String taunt;
    private String gender;
    private String psn;
    private String xboxlive;
    private String steam;
    private String origin;
    private String dateofbirth;



    private String genderPrivacy;
    private String profilePrivacy;

    public UserProfile( String phoneNumber, String name,
                       String taunt, String gender, String dateofbirth,
                       String genderPrivacy, String profilePrivacy) {


        this.phoneNumber = phoneNumber;
        this.name = name;
        this.taunt = taunt;
        this.gender = gender;
        this.dateofbirth = dateofbirth;
        this.genderPrivacy = genderPrivacy;
        this.profilePrivacy = profilePrivacy;
    }


    public UserProfile(String phoneNumber, String name,
                       String taunt, String gender, String dateofbirth,
                       String genderPrivacy, String profilePrivacy,
                       String psn, String xboxlive, String steam, String origin) {


        this.phoneNumber = phoneNumber;
        this.name = name;
        this.taunt = taunt;
        this.gender = gender;
        this.psn = psn;
        this.xboxlive = xboxlive;
        this.steam = steam;
        this.origin = origin;
        this.dateofbirth = dateofbirth;
        this.genderPrivacy = genderPrivacy;
        this.profilePrivacy = profilePrivacy;
    }

    public UserProfile() {

    }


    public String getDateofbirth() {
        return dateofbirth;
    }

    public String getGender() {
        return gender;
    }

    public String getGenderPrivacy() {
        return genderPrivacy;
    }

    public String getName() {
        return name;
    }

    public String getOrigin() {
        return origin;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPsn() {
        return psn;
    }

    public String getProfilePrivacy() {
        return profilePrivacy;
    }

    public String getTaunt() {
        return taunt;
    }


    public String getSteam() {
        return steam;
    }

    public String getXboxlive() {
        return xboxlive;
    }

    public void setDateofbirth(String dateofbirth) {
        this.dateofbirth = dateofbirth;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setGenderPrivacy(String genderPrivacy) {
        this.genderPrivacy = genderPrivacy;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setProfilePrivacy(String profilePrivacy) {
        this.profilePrivacy = profilePrivacy;
    }

    public void setPsn(String psn) {
        this.psn = psn;
    }

    public void setSteam(String steam) {
        this.steam = steam;
    }

    public void setTaunt(String taunt) {
        this.taunt = taunt;
    }


    public void setXboxlive(String xboxlive) {
        this.xboxlive = xboxlive;
    }

}
