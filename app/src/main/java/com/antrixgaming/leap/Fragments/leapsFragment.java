package com.antrixgaming.leap.Fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;

import android.support.v4.view.accessibility.AccessibilityManagerCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.antrixgaming.leap.Leap;
import com.antrixgaming.leap.LeapClasses.UserLeap;
import com.antrixgaming.leap.R;
import com.antrixgaming.leap.activity_one_circle;
import com.antrixgaming.leap.leapDetailsActivity;
import com.antrixgaming.leap.newLeap;
import com.firebase.ui.auth.ui.User;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


public class leapsFragment extends Fragment {

    public DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    public String leaperTwoUID;
    public String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_leaps, container, false);





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
                final Button leapInButton = (Button) v.findViewById(R.id.leapInButton);
                final Button recordScoreButton = (Button) v.findViewById(R.id.recordScoreButton);
                final CardView leapCard = (CardView) v.findViewById(R.id.leapCard);
                final RelativeLayout leapInButtonLayout = (RelativeLayout) v.findViewById(R.id.leapInButtonLayout);
                final TextView leapsLeaperTwoUID = (TextView) v.findViewById(R.id.leapsLeaperTwoUID);

                final String lDay = model.getleapDay();
                final String lTime = model.getleapTime();


                // Set their texts
                leapID.setText(model.getleapID());
                gameType.setText(model.getgameType());
                gameFormat.setText(model.getgameFormat());
                gameTime.setText(lDay + " | " +lTime);
                leaperOne.setText(model.getleaperOne());
                leaperTwo.setText(model.getleaperTwo());
                int leapStatus = Integer.parseInt(model.getleapStatus());



                // set conditions
                // countdown timer
                // leap button status
                if (leapStatus == 0){
                    if(Objects.equals(leaperOne.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())){
                        recordScoreButton.setText("CANCEL");
                        leapInButtonLayout.setVisibility(view.GONE);
                        leapInButton.setEnabled(false);
                        leapInButton.setVisibility(view.GONE);
                        recordScoreButton.setVisibility(View.VISIBLE);
                    }
                    else{
                        leapInButton.setText("LEAP IN");
                        leapInButtonLayout.setVisibility(view.GONE);

                        recordScoreButton.setText("DECLINE");
                        recordScoreButton.setVisibility(View.VISIBLE);
                        leapInButtonLayout.setVisibility(view.VISIBLE);
                    }

                } else if (leapStatus == 1){
                    leapInButton.setText("ACCEPTED");
                    leapInButton.setVisibility(view.GONE);
                    leapInButtonLayout.setVisibility(view.GONE);
                    leapInButton.setBackgroundResource(R.drawable.grey_button);
                    leapInButton.setEnabled(false);
                    recordScoreButton.setText("SCORE");
                    recordScoreButton.setVisibility(View.VISIBLE);

                }  else if (leapStatus == 2){
                    leapInButton.setText("DECLINED");
                    leapInButton.setVisibility(view.VISIBLE);
                    leapInButtonLayout.setVisibility(view.VISIBLE);
                    leapInButton.setBackgroundResource(R.drawable.redbuttonlight);
                    leapInButton.setEnabled(false);
                    recordScoreButton.setVisibility(View.GONE);
                } else if (leapStatus == 3){
                    leapInButton.setText("CANCELED");
                    leapInButton.setBackgroundResource(R.drawable.grey_button);
                    leapInButton.setEnabled(false);
                    leapInButton.setVisibility(view.VISIBLE);
                    leapInButtonLayout.setVisibility(view.VISIBLE);
                    recordScoreButton.setVisibility(View.GONE);

                } else{


                }
                // record score button





                // Format the date before showing it
                //messageTime.setText(DateFormat.format("HH:mm", model.getMessageTime()));

                if (leaperOne.getText().toString() == FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()){
                    DatabaseReference leaperTwoDbRef = FirebaseDatabase.getInstance().getReference()
                            .child("phonenumbers").child(leaperTwo.getText().toString()).child("uid");

                    leaperTwoDbRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            leaperTwoUID = dataSnapshot.getValue().toString();
                            leapsLeaperTwoUID.setText(leaperTwoUID);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else{
                    DatabaseReference leaperTwoDbRef = FirebaseDatabase.getInstance().getReference()
                            .child("phonenumbers").child(leaperOne.getText().toString()).child("uid");

                    leaperTwoDbRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            leaperTwoUID = dataSnapshot.getValue().toString();
                            leapsLeaperTwoUID.setText(leaperTwoUID);
                        }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }









                leapInButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(Objects.equals(leapInButton.getText().toString(), "LEAP IN")){



                            // Changes for first leaper
                            dbRef.child("leaps").child(myUID)
                                    .child(leapID.getText().toString()).child("leapStatus").setValue("1");
                            dbRef.child("leaps").child(myUID)
                                    .child(leapID.getText().toString()).child("leapStatusChangeTime").setValue(new Date().getTime());

                            // Changes for second leaper
                            dbRef.child("leaps").child(leaperTwoUID)
                                    .child(leapID.getText().toString()).child("leapStatus").setValue("1");
                            dbRef.child("leaps").child(leaperTwoUID)
                                    .child(leapID.getText().toString()).child("leapStatusChangeTime").setValue(new Date().getTime());

                            //Toast.makeText(getActivity(), "LEAP ACCEPTED", Toast.LENGTH_SHORT).show();

                            Snackbar.make(view, "LEAP ACCEPTED", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();

                            //Long acceptTime = model.getleapStatusChangeTime();
                            //Long matchTime = Long.parseLong(lDay + " "  + lTime);


                            new CountDownTimer(30000, 1000) {

                                public void onTick(long millisUntilFinished) {
                                    countdownTimer.setText("" + millisUntilFinished / 1000);
                                }

                                public void onFinish() {
                                    countdownTimer.setText("LIVE!");
                                    //countdownTimer.setTextColor(getResources().getColor(R.color.colorPrimary));
                                    countdownTimer.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                                }
                            }.start();



                        }else{


                        }
                    }
                });

                recordScoreButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(Objects.equals(recordScoreButton.getText().toString(), "DECLINE")) {

                            dbRef.child("leaps").child(myUID)
                                    .child(leapID.getText().toString()).child("leapStatus").setValue("2");
                            dbRef.child("leaps").child(leaperTwoUID)
                                    .child(leapID.getText().toString()).child("leapStatus").setValue("2");

                            Toast.makeText(getActivity(), "LEAP DECLINED", Toast.LENGTH_SHORT).show();





                        } else if(Objects.equals(recordScoreButton.getText().toString(), "SCORE")){

                            Intent openLeapDetails = new Intent(getActivity(), leapDetailsActivity.class);
                            openLeapDetails.putExtra("leapID", model.getleapID());
                            startActivity(openLeapDetails);

                        } else if (Objects.equals(recordScoreButton.getText().toString(), "CANCEL")){

                            dbRef.child("leaps").child(myUID)
                                    .child(leapID.getText().toString()).removeValue();
                            dbRef.child("leaps").child(leaperTwoUID)
                                    .child(leapID.getText().toString()).removeValue();

                            Toast.makeText(getActivity(), "LEAP CANCELLED", Toast.LENGTH_SHORT).show();

                        } else{


                        }
                    }
                });


                leapCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent openLeapDetails = new Intent(getActivity(), leapDetailsActivity.class);
                        openLeapDetails.putExtra("leapID", model.getleapID());
                        startActivity(openLeapDetails);

                    }
                });


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






    private class leapAdapter extends ArrayAdapter<String>{

        private int layout;

        public leapAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<String> objects) {
            super(context, resource, objects);

            layout = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            ViewHolder miniViewHolder = null;
            if(convertView == null){

                LayoutInflater inflator = LayoutInflater.from(getContext());
                convertView = inflator.inflate(layout, parent, false);

                ViewHolder viewHolder = new ViewHolder();
                viewHolder.leaperOneImage = (ImageView) convertView.findViewById(R.id.leaperOneImage);
                viewHolder.leaperTwoImage = (ImageView) convertView.findViewById(R.id.leaperTwoImage);
                viewHolder.gameImage = (ImageView) convertView.findViewById(R.id.gameImage);
                viewHolder.leapCard = (CardView) convertView.findViewById(R.id.leapCard);
                viewHolder.leapInButton = (Button) convertView.findViewById(R.id.leapInButton);
                viewHolder.recordScoreButton = (Button) convertView.findViewById(R.id.recordScoreButton);


                viewHolder.leapInButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(), "Leap In Button Toasted", Toast.LENGTH_LONG).show();
                    }
                });
                viewHolder.recordScoreButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(), "Record Score Button Toasted", Toast.LENGTH_LONG).show();
                    }
                });
                viewHolder.leapCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getContext(), "Leap Card Toasted", Toast.LENGTH_LONG).show();
                    }
                });


                convertView.setTag(viewHolder);
            } else{
                miniViewHolder = (ViewHolder)convertView.getTag();
                //miniViewHolder.leapInButton.setText(getItem(position));


            }

            return convertView;

        }
    }

    public class ViewHolder{

        Button leapInButton;
        Button recordScoreButton;
        CardView leapCard;
        ImageView leaperOneImage;
        ImageView leaperTwoImage;
        ImageView gameImage;


    }



}




