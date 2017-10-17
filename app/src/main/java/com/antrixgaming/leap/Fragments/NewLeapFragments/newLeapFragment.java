package com.antrixgaming.leap.Fragments.NewLeapFragments;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.antrixgaming.leap.LeapClasses.LeapUtilities;
import com.antrixgaming.leap.Models.GameList;
import com.antrixgaming.leap.Models.UserLeap;
import com.antrixgaming.leap.R;
import com.antrixgaming.leap.leapDetailsActivity;
import com.antrixgaming.leap.newLeap;
import com.antrixgaming.leap.selectLeaperContact;
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

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class newLeapFragment extends Fragment implements newLeap.KeyEventListener {

    private EditText timeTextView;
    private EditText dateTextView;
    private Button leapButton;
    private TextView leaperOne;
    private TextView leaperTwo;
    private String myUID;
    private TextView gameType;
    private Spinner gameFormat;
    private CircleImageView leaperOneImage;
    private CircleImageView leaperTwoImage;
    private String leaperTwoUID;
    private TextView newLeaperTwoUID;
    private DatabaseReference dbRef;
    private EditText searchBox;
    private ImageButton searchButton;
    FrameLayout newLeapDimmer;
    CircleImageView gameTypeImage;
    private TextView gameTitle;


    private String circleID = "null";
    String leapedPhoneNumber = "null";
    String myPhoneNumber;


    LeapUtilities leapUtilities;

    StorageReference mStorage;
    StorageReference mLeaperOneStorageRef;
    StorageReference mLeaperTwoStorageRef;
    CircleImageView detailsLeaperOneImage;
    CircleImageView detailsLeaperTwoImage;

    Long leapDay;


    TextView displayedLeaperOneName;
    TextView displayedLeaperTwoName;



    private int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_new_leap, container, false);

        leapUtilities = new LeapUtilities();
        mStorage = FirebaseStorage.getInstance().getReference();
        leapDay = new Date().getTime();


        dbRef = FirebaseDatabase.getInstance().getReference();

        myPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

        myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        timeTextView = (EditText) view.findViewById(R.id.freeLeapTime);
        dateTextView = (EditText) view.findViewById(R.id.freeLeapDate);
        leapButton = (Button) view.findViewById(R.id.freeLeapConfirmButton);
        leaperOne = (TextView) view.findViewById(R.id.leaperOne);
        leaperTwo = (TextView) view.findViewById(R.id.leaperTwo);
        gameType = (TextView) view.findViewById(R.id.freeLeapGameType);
        gameFormat = (Spinner) view.findViewById(R.id.freeLeapGameFormat);
        leaperOneImage = (CircleImageView) view.findViewById(R.id.freeLeapLeaper1Image);
        leaperTwoImage = (CircleImageView) view.findViewById(R.id.freeLeapLeaper2Image);
        gameTypeImage = (CircleImageView) view.findViewById(R.id.freeLeapGameImage);
        newLeaperTwoUID = (TextView) view.findViewById(R.id.freeLeaperTwoUID);
        searchBox = (EditText)view.findViewById(R.id.newLeapSearchBox);
        newLeapDimmer = (FrameLayout)view.findViewById(R.id.newLeapDimmer);

        displayedLeaperOneName = (TextView)view.findViewById(R.id.displayedLeaperOneName);
        displayedLeaperTwoName = (TextView)view.findViewById(R.id.displayedLeaperTwoName);





        mLeaperOneStorageRef = mStorage.child("leaperProfileImage").child(myPhoneNumber).child(myPhoneNumber);
        //mLeaperTwoStorageRef = mStorage.child("leaperProfileImage").child(leaperTwoUID).child(leaperTwoUID);

        leapUtilities.CircleImageFromFirebase(getActivity(), mLeaperOneStorageRef, leaperOneImage);
        //leapUtilities.CircleImageFromFirebase(getActivity(), mLeaperTwoStorageRef, leaperTwoImage);




        //Check for extras in bundle
        if (getActivity().getIntent().getExtras() == null){

        } else {

            /// Source Activity Information
            /// 1 - originating from One Circle Activity, Leap button pressed circleLeaperListFragment
            /// 2 - originating from One Circle Activity, Open Leap button pressed from activity_one_circle

            Bundle bundle = getActivity().getIntent().getExtras();
            leapedPhoneNumber = bundle.getString("leapedPhoneNumber");
            String sourceActivity = bundle.getString("SourceActivity");

            switch (sourceActivity){

                case "1":
                    circleID = bundle.getString("circleID");
                    leaperTwo.setText(leapedPhoneNumber);


                    mLeaperTwoStorageRef = mStorage.child("leaperProfileImage").child(leapedPhoneNumber).child(leapedPhoneNumber);
                    leapUtilities.CircleImageFromFirebase(getActivity(), mLeaperTwoStorageRef, leaperTwoImage);



                    break;
                case "2":
                    circleID = bundle.getString("circleID");
                    //leaperOne.setText(leapedPhoneNumber);
                    leaperTwo.setText(R.string.open_leap); /// "Open Leap"
                    break;

                case "3":
                    circleID = "null";
                    leaperTwo.setText(leapedPhoneNumber);


                    mLeaperTwoStorageRef = mStorage.child("leaperProfileImage").child(leapedPhoneNumber).child(leapedPhoneNumber);
                    leapUtilities.CircleImageFromFirebase(getActivity(), mLeaperTwoStorageRef, leaperTwoImage);


            }


        }




        leaperOne.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());

        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                new SingleDateAndTimePickerDialog.Builder(getContext())
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
                            public void onDateSelected(Date date) {

                                leapDay = date.getTime();


                                CharSequence mDay = DateFormat.format("dd-MMM-yyyy", leapDay);

                                CharSequence mTime = DateFormat.format("HH:mm", leapDay);


                                timeTextView.setText(mTime);
                                dateTextView.setText(mDay);


                            }
                        }).display();





            }


        });

        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new SingleDateAndTimePickerDialog.Builder(getContext())
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
                            public void onDateSelected(Date date) {

                                leapDay = date.getTime();


                                CharSequence mDay = DateFormat.format("dd/MMM/yyyy", leapDay);

                                CharSequence mTime = DateFormat.format("HH:mm", leapDay);


                                timeTextView.setText(mTime);
                                dateTextView.setText(mDay);


                            }
                        }).display();
            }
        });

        leapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // make sure actual time is selected
                if (TextUtils.isEmpty(dateTextView.getText().toString())) {
                    //mVerificationField.setError("Cannot be empty.");
                    dateTextView.setHintTextColor(getResources().getColor(R.color.cherry));
                    timeTextView.setHintTextColor(getResources().getColor(R.color.cherry));
                    Snackbar.make(getView(), "Leap day and time required", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();



                    return;
                }


                // initialize views
                final String mGameType = gameType.getText().toString();
                final String mGameFormat = gameFormat.getSelectedItem().toString();
                final String mLeaperOne = leaperOne.getText().toString();
                final String mLeaperTwo = leaperTwo.getText().toString();
                final String mLeapDate = dateTextView.getText().toString();
                final String mLeapTime = timeTextView.getText().toString();

                // make sure a leaer has been elsected
                if (Objects.equals(mLeaperTwo, "Leaper Two")) {
                    //mVerificationField.setError("Cannot be empty.");
                    leaperTwo.setHintTextColor(getResources().getColor(R.color.cherry));
                    Snackbar.make(getView(), "Choose opposing leaper", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    return;
                }

                if (Objects.equals(mLeaperTwo, "Open Leap")) {

                    // SAVE IN LEAPER ONE LIST OF LEAPS
                    // status "0" show a new leap. "1" = accepted. "2" = declined. "3" = cancelled
                    String key = dbRef.child("leaps").child(myUID).push().getKey();
                    dbRef.child("leaps").child(myUID).child(key).setValue(new UserLeap(key, mGameType, mGameFormat, mLeaperOne,
                            mLeaperTwo, leapDay, "0", circleID));


                    /// mLeaperTwo is a bundled string from previous intent (
                    /// if it's same as leaper one then it's an open leap

                    dbRef.child("leapsforcircles").child("openleaps").child(circleID).child(key).setValue(new UserLeap(key, mGameType, mGameFormat, mLeaperOne,
                            mLeaperTwo, leapDay, "0", circleID));




                    Snackbar.make(getView(), "you just leaped", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    getActivity().finish();

                } else {

                    DatabaseReference leaperTwoDbRef = FirebaseDatabase.getInstance().getReference()
                            .child("phonenumbers").child(mLeaperTwo).child("uid");

                    //long lastLeapTimeWithMe = new Date().getTime();
                    //leaperTwoDbRef.getParent().child("lastleapwith" + myUID).setValue(lastLeapTimeWithMe);

                    leaperTwoDbRef.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //Log.i(TAG, dataSnapshot.getValue(String.class);

                                leaperTwoUID = dataSnapshot.getValue().toString();
                                // newLeaperTwoUID.setText(leaperTwoUID);



                            //leaperTwoUID = newLeaperTwoUID.getText().toString();


                            // SAVE IN LEAPER ONE LIST OF LEAPS
                            // status "0" show a new leap. "1" = accepted. "2" = declined. "3" = cancelled
                            String key = dbRef.child("leaps").child(myUID).push().getKey();
                            dbRef.child("leaps").child(myUID).child(key).setValue(new UserLeap(key, mGameType, mGameFormat, mLeaperOne,
                                    mLeaperTwo, leapDay, "0", circleID));






                            ///  IT'S NOT A CIRCLE LEAP
                            if (Objects.equals(circleID, "null")){

                                dbRef.child("leaps").child(leaperTwoUID).child(key).setValue(new UserLeap(key, mGameType, mGameFormat, mLeaperOne,
                                        mLeaperTwo, leapDay, "0", circleID));



                                Snackbar.make(getView(), "you just leaped", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();

                                getActivity().finish();
                            }

                            //// IT'S A CIRCLE LEAP
                            /// if it's a leap for a particular circle, update database so it can be populated under leaps for the circle
                            else {


                                    /// mLeaperTwo is a bundled string from previous intent (
                                    /// if not the same as leaper one then it a leap to a particular leaper

                                    // add to leapertoleaper list for the circle
                                    dbRef.child("leapsforcircles").child("leapertoleaper").child(circleID).child(key).setValue(new UserLeap(key, mGameType, mGameFormat, mLeaperOne,
                                            mLeaperTwo, leapDay, "0", circleID));

                                    // add to the leaped person's leap list
                                    dbRef.child("leaps").child(leaperTwoUID).child(key).setValue(new UserLeap(key, mGameType, mGameFormat, mLeaperOne,
                                            mLeaperTwo, leapDay, "0", circleID));





                                Snackbar.make(getView(), "you just leaped", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();

                                getActivity().finish();

                            }




















                        }



                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //Log.w(TAG, "onCancelled", databaseError.toException());
                        }


                    });

                }





            }
        });

        leaperTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openSelectLeaperIntent = new Intent(getActivity(), selectLeaperContact.class);
                openSelectLeaperIntent.putExtra("SourceActivity", "2");
                startActivityForResult(openSelectLeaperIntent, 1);
            }
        });

        leaperTwoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openSelectLeaperIntent = new Intent(getActivity(), selectLeaperContact.class);
                openSelectLeaperIntent.putExtra("SourceActivity", "2");
                startActivityForResult(openSelectLeaperIntent, 1);
            }
        });











        final ListView listView = (ListView)view.findViewById(R.id.listOfGames);

        final FirebaseListAdapter<GameList> adapter;
        adapter = new FirebaseListAdapter<GameList>(getActivity(), GameList.class, R.layout.game_list, dbRef.child("gamelist")) {
            @Override
            protected void populateView(View v, final GameList model, int position) {

                //CircleImageView gameListGameImage = (CircleImageView)v.findViewById(R.id.gameListGameImage);
                gameTitle = (TextView)v.findViewById(R.id.gameListGameTitle);

                gameTitle.setText(model.getGameTitle());








            }
        };

        listView.setAdapter(adapter);







        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gameTitle = (TextView)view.findViewById(R.id.gameListGameTitle);
                gameType.setText(gameTitle.getText().toString());


                newLeapDimmer.setVisibility(View.GONE);
            }
        });










        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                Query query = null;
                String searchedText = "%" + searchBox.getText().toString() + "%";

                if (searchBox.getText().toString() == ""){

                    query = dbRef.child("gamelist").orderByChild("name");

                } else {

                    query = dbRef.child("gamelist").orderByValue().equalTo(searchedText);
                }

                final FirebaseListAdapter<GameList> adapter;
                adapter = new FirebaseListAdapter<GameList>(getActivity(), GameList.class, R.layout.game_list, query) {
                    @Override
                    protected void populateView(View v, GameList model, int position) {

                        CircleImageView gameListGameImage = (CircleImageView)v.findViewById(R.id.gameListGameImage);
                        gameTitle = (TextView)v.findViewById(R.id.gameListGameTitle);

                        gameTitle.setText(model.getGameTitle());



                    }
                };

                listView.setAdapter(adapter);





            }
        });










        gameTypeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                newLeapDimmer.setVisibility(View.VISIBLE);

            }
        });


        gameType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newLeapDimmer.setVisibility(View.VISIBLE);


            }
        });















        /////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////
        //////// GETTING AND SETTING NAMES IN PLACE OF PHONE NUMBER

        ///////////////////////////////////////
        //////////////////   STARTING    ///////////////////////////////




        //// CHECK MY CONTACT LIST IF THIS PERSON IS A CONTACT
        dbRef.child("ContactList").child(myUID).child("leapSortedContacts").child(leaperOne.getText().toString())
                .addValueEventListener(new ValueEventListener() {////////
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.child("name").getValue() == null || dataSnapshot.child("name")
                                .getValue() == "") {///// IF THEY ARE NOT A CONTACT OR THE VALUE IS EMPTY



                            ///// CHECK THE USERS PROFILES TO SEE IF THEY HAVE AN ENTRY THERE
                            dbRef.child("userprofiles").child(leaperOne.getText().toString()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.child("name").getValue() == null || Objects.equals(dataSnapshot.child("name")
                                            .getValue().toString(), "")){///IF THEY DON'T HAVE AN ENTRY USE THEIR PHONE NUMBER

                                        /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////
                                        displayedLeaperOneName.setText(leaperOne.getText().toString());
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
















        if (Objects.equals(leaperTwo.getText().toString(), "Open Leap")){

            displayedLeaperTwoName.setText("Open Leap");


        } else {








            /////////////////////////////////////////////////////////////////////
            ///////////////////////////////////////////
            //////// GETTING AND SETTING NAMES IN PLACE OF PHONE NUMBER

            ///////////////////////////////////////
            //////////////////   STARTING    ///////////////////////////////




            //// CHECK MY CONTACT LIST IF THIS PERSON IS A CONTACT
            dbRef.child("ContactList").child(myUID).child("leapSortedContacts").child(leaperTwo.getText().toString())
                    .addValueEventListener(new ValueEventListener() {////////
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.child("name").getValue() == null || dataSnapshot.child("name")
                                    .getValue() == "") {///// IF THEY ARE NOT A CONTACT OR THE VALUE IS EMPTY



                                ///// CHECK THE USERS PROFILES TO SEE IF THEY HAVE AN ENTRY THERE
                                dbRef.child("userprofiles").child(leaperTwo.getText().toString()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.child("name").getValue() == null || Objects.equals(dataSnapshot.child("name")
                                                .getValue().toString(), "")){///IF THEY DON'T HAVE AN ENTRY USE THEIR PHONE NUMBER

                                            /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////
                                            displayedLeaperTwoName.setText(leaperTwo.getText().toString());
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








        return view;


    }






    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (1) : {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    // TODO Extract the data returned from the child Activity.
                    final String leaperTwoResult = data.getStringExtra("SecondLeaper");
                    leaperTwo.setText(leaperTwoResult);
                    displayedLeaperTwoName.setText(leaperTwoResult);







                    /////////////////////////////////////////////////////////////////////
                    ///////////////////////////////////////////
                    //////// GETTING AND SETTING NAMES IN PLACE OF PHONE NUMBER

                    ///////////////////////////////////////
                    //////////////////   STARTING    ///////////////////////////////




                    //// CHECK MY CONTACT LIST IF THIS PERSON IS A CONTACT
                    dbRef.child("ContactList").child(myUID).child("leapSortedContacts").child(leaperTwoResult)
                            .addValueEventListener(new ValueEventListener() {////////
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.child("name").getValue() == null || dataSnapshot.child("name")
                                            .getValue() == "") {///// IF THEY ARE NOT A CONTACT OR THE VALUE IS EMPTY



                                        ///// CHECK THE USERS PROFILES TO SEE IF THEY HAVE AN ENTRY THERE
                                        dbRef.child("userprofiles").child(leaperTwoResult).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.child("name").getValue() == null || Objects.equals(dataSnapshot.child("name")
                                                        .getValue().toString(), "")){///IF THEY DON'T HAVE AN ENTRY USE THEIR PHONE NUMBER

                                                    /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////
                                                    displayedLeaperTwoName.setText(leaperTwo.getText().toString());
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















                    mLeaperTwoStorageRef = mStorage.child("leaperProfileImage").child(leaperTwoResult).child(leaperTwoResult);
                    leapUtilities.CircleImageFromFirebase(getActivity(), mLeaperTwoStorageRef, leaperTwoImage);





                }
                break;
            }
        }
    }






    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof newLeap) {
            ((newLeap) activity).addKeyEventHandler(this);
        }
    }

    @Override
    public void onDetach() {
        Activity activity = getActivity();
        if (activity instanceof newLeap) {
            ((newLeap) activity).removeKeyEventHandler(this);
        }
        super.onDetach();
    }


    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (newLeapDimmer.getVisibility() == View.VISIBLE){
            newLeapDimmer.setVisibility(View.GONE);
        }
        else {
            return false;
        }
        return true;

    }
}



