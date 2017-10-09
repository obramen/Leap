package com.antrixgaming.leap.Models;

import android.widget.ArrayAdapter;

public class getPhoneContacts {

    public String mContactName;
    public String mContactPhoneNumber;
    public String mPhoneContactType;

    public getPhoneContacts(String mContactName, String mContactPhoneNumber, String mPhoneContactType) {

        this.mContactName = mContactName;
        this.mContactPhoneNumber = mContactPhoneNumber;
        this.mPhoneContactType = mPhoneContactType;

    }


    public String getmContactName() {
        return mContactName;
    }

    public String getmContactPhoneNumber() {
        return mContactPhoneNumber;
    }

    public String getmPhoneContactType() {
        return mPhoneContactType;
    }

    public void setmContactName(String mContactName) {
        this.mContactName = mContactName;
    }

    public void setmContactPhoneNumber(String mContactPhoneNumber) {
        this.mContactPhoneNumber = mContactPhoneNumber;
    }


}