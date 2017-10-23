package com.antrixgaming.leap.Fragments.CircleFragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.antrixgaming.leap.LeapClasses.LeapUtilities;
import com.antrixgaming.leap.Models.ChatMessage;
import com.antrixgaming.leap.Models.UserLeap;
import com.antrixgaming.leap.R;
import com.antrixgaming.leap.leapDetailsActivity;
import com.antrixgaming.leap.leaperProfileActivity;
import com.firebase.ui.auth.ui.User;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class circleLiveLeapsFragment extends Fragment {

    DatabaseReference dbRef;
    DatabaseReference dbRefOpenLeap;
    String circleID = null;
    String myPhoneNumber;
    String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();


    StorageReference mStorage;
    StorageReference mLeaperOneStorageRef;
    StorageReference mLeaperTwoStorageRef;

    DatabaseReference scoreDbRef;

    LeapUtilities leapUtilities;
    Long mleaperOneScore;
    Long mleaperTwoScore;
    String leapID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_circle_live_leaps, container, false);

        dbRef = FirebaseDatabase.getInstance().getReference();
        myPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        dbRefOpenLeap = dbRef.child("leapsforcircles").child("liveleaps");

        leapUtilities=new LeapUtilities();



        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        circleID = sharedPreferences.getString("currentCircleID", null);

        mStorage = FirebaseStorage.getInstance().getReference();





        final ListView listView = (ListView) view.findViewById(R.id.list_of_circle_live_leaps);
        FirebaseListAdapter<UserLeap> adapter;
        adapter = new FirebaseListAdapter<UserLeap>(getActivity(), UserLeap.class,
                R.layout.circle_live_leaps_list, dbRefOpenLeap.child(circleID).orderByChild("leapSetupTime")) {
            @Override
            protected void populateView(View v, final UserLeap model, int position) {

                TextView circleLiveListLeaperOneName = (TextView) v.findViewById(R.id.circleLiveListLeaperOneName);
                TextView circleLiveListLeaperTwoName = (TextView) v.findViewById(R.id.circleLiveListLeaperTwoName);
                final TextView circleListLeaperOneScore = (TextView) v.findViewById(R.id.circleListLeaperOneScore);
                final TextView circleListLeaperTwoScore = (TextView) v.findViewById(R.id.circleListLeaperTwoScore);
                TextView gameType = (TextView) v.findViewById(R.id.gameType);
                TextView gameFormat = (TextView) v.findViewById(R.id.gameFormat);
                TextView gameTime = (TextView) v.findViewById(R.id.gameTime);
                CircleImageView circleLiveListImage1 = (CircleImageView) v.findViewById(R.id.circleLiveListImage1);
                CircleImageView circleLiveListImage2 = (CircleImageView) v.findViewById(R.id.circleLiveListImage2);

                TextView mleapID = (TextView) v.findViewById(R.id.leapID);

                final TextView displayedLeaperOneName = (TextView) v.findViewById(R.id.displayedLeaperOneName);
                final TextView displayedLeaperTwoName = (TextView) v.findViewById(R.id.displayedLeaperTwoName);


                circleLiveListLeaperOneName.setText(model.getleaperOne());
                circleLiveListLeaperTwoName.setText(model.getleaperTwo());
                gameType.setText(model.getgameType());
                gameFormat.setText(model.getgameFormat());
                mleapID.setText(model.getleapID());



                /// SPLIT LEAP TIME OF LONG FORMAT INTO DAY AND TIME
                CharSequence mDay = DateFormat.format("dd-MMM-yy", model.getleapDay());
                CharSequence mTime = DateFormat.format("HH:MM", model.getleapDay());


                CharSequence lDay;


                if (DateUtils.isToday(model.getleapDay())){
                    lDay = "Today";

                }
                else{
                    lDay = mDay;
                }


                final CharSequence lTime = mTime;

                gameTime.setText(lDay + ", " +lTime);





                String leapStatus = model.getleapStatus();

                leapID = model.getleapID();

                scoreDbRef = dbRef.child("leapscore").child(leapID);


                // leap button status
                // 0 - new leap
                // 1 - accepted leap
                // 2 - declined leap
                // 3 - cancelled leap

                mLeaperOneStorageRef = mStorage.child("leaperProfileImage").child(model.leaperOne).child(model.leaperOne);
                leapUtilities.CircleImageFromFirebase(getActivity(), mLeaperOneStorageRef, circleLiveListImage1);

                mLeaperTwoStorageRef = mStorage.child("leaperProfileImage").child(model.leaperTwo).child(model.leaperTwo);
                leapUtilities.CircleImageFromFirebase(getActivity(), mLeaperTwoStorageRef, circleLiveListImage2);




                scoreDbRef.orderByChild("winner").equalTo(model.leaperOne).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        mleaperOneScore = dataSnapshot.getChildrenCount();
                        circleListLeaperOneScore.setText(String.valueOf(mleaperOneScore));

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                scoreDbRef.orderByChild("winner").equalTo(model.leaperTwo).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        mleaperTwoScore = dataSnapshot.getChildrenCount();
                        circleListLeaperTwoScore.setText(String.valueOf(mleaperTwoScore));

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });









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

        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView mLeapID = (TextView) view.findViewById(R.id.leapID);


                Intent openLeapDetails = new Intent(getActivity(), leapDetailsActivity.class);
                openLeapDetails.putExtra("leapID", mLeapID.getText().toString());
                openLeapDetails.putExtra("circleID", circleID);
                openLeapDetails.putExtra("sourceActivity", "1");
                startActivity(openLeapDetails);

            }
        });





        return view;
    }

}


