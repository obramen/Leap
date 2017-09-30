package com.antrixgaming.leap.LeapServices;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;


public class ContactService extends IntentService {

    String countryCode;

    public ContactService() {
        super("CONTACT_SYNC_SERVICE");
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Toast.makeText(this, "Contacts Sync Service Starting", Toast.LENGTH_LONG).show();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        Toast.makeText(this, "Contacts Sync Service Stopped", Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {



        synchronized (this) {


            sortCotact();
        }



    }

    class getPhoneContacts {

        public String mContactName;
        public String mContactPhoneNumber;
        public String mPhoneContactType;

        public getPhoneContacts(String mContactName, String mContactPhoneNumber, String mPhoneContactType) {

            this.mContactName = mContactName;
            this.mContactPhoneNumber = mContactPhoneNumber;
            this.mPhoneContactType = mPhoneContactType;

        }


    }






    private void sortCotact(){


        FirebaseDatabase.getInstance().getReference().child("uid").child(FirebaseAuth.getInstance()
                .getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                countryCode = dataSnapshot.child("countrycode").getValue().toString();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });






        String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference rootdbRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("ContactList").child(myUID);


        // Construct the data source
        ArrayList<getPhoneContacts> arrayOfContacts = new ArrayList<getPhoneContacts>();

        // Create the adapter to convert the array to views

        ContentResolver cr = getContentResolver();


        // Read Contacts
        Cursor c = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.Contacts._ID,
                        ContactsContract.Contacts.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.RawContacts.ACCOUNT_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE},
                ContactsContract.RawContacts.ACCOUNT_TYPE + " <> 'google' ", null, null);
        if(c.getCount()<=0)

        {

        } else

        {
            while (c.moveToNext()) {


                String Phone_number = c
                        .getString(c
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)); //Phone number
                final String name = c
                        .getString(c
                                .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)); //Name of contact
                final String contactType = c
                        .getString(c
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));


                // SAVE THE RAW STATE OF THE PHONE NUMBER
                final String rawPhoneNumber =  Phone_number;

                /////////////////////////////////////////////////////////////////////////////////////////
                /////////////////////////////////////////////////////////////////////////////////////////
                /////////////////////////////////////////////////////////////////////////////////////////
                /////////////////////////////////////////////////////////////////////////////////////////
                ///////////////////// FORMATTING PHONE NUMBER BEGINS HERE ///////////////////////////////

                /// STRINGS FOR CHECKING
                String noPlus = null;
                String noZeroZero = null;
                String goodLenth = null;





                // CHECK IF NUMBER BEGINS WITH "+"
                String plusCheck = Phone_number.substring(0, 1);
                if (Objects.equals(plusCheck, "+")){
                    //PROCEED TO SAVE AS IT IS
                    Phone_number = Phone_number.replaceAll("[^0-9]", "");
                    Phone_number = "+" + Phone_number;
                } else {
                    // used to proceed to next check
                    noPlus = Phone_number;
                }


                if (noPlus == null){

                }else {
                    // remove symbols and non digit variables from number
                    noPlus = noPlus.replaceAll("[^0-9]", "");


                    // CHECK IF NUMBER BEGINS WITH 00 and followed by another number
                    String twoZerosCheck = noPlus.substring(0, 2);
                    if (Objects.equals(twoZerosCheck, "00")){
                        String ZeroPhoneNumber = noPlus.substring(2);
                        String thirdZero = ZeroPhoneNumber.substring(0,1);
                        if (Objects.equals(thirdZero, "0")){
                            // used to proceed to next check
                            noZeroZero = Phone_number;
                        } else{
                            noPlus = noPlus.substring(2);
                            noPlus = "+" + noPlus;
                            Phone_number = noPlus;
                        }

                    }else {
                        // used to proceed to next check
                        noZeroZero = noPlus;
                    }
                }









                // MAKE EXCEPTIONS FOR SAINT HALENA & NIUE
                //if ((Objects.equals(countryCode, "+290")) || (Objects.equals(countryCode, "+683"))){

                // PHONE NUMBER LENGTH 4
                //PUT PROCEED HERE

                //}

                // MAKE EXCEPTION FOR AUSTRIA
                //if (Objects.equals(countryCode, "+43")){

                // PHONE NUMBER LENGTH 13
                // PUT PROCEED HERE



                //}


                if (noZeroZero == null){

                } else {
                    int numberLength = noZeroZero.length();

                    // ELIMINATE TOO SHORT AND TOO LONG NUMBERS
                    if (numberLength < 8 || numberLength > 15){
                        // do nothing. number too short or too long
                        Phone_number = null;
                    } else {
                        // CHECK FOR COUNTRY CODE AVAILABILITY
                        String firstDigit = noZeroZero.substring(0, 1);
                        String secondDigit = noZeroZero.substring(0, 2);
                        String thirdDigit = noZeroZero.substring(0, 3);
                        if ((Objects.equals(firstDigit, countryCode)) || (Objects.equals(secondDigit, countryCode)) || (Objects.equals(thirdDigit, countryCode))) {
                            Phone_number = "+" + Phone_number;
                        } else // check for locally saved number without zero
                            if (numberLength > 10){
                                Phone_number = "+" + noZeroZero;
                            }else if ((Objects.equals(firstDigit, "0")) && (!Objects.equals(secondDigit, "00"))) {
                                noZeroZero = noZeroZero.substring(1);
                                Phone_number = countryCode + noZeroZero;
                            } else {
                                Phone_number = null;
                            }
                    }

                }


                ///////////////////// FORMATTING PHONE NUMBER BEGINS HERE ///////////////////////////////
                /////////////////////////////////////////////////////////////////////////////////////////
                /////////////////////////////////////////////////////////////////////////////////////////
                /////////////////////////////////////////////////////////////////////////////////////////
                /////////////////////////////////////////////////////////////////////////////////////////



                ///////////////////////// SAVING THE PHONE NUMBER TO DATABASE ////////////////////////////
                ///////////////////////// SAVING THE PHONE NUMBER TO DATABASE ////////////////////////////
                ///////////////////////// SAVING THE PHONE NUMBER TO DATABASE ////////////////////////////

                if (Phone_number == null){

                } else {

                    // save to sorted phone contact list table
                    getPhoneContacts newContact = new getPhoneContacts(name, Phone_number, contactType);
                    arrayOfContacts.add(newContact);
                    // check for availability to avoid double saving
                    DatabaseReference users = rootdbRef.child("users");
                    final String finalPhone_number1 = Phone_number;
                    users.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.child(finalPhone_number1).exists()) {

                            } else {
                                dbRef.child("phoneSortedContacts").child(rawPhoneNumber).child("name").setValue(name);
                                dbRef.child("phoneSortedContacts").child(rawPhoneNumber).child("phoneNumber").setValue(finalPhone_number1);
                                dbRef.child("phoneSortedContacts").child(rawPhoneNumber).child("contactType").setValue(contactType);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                    // save to sorted leap contacts table. this is used to populate friend list / contacts
                    final String finalPhone_number = Phone_number;
                    users.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.child(finalPhone_number).exists()) {
                                dbRef.child("leapSortedContacts").child(finalPhone_number).child("name").setValue(name);
                                dbRef.child("leapSortedContacts").child(finalPhone_number).child("phoneNumber").setValue(finalPhone_number);
                                dbRef.child("leapSortedContacts").child(finalPhone_number).child("contactType").setValue(contactType);
                            } else {

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
                ///////////////////////// SAVING THE PHONE NUMBER TO DATABASE ////////////////////////////
                ///////////////////////// SAVING THE PHONE NUMBER TO DATABASE ////////////////////////////
                ///////////////////////// SAVING THE PHONE NUMBER TO DATABASE ////////////////////////////


            }

            c.close();

            //dbRef.setValue(arrayOfContacts);

        }


    }

}
