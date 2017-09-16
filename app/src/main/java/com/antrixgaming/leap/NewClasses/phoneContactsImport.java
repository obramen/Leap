package com.antrixgaming.leap.NewClasses;

import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class phoneContactsImport extends AppCompatActivity {

    public phoneContactsImport(){


    }


    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    //private ArrayList<String> ContactList;

    private String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("phonecontacts").child(myUID);


    public String mContactName;
    public String mContactPhoneNumber;
    public String mPhoneContactType;
    public String countryCode = "";


    private void gettingPhoneContacts() {

        FirebaseDatabase.getInstance().getReference().child("uid").child(FirebaseAuth.getInstance()
                .getCurrentUser().getUid()).child("countrycode").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                countryCode = dataSnapshot.getValue().toString();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        // Construct the data source
        ArrayList<getPhoneContacts> arrayOfContacts = new ArrayList<getPhoneContacts>();

        // Create the adapter to convert the array to views

        ContentResolver cr = getContentResolver();
        // Read Contacts
        Cursor c = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.Contacts._ID,
                        ContactsContract.Contacts.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.RawContacts.ACCOUNT_TYPE,ContactsContract.CommonDataKinds.Phone.TYPE},
                ContactsContract.RawContacts.ACCOUNT_TYPE + " <> 'google' ", null, null);
        if (c.getCount() <= 0) {
            Toast.makeText(this, "No Phone Contact Found..!",
                    Toast.LENGTH_SHORT).show();
        } else {
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



                // TODO put error checker here
                // remove symbols and non digit variables from number
                Phone_number = Phone_number.replaceAll("[^0-9]", "");

                // if number begins with "0", remove it (this shows it's number saved without the country code.
                // Eg. 02423666623 / 0540853936
                int firstDigit = Integer.parseInt(Phone_number.substring(0, 1));
                int secondDigit = Integer.parseInt(Phone_number.substring(0, 2));
                //int thirdDigit = Integer.parseInt(Phone_number.substring(0, 3));
                if ((firstDigit == 0)&&(secondDigit != 00)){
                    Phone_number = Phone_number.substring(1);

                } else if ((firstDigit == 0)&&(secondDigit == 00)) {
                    Phone_number = Phone_number.substring(1);
                    Phone_number = Phone_number.substring(1);

                } else{

                }




                if (Phone_number.length() < 8){
                    //skip short numbers that are possibly not phone numbers
                } else if (Phone_number.length() > 14){
                    // skip potentially too long numbers
                } else if ((Phone_number.length() == 8) || (Phone_number.length() == 9)){



                    Phone_number = countryCode + Phone_number;
                    // Add items to array
                    getPhoneContacts newContact = new getPhoneContacts(name, Phone_number, contactType);

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


                    if (arrayOfContacts.contains(newContact)){


                    }
                    else{
                        arrayOfContacts.add(newContact);
                        //dbRef.child("contactlist").child(U
                        // ID).child(Phone_number).setValue(newContact);

                    }






                }else if((Phone_number.length() == 11) && (Integer.parseInt(Phone_number.substring(0, 1)) == 1)) {
                    Phone_number = "+" + Phone_number;

                    getPhoneContacts newContact = new getPhoneContacts(name, Phone_number, contactType);

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



                    if (arrayOfContacts.contains(newContact)){


                    }
                    else{
                        arrayOfContacts.add(newContact);
                        //dbRef.child("contactlist").child(U
                        // ID).child(Phone_number).setValue(newContact);

                    }

                }else if((Phone_number.length() == 11)&& (Integer.parseInt(Phone_number.substring(0, 1)) == 1) &&
                        (Integer.parseInt(Phone_number.substring(0, 2)) == 10)){
                    Phone_number = Phone_number.substring(1);
                    Phone_number = Phone_number.substring(1);
                    Phone_number = "+1" + Phone_number;

                    getPhoneContacts newContact = new getPhoneContacts(name, Phone_number, contactType);

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

                    if (arrayOfContacts.contains(newContact)){


                    }
                    else{
                        arrayOfContacts.add(newContact);
                        //dbRef.child("contactlist").child(U
                        // ID).child(Phone_number).setValue(newContact);

                    }                }

                else if((Phone_number.length() > 9) && (Phone_number.length() < 14)  && (Phone_number.length() != 11) ) {

                    int CCDGH = Integer.parseInt(Phone_number.substring(0, 3));
                    if (Objects.equals("+" + CCDGH, countryCode))
                    {
                        Phone_number = "+" + Phone_number;

                    } else {

                    }


                    getPhoneContacts newContact = new getPhoneContacts(name, Phone_number, contactType);

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



                    if (arrayOfContacts.contains(newContact)){


                    }
                    else{
                        arrayOfContacts.add(newContact);
                        //dbRef.child("contactlist").child(U
                        // ID).child(Phone_number).setValue(newContact);

                    }
                    //dbRef.child("contactlist").child(UID).child(Phone_number).setValue(newContact);
                }


         }






        }
        c.close();

        dbRef.setValue(arrayOfContacts);

    }





    private void getContactRetrievalPermission(){

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_CONTACTS);

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this, "permission not available, request", Toast.LENGTH_SHORT).show();


            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.READ_CONTACTS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                Toast.makeText(this, "permission request starting", Toast.LENGTH_SHORT).show();


                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        else {

            Toast.makeText(this, "contactt permission already available", Toast.LENGTH_SHORT).show();

            gettingPhoneContacts();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show();

                    gettingPhoneContacts();


                } else {

                    Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();


                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

}
