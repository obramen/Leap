package com.antrixgaming.leap.Fragments.LeapFragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.antrixgaming.leap.LeapClasses.LeapUtilities;
import com.antrixgaming.leap.Models.UserLeap;
import com.antrixgaming.leap.R;
import com.antrixgaming.leap.leapDetailsActivity;
import com.antrixgaming.leap.newLeap;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import cn.iwgang.countdownview.CountdownView;
import de.hdodenhof.circleimageview.CircleImageView;


public class leapsFragment extends Fragment {

    public DatabaseReference dbRef;
    public String leaperTwoUID;
    public String myUID;
    public String myPhoneNumber;
    DatabaseReference scoreDbRef;



    LeapUtilities leapUtilities;

    StorageReference mStorage;
    StorageReference mLeaperOneStorageRef;
    StorageReference mLeaperTwoStorageRef;
    CircleImageView leaperOneImage;
    CircleImageView leaperTwoImage;

    String mLeaperOneUID;
    String mLeaperTwoUID;

    Long mleaperOneScore = null;
    Long mleaperTwoScore = null;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_leaps, container, false);


        dbRef = FirebaseDatabase.getInstance().getReference();


        myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        myPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

        leapUtilities = new LeapUtilities();

        mStorage = FirebaseStorage.getInstance().getReference();





        FloatingActionButton chatFab = (FloatingActionButton) view.findViewById(R.id.leap_fab);
        chatFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent startNewLeapIntent = new Intent(getActivity(), newLeap.class);
                getActivity().startActivity(startNewLeapIntent);

            }
        });



        DatabaseReference dataSource = dbRef.child("leaps").child(FirebaseAuth.getInstance()
                .getCurrentUser().getUid());

        final ListView listOfLeaps = (ListView)view.findViewById(R.id.list_of_leaps);
        FirebaseListAdapter<UserLeap> adapter;
        adapter = new FirebaseListAdapter<UserLeap>(getActivity(), UserLeap.class,
                R.layout.leaps_list, dataSource.orderByChild("index")) {



            private int layout;


            @Override
            protected void populateView(final View v, final UserLeap model, int position) {
                // Get references to the views of message.xml
                final TextView leapID = (TextView)v.findViewById(R.id.leapID);
                final TextView gameType = (TextView)v.findViewById(R.id.gameType);
                TextView gameFormat = (TextView)v.findViewById(R.id.gameFormat);
                final TextView countdownTimer = (TextView)v.findViewById(R.id.countdownTimer);
                TextView leaperOne = (TextView)v.findViewById(R.id.leaperOne);
                final TextView leaperOneScore = (TextView)v.findViewById(R.id.leaperOneScore);
                final TextView leaperTwo = (TextView)v.findViewById(R.id.leaperTwo);
                final TextView leaperTwoScore = (TextView)v.findViewById(R.id.leaperTwoScore);
                TextView gameTime = (TextView)v.findViewById(R.id.gameTime);
                final CardView leapCard = (CardView) v.findViewById(R.id.leapCard);
                final TextView mcircleID = (TextView)v.findViewById(R.id.circleID);
                LinearLayout leapsDetails = (LinearLayout)v.findViewById(R.id.leapDetails);
                final LinearLayout circleDetailsLayout = (LinearLayout)v.findViewById(R.id.circleDetailsLayout);

                final CircleImageView leaperOneImage = (CircleImageView)v.findViewById(R.id.leaperOneImage);
                final CircleImageView leaperTwoImage = (CircleImageView)v.findViewById(R.id.leaperTwoImage);

                final TextView displayedLeaperOneName = (TextView)v.findViewById(R.id.displayedLeaperOneName);
                final TextView displayedLeaperTwoName = (TextView)v.findViewById(R.id.displayedLeaperTwoName);
                CountdownView countdownTimerA = (CountdownView)v.findViewById(R.id.countdownTimerA);





                final String circleID = model.getCircleID();
                if (Objects.equals(circleID, "null")){
                    mcircleID.setText("");
                    circleDetailsLayout.setVisibility(v.GONE);


                } else{

                    FirebaseDatabase.getInstance().getReference().child("groupcirclenames").child(circleID)
                            .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            String circleName = dataSnapshot.child("circleName").getValue().toString();

                            mcircleID.setText(circleName);
                            circleDetailsLayout.setVisibility(v.VISIBLE);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }


                String gameId = model.getgameType().replaceAll("\\s+","").toLowerCase();
                CircleImageView gameImage = (CircleImageView)v.findViewById(R.id.gameImage);
                StorageReference mGameImageStorage = mStorage.child("gameImages").child(gameId + ".jpg");
                leapUtilities.CircleImageFromFirebase(getActivity(), mGameImageStorage, gameImage);




                /// SPLIT LEAP TIME OF LONG FORMAT INTO DAY AND TIME
                CharSequence mDay = DateFormat.format("dd-MMM-yy", model.getleapDay());
                CharSequence mTime = DateFormat.format("HH:mm", model.getleapDay());


                CharSequence lDay;


                if (DateUtils.isToday(model.getleapDay())){
                    lDay = "Today";

                }
                else{
                    lDay = mDay;
                }


                final CharSequence lTime = mTime;


                // Set their texts
                leapID.setText(model.getleapID());
                gameType.setText(model.getgameType());
                gameFormat.setText(model.getgameFormat());
                gameTime.setText(lDay + " | " +lTime);
                leaperOne.setText(model.getleaperOne());
                leaperTwo.setText(model.getleaperTwo());
                final int leapStatus = Integer.parseInt(model.getleapStatus());
                leaperOneScore.setText(model.leaperOneScore);
                leaperTwoScore.setText(model.leaperTwoScore);

                scoreDbRef = dbRef.child("leapscore").child(model.getleapID());



                // set conditions
                // countdown timer
                // leap button status
                // 0 - new leap
                // 1 - accepted leap
                // 2 - declined leap
                // 3 - cancelled leap
                switch (leapStatus){




                    case 0:
                        leapsDetails.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                        break;






                    case 1:
                        leapsDetails.setBackgroundColor(getResources().getColor(R.color.green));





                        long NowTime = new Date().getTime();


                        if (model.getleapDay() >=  NowTime){



                            countdownTimerA.start(model.getleapDay() - NowTime); // Millisecond
                            //countdownTimerA.setText("LIVE");
                            countdownTimer.setVisibility(View.GONE);
                            countdownTimerA.setVisibility(View.VISIBLE);

                        } else{}

                        if (NowTime > model.getleapDay() && (NowTime < (model.getleapDay() + TimeUnit.HOURS.toMillis(6)))){

                            countdownTimer.setVisibility(View.VISIBLE);
                            countdownTimerA.setVisibility(View.GONE);

                        } else{}

                        if (NowTime > model.getleapDay() && (NowTime >= (model.getleapDay() + TimeUnit.HOURS.toMillis(6)))){

                            countdownTimer.setVisibility(View.GONE);
                            countdownTimerA.setVisibility(View.GONE);
                            leapsDetails.setBackgroundColor(getResources().getColor(R.color.black));


                        }else {}




                        break;







                    case 2:
                        leapsDetails.setBackgroundColor(getResources().getColor(R.color.berry));
                        break;





                    case 3:
                        leapsDetails.setBackgroundColor(getResources().getColor(R.color.black));
                        break;


                }





                scoreDbRef.orderByChild("winner").equalTo(model.leaperOne).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        mleaperOneScore = dataSnapshot.getChildrenCount();
                        leaperOneScore.setText(String.valueOf(mleaperOneScore));

                        }



                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                scoreDbRef.orderByChild("winner").equalTo(model.leaperTwo).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (Objects.equals(model.getleaperTwo(), "Open Leap")){

                            leaperTwoScore.setText("0");


                        } else {
                            mleaperTwoScore = dataSnapshot.getChildrenCount();
                            leaperTwoScore.setText(String.valueOf(mleaperTwoScore));



                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });








                // Format the date before showing it
                //messageTime.setText(DateFormat.format("HH:mm", model.getMessageTime()));


                leapCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(leapStatus == 2){


                            Toast.makeText(getActivity(), "Leap declined", Toast.LENGTH_SHORT).show();
                        } else{

                            Intent openLeapDetails = new Intent(getActivity(), leapDetailsActivity.class);
                            openLeapDetails.putExtra("leapID", model.getleapID());
                            openLeapDetails.putExtra("sourceActivity", "1");
                            startActivity(openLeapDetails);
                        }



                    }
                });








                mLeaperOneStorageRef = mStorage.child("leaperProfileImage").child(model.leaperOne).child(model.leaperOne);
                leapUtilities.CircleImageFromFirebase(getActivity(), mLeaperOneStorageRef, leaperOneImage);


                if (Objects.equals(model.getleaperTwo(), "Leaper Two") || Objects.equals(model.getleaperTwo(), "Open Leap")){


                }else {
                        mLeaperTwoStorageRef = mStorage.child("leaperProfileImage").child(model.leaperTwo).child(model.leaperTwo);
                        leapUtilities.CircleImageFromFirebase(getActivity(), mLeaperTwoStorageRef, leaperTwoImage);

               }




















                /////////////////////////////////////////////////////////////////////
                ///////////////////////////////////////////
                //////// GETTING AND SETTING NAMES IN PLACE OF PHONE NUMBER

                ///////////////////////////////////////
                //////////////////   STARTING    ///////////////////////////////




                //// CHECK MY CONTACT LIST IF THIS PERSON IS A CONTACT
                dbRef.child("ContactList").child(myUID).child("leapSortedContacts").child(model.getleaperOne())
                        .addValueEventListener(new ValueEventListener() {////////
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (dataSnapshot.child("name").getValue() == null || dataSnapshot.child("name")
                                        .getValue() == "") {///// IF THEY ARE NOT A CONTACT OR THE VALUE IS EMPTY


                                    ///// CHECK THE USERS PROFILES TO SEE IF THEY HAVE AN ENTRY THERE
                                    dbRef.child("userprofiles").child(model.getleaperOne()).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.child("name").getValue() == null || Objects.equals(dataSnapshot.child("name")
                                                    .getValue().toString(), "")){///IF THEY DON'T HAVE AN ENTRY USE THEIR PHONE NUMBER

                                                /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////
                                                displayedLeaperOneName.setText(model.getleaperOne());
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
















                if (Objects.equals(model.getleaperTwo(), "Open Leap")){

                    displayedLeaperTwoName.setText("Open Leap");


                } else {








                    /////////////////////////////////////////////////////////////////////
                    ///////////////////////////////////////////
                    //////// GETTING AND SETTING NAMES IN PLACE OF PHONE NUMBER

                    ///////////////////////////////////////
                    //////////////////   STARTING    ///////////////////////////////




                    //// CHECK MY CONTACT LIST IF THIS PERSON IS A CONTACT
                    dbRef.child("ContactList").child(myUID).child("leapSortedContacts").child(model.getleaperTwo())
                            .addValueEventListener(new ValueEventListener() {////////
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.child("name").getValue() == null || dataSnapshot.child("name")
                                            .getValue() == "") {///// IF THEY ARE NOT A CONTACT OR THE VALUE IS EMPTY



                                        ///// CHECK THE USERS PROFILES TO SEE IF THEY HAVE AN ENTRY THERE
                                        dbRef.child("userprofiles").child(model.getleaperTwo()).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.child("name").getValue() == null || Objects.equals(dataSnapshot.child("name")
                                                        .getValue().toString(), "")){///IF THEY DON'T HAVE AN ENTRY USE THEIR PHONE NUMBER

                                                    /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////
                                                    displayedLeaperTwoName.setText(model.getleaperTwo());
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
                    //////////////////////////////////////////////////////////////////


                }























            }





        };

        listOfLeaps.setAdapter(adapter);


        ListView listView = (ListView) view.findViewById(R.id.list_of_leaps);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //listOfLeaps.setBackgroundResource(R.color.verylightblack);
            }
        });


        //return inflated layout
        return view;


    }







}




