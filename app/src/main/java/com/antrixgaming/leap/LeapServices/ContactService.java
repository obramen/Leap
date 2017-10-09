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
    public ArrayList<String> currentUsers;
    public ArrayList<String> phoneContacts;
    public ArrayList<String> leapContacts;

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

        String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String myPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        DatabaseReference rootdbRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("ContactList").child(myUID);



        currentUsers = new ArrayList<String>();
        phoneContacts = new ArrayList<String>();
        leapContacts = new ArrayList<String>();


        FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentUsers.add(dataSnapshot.getChildren().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        FirebaseDatabase.getInstance().getReference().child("ContactList").child(myUID).child("phoneSortedContacts")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                phoneContacts.add(dataSnapshot.getChildren().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        /*
        // Create the adapter to convert the array to views
             // check for availability to avoid double saving
                    DatabaseReference users = rootdbRef.child("users");

                    users.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            if (snapshot.child(finalPhone_number1).exists()) {
                                dbRef.child("leapSortedContacts").child(finalPhone_number1).child("name").setValue(name);
                                dbRef.child("leapSortedContacts").child(finalPhone_number1).child("phoneNumber").setValue(finalPhone_number1);
                                dbRef.child("leapSortedContacts").child(finalPhone_number1).child("contactType").setValue(contactType);
                            } else {

                                //checkerForContacts.add(newContact);
                                //dbRef.child("phoneSortedContacts").child(rawPhoneNumber).child("name").setValue(name);
                                //dbRef.child("phoneSortedContacts").child(rawPhoneNumber).child("phoneNumber").setValue(finalPhone_number1);
                                //dbRef.child("phoneSortedContacts").child(rawPhoneNumber).child("contactType").setValue(contactType);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
            //dbRef.setValue(arrayOfContacts);

         */
        }


    }


