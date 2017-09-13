package com.antrixgaming.leap.Fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;

import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class leapsFragment extends Fragment {



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



        DatabaseReference dataSource = FirebaseDatabase.getInstance().getReference().child("leaps").child(FirebaseAuth.getInstance()
                .getCurrentUser().getUid());

        final ListView listOfLeaps = (ListView)view.findViewById(R.id.list_of_leaps);
        FirebaseListAdapter<UserLeap> adapter;
        adapter = new FirebaseListAdapter<UserLeap>(getActivity(), UserLeap.class,
                R.layout.leaps_list, dataSource) {

            private int layout;


            @Override
            protected void populateView(final View v, final UserLeap model, int position) {
                // Get references to the views of message.xml
                final TextView leapID = (TextView)v.findViewById(R.id.leapID);
                TextView gameType = (TextView)v.findViewById(R.id.gameType);
                TextView gameFormat = (TextView)v.findViewById(R.id.gameFormat);
                TextView countdownTimer = (TextView)v.findViewById(R.id.countdownTimer);
                TextView leaperOne = (TextView)v.findViewById(R.id.leaperOne);
                TextView leaperTwo = (TextView)v.findViewById(R.id.leaperTwo);
                TextView gameTime = (TextView)v.findViewById(R.id.gameTime);
                final Button leapInButton = (Button) v.findViewById(R.id.leapInButton);
                final Button recordScoreButton = (Button) v.findViewById(R.id.recordScoreButton);
                final CardView leapCard = (CardView) v.findViewById(R.id.leapCard);

                String lDay = model.getleapDay();
                String lTime = model.getleapTime();

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
                    leapInButton.setText("LEAP IN");

                    //if(Objects.equals(leaperOne.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())){
                        //recordScoreButton.setVisibility(View.GONE);
                    //} else{
                        recordScoreButton.setText("DECLINE");
                    //}

                } else if (leapStatus == 1){
                    leapInButton.setText("ACCEPTED");
                    leapInButton.setBackgroundResource(R.drawable.grey_button);
                    leapInButton.setEnabled(false);
                    recordScoreButton.setText("SCORE");
                }  else if (leapStatus == 2){
                    leapInButton.setText("DECLINE");
                    leapInButton.setEnabled(false);
                    recordScoreButton.setVisibility(View.GONE);
                } else if (leapStatus == 3){
                    leapInButton.setText("CANCELED");
                    leapInButton.setEnabled(false);
                    recordScoreButton.setVisibility(View.GONE);

                } else{


                }
                // record score button



                // Format the date before showing it
                //messageTime.setText(DateFormat.format("HH:mm", model.getMessageTime()));



                leapInButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(Objects.equals(leapInButton.getText().toString(), "LEAP IN")){


                            FirebaseDatabase.getInstance().getReference().child("leaps").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(leapID.getText().toString()).child("leapStatus").setValue("1");

                            Toast.makeText(getActivity(), "accepted", Toast.LENGTH_SHORT).show();



                        }else{


                        }
                    }
                });

                recordScoreButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(Objects.equals(recordScoreButton.getText().toString(), "DECLINE")) {

                            FirebaseDatabase.getInstance().getReference().child("leaps").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(leapID.getText().toString()).child("leapStatus").setValue("2");

                            Toast.makeText(getActivity(), "declined", Toast.LENGTH_SHORT).show();





                        } else if(Objects.equals(recordScoreButton.getText().toString(), "SCORE")){

                            Intent openLeapDetails = new Intent(getActivity(), leapDetailsActivity.class);
                            startActivity(openLeapDetails);

                        } else {


                        }
                    }
                });


                leapCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        leapCard.setBackgroundResource(R.drawable.grey_button);
                        Intent openLeapDetails = new Intent(getActivity(), leapDetailsActivity.class);
                        startActivity(openLeapDetails);

                    }
                });


            }

        };

        listOfLeaps.setAdapter(adapter);


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




