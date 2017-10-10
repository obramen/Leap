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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.antrixgaming.leap.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;


public class ContactService extends IntentService {

    String countryCode;
    public List<String> currentUsers;
    public List<String> phoneContacts;
    public ArrayList<getPhoneContacts> leapContacts;
    ArrayList<getPhoneContacts> arrayOfContacts;
    ContactsAdapter adapter1;
    List<String> doubleChecker;




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

            Toast.makeText(this, "Service Toasted", Toast.LENGTH_SHORT).show();
            //sortCotact();
            retrieveContact();

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

        public void setmPhoneContactType(String mPhoneContactType) {
            this.mPhoneContactType = mPhoneContactType;
        }

        public void setmContactPhoneNumber(String mContactPhoneNumber) {
            this.mContactPhoneNumber = mContactPhoneNumber;
        }

        public void setmContactName(String mContactName) {
            this.mContactName = mContactName;
        }
    }


    private  void retrieveContact(){

        final String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String myPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        doubleChecker = new ArrayList<>();




        FirebaseDatabase.getInstance().getReference().child("uid").child(myUID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        countryCode = dataSnapshot.child("countrycode").getValue().toString();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });





        // Construct the data source
        arrayOfContacts = new ArrayList<getPhoneContacts>();
        adapter1 = new ContactsAdapter(this, arrayOfContacts);





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

                } else if(Objects.equals(Phone_number, myPhoneNumber)){


                } else {


                    int x = 0;

                    if (doubleChecker.contains(Phone_number)){


                    } else {
                        getPhoneContacts newContact = new getPhoneContacts(name, Phone_number, contactType);
                        doubleChecker.add(Phone_number);
                        //arrayOfContacts.add(newContact);



                        Collections.sort(arrayOfContacts, new Comparator<getPhoneContacts>(){
                            public int compare(getPhoneContacts obj1, getPhoneContacts obj2) {
                                // ## Ascending order




                                return obj1.mContactName.compareToIgnoreCase(obj2.mContactName); // To compare string values
                                // return Integer.valueOf(obj1.empId).compareTo(obj2.empId); // To compare integer values

                                // ## Descending order
                                // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                                // return Integer.valueOf(obj2.empId).compareTo(obj1.empId); // To compare integer values
                            }
                        });

                       //dbRef.child("ContactList").child(myUID).child("phoneSortedContacts").setValue(adapter1);

                        //adapter1.add(newContact);


                        /// querry user database for existence
                        final String finalPhone_number = Phone_number;
                        dbRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot1) {
                                //currentUsers.add(dataSnapshot.getValue().toString());

                                if (dataSnapshot1.child(finalPhone_number).exists()){

                                    // save to firebase
                                    dbRef.child("ContactList").child(myUID).child("leapSortedContacts").child(finalPhone_number).child("contactType").setValue(contactType);
                                    dbRef.child("ContactList").child(myUID).child("leapSortedContacts").child(finalPhone_number).child("name").setValue(name);
                                    dbRef.child("ContactList").child(myUID).child("leapSortedContacts").child(finalPhone_number).child("phoneNumber").setValue(finalPhone_number);


                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });





                    }





                }
                ///////////////////////// SAVING THE PHONE NUMBER TO DATABASE ////////////////////////////
                ///////////////////////// SAVING THE PHONE NUMBER TO DATABASE ////////////////////////////
                ///////////////////////// SAVING THE PHONE NUMBER TO DATABASE ////////////////////////////


            }
            //dbRef.child("phoneSortedContacts").setValue(arrayOfContacts);

            c.close();

            //dbRef.child("ContactList").child(myUID).child("phoneSortedContacts").setValue(arrayOfContacts);

        }
    }


    public static class ContactsAdapter extends ArrayAdapter<getPhoneContacts> {
        public ContactsAdapter(Context context, ArrayList<getPhoneContacts> contacts) {
            super(context, 0, contacts);
        }



        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position

            return convertView;
        }


    }

    private void sortCotact(){

        retrieveContact();

        final String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String myPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        //final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("ContactList").child(myUID);



        currentUsers = new ArrayList<String>();
        phoneContacts = new ArrayList<String>();
        leapContacts = new ArrayList<>();





        dbRef.child("ContactList").child(myUID).child("phoneSortedContacts")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                //phoneContacts.add(dataSnapshot.getValue().toString());


                //long count = dataSnapshot.getChildrenCount();


                for (long x = 0; x < dataSnapshot.getChildrenCount(); x++){

                    final String mContactPhoneNumber = dataSnapshot.child(String.valueOf(x)).child("mContactPhoneNumber").getValue().toString();






                    /// querry user database for existence
                    final long finalX = x;
                    dbRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot1) {
                            //currentUsers.add(dataSnapshot.getValue().toString());

                            if (dataSnapshot1.child(mContactPhoneNumber).exists()){

                                //currentUsers.add(mContactPhoneNumber);

                                // if it exists, get other values to go with it
                                final String mContactName = dataSnapshot.child(String.valueOf(finalX)).child("mContactName").getValue().toString();
                                final String mPhoneContactType = dataSnapshot.child(String.valueOf(finalX)).child("mPhoneContactType").getValue().toString();


                                // create new contact
                                //getPhoneContacts newContact = new getPhoneContacts(mContactName, mContactPhoneNumber, mPhoneContactType);
                                //leapContacts.add(newContact);


                                // save to firebase
                                dbRef.child("ContactList").child(myUID).child("leapSortedContacts").child(mContactPhoneNumber).child("contactType").setValue(mPhoneContactType);
                                dbRef.child("ContactList").child(myUID).child("leapSortedContacts").child(mContactPhoneNumber).child("name").setValue(mContactName);
                                dbRef.child("ContactList").child(myUID).child("leapSortedContacts").child(mContactPhoneNumber).child("phoneNumber").setValue(mContactPhoneNumber);




                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        }

    }


