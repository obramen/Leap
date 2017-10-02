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

import android.support.v7.widget.CardView;
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

import de.hdodenhof.circleimageview.CircleImageView;


public class leapsFragment extends Fragment {

    public DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    public String leaperTwoUID;
    public String myUID;
    public String myPhoneNumber;


    LeapUtilities leapUtilities;

    StorageReference mStorage;
    StorageReference mLeaperOneStorageRef;
    StorageReference mLeaperTwoStorageRef;
    CircleImageView leaperOneImage;
    CircleImageView leaperTwoImage;

    String mLeaperOneUID;
    String mLeaperTwoUID;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_leaps, container, false);


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
                R.layout.leaps_list, dataSource.orderByChild("leapSetupTime")) {

            private int layout;


            @Override
            protected void populateView(final View v, final UserLeap model, int position) {
                // Get references to the views of message.xml
                final TextView leapID = (TextView)v.findViewById(R.id.leapID);
                TextView gameType = (TextView)v.findViewById(R.id.gameType);
                TextView gameFormat = (TextView)v.findViewById(R.id.gameFormat);
                final TextView countdownTimer = (TextView)v.findViewById(R.id.countdownTimer);
                TextView leaperOne = (TextView)v.findViewById(R.id.leaperOne);
                final TextView leaperTwo = (TextView)v.findViewById(R.id.leaperTwo);
                TextView gameTime = (TextView)v.findViewById(R.id.gameTime);
                final CardView leapCard = (CardView) v.findViewById(R.id.leapCard);
                final TextView mcircleID = (TextView)v.findViewById(R.id.circleID);
                LinearLayout leapsDetails = (LinearLayout)v.findViewById(R.id.leapDetails);
                final LinearLayout circleDetailsLayout = (LinearLayout)v.findViewById(R.id.circleDetailsLayout);

                final CircleImageView leaperOneImage = (CircleImageView)v.findViewById(R.id.leaperOneImage);
                final CircleImageView leaperTwoImage = (CircleImageView)v.findViewById(R.id.leaperTwoImage);



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

                final String lDay = model.getleapDay();
                final String lTime = model.getleapTime();


                // Set their texts
                leapID.setText(model.getleapID());
                gameType.setText(model.getgameType());
                gameFormat.setText(model.getgameFormat());
                gameTime.setText(lDay + " | " +lTime);
                leaperOne.setText(model.getleaperOne());
                leaperTwo.setText(model.getleaperTwo());
                final int leapStatus = Integer.parseInt(model.getleapStatus());


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
                        break;

                    case 2:
                        leapsDetails.setBackgroundColor(getResources().getColor(R.color.black));
                        break;

                    case 3:
                        leapsDetails.setBackgroundColor(getResources().getColor(R.color.black));
                        break;


                }


                // Format the date before showing it
                //messageTime.setText(DateFormat.format("HH:mm", model.getMessageTime()));


                leapCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent openLeapDetails = new Intent(getActivity(), leapDetailsActivity.class);
                        openLeapDetails.putExtra("leapID", model.getleapID());
                        startActivity(openLeapDetails);

                    }
                });



                new CountDownTimer(300000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        countdownTimer.setText("" + millisUntilFinished / 1000);
                    }

                    public void onFinish() {
                        countdownTimer.setText("LIVE!");
                        countdownTimer.setTextColor(getResources().getColor(R.color.white));
                        //countdownTimer.setTextColor(getResources().getColor(R.color.colorPrimary));
                        countdownTimer.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    }
                }.start();


                mLeaperOneStorageRef = mStorage.child("leaperProfileImage").child(model.leaperOne).child(model.leaperOne);
                leapUtilities.CircleImageFromFirebase(getActivity(), mLeaperOneStorageRef, leaperOneImage);


                if (Objects.equals(model.getleaperTwo(), "Leaper Two") || Objects.equals(model.getleaperTwo(), "Open Leap")){


                }else {
                        mLeaperTwoStorageRef = mStorage.child("leaperProfileImage").child(model.leaperTwo).child(model.leaperTwo);
                        leapUtilities.CircleImageFromFirebase(getActivity(), mLeaperTwoStorageRef, leaperTwoImage);

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




