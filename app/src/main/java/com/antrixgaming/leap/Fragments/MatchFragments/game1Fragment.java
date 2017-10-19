package com.antrixgaming.leap.Fragments.MatchFragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.antrixgaming.leap.Models.LeapScore;
import com.antrixgaming.leap.R;
import com.antrixgaming.leap.leapDetailsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;


public class game1Fragment extends Fragment {

    Context context;

    DatabaseReference dbRef;
    DatabaseReference game1DbRef;

    String myUID;

    ImageView g1SaveScoreButton;
    ImageView g1CancelScoreButton;
    TextView editScoreButton;
    TextView leaperOneText;
    TextView leaperTwoText;
    TextView disputeScoreButton;
    TextView acceptReset;
    TextView forceReset;
    TextView requestBy;
    EditText g1leaperOneScoreBox;
    EditText g1leaperTwoScoreBox;

    String g1leapID;
    String leaperOneScore;
    String leaperTwoScore;
    String scoreStatus;
    String leaperOne;
    String leaperTwo;
    String lastModifiedTime;

    ProgressDialog progressDialog;

    String myPhoneNumber;
    String winner;
    String looser;
    int leapStatus;
    String circleID;
    String AdminFlag;
    String mScoreStatus;
    String resetBy;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game_1, container, false);


        context = getActivity();
        g1leapID = ((leapDetailsActivity)context).leapID;
        leaperOne = ((leapDetailsActivity)context).mLeaperOne;
        leaperTwo = ((leapDetailsActivity)context).mLeaperTwo;
        leapStatus = ((leapDetailsActivity)context).leapStatus;
        circleID = ((leapDetailsActivity)context).mCircleID;
        AdminFlag = ((leapDetailsActivity)context).AdminFlag;



        progressDialog = new ProgressDialog(getActivity());

        dbRef = FirebaseDatabase.getInstance().getReference();
        game1DbRef = dbRef.child("leapscore").child(g1leapID).child("Game1");
        myPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();


        g1SaveScoreButton = (ImageView)view.findViewById(R.id.g1SaveScoreButton);
        g1CancelScoreButton = (ImageView)view.findViewById(R.id.g1CancelScoreButton);
        g1leaperOneScoreBox = (EditText)view.findViewById(R.id.g1leaperOneScoreBox);
        g1leaperTwoScoreBox = (EditText)view.findViewById(R.id.g1leaperTwoScoreBox);
        editScoreButton = (TextView) view.findViewById(R.id.editScoreButton);
        disputeScoreButton = (TextView) view.findViewById(R.id.disputeScoreButton);
        leaperOneText = (TextView) view.findViewById(R.id.leaperOneText);
        leaperTwoText = (TextView) view.findViewById(R.id.leaperTwoText);
        acceptReset = (TextView) view.findViewById(R.id.acceptReset);
        forceReset = (TextView) view.findViewById(R.id.forceReset);
        requestBy = (TextView) view.findViewById(R.id.requestBy);

        final TextView displayedLeaperOneName = (TextView)view.findViewById(R.id.displayedLeaperOneName);
        final TextView displayedLeaperTwoName = (TextView)view.findViewById(R.id.displayedLeaperTwoName);



        g1leaperOneScoreBox.setEnabled(false);
        g1leaperTwoScoreBox.setEnabled(false);
        g1SaveScoreButton.setVisibility(View.GONE);
        g1CancelScoreButton.setVisibility(View.GONE);
        editScoreButton.setVisibility(View.GONE);
        requestBy.setVisibility(View.GONE);




        g1leaperOneScoreBox.setText("");
        g1leaperTwoScoreBox.setText("");

        leaperOneText.setText(leaperOne);
        leaperTwoText.setText(leaperTwo);

        if (Objects.equals(leaperTwo, "Open Leap")){

            editScoreButton.setVisibility(View.GONE);
            disputeScoreButton.setVisibility(View.GONE);
            acceptReset.setVisibility(View.GONE);
            forceReset.setVisibility(View.GONE);
            requestBy.setVisibility(View.GONE);

        }

        if (leapStatus == 0){

            editScoreButton.setVisibility(View.GONE);
            disputeScoreButton.setVisibility(View.GONE);
            acceptReset.setVisibility(View.GONE);
            forceReset.setVisibility(View.GONE);
            requestBy.setVisibility(View.GONE);

        }







            game1DbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("leapID").getValue() == null){

                    disputeScoreButton.setVisibility(View.GONE);

                }else {



                    String L1Score = dataSnapshot.child("leaperOneScore").getValue().toString();
                    String L2Score = dataSnapshot.child("leaperTwoScore").getValue().toString();


                    g1leaperOneScoreBox.setText(L1Score);
                    g1leaperTwoScoreBox.setText(L2Score);




                    /*


                    int mL1Score = Integer.parseInt(L1Score);
                    int mL2Score = Integer.parseInt(L2Score);

                    if(mL1Score < mL2Score){
                        g1leaperOneScoreBox.setTextColor(ContextCompat.getColor(getContext(),R.color.berry));
                        g1leaperTwoScoreBox.setTextColor(ContextCompat.getColor(getContext(),R.color.md_green_900));
                        displayedLeaperOneName.setTextColor(ContextCompat.getColor(getContext(),R.color.berry));
                        displayedLeaperTwoName.setTextColor(ContextCompat.getColor(getContext(),R.color.md_green_900));


                    }
                    else if(mL1Score == mL2Score){

                        g1leaperOneScoreBox.setTextColor(ContextCompat.getColor(getContext(),R.color.grey));
                        g1leaperTwoScoreBox.setTextColor(ContextCompat.getColor(getContext(),R.color.grey));
                        displayedLeaperOneName.setTextColor(ContextCompat.getColor(getContext(),R.color.grey));
                        displayedLeaperTwoName.setTextColor(ContextCompat.getColor(getContext(),R.color.grey));


                    }

                    else if(mL1Score > mL2Score){

                        g1leaperOneScoreBox.setTextColor(ContextCompat.getColor(getContext(), R.color.md_green_900 ));
                        g1leaperTwoScoreBox.setTextColor(ContextCompat.getColor(getContext(),R.color.berry));
                        displayedLeaperOneName.setTextColor(ContextCompat.getColor(getContext(),R.color.md_green_900));
                        displayedLeaperTwoName.setTextColor(ContextCompat.getColor(getContext(),R.color.berry));

                    }



                    */




                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });









        editScoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                g1leaperOneScoreBox.setEnabled(true);
                g1leaperTwoScoreBox.setEnabled(true);
                g1SaveScoreButton.setVisibility(View.VISIBLE);
                g1CancelScoreButton.setVisibility(View.VISIBLE);
                editScoreButton.setVisibility(View.GONE);

            }
        });



        g1SaveScoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {





                if (TextUtils.isEmpty(g1leaperOneScoreBox.getText().toString())) {
                    Toast.makeText(getActivity(), "leaper one's enter score", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(g1leaperTwoScoreBox.getText().toString())) {
                    Toast.makeText(getActivity(), "leaper two's enter score", Toast.LENGTH_SHORT).show();
                    return;
                }




                g1leaperOneScoreBox.setEnabled(false);
                g1leaperTwoScoreBox.setEnabled(false);


                progressDialog.setMessage("Saving score...");
                progressDialog.show();

                leaperOneScore  = g1leaperOneScoreBox.getText().toString();
                leaperTwoScore = g1leaperTwoScoreBox.getText().toString();
                scoreStatus = "1";



                int mL1Score = Integer.parseInt(g1leaperOneScoreBox.getText().toString());
                int mL2Score = Integer.parseInt(g1leaperTwoScoreBox.getText().toString());

                if(mL1Score < mL2Score){
                    looser  = leaperOne;
                    winner = leaperTwo;



                }
                else if(mL1Score == mL2Score){

                    looser  = "";
                    winner = "";


                }

                else if(mL1Score > mL2Score){


                    looser  = leaperTwo;
                    winner = leaperOne;

                }




                game1DbRef.setValue(new LeapScore(g1leapID, leaperOneScore, leaperTwoScore, "1", myPhoneNumber, winner, looser));

                progressDialog.dismiss();
                Toast.makeText(context, "Score saved", Toast.LENGTH_SHORT).show();







            }
        });

        g1CancelScoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                g1leaperOneScoreBox.setEnabled(false);
                g1leaperTwoScoreBox.setEnabled(false);
                g1SaveScoreButton.setVisibility(View.GONE);
                g1CancelScoreButton.setVisibility(View.GONE);
                editScoreButton.setVisibility(View.VISIBLE);

            }
        });
















        /////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////
        //////// GETTING AND SETTING NAMES IN PLACE OF PHONE NUMBER

        ///////////////////////////////////////
        //////////////////   STARTING    ///////////////////////////////




        //// CHECK MY CONTACT LIST IF THIS PERSON IS A CONTACT
        dbRef.child("ContactList").child(myUID).child("leapSortedContacts").child(leaperOne)
                .addValueEventListener(new ValueEventListener() {////////
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.child("name").getValue() == null || dataSnapshot.child("name")
                                .getValue() == "") {///// IF THEY ARE NOT A CONTACT OR THE VALUE IS EMPTY



                            ///// CHECK THE USERS PROFILES TO SEE IF THEY HAVE AN ENTRY THERE
                            dbRef.child("userprofiles").child(leaperOne).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.child("name").getValue() == null || Objects.equals(dataSnapshot.child("name")
                                            .getValue().toString(), "")){///IF THEY DON'T HAVE AN ENTRY USE THEIR PHONE NUMBER

                                        /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////
                                        displayedLeaperOneName.setText(leaperOne);
                                        /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////


                                    } else { //// IF THEY HAVE AN ENTRY USE THEIR ENTERED NAME

                                        String myName = dataSnapshot.child("name").getValue().toString();

                                        /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////
                                        displayedLeaperOneName.setText("~ " + myName);
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
                            displayedLeaperOneName.setText(mName);
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
        /////////////////////////////////////////////////////////////////////
















        if (Objects.equals(leaperTwo, "Open Leap")){

            displayedLeaperTwoName.setText("Open Leap");


        } else {


            /////////////////////////////////////////////////////////////////////
            ///////////////////////////////////////////
            //////// GETTING AND SETTING NAMES IN PLACE OF PHONE NUMBER

            ///////////////////////////////////////
            //////////////////   STARTING    ///////////////////////////////


            //// CHECK MY CONTACT LIST IF THIS PERSON IS A CONTACT
            dbRef.child("ContactList").child(myUID).child("leapSortedContacts").child(leaperTwo)
                    .addValueEventListener(new ValueEventListener() {////////
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.child("name").getValue() == null || dataSnapshot.child("name")
                                    .getValue() == "") {///// IF THEY ARE NOT A CONTACT OR THE VALUE IS EMPTY


                                ///// CHECK THE USERS PROFILES TO SEE IF THEY HAVE AN ENTRY THERE
                                dbRef.child("userprofiles").child(leaperTwo).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.child("name").getValue() == null || Objects.equals(dataSnapshot.child("name")
                                                .getValue().toString(), "")) {///IF THEY DON'T HAVE AN ENTRY USE THEIR PHONE NUMBER

                                            /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////
                                            displayedLeaperTwoName.setText(leaperTwo);
                                            /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////


                                        } else { //// IF THEY HAVE AN ENTRY USE THEIR ENTERED NAME

                                            String myName = dataSnapshot.child("name").getValue().toString();

                                            /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////
                                            displayedLeaperTwoName.setText("~ " + myName);
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
                                displayedLeaperTwoName.setText(mName);
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
            /////////////////////////////////////////////////////////////////////

        }












        if (Objects.equals(circleID, "null")){



            game1DbRef.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {



                    if (dataSnapshot.child("leapID").getValue() == null){

                        if (Objects.equals(myPhoneNumber, leaperOne) || Objects.equals(myPhoneNumber, leaperTwo)){
                            g1leaperOneScoreBox.setEnabled(false);
                            g1leaperTwoScoreBox.setEnabled(false);
                            g1SaveScoreButton.setVisibility(View.GONE);
                            g1CancelScoreButton.setVisibility(View.GONE);
                            editScoreButton.setVisibility(View.VISIBLE);
                            acceptReset.setVisibility(View.GONE);
                            requestBy.setVisibility(View.GONE);
                            forceReset.setVisibility(View.GONE);

                        } else {

                            g1leaperOneScoreBox.setEnabled(false);
                            g1leaperTwoScoreBox.setEnabled(false);
                            g1SaveScoreButton.setVisibility(View.GONE);
                            g1CancelScoreButton.setVisibility(View.GONE);
                            editScoreButton.setVisibility(View.GONE);
                            acceptReset.setVisibility(View.GONE);
                            requestBy.setVisibility(View.GONE);
                            forceReset.setVisibility(View.GONE);


                        }



                    }

                    else {





                        if (Objects.equals(myPhoneNumber, leaperOne) || Objects.equals(myPhoneNumber, leaperTwo)){





                            mScoreStatus = dataSnapshot.child("scoreStatus").getValue().toString();


                            if (Objects.equals(mScoreStatus, "2")) {


                                resetBy = dataSnapshot.child("resetBy").getValue().toString();

                                if (Objects.equals(resetBy, myPhoneNumber)) {

                                    disputeScoreButton.setVisibility(View.GONE);
                                    acceptReset.setVisibility(View.GONE);
                                    forceReset.setVisibility(View.VISIBLE);
                                } else {
                                    disputeScoreButton.setVisibility(View.GONE);
                                    acceptReset.setVisibility(View.VISIBLE);
                                    forceReset.setVisibility(View.GONE);

                                }

                            } else if (Objects.equals(mScoreStatus, "0")) {

                                g1SaveScoreButton.setVisibility(View.GONE);
                                g1CancelScoreButton.setVisibility(View.GONE);
                                editScoreButton.setVisibility(View.VISIBLE);
                                disputeScoreButton.setVisibility(View.GONE);
                                acceptReset.setVisibility(View.GONE);
                                forceReset.setVisibility(View.GONE);

                            } else if (Objects.equals(mScoreStatus, "1")){

                                g1SaveScoreButton.setVisibility(View.GONE);
                                g1CancelScoreButton.setVisibility(View.GONE);
                                editScoreButton.setVisibility(View.GONE);
                                disputeScoreButton.setVisibility(View.VISIBLE);
                                acceptReset.setVisibility(View.GONE);
                                forceReset.setVisibility(View.GONE);

                            }







                        } else {

                            g1leaperOneScoreBox.setEnabled(false);
                            g1leaperTwoScoreBox.setEnabled(false);
                            g1SaveScoreButton.setVisibility(View.GONE);
                            g1CancelScoreButton.setVisibility(View.GONE);
                            editScoreButton.setVisibility(View.GONE);
                            acceptReset.setVisibility(View.GONE);
                            requestBy.setVisibility(View.GONE);
                            forceReset.setVisibility(View.GONE);


                        }






                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });








            disputeScoreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {



                    /////////// SCORE STATUS
                    /////////// 0 - NOT ENTERED
                    /////////// 1 - ENTERED (FIRST ENTRY)
                    /////////// 2 - DISPUTED
                    /////////// 3 - RESET REQUEST


                    game1DbRef.child("resetBy").setValue(myPhoneNumber);
                    game1DbRef.child("scoreStatus").setValue("2");

                }
            });


            acceptReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    game1DbRef.child("scoreStatus").setValue("0");


                }
            });


            forceReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    game1DbRef.child("scoreStatus").setValue("0");

                }
            });













        } else {









            game1DbRef.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {



                    if (dataSnapshot.child("leapID").getValue() == null){
                        if (Objects.equals(myPhoneNumber, leaperOne) || Objects.equals(myPhoneNumber, leaperTwo) || Objects.equals(AdminFlag, "true")){
                            g1leaperOneScoreBox.setEnabled(false);
                            g1leaperTwoScoreBox.setEnabled(false);
                            g1SaveScoreButton.setVisibility(View.GONE);
                            g1CancelScoreButton.setVisibility(View.GONE);
                            editScoreButton.setVisibility(View.VISIBLE);
                            acceptReset.setVisibility(View.GONE);
                            requestBy.setVisibility(View.GONE);
                            forceReset.setVisibility(View.GONE);

                        } else {

                            g1leaperOneScoreBox.setEnabled(false);
                            g1leaperTwoScoreBox.setEnabled(false);
                            g1SaveScoreButton.setVisibility(View.GONE);
                            g1CancelScoreButton.setVisibility(View.GONE);
                            editScoreButton.setVisibility(View.GONE);
                            acceptReset.setVisibility(View.GONE);
                            requestBy.setVisibility(View.GONE);
                            forceReset.setVisibility(View.GONE);


                        }


                    }

                    else {





                        if (Objects.equals(myPhoneNumber, leaperOne) || Objects.equals(myPhoneNumber, leaperTwo) || Objects.equals(AdminFlag, "true")){





                            mScoreStatus = dataSnapshot.child("scoreStatus").getValue().toString();


                            if (Objects.equals(mScoreStatus, "2")) {


                                resetBy = dataSnapshot.child("resetBy").getValue().toString();

                                if (Objects.equals(AdminFlag, "true")) {

                                    disputeScoreButton.setVisibility(View.GONE);
                                    acceptReset.setVisibility(View.VISIBLE);
                                    forceReset.setVisibility(View.GONE);
                                    requestBy.setVisibility(View.VISIBLE);
                                    requestBy.setText("Dispute request from " + requestBy);
                                } else {
                                    disputeScoreButton.setVisibility(View.GONE);
                                    acceptReset.setVisibility(View.GONE);
                                    forceReset.setVisibility(View.GONE);
                                    requestBy.setVisibility(View.GONE);


                                }

                            } else if (Objects.equals(mScoreStatus, "0")) {

                                g1SaveScoreButton.setVisibility(View.GONE);
                                g1CancelScoreButton.setVisibility(View.GONE);
                                editScoreButton.setVisibility(View.VISIBLE);
                                disputeScoreButton.setVisibility(View.GONE);
                                acceptReset.setVisibility(View.GONE);
                                forceReset.setVisibility(View.GONE);
                                requestBy.setVisibility(View.GONE);


                            } else if (Objects.equals(mScoreStatus, "1")){

                                g1SaveScoreButton.setVisibility(View.GONE);
                                g1CancelScoreButton.setVisibility(View.GONE);
                                editScoreButton.setVisibility(View.GONE);
                                disputeScoreButton.setVisibility(View.VISIBLE);
                                acceptReset.setVisibility(View.GONE);
                                forceReset.setVisibility(View.GONE);
                                requestBy.setVisibility(View.GONE);


                            }







                        } else {

                            g1leaperOneScoreBox.setEnabled(false);
                            g1leaperTwoScoreBox.setEnabled(false);
                            g1SaveScoreButton.setVisibility(View.GONE);
                            g1CancelScoreButton.setVisibility(View.GONE);
                            editScoreButton.setVisibility(View.GONE);
                            acceptReset.setVisibility(View.GONE);
                            requestBy.setVisibility(View.GONE);
                            forceReset.setVisibility(View.GONE);


                        }






                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });








            disputeScoreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {



                    /////////// SCORE STATUS
                    /////////// 0 - NOT ENTERED
                    /////////// 1 - ENTERED (FIRST ENTRY)
                    /////////// 2 - DISPUTED
                    /////////// 3 - RESET REQUEST

                    game1DbRef.child("resetBy").setValue(myPhoneNumber);
                    game1DbRef.child("scoreStatus").setValue("2");

                }
            });


            acceptReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    game1DbRef.child("scoreStatus").setValue("0");


                }
            });


            forceReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    game1DbRef.child("scoreStatus").setValue("0");

                }
            });






        }
















            return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        g1leaperOneScoreBox = (EditText)view.findViewById(R.id.g1leaperOneScoreBox);
        g1leaperTwoScoreBox = (EditText)view.findViewById(R.id.g1leaperTwoScoreBox);
    }
}
