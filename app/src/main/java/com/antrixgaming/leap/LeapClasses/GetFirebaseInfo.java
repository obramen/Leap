package com.antrixgaming.leap.LeapClasses;


import android.content.Context;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class GetFirebaseInfo {

    //DatabaseReference dbRef;
    //String myPhoneNumber;
    //String myUID;
    String PhoneNumber;
    Context context;
    TextView textView;



    public GetFirebaseInfo(){

    }

    public  void GetLeaperName(final String PhoneNumber, final TextView textView){

        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        String myPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        this.PhoneNumber = PhoneNumber;
        this.textView = textView;


        /////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////
        //////// GETTING AND SETTING NAMES IN PLACE OF PHONE NUMBER

        ///////////////////////////////////////
        //////////////////   STARTING    ///////////////////////////////




        //// CHECK MY CONTACT LIST IF THIS PERSON IS A CONTACT
        dbRef.child("ContactList").child(myUID).child("leapSortedContacts").child(PhoneNumber)
                .addListenerForSingleValueEvent(new ValueEventListener() {////////
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.child("name").getValue() == null || dataSnapshot.child("name")
                                .getValue() == "") {///// IF THEY ARE NOT A CONTACT OR THE VALUE IS EMPTY



                            ///// CHECK THE USERS PROFILES TO SEE IF THEY HAVE AN ENTRY THERE
                            dbRef.child("userprofiles").child(PhoneNumber).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.child("name").getValue() == null || Objects.equals(dataSnapshot.child("name")
                                            .getValue().toString(), "")){///IF THEY DON'T HAVE AN ENTRY USE THEIR PHONE NUMBER

                                        /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////
                                        textView.setText(PhoneNumber);
                                        /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////


                                    } else { //// IF THEY HAVE AN ENTRY USE THEIR ENTERED NAME

                                        String myName = dataSnapshot.child("name").getValue().toString();

                                        /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////
                                        textView.setText("~ " + myName);
                                        /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////s


                                    }




                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });







                        } else {/// IF THEY ARE A CONTACT USE THE SAVED NAME


                            String mName = dataSnapshot.child("name").getValue().toString();

                            /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////
                            textView.setText(mName);
                            /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////


                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });







        //////////////////   ENDING    ///////////////////////////////
        ///////////////////////////////////////

        //////// GETTING AND SETTING NAMES IN PLACE OF PHONE NUMBER
        ///////////////////////////////////////////
        //////////////////////////////////////////////////////////////////


    }


    public void GetCircleName(String CircleID, final TextView textView2){
        FirebaseDatabase.getInstance().getReference().child("groupcirclenames").child(CircleID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        String circleName = dataSnapshot.child("circleName").getValue().toString();

                        textView2.setText(circleName);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



    }




}


