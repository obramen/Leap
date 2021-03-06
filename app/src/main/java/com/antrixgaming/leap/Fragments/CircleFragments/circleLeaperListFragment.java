package com.antrixgaming.leap.Fragments.CircleFragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.antrixgaming.leap.LeapClasses.LeapUtilities;
import com.antrixgaming.leap.LeapClasses.OnlinePressence;
import com.antrixgaming.leap.Models.CircleMember;
import com.antrixgaming.leap.R;
import com.antrixgaming.leap.groupInfoActivity;
import com.antrixgaming.leap.leaperProfileActivity;
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

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;



public class circleLeaperListFragment extends Fragment {

    String selectedContact = null;
    String circleID = null;
    String mLeapStatus = "1";

    SharedPreferences sharedPreferences;

    LeapUtilities leapUtilities;
    OnlinePressence onlinePressence;
    private StorageReference mLeaperStorageRef;

    StorageReference mStorage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_circle_leaper_list, container, false);

        leapUtilities = new LeapUtilities();
        onlinePressence = new OnlinePressence();
        mStorage = FirebaseStorage.getInstance().getReference();


        final String myPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        final String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        //DatabaseReference dbRefLeaper= dbRef.child("ContactList").child(myUID).child("leapSortedContacts");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        circleID = sharedPreferences.getString("currentCircleID", null);

        ListView listOfContacts = (ListView) view.findViewById(R.id.list_of_circleLeapers);

        DatabaseReference dbRefLeaper = FirebaseDatabase.getInstance().getReference().child("groupcirclemembers")
                .child(circleID).child("currentmembers");









        FirebaseListAdapter<CircleMember> adapter;
        adapter = new FirebaseListAdapter<CircleMember>(getActivity(), CircleMember.class,
                R.layout.circle_leaper_list, dbRefLeaper.orderByChild("phoneNumber")) {
            @Override
            protected void populateView(final View v, final CircleMember model, int position) {


                TextView circleLeaperListLeaperName = (TextView) v.findViewById(R.id.circleLeaperListLeaperName);
                final Button circleLeaperListButton = (Button) v.findViewById(R.id.circleLeaperListButton);
                LinearLayout circleLeaperListLayout = (LinearLayout) v.findViewById(R.id.circleLeaperListLayout);
                final CircleImageView leaperImage = (CircleImageView)v.findViewById(R.id.circleLeaperListImage);
                final TextView displayedLeaperName = (TextView)v.findViewById(R.id.displayedLeaperName);




                // Populate the data into the template view using the data object
                //circleLeaperListLeaperName.setText(model.getcontactName());
                circleLeaperListLeaperName.setText(model.getPhoneNumber());

                final String leaperPhoneNumber = model.getPhoneNumber();




                FirebaseDatabase.getInstance().getReference().child("profileImageTimestamp").child(leaperPhoneNumber)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (dataSnapshot.hasChildren()){

                                    String timestamp = dataSnapshot.child(leaperPhoneNumber).getValue().toString();
                                    mLeaperStorageRef = mStorage.child("leaperProfileImage").child(leaperPhoneNumber).child(leaperPhoneNumber);
                                    leapUtilities.CircleImageFromFirebase(getActivity(), mLeaperStorageRef, leaperImage, timestamp);




                                }



                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });






                if (Objects.equals(myPhoneNumber, leaperPhoneNumber)){
                    circleLeaperListButton.setVisibility(View.GONE);
                }





                DatabaseReference leapStatus =  FirebaseDatabase.getInstance().getReference().child("groupcirclesettings").child(model.getPhoneNumber())
                        .child(circleID);
                leapStatus.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String xLeapStatus = dataSnapshot.child("leapStatus").getValue().toString();

                        if(Objects.equals(xLeapStatus, "0")){
                            circleLeaperListButton.setVisibility(v.GONE);

                        } else if (Objects.equals(xLeapStatus, "1")){

                            circleLeaperListButton.setVisibility(v.VISIBLE);


                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });









                circleLeaperListButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Intent startNewLeapIntent = new Intent(getActivity(), newLeap.class);
                        startNewLeapIntent.putExtra("leapedPhoneNumber", leaperPhoneNumber);
                        startNewLeapIntent.putExtra("SourceActivity", "1");  // to be used to identify that the extras came from here
                        startNewLeapIntent.putExtra("circleID", circleID);
                        getActivity().startActivity(startNewLeapIntent);

                    }
                });


                onlinePressence.circleLeaperOnlinePrescence(getActivity(), model.getPhoneNumber(), circleID, leaperImage);













                displayedLeaperName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(getActivity(), leaperProfileActivity.class);
                        intent.putExtra("leaperPhoneNumber", leaperPhoneNumber);
                        startActivity(intent);



                    }
                });









                /////////////////////////////////////////////////////////////////////
                ///////////////////////////////////////////
                //////// GETTING AND SETTING NAMES IN PLACE OF PHONE NUMBER

                ///////////////////////////////////////
                //////////////////   STARTING    ///////////////////////////////




                //// CHECK MY CONTACT LIST IF THIS PERSON IS A CONTACT
                dbRef.child("ContactList").child(myUID).child("leapSortedContacts").child(model.getPhoneNumber())
                        .addValueEventListener(new ValueEventListener() {////////
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (dataSnapshot.child("name").getValue() == null || dataSnapshot.child("name")
                                        .getValue() == "") {///// IF THEY ARE NOT A CONTACT OR THE VALUE IS EMPTY



                                    ///// CHECK THE USERS PROFILES TO SEE IF THEY HAVE AN ENTRY THERE
                                    dbRef.child("userprofiles").child(model.getPhoneNumber()).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.child("name").getValue() == null || Objects.equals(dataSnapshot.child("name")
                                                    .getValue().toString(), "")){///IF THEY DON'T HAVE AN ENTRY USE THEIR PHONE NUMBER

                                                /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////
                                                displayedLeaperName.setText(model.getPhoneNumber());
                                                /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////


                                            } else { //// IF THEY HAVE AN ENTRY USE THEIR ENTERED NAME

                                                String myName = dataSnapshot.child("name").getValue().toString();

                                                /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////
                                                displayedLeaperName.setText("~ " + myName);
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
                                    displayedLeaperName.setText(mName);
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


        };

        listOfContacts.setAdapter(adapter);




        return view;
    }

}
