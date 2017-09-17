package com.antrixgaming.leap.LeapClasses;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class savePhoneContacts {


    public String name;
    public String phoneNumber;
    public String contactType;



    public savePhoneContacts(String contactType, String name, String phoneNumber) {
        this.contactType = contactType;
        this.name = name;
        this.phoneNumber = phoneNumber;


    }


    public savePhoneContacts(){

    }


    public String getcontactName() {
        return name;
    }
    public void setcontactName(String name) {
        this.name = name;
    }


    public String getcontactPhoneNumber() {
        return phoneNumber;
    }
    public void setcontactPhoneNumber(String phoneNumber) {this.phoneNumber = phoneNumber;}


    public String getcontactType() {
        return contactType;
    }
    public void setcontactType(String contactType) {
        this.contactType = contactType;
    }
}
