package com.antrixgaming.leap.NewClasses;


import android.widget.EditText;

import com.antrixgaming.leap.R;
import com.hbb20.CountryCodePicker;

public class userFullPhoneNumber {



    private CountryCodePicker ccp;
    private EditText phoneNumber;
    String phoneNumberAndCode;

    public userFullPhoneNumber(CountryCodePicker ccp, EditText phoneNumber){

        this.ccp = ccp;
        this.phoneNumber = phoneNumber;


       //assign phone number to variable CarrierNumberEditText inside CountryCodePicker class.
        // More info here https://github.com/hbb20/CountryCodePickerProject
        ccp.registerCarrierNumberEditText(phoneNumber);


        // get phone number with country code and plus
        String phoneNumberAndCode = ccp.getFullNumber().toString();
        //////////////////// NEW LINES END HERE //////////////////////////////////


    }


    public String youPhoneNumber() {

        return phoneNumberAndCode;
    }


}
