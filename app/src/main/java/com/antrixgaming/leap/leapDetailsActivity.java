package com.antrixgaming.leap;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.TimeUtils;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.antrixgaming.leap.Fragments.MatchFragments.game1Fragment;
import com.antrixgaming.leap.Fragments.MatchFragments.game2Fragment;
import com.antrixgaming.leap.Fragments.MatchFragments.game3Fragment;
import com.antrixgaming.leap.Fragments.MatchFragments.game4Fragment;
import com.antrixgaming.leap.Fragments.MatchFragments.game5Fragment;
import com.antrixgaming.leap.LeapClasses.ConfirmDialog;
import com.antrixgaming.leap.LeapClasses.LeapUtilities;
import com.antrixgaming.leap.LeapClasses.MenuFAB;
import com.antrixgaming.leap.Models.UserLeap;
import com.firebase.ui.database.FirebaseListAdapter;
import com.github.florent37.singledateandtimepicker.SingleDateAndTimePicker;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import cn.iwgang.countdownview.CountdownView;
import de.hdodenhof.circleimageview.CircleImageView;


public class leapDetailsActivity extends BaseActivity {

    public String mGameFormat = null;

    DatabaseReference scoreDbRef;
    DatabaseReference dbRef;



    private MaterialSheetFab materialSheetFab;
    private int statusBarColor;
    public String leaperTwoUID;
    public String leaperOneUID;

    int loadFlag = 0;
    public String AdminFlag = "false";
    private String TrueAdmin;
    private String FalseAdmin;

    MenuFAB fab;
    View sheetView;
    View overlay;
    int sheetColor;
    int fabColor;

    public int leapStatus;



    LeapUtilities leapUtilities;

    StorageReference mStorage;
    StorageReference mLeaperOneStorageRef;
    StorageReference mLeaperTwoStorageRef;
    CircleImageView detailsLeaperOneImage;
    CircleImageView detailsLeaperTwoImage;


    String mLeaperOneUID;
    String mLeaperTwoUID;

    public String leapID;
    public String sourceActivity;
    public String mLeaperOne;
    public String mLeaperTwo;
    public String mCircleID;


    Long mleaperOneScore;
    Long mleaperTwoScore;

    Query leapDbRef;
    public String circleID;

    ConfirmDialog confirmDialog;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leap_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        leapUtilities = new LeapUtilities();
        TrueAdmin = "true";
        FalseAdmin = "false";

        confirmDialog = new ConfirmDialog();
        context = leapDetailsActivity.this;


        fab = (MenuFAB) findViewById(R.id.leapDetailsMenuFAB);
        sheetView = findViewById(R.id.leapDetailsFab_sheet);
        overlay = findViewById(R.id.leapDetailsDimOverlay);
        sheetColor = getResources().getColor(R.color.white);
        fabColor = getResources().getColor(R.color.colorPrimary);

        setupFab();


        final String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String myPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        dbRef = FirebaseDatabase.getInstance().getReference();

        mStorage = FirebaseStorage.getInstance().getReference();







        // Create the adapter that will return a fragment for each of the
        // primary sections of the activity.
        // source activities:
        // 1 - FragmentLeaps from main screen
        // 2 - Open Fragment from circle
        // 3 - Live fragment from circle

        Bundle bundle = getIntent().getExtras();
        leapID = bundle.getString("leapID");
        sourceActivity = bundle.getString("sourceActivity");

        if (Objects.equals(sourceActivity, "1")){

                    leapDbRef = dbRef.child("leaps").child(myUID).orderByKey().equalTo(leapID);


        } else if (Objects.equals(sourceActivity, "2")){

                    circleID = bundle.getString("circleID");


                    leapDbRef = dbRef.child("leapsforcircles").child("openleaps").child(circleID).orderByKey().equalTo(leapID);




                                DatabaseReference circleIDRef = dbRef.child("groupcirclemembers").child(circleID).child("currentmembers").child(myPhoneNumber);
                                circleIDRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        AdminFlag = dataSnapshot.child("admin").getValue().toString();


                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });



        } else if (Objects.equals(sourceActivity, "3")) {
                    circleID = bundle.getString("circleID");
                    AdminFlag = bundle.getString("AdminFlag");


                    leapDbRef = dbRef.child("leapsforcircles").child("liveleaps").child(circleID).orderByKey().equalTo(leapID);

                    DatabaseReference circleIDRef = dbRef.child("groupcirclemembers").child(circleID).child("currentmembers").child(myPhoneNumber);
                    circleIDRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            AdminFlag = dataSnapshot.child("admin").getValue().toString();

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

        }




        scoreDbRef = dbRef.child("leapscore").child(leapID);












        ListView listOfDetails = (ListView)findViewById(R.id.list_of_Leap_details);
        FirebaseListAdapter<UserLeap> adapter;
        adapter = new FirebaseListAdapter<UserLeap>(this, UserLeap.class,
                R.layout.activity_leap_details_listview,
                leapDbRef) {

            @Override
            protected void populateView(final View v, final UserLeap model, int position) {

                // Get references to the views of message.xml
                //final TextView leapID = (TextView)v.findViewById(R.id.leapID);
                TextView gameType = (TextView)v.findViewById(R.id.detailsGameType);
                TextView gameFormat = (TextView)v.findViewById(R.id.detailsGameFormat);
                final TextView countdownTimer = (TextView)v.findViewById(R.id.detailsCountdownTimer);
                final TextView leaperOne = (TextView)v.findViewById(R.id.detailsLeaperOne);
                final TextView leaperTwo = (TextView)v.findViewById(R.id.detailsLeaperTwo);
                final TextView leaperOneScore = (TextView)v.findViewById(R.id.detailsLeaperOneScore);
                final TextView leaperTwoScore = (TextView)v.findViewById(R.id.detailsLeaperTwoScore);
                TextView gameTime = (TextView)v.findViewById(R.id.detailsLeapTime);
                final CardView circleDetailsLayout = (CardView) v.findViewById(R.id.circleDetailsLayout);


                final TextView displayedLeaperOneName = (TextView)v.findViewById(R.id.displayedLeaperOneName);
                final TextView displayedLeaperTwoName = (TextView)v.findViewById(R.id.displayedLeaperTwoName);

                CountdownView countdownTimerA = (CountdownView)v.findViewById(R.id.countdownTimerA);




















                //final TextView leapInText = (TextView)v.findViewById(R.id.leapInText);
                //final TextView leapOutText = (TextView)v.findViewById(R.id.leapOutText);
                final TextView mcircleID = (TextView)v.findViewById(R.id.circleID);




                // FAB MENU ITEMS
                LinearLayout leapDetailsLeapInLayout = (LinearLayout)findViewById(R.id.leapDetailsLeapInLayout); // enclosing layout
                LinearLayout leapDetailsLeapIn = (LinearLayout)findViewById(R.id.leapDetailsLeapIn);
                LinearLayout leapDetailsLeapOut = (LinearLayout)findViewById(R.id.leapDetailsLeapOut);

                LinearLayout leapDetailsScoreLayout = (LinearLayout)findViewById(R.id.leapDetailsScoreLayout); // enclosing layout
                LinearLayout leapDetailsEnterScore = (LinearLayout)findViewById(R.id.leapDetailsEnterScore);
                RelativeLayout leapDetailsDisputeScore = (RelativeLayout)findViewById(R.id.leapDetailsDisputeScore);
                LinearLayout leapDetailsPostpone = (LinearLayout)findViewById(R.id.leapDetailsPostpone);
                LinearLayout leapDetailsCancel = (LinearLayout)findViewById(R.id.leapDetailsCancel);





                mLeaperOne = model.getleaperOne();
                mLeaperTwo = model.getleaperTwo();
                final String mLeapID = model.getleapID();
                mCircleID = model.getCircleID();
                final String mPhoneNumber = myPhoneNumber;

                detailsLeaperOneImage = (CircleImageView)v.findViewById(R.id.detailsLeaperOneImage);
                detailsLeaperTwoImage = (CircleImageView)v.findViewById(R.id.detailsLeaperTwoImage);



                leaperOne.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(leapDetailsActivity.this, leaperProfileActivity.class);
                        intent.putExtra("leaperPhoneNumber", leaperOne.getText().toString());
                        startActivity(intent);


                    }
                });

                displayedLeaperOneName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(leapDetailsActivity.this, leaperProfileActivity.class);
                        intent.putExtra("leaperPhoneNumber", leaperOne.getText().toString());
                        startActivity(intent);
                    }
                });

                detailsLeaperOneImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(leapDetailsActivity.this, leaperProfileActivity.class);
                        intent.putExtra("leaperPhoneNumber", leaperOne.getText().toString());
                        startActivity(intent);
                    }
                });





                FirebaseDatabase.getInstance().getReference().child("profileImageTimestamp").child(model.leaperOne)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (dataSnapshot.hasChildren()){

                                    String timestamp = dataSnapshot.child(model.leaperOne).getValue().toString();
                                    mLeaperOneStorageRef = mStorage.child("leaperProfileImage").child(model.leaperOne).child(model.leaperOne);
                                    leapUtilities.CircleImageFromFirebase(leapDetailsActivity.this, mLeaperOneStorageRef, detailsLeaperOneImage, timestamp);



                                }



                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });







                if (Objects.equals(model.getleaperTwo(), "Leaper Two") || Objects.equals(model.getleaperTwo(), "Open Leap")){


                }else {




                    FirebaseDatabase.getInstance().getReference().child("profileImageTimestamp").child(model.leaperTwo)
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.hasChildren()){

                                        String timestamp = dataSnapshot.child(model.leaperTwo).getValue().toString();

                                        mLeaperTwoStorageRef = mStorage.child("leaperProfileImage").child(model.leaperTwo).child(model.leaperTwo);
                                        leapUtilities.CircleImageFromFirebase(leapDetailsActivity.this, mLeaperTwoStorageRef, detailsLeaperTwoImage, timestamp);




                                    }



                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });




                    leaperTwo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(leapDetailsActivity.this, leaperProfileActivity.class);
                            intent.putExtra("leaperPhoneNumber", leaperTwo.getText().toString());
                            startActivity(intent);


                        }
                    });

                    displayedLeaperTwoName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(leapDetailsActivity.this, leaperProfileActivity.class);
                            intent.putExtra("leaperPhoneNumber", leaperTwo.getText().toString());
                            startActivity(intent);
                        }
                    });

                    detailsLeaperTwoImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(leapDetailsActivity.this, leaperProfileActivity.class);
                            intent.putExtra("leaperPhoneNumber", leaperTwo.getText().toString());
                            startActivity(intent);

                        }
                    });

                }













                //final TextView leapsLeaperTwoUID = (TextView) v.findViewById(R.id.leapsLeaperTwoUID);



                /// SPLIT LEAP TIME OF LONG FORMAT INTO DAY AND TIME
                CharSequence mDay = DateFormat.format("dd-MMM-yyyy", model.getleapDay());
                CharSequence mTime = DateFormat.format("HH:mm", model.getleapDay());


                CharSequence lDay;


                if (DateUtils.isToday(model.getleapDay())){
                    lDay = "Today";

                }
                else{
                    lDay = mDay;
                }


                final CharSequence lTime = mTime;




                mGameFormat = model.getgameFormat();

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










                // Set their texts
                //leapID = model.getleapID();
                gameType.setText(model.getgameType());
                gameFormat.setText(model.getgameFormat());
                gameTime.setText(lDay + ", " +lTime);
                leaperOne.setText(model.getleaperOne());
                leaperTwo.setText(model.getleaperTwo());
                //leaperOneScore.setText(model.leaperOneScore);
                //leaperTwoScore.setText(model.leaperTwoScore);






                leapStatus = Integer.parseInt(model.getleapStatus());


                // set conditions
                // countdown timer
                // leap button status
                // 0 - new leap
                // 1 - accepted leap
                // 2 - declined leap
                // 3 - cancelled leap


                if (Objects.equals(myPhoneNumber, model.leaperOne) || Objects.equals(myPhoneNumber, model.leaperTwo) || Objects.equals(AdminFlag, TrueAdmin)){



                    switch (leapStatus){

                        case 0:
                            fab.setVisibility(v.VISIBLE);
                            leapDetailsLeapInLayout.setVisibility(v.VISIBLE);
                            leapDetailsScoreLayout.setVisibility(v.GONE);


                            break;


                        case 1:
                            fab.setVisibility(v.VISIBLE);
                            leapDetailsLeapInLayout.setVisibility(v.GONE);
                            leapDetailsScoreLayout.setVisibility(v.VISIBLE);

                            break;



                        case 2:
                            fab.setVisibility(v.GONE);
                            leapDetailsLeapInLayout.setVisibility(v.GONE);
                            leapDetailsScoreLayout.setVisibility(v.GONE);

                            break;



                        case 3:
                            fab.setVisibility(v.GONE);
                            leapDetailsLeapInLayout.setVisibility(v.GONE);
                            leapDetailsScoreLayout.setVisibility(v.GONE);

                            break;


                    }



                } else {


                    fab.setVisibility(v.GONE);
                    leapDetailsLeapInLayout.setVisibility(v.GONE);
                    leapDetailsScoreLayout.setVisibility(v.GONE);


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

                        mleaperTwoScore = dataSnapshot.getChildrenCount();
                        leaperTwoScore.setText(String.valueOf(mleaperTwoScore));

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });




/*
                scoreDbRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.child("Game1").child("winner").getValue() == null){



                        }else {




                            if(mleaperOneScore < mleaperTwoScore){
                                leaperOneScore.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                                leaperTwoScore.setTextColor(getResources().getColor(R.color.md_green_900));
                                leaperOne.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                                leaperTwo.setTextColor(getResources().getColor(R.color.md_green_900));



                            }
                            else if(Objects.equals(mleaperOneScore, mleaperTwoScore)){

                                leaperOneScore.setTextColor(getResources().getColor(R.color.grey));
                                leaperTwoScore.setTextColor(getResources().getColor(R.color.grey));
                                leaperOne.setTextColor(getResources().getColor(R.color.grey));
                                leaperTwo.setTextColor(getResources().getColor(R.color.grey));


                            }

                            else if(mleaperOneScore > mleaperTwoScore){

                                leaperOneScore.setTextColor(getResources().getColor(R.color.md_green_900));
                                leaperTwoScore.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                                leaperOne.setTextColor(getResources().getColor(R.color.md_green_900));
                                leaperTwo.setTextColor(getResources().getColor(R.color.colorPrimaryDark));

                            }




                        }

                    }





                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


    */













                //// IF THE SENDER IS THE ONE OPENING THE LEAP
                if (Objects.equals(model.getleaperOne(), myPhoneNumber)){

                    leapDetailsLeapIn.setVisibility(v.GONE);
                    leapDetailsLeapOut.setVisibility(v.VISIBLE);
                    leapDetailsPostpone.setVisibility(v.VISIBLE);
                    leapDetailsCancel.setVisibility(v.VISIBLE);





                    //// CHECK IF IT'S AN OPEN LEAP. IF YES THERE'S NO LEAPER TWO UID
                    if (Objects.equals(model.getleaperTwo(), "Open Leap")) {

                        leaperOneUID = myUID;




                    }


                    //// IF IT'S NOT AN OPEN LEAP, THAT MEANS LEAPER TWO PRESENT. SO USING
                    //// HIS PHONE NUMBER (MODEL.GETLEAPERTWO) FIND HIS UID
                    else{
                        DatabaseReference leaperTwoDbRef = FirebaseDatabase.getInstance().getReference()
                                .child("phonenumbers").child(model.getleaperTwo()).child("uid");

                        leaperTwoDbRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                leaperOneUID = myUID;
                                leaperTwoUID = dataSnapshot.getValue().toString();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }



                /// IF THE PERSON TO ACCEPT IS THE ONE OPENING THE LEAP
                else{

                    if (leapStatus == 0){
                        leapDetailsLeapInLayout.setVisibility(v.VISIBLE);
                        fab.setVisibility(v.VISIBLE);
                        leapDetailsLeapIn.setVisibility(v.VISIBLE);

                    } else if (leapStatus == 1){
                        fab.setVisibility(v.VISIBLE);
                        leapDetailsLeapInLayout.setVisibility(v.GONE);
                        leapDetailsLeapIn.setVisibility(v.VISIBLE);
                        leapDetailsLeapOut.setVisibility(v.VISIBLE);
                        leapDetailsPostpone.setVisibility(v.VISIBLE);
                        leapDetailsCancel.setVisibility(v.VISIBLE);

                    } else {
                        fab.setVisibility(v.GONE);
                    }





                    //// WHETHER IT'S AN OPEN LEAP OR NOT, THE ONE OPENING IT IS A THE SECOND LEAPER / ACCEPTOR

                    ///// FIND THE UID FOR THE SENDER (MODEL.GETLEAPERONE)
                    DatabaseReference leaperTwoDbRef = FirebaseDatabase.getInstance().getReference()
                            .child("phonenumbers").child(model.getleaperOne()).child("uid");

                    leaperTwoDbRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            leaperOneUID = dataSnapshot.getValue().toString();
                            leaperTwoUID = myUID;
                        }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });





                }



                //// IF IT'S AN ADMIN OPENING THE LEAP
                if (Objects.equals(AdminFlag, TrueAdmin)) {
                    leapDetailsPostpone.setVisibility(v.VISIBLE);
                    leapDetailsCancel.setVisibility(v.VISIBLE);
                }

                else if (!Objects.equals(AdminFlag, FalseAdmin)) {
                    //leapDetailsPostpone.setVisibility(v.GONE);
                    //leapDetailsCancel.setVisibility(v.GONE);
                }

                if (Objects.equals(model.leaperTwo, "Open Leap") && !Objects.equals(myPhoneNumber, model.leaperOne)){
                    fab.setVisibility(v.VISIBLE);
                    leapDetailsLeapIn.setVisibility(v.VISIBLE);
                    leapDetailsLeapOut.setVisibility(v.VISIBLE);
                    leapDetailsPostpone.setVisibility(v.VISIBLE);
                    leapDetailsCancel.setVisibility(v.VISIBLE);
                }






                    /*
                mLeaperOneStorageRef = mStorage.child("leaperProfileImage").child(leaperOneUID).child(leaperOneUID);


                leapUtilities.CircleImageFromFirebase(leapDetailsActivity.this, mLeaperOneStorageRef, detailsLeaperOneImage);


                if (Objects.equals(model.getleaperTwo(), "Open Leap")){


                } else{
                    mLeaperTwoStorageRef = mStorage.child("leaperProfileImage").child(leaperTwoUID).child(leaperTwoUID);
                    leapUtilities.CircleImageFromFirebase(leapDetailsActivity.this, mLeaperTwoStorageRef, detailsLeaperTwoImage);



                }

                */





                // leapertwoUID is used to referenced the other leaper in context


                leapDetailsLeapIn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        confirmDialog.NewConfirmDialog(context, "Accept Leap", "Leap in and compete", "Leap In", "Cancel");
                        confirmDialog.confirmAccept.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                confirmDialog.dialog.dismiss();

                                if(Objects.equals(circleID, "null")) {


                                    // Changes for sender
                                    dbRef.child("leaps").child(leaperOneUID)
                                            .child(leapID).child("leapStatus").setValue("1");
                                    dbRef.child("leaps").child(leaperTwoUID)
                                            .child(leapID).child("leapStatusChangeTime").setValue(new Date().getTime());



                                    // Changes for accepter
                                    dbRef.child("leaps").child(leaperTwoUID)
                                            .child(leapID).child("leapStatus").setValue("1");
                                    dbRef.child("leaps").child(leaperTwoUID)
                                            .child(leapID).child("leapStatusChangeTime").setValue(new Date().getTime());


                                }



                                else{

                                    if (Objects.equals(model.getleaperTwo(), "Open Leap")){

                                        // remove leap from open leaps
                                        dbRef.child("leapsforcircles").child("openleaps").child(circleID).child(model.getleapID())
                                                .removeValue();

                                        // add to new live leaps
                                        dbRef.child("leapsforcircles").child("liveleaps").child(circleID).child(model.getleapID())
                                                .setValue(new UserLeap(model.getleapID(), model.getgameType(), model.getgameFormat(), model.getleaperOne(),
                                                        myPhoneNumber, model.getleapDay(), "1", circleID));




                                        // update for sender to show accepted // LEAPER ONE
                                        // leaper two in this case is the other leaper in context
                                        dbRef.child("leaps").child(leaperOneUID).child(model.getleapID()).child("leapStatus").setValue("1");
                                        dbRef.child("leaps").child(leaperOneUID).child(model.getleapID()).child("leaperTwo").setValue(myPhoneNumber);



                                        // update/add leap to list for acceptor // LEAPER TWO
                                        dbRef.child("leaps").child(myUID).child(model.getleapID()).setValue(new UserLeap(model.getleapID(), model.getgameType()
                                                , model.getgameFormat(), model.getleaperOne(),
                                                myPhoneNumber, model.getleapDay(), "1", circleID));


                                        finish();



                                    }

                                    else {

                                        // remove leap from leapertoleaper leaps
                                        dbRef.child("leapsforcircles").child("leapertoleaper").child(circleID).child(model.getleapID())
                                                .removeValue();

                                        // add to new live leaps
                                        dbRef.child("leapsforcircles").child("liveleaps").child(circleID).child(model.getleapID())
                                                .setValue(new UserLeap(model.getleapID(), model.getgameType(), model.getgameFormat(), model.getleaperOne(),
                                                        myPhoneNumber, model.getleapDay(), "1", circleID));



                                        /// UPDATE FOR SENDER OR LEAPER ONE
                                        dbRef.child("leaps").child(leaperOneUID).child(model.getleapID()).child("leapStatus").setValue("1");



                                        /// UPDATE FOR ACCEPTOR OR LEAPER TWO
                                        dbRef.child("leaps").child(leaperTwoUID).child(model.getleapID()).setValue(new UserLeap(model.getleapID(), model.getgameType()
                                                , model.getgameFormat(), model.getleaperOne(),
                                                myPhoneNumber, model.getleapDay(), "1", circleID));




                                    }




                                }

                                Toast.makeText(leapDetailsActivity.this, "You leaped in", Toast.LENGTH_LONG).show();


                                //Long acceptTime = model.getLeapStatusChangeTime();
                                //Long matchTime = Long.parseLong(lDay + " "  + lTime);











                            }
                        });









                    }
                });













                long NowTime = new Date().getTime();


                if (model.getleapDay() >=  NowTime){



                    countdownTimerA.start(model.getleapDay() - NowTime); // Millisecond
                    //countdownTimerA.setText("LIVE");
                    countdownTimer.setVisibility(View.GONE);
                    countdownTimerA.setVisibility(View.VISIBLE);

                }

                if (NowTime > model.getleapDay() && (NowTime < (model.getleapDay() + TimeUnit.HOURS.toMillis(6)))){

                    countdownTimer.setVisibility(View.VISIBLE);
                    countdownTimerA.setVisibility(View.GONE);

                }

                if (NowTime > model.getleapDay() && (NowTime >= (model.getleapDay() + TimeUnit.HOURS.toMillis(6)))){

                    countdownTimer.setVisibility(View.GONE);
                    countdownTimerA.setVisibility(View.GONE);

                }









                leapDetailsLeapOut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        confirmDialog.NewConfirmDialog(context, "Decline Leap", "Confirm refusal of leap invite", "Leap Out", "Cancel");
                        confirmDialog.confirmAccept.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                confirmDialog.dialog.dismiss();



                                // add leap out or decline commands here







                                /// IF I SENT THIS LEAP // I GET TO CANCEL IT
                                if (Objects.equals(myPhoneNumber, model.getleaperOne())){


                                    // IF THE LEAP IS NOT TO A CIRCLE
                                    if (Objects.equals(circleID, "null")) {

                                        // Delete leap for leaper one and two
                                        dbRef.child("leaps").child(leaperOneUID).child(leapID).removeValue();
                                        dbRef.child("leaps").child(leaperTwoUID).child(leapID).removeValue();


                                        Toast.makeText(leapDetailsActivity.this, "You leaped out", Toast.LENGTH_LONG).show();
                                        finish();


                                    } else { // IF THE LEAP IS TO A CIRCLE


                                        // IS IT AN OPEN LEAP
                                        if (Objects.equals(model.getleaperTwo(), "Open Leap")) {

                                            // remove leap from open leaps
                                            dbRef.child("leapsforcircles").child("openleaps").child(circleID).child(model.getleapID())
                                                    .removeValue();
                                            // remove from my leaps
                                            dbRef.child("leaps").child(myUID)
                                                    .child(leapID).removeValue();



                                            Toast.makeText(leapDetailsActivity.this, "You leaped out", Toast.LENGTH_LONG).show();
                                            finish();
                                        }



                                        else { // IT IS TO A PARTICULAR LEAPER

                                            // Delete fromm list of leaper-to-leaper leaps
                                            dbRef.child("leapsforcircles").child("leapertoleaper").child(circleID).child(model.getleapID())
                                                    .removeValue();


                                            // Delete leap for leaper one and two
                                            dbRef.child("leaps").child(leaperOneUID).child(leapID).removeValue();
                                            dbRef.child("leaps").child(leaperTwoUID).child(leapID).removeValue();


                                            Toast.makeText(leapDetailsActivity.this, "You leaped out", Toast.LENGTH_LONG).show();
                                            finish();

                                        }


                                    }


                                }





                                else { // IF I DIDN'T SEND THIS LEAP // I GET TO DECLINE IT


                                    // IF THE LEAP IS NOT TO A CIRCLE // meaning it is to me so i can decline it
                                    if (Objects.equals(circleID, "null")) {

                                        // Set leap status to declined for leaper one and two
                                        dbRef.child("leaps").child(leaperOneUID).child(leapID).child("leapStatus").setValue("2");
                                        dbRef.child("leaps").child(leaperTwoUID).child(leapID).child("leapStatus").setValue("2");



                                        Toast.makeText(leapDetailsActivity.this, "You leaped out", Toast.LENGTH_LONG).show();
                                        finish();


                                    }




                                    else { // IF THE LEAP IS TO A CIRCLE


                                        // IF IT IS AN OPEN LEAP AND I AM AN ADMIN I CAN CANCEL IT
                                        if (Objects.equals(myPhoneNumber, model.getleaperOne())) {

                                            // remove leap from open leaps
                                            dbRef.child("leapsforcircles").child("openleaps").child(circleID).child(model.getleapID())
                                                    .removeValue();


                                            // remove from list of leaps for the sender // leaper one
                                            dbRef.child("leaps").child(leaperOneUID).child(leapID).child("leapStatus").setValue("2");





                                            Toast.makeText(leapDetailsActivity.this, "Leap cancelled", Toast.LENGTH_LONG).show();
                                            finish();
                                        }




                                        else { // IF IT'S NOT AN OPEN LEAP AND IT WAS SENT TO ME, I CAN DECLINE IT

                                            // remove leap from leapertoleaper leaps
                                            dbRef.child("leapsforcircles").child("leapertoleaper").child(circleID).child(model.getleapID())
                                                    .removeValue();



                                            // Set leap status to declined for leaper one and two
                                            dbRef.child("leaps").child(leaperOneUID).child(leapID).child("leapStatus").setValue("2");
                                            dbRef.child("leaps").child(leaperTwoUID).child(leapID).child("leapStatus").setValue("2");




                                            Toast.makeText(leapDetailsActivity.this, "You leaped out", Toast.LENGTH_LONG).show();
                                            finish();


                                        }


                                    }





                                }


                            }
                        });







                    }
                });






                leapDetailsPostpone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {




                                new SingleDateAndTimePickerDialog.Builder(leapDetailsActivity.this)
                                        .bottomSheet()
                                        //.curved()
                                        //.minutesStep(15)
                                        //.defaultDate(new Date())
                                        //.minDateRange(new Date())

                                        //.displayHours(false)
                                        //.displayMinutes(false)
                                        .mustBeOnFuture()
                                        .backgroundColor(getResources().getColor(R.color.white))
                                        .mainColor(getResources().getColor(R.color.colorPrimary))
                                        .titleTextColor(getResources().getColor(R.color.colorPrimary))

                                        .displayListener(new SingleDateAndTimePickerDialog.DisplayListener() {
                                            @Override
                                            public void onDisplayed(SingleDateAndTimePicker picker) {
                                                //retrieve the SingleDateAndTimePicker
                                            }
                                        })

                                        .title("Leap time")
                                        .listener(new SingleDateAndTimePickerDialog.Listener() {
                                            @Override
                                            public void onDateSelected(final Date date) {confirmDialog.NewConfirmDialog(context, "Postpone Leap", "Confirm change of leap time", "Postpone", "Cancel");
                                                confirmDialog.confirmAccept.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        confirmDialog.dialog.dismiss();


                                                        CharSequence mDay = DateFormat.format("dd/MMM/yyyy", date.getTime());

                                                        CharSequence mTime = DateFormat.format("HH:mm", date.getTime());

                                                        long leapDay = date.getTime();







                                                        /// IF I SENT THIS LEAP // I GET TO CHANGE TIME
                                                        if (Objects.equals(myPhoneNumber, model.getleaperOne())){


                                                            // IF THE LEAP IS NOT TO A CIRCLE
                                                            if (Objects.equals(circleID, "null")) {

                                                                // Change time for leaper one and two
                                                                dbRef.child("leaps").child(leaperOneUID).child(leapID).child("leapDay").setValue(leapDay);
                                                                dbRef.child("leaps").child(leaperTwoUID).child(leapID).child("leapDay").setValue(leapDay);


                                                                Toast.makeText(leapDetailsActivity.this, "Time changed", Toast.LENGTH_LONG).show();

                                                            } else { // IF THE LEAP IS TO A CIRCLE

                                                                // IS IT AN OPEN LEAP
                                                                if (Objects.equals(model.getleaperTwo(), "Open Leap")) {

                                                                    // change time for open leaps
                                                                    dbRef.child("leapsforcircles").child("openleaps").child(circleID).child(model.getleapID()).child("leapDay").setValue(leapDay);
                                                                    // change time for my leaps
                                                                    dbRef.child("leaps").child(myUID).child(leapID).child("leapDay").setValue(leapDay);



                                                                    Toast.makeText(leapDetailsActivity.this, "Time changed", Toast.LENGTH_LONG).show();
                                                                }



                                                                else { // IT IS TO A PARTICULAR LEAPER

                                                                    // change time for list of leaper-to-leaper leaps
                                                                    dbRef.child("leapsforcircles").child("leapertoleaper").child(circleID).child(model.getleapID()).child("leapDay").setValue(leapDay);


                                                                    // Delete leap for leaper one and two
                                                                    dbRef.child("leaps").child(leaperOneUID).child(leapID).child("leapDay").setValue(leapDay);
                                                                    dbRef.child("leaps").child(leaperTwoUID).child(leapID).child("leapDay").setValue(leapDay);


                                                                    Toast.makeText(leapDetailsActivity.this, "You leaped out", Toast.LENGTH_LONG).show();

                                                                }


                                                            }


                                                        }





                                                        else { // IF I DIDN'T SEND THIS LEAP // I GET TO DECLINE IT


                                                            // IF THE LEAP IS NOT TO A CIRCLE // meaning it is to me so i can change the time
                                                            if (Objects.equals(circleID, "null")) {

                                                                //change time for leaper one and two
                                                                dbRef.child("leaps").child(leaperOneUID).child(leapID).child("leapDay").setValue(leapDay);
                                                                dbRef.child("leaps").child(leaperTwoUID).child(leapID).child("leapDay").setValue(leapDay);



                                                                Toast.makeText(leapDetailsActivity.this, "Time changed", Toast.LENGTH_LONG).show();


                                                            }




                                                            else { // IF THE LEAP IS TO A CIRCLE


                                                                // IF IT IS AN OPEN LEAP AND I AM AN ADMIN I CAN CHANGE THE TIME
                                                                if (Objects.equals(myPhoneNumber, model.getleaperOne())) {

                                                                    // change timie for open leaps
                                                                    dbRef.child("leapsforcircles").child("openleaps").child(circleID).child(model.getleapID()).child("leapDay").setValue(leapDay);


                                                                    // change time for the sender // leaper one
                                                                    dbRef.child("leaps").child(leaperOneUID).child(leapID).child("leapDay").setValue(leapDay);





                                                                    Toast.makeText(leapDetailsActivity.this, "Time changed", Toast.LENGTH_LONG).show();
                                                                }




                                                                else { // IF IT'S NOT AN OPEN LEAP AND IT WAS SENT TO ME, I CAN CHANGE TIME

                                                                    // change time for leapertoleaper leaps
                                                                    dbRef.child("leapsforcircles").child("leapertoleaper").child(circleID).child(model.getleapID()).child("leapDay").setValue(leapDay);



                                                                    // change time for leaper one and two
                                                                    dbRef.child("leaps").child(leaperOneUID).child(leapID).child("leapDay").setValue(leapDay);
                                                                    dbRef.child("leaps").child(leaperTwoUID).child(leapID).child("leapDay").setValue(leapDay);




                                                                    Toast.makeText(leapDetailsActivity.this, "Time changed", Toast.LENGTH_LONG).show();


                                                                }


                                                            }





                                                        }


                                                    }
                                                });










                                            }
                                        }).display();
                            }
                        });























                leapDetailsCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        confirmDialog.NewConfirmDialog(context, "Cancel Leap", "Confirm leap cancel, entered score will no longer apply", "Cancel Leap", "Cancel");
                        confirmDialog.confirmAccept.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                confirmDialog.dialog.dismiss();


                                /// IF I SENT THIS LEAP // I GET TO CHANGE TIME
                                if (Objects.equals(myPhoneNumber, model.getleaperOne())){



                                    // IF THE LEAP IS NOT TO A CIRCLE
                                    if (Objects.equals(circleID, "null")) {

                                        // Change time for leaper one and two
                                        dbRef.child("leaps").child(leaperOneUID).child(leapID).child("leapStatus").setValue("3");
                                        dbRef.child("leaps").child(leaperTwoUID).child(leapID).child("leapStatus").setValue("3");


                                        Toast.makeText(leapDetailsActivity.this, "Leap cancelled", Toast.LENGTH_LONG).show();

                                    } else { // IF THE LEAP IS TO A CIRCLE

                                        // IS IT AN OPEN LEAP
                                        if (Objects.equals(model.getleaperTwo(), "Open Leap")) {

                                            // change time for open leaps
                                            dbRef.child("leapsforcircles").child("openleaps").child(circleID).child(model.getleapID()).child("leapStatus").setValue("3");
                                            // change time for my leaps
                                            dbRef.child("leaps").child(myUID).child(leapID).child("leapStatus").setValue("3");



                                            Toast.makeText(leapDetailsActivity.this, "Leap cancelled", Toast.LENGTH_LONG).show();
                                        }



                                        else { // IT IS TO A PARTICULAR LEAPER

                                            // change time for list of leaper-to-leaper leaps
                                            dbRef.child("leapsforcircles").child("leapertoleaper").child(circleID).child(model.getleapID()).child("leapStatus").setValue("3");


                                            // Delete leap for leaper one and two
                                            dbRef.child("leaps").child(leaperOneUID).child(leapID).child("leapStatus").setValue("3");
                                            dbRef.child("leaps").child(leaperTwoUID).child(leapID).child("leapStatus").setValue("3");


                                            Toast.makeText(leapDetailsActivity.this, "Leap cancelled", Toast.LENGTH_LONG).show();

                                        }


                                    }


                                }





                                else { // IF I DIDN'T SEND THIS LEAP // I GET TO DECLINE IT


                                    // IF THE LEAP IS NOT TO A CIRCLE // meaning it is to me so i can change the time
                                    if (Objects.equals(circleID, "null")) {

                                        //change time for leaper one and two
                                        dbRef.child("leaps").child(leaperOneUID).child(leapID).child("leapStatus").setValue("3");
                                        dbRef.child("leaps").child(leaperTwoUID).child(leapID).child("leapStatus").setValue("3");


                                        Toast.makeText(leapDetailsActivity.this, "Leap cancelled", Toast.LENGTH_LONG).show();


                                    } else { // IF THE LEAP IS TO A CIRCLE


                                        // IF IT IS AN OPEN LEAP AND I AM AN ADMIN I CAN CHANGE THE TIME
                                        if (Objects.equals(myPhoneNumber, model.getleaperOne())) {

                                            // change timie for open leaps
                                            dbRef.child("leapsforcircles").child("openleaps").child(circleID).child(model.getleapID()).child("leapStatus").setValue("3");


                                            // change time for the sender // leaper one
                                            dbRef.child("leaps").child(leaperOneUID).child(leapID).child("leapStatus").setValue("3");


                                            Toast.makeText(leapDetailsActivity.this, "Leap cancelled", Toast.LENGTH_LONG).show();
                                        } else { // IF IT'S NOT AN OPEN LEAP AND IT WAS SENT TO ME, I CAN CHANGE TIME

                                            // change time for leapertoleaper leaps
                                            dbRef.child("leapsforcircles").child("leapertoleaper").child(circleID).child(model.getleapID()).child("leapStatus").setValue("3");


                                            // change time for leaper one and two
                                            dbRef.child("leaps").child(leaperOneUID).child(leapID).child("leapStatus").setValue("3");
                                            dbRef.child("leaps").child(leaperTwoUID).child(leapID).child("leapStatus").setValue("3");


                                            Toast.makeText(leapDetailsActivity.this, "Leap cancelled", Toast.LENGTH_LONG).show();


                                        }


                                    }


                                }

                            }
                        });



                    }
                });












                switch(loadFlag){




                    case 0:

                        TabLayout tabLayout;
                        SectionsPagerAdapter mSectionsPagerAdapter;
                        ViewPager mViewPager;

                        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

                        mViewPager = (ViewPager) v.findViewById(R.id.detailsMatchContainer);




                        // Set up the ViewPager with the sections adapter.
                        mViewPager.setAdapter(mSectionsPagerAdapter);

                        //Tab layout with tabs
                        tabLayout = (TabLayout) v.findViewById(R.id.detailsMatchTabs);
                        tabLayout.setupWithViewPager(mViewPager);
                        loadFlag = 1;
                        break;

                    case 1:


                        break;

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
                                                        .getValue().toString(), "")) {///IF THEY DON'T HAVE AN ENTRY USE THEIR PHONE NUMBER

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
                    /////////////////////////////////////////////////////////////////////


                }



















                }







            class SectionsPagerAdapter extends FragmentPagerAdapter {




                public SectionsPagerAdapter(android.support.v4.app.FragmentManager fm) {
                    super(fm);
                }

                @Override
                public android.support.v4.app.Fragment getItem(int position) {
                    switch (position) {
                        case 0:
                            return new game1Fragment();
                        case 1:
                            return new game2Fragment();
                        case 2:
                            return new game3Fragment();
                        case 3:
                            return new game4Fragment();
                        case 4:
                            return new game5Fragment();
                        default:
                            return null;
                    }
                }

                @Override
                public int getCount() {

                    int x = 1;
                    switch (mGameFormat){
                        case "single match":
                            x = 1;
                            break;
                        case "home and away":
                            x = 2;
                            break;
                        case "best of three":
                            x = 3;
                            break;
                        case "straight three":
                            x = 3;
                            break;
                        case "best of five":
                            x = 5;
                            break;
                        case "straight five":
                            x = 5;
                            break;
                    }

                    return x;
                }

                @Override
                public CharSequence getPageTitle(int position) {
                    switch (position) {
                        case 0:
                            return "Game 1";
                        case 1:
                            return "Game 2";
                        case 2:
                            return "Game 3";
                        case 3:
                            return "Game 4";
                        case 4:
                            return "Game 5";
                        default:
                            return null;

                    }
                }


            }







        };

        listOfDetails.setAdapter(adapter);




    }










    private void setupFab() {



        // Create material sheet FAB
        materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay, sheetColor, fabColor);

        // Set material sheet event listener
        materialSheetFab.setEventListener(new MaterialSheetFabEventListener() {
            @Override
            public void onShowSheet() {
                // Save current status bar color
                statusBarColor = getStatusBarColor();
                // Set darker status bar color to match the dim overlay
                setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            }

            @Override
            public void onHideSheet() {
                // Restore status bar color
                setStatusBarColor(statusBarColor);
            }
        });









    }

    private int getStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getWindow().getStatusBarColor();
        }
        return 0;
    }

    private void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(color);
        }
    }









    public static class TutsPlusBottomSheetDialogFragment extends BottomSheetDialogFragment {

        private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    dismiss();
                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        };

        @Override
        public void setupDialog(Dialog dialog, int style) {
            super.setupDialog(dialog, style);
            View contentView = View.inflate(getContext(), R.layout.fragment_leap_history, null);
            dialog.setContentView(contentView);

            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
            CoordinatorLayout.Behavior behavior = params.getBehavior();

            if( behavior != null && behavior instanceof BottomSheetBehavior ) {
                ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
            }
        }
    }














    @Override
    public void onBackPressed() {
        if (materialSheetFab.isSheetVisible()) {
            materialSheetFab.hideSheet();
        } else {
            super.onBackPressed();
        }
    }




    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }


}
