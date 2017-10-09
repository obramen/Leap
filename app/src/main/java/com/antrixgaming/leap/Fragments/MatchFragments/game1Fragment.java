package com.antrixgaming.leap.Fragments.MatchFragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

    ImageView g1SaveScoreButton;
    ImageView g1CancelScoreButton;
    TextView editScoreButton;
    TextView leaperOneText;
    TextView leaperTwoText;
    TextView disputeScoreButton;
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


        progressDialog = new ProgressDialog(getActivity());

        dbRef = FirebaseDatabase.getInstance().getReference();
        game1DbRef = dbRef.child("leapscore").child(g1leapID).child("Game1");
        myPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();


        g1SaveScoreButton = (ImageView)view.findViewById(R.id.g1SaveScoreButton);
        g1CancelScoreButton = (ImageView)view.findViewById(R.id.g1CancelScoreButton);
        g1leaperOneScoreBox = (EditText)view.findViewById(R.id.g1leaperOneScoreBox);
        g1leaperTwoScoreBox = (EditText)view.findViewById(R.id.g1leaperTwoScoreBox);
        editScoreButton = (TextView) view.findViewById(R.id.editScoreButton);
        disputeScoreButton = (TextView) view.findViewById(R.id.disputeScoreButton);
        leaperOneText = (TextView) view.findViewById(R.id.leaperOneText);
        leaperTwoText = (TextView) view.findViewById(R.id.leaperTwoText);



        g1leaperOneScoreBox.setText("");
        g1leaperTwoScoreBox.setText("");

        leaperOneText.setText(leaperOne);
        leaperTwoText.setText(leaperTwo);

        if (Objects.equals(leaperTwo, "Open Leap")){

            editScoreButton.setVisibility(View.GONE);
            disputeScoreButton.setVisibility(View.GONE);

        }

        if (leapStatus == 0){

            editScoreButton.setVisibility(View.GONE);
            disputeScoreButton.setVisibility(View.GONE);

        }





        game1DbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("leapID").getValue() == null){

                    disputeScoreButton.setVisibility(View.GONE);

                }else {




                    editScoreButton.setVisibility(View.GONE);
                    disputeScoreButton.setVisibility(View.VISIBLE);

                    String L1Score = dataSnapshot.child("leaperOneScore").getValue().toString();
                    String L2Score = dataSnapshot.child("leaperTwoScore").getValue().toString();

                    disputeScoreButton.setVisibility(View.VISIBLE);

                    g1leaperOneScoreBox.setText(L1Score);
                    g1leaperTwoScoreBox.setText(L2Score);






                    int mL1Score = Integer.parseInt(L1Score);
                    int mL2Score = Integer.parseInt(L2Score);

                    if(mL1Score < mL2Score){
                        g1leaperOneScoreBox.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        g1leaperTwoScoreBox.setTextColor(getResources().getColor(R.color.md_green_900));
                        leaperOneText.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        leaperTwoText.setTextColor(getResources().getColor(R.color.md_green_900));


                    }
                    else if(mL1Score == mL2Score){

                        g1leaperOneScoreBox.setTextColor(getResources().getColor(R.color.grey));
                        g1leaperTwoScoreBox.setTextColor(getResources().getColor(R.color.grey));
                        leaperOneText.setTextColor(getResources().getColor(R.color.grey));
                        leaperTwoText.setTextColor(getResources().getColor(R.color.grey));


                    }

                    else if(mL1Score > mL2Score){

                        g1leaperOneScoreBox.setTextColor(getResources().getColor(R.color.md_green_900));
                        g1leaperTwoScoreBox.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        g1leaperOneScoreBox.setTextColor(getResources().getColor(R.color.md_green_900));
                        g1leaperTwoScoreBox.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                    }




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

        disputeScoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

                    leaperOneText.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    leaperTwoText.setTextColor(getResources().getColor(R.color.md_green_900));
                    g1leaperOneScoreBox.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    g1leaperTwoScoreBox.setTextColor(getResources().getColor(R.color.md_green_900));


                }
                else if(mL1Score == mL2Score){

                    leaperOneText.setTextColor(getResources().getColor(R.color.grey));
                    leaperTwoText.setTextColor(getResources().getColor(R.color.grey));
                    g1leaperOneScoreBox.setTextColor(getResources().getColor(R.color.grey));
                    g1leaperTwoScoreBox.setTextColor(getResources().getColor(R.color.grey));
                    looser  = "";
                    winner = "";


                }

                else if(mL1Score > mL2Score){

                    leaperOneText.setTextColor(getResources().getColor(R.color.md_green_900));
                    leaperTwoText.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                    g1leaperOneScoreBox.setTextColor(getResources().getColor(R.color.md_green_900));
                    g1leaperTwoScoreBox.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                    looser  = leaperTwo;
                    winner = leaperOne;

                }




                game1DbRef.setValue(new LeapScore(g1leapID, leaperOneScore, leaperTwoScore, "1", myPhoneNumber, winner, looser));

                g1leaperOneScoreBox.setEnabled(false);
                g1leaperTwoScoreBox.setEnabled(false);
                g1SaveScoreButton.setVisibility(View.GONE);
                g1CancelScoreButton.setVisibility(View.GONE);
                editScoreButton.setVisibility(View.VISIBLE);

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









        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        g1leaperOneScoreBox = (EditText)view.findViewById(R.id.g1leaperOneScoreBox);
        g1leaperTwoScoreBox = (EditText)view.findViewById(R.id.g1leaperTwoScoreBox);
    }
}
