package com.antrixgaming.leap.Fragments.LeapFragments;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.antrixgaming.leap.Fragments.leapHistoryFragment;
import com.antrixgaming.leap.Fragments.openLeapsFragment;
import com.antrixgaming.leap.Fragments.recentLeapsFragment;
import com.antrixgaming.leap.LeapClasses.ConfirmDialog;
import com.antrixgaming.leap.LeapClasses.LeapUtilities;
import com.antrixgaming.leap.Models.CircleMember;
import com.antrixgaming.leap.Models.circleMessage;
import com.antrixgaming.leap.Models.createGroupCircle;
import com.antrixgaming.leap.R;
import com.antrixgaming.leap.groupInfoActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class userProfileFragment extends Fragment {



    DatabaseReference dbRef;
    DatabaseReference openCircleDbRef;
    Query query;
    String myUID;

    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    private RecyclerView recyclerView;

    StorageReference mStorage;

    View view;

    Context context;

    LeapUtilities leapUtilities;

    String myPhoneNumber;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        context = getActivity();

        dbRef = FirebaseDatabase.getInstance().getReference();
        query = dbRef.child("groupcircles").orderByChild("publicGroup").equalTo("true").limitToFirst(5);
        myPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        mStorage = FirebaseStorage.getInstance().getReference();

        leapUtilities = new LeapUtilities();

        myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();


        loadPublicGroups();





        return view;
    }











    private void loadPublicGroups(){


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);

        recyclerView = (RecyclerView)view.findViewById(R.id.list_of_public_circles);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        linearLayoutManager.setOrientation(linearLayoutManager.HORIZONTAL);



        FirebaseRecyclerAdapter<createGroupCircle, circleViewHolder> adapter;
        adapter = new FirebaseRecyclerAdapter<createGroupCircle, circleViewHolder>(
                createGroupCircle.class, R.layout.public_circle_list, circleViewHolder.class, query
        ) {
            @Override
            protected void populateViewHolder(final circleViewHolder viewHolder, final createGroupCircle model, int position) {




                viewHolder.setDefaultGame(model.getDefaultGame());



                // get the created date and user who created the group
                FirebaseDatabase.getInstance().getReference().child("groupcircles").child(model.getGroupid())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                final String groupName = dataSnapshot.child("groupName").getValue().toString();

                                viewHolder.setCircleName(groupName);


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                viewHolder.leapInText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // using the new key update members list under same table with the creator of the group setting status to true
                        /// true = admin
                        /// false = not admin
                        dbRef.child("groupcirclemembers").child(model.getGroupid()).child("currentmembers").child(myPhoneNumber)
                                .setValue(new CircleMember(myPhoneNumber,"false","1"));
                        // update the usergroupcirclelist (a list containing all groups a leaper is part of
                        dbRef.child("groupcirclesettings").child(myPhoneNumber).child(model.getGroupid()).child("leapStatus").setValue("1");


                        // This list is used to load the circles fragment
                        // First add the group id
                        dbRef.child("usergroupcirclelist")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(model.getGroupid()).child("groupid").setValue(model.getGroupid());
                        // Next add the group name
                        dbRef.child("usergroupcirclelist")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(model.getGroupid()).child("groupName").setValue(model.getGroupid());
                        // Lastly add the admin status
                        dbRef.child("usergroupcirclelist")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(model.getGroupid()).child("admin").setValue("false");


                        //push new messages using Circle ID stored
                        /// 1 - shows it's a notification message // 0 - normal message
                        String key = FirebaseDatabase.getInstance().getReference().child("groupcirclemessages").child(model.getGroupid())
                                .push().getKey();
                        FirebaseDatabase.getInstance().getReference().child("groupcirclemessages").child(model.getGroupid())
                                .child(key).setValue(new circleMessage(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() + " joined circle",
                                model.getGroupid(), "", "", "1", "false"));
                        FirebaseDatabase.getInstance().getReference().child("groupcircles").child(model.getGroupid())
                                .child("lastgroupmessage").setValue(new circleMessage(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() + " joined circle",
                                model.getGroupid(), "", "", "1", "true"));
                        //FirebaseDatabase.getInstance().getReference().child("groupcirclemessages").child(circleID)
                        //.child(circleID).child("members").setValue(memberList);
                        FirebaseDatabase.getInstance().getReference().child("groupcirclelastmessages").child(model.getGroupid())
                                .setValue(new circleMessage(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() + " joined circle", model.getGroupid(),
                                        "", "", "1", "true"));


                    }
                });




                //// DIALOG WHEN CIRCLE IS CLICKED
                viewHolder.publicCircleCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        final Dialog dialog = new Dialog(context);
                        dialog.setContentView(R.layout.dialog_public_circle);

                        FirebaseDatabase.getInstance().getReference().child("groupcircles").child(model.getGroupid())
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        final String groupName = dataSnapshot.child("groupName").getValue().toString();
                                        final String groupCreatedBy = dataSnapshot.child("createdBy").getValue().toString();
                                        final String groupCreatedOn = dataSnapshot.child("createdOn").getValue().toString();






                                        dialog.setTitle(groupName);














                                        TextView cancelButton = (TextView) dialog.findViewById(R.id.cancelButton);
                                        cancelButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                dialog.dismiss();
                                            }
                                        });






                                        TextView publicCircleName = (TextView) dialog.findViewById(R.id.publicCircleName);
                                        publicCircleName.setText(groupName);


                                        final TextView membersCount = (TextView) dialog.findViewById(R.id.membersCount);


                                        FirebaseDatabase.getInstance().getReference().child("groupcirclemembers").child(model.getGroupid()).child("currentmembers")
                                                .addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        long mMemberCount = dataSnapshot.getChildrenCount();
                                                        membersCount.setText(String.valueOf(mMemberCount));
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });













                                        TextView acceptButton = (TextView) dialog.findViewById(R.id.acceptButton);

                                        acceptButton.setOnClickListener(new View.OnClickListener() {




                                            @Override
                                            public void onClick(View v) {


                                                dialog.dismiss();


                                                viewHolder.confirmDialog.NewConfirmDialog(context, "Confirm Leap In", "Confirm leap into " + groupName,
                                                        "Leap In", "Cancel");
                                                viewHolder.confirmDialog.confirmAccept.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

                                                        // using the new key update members list under same table with the creator of the group setting status to true
                                                        /// true = admin
                                                        /// false = not admin
                                                        dbRef.child("groupcirclemembers").child(model.getGroupid()).child("currentmembers").child(myPhoneNumber)
                                                                .setValue(new CircleMember(myPhoneNumber,"false","1"));
                                                        // update the usergroupcirclelist (a list containing all groups a leaper is part of
                                                        dbRef.child("groupcirclesettings").child(myPhoneNumber).child(model.getGroupid()).child("leapStatus").setValue("1");


                                                        // This list is used to load the circles fragment
                                                        // First add the group id
                                                        dbRef.child("usergroupcirclelist")
                                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(model.getGroupid()).child("groupid").setValue(model.getGroupid());
                                                        // Next add the group name
                                                        dbRef.child("usergroupcirclelist")
                                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(model.getGroupid()).child("groupName").setValue(model.getGroupid());
                                                        // Lastly add the admin status
                                                        dbRef.child("usergroupcirclelist")
                                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(model.getGroupid()).child("admin").setValue("false");


                                                        //push new messages using Circle ID stored
                                                        /// 1 - shows it's a notification message // 0 - normal message
                                                        String key = FirebaseDatabase.getInstance().getReference().child("groupcirclemessages").child(model.getGroupid())
                                                                .push().getKey();
                                                        FirebaseDatabase.getInstance().getReference().child("groupcirclemessages").child(model.getGroupid())
                                                                .child(key).setValue(new circleMessage(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() + " joined circle",
                                                                model.getGroupid(), "", "", "1", "false"));
                                                        FirebaseDatabase.getInstance().getReference().child("groupcircles").child(model.getGroupid())
                                                                .child("lastgroupmessage").setValue(new circleMessage(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() + " joined circle",
                                                                model.getGroupid(), "", "", "1", "true"));
                                                        //FirebaseDatabase.getInstance().getReference().child("groupcirclemessages").child(circleID)
                                                        //.child(circleID).child("members").setValue(memberList);
                                                        FirebaseDatabase.getInstance().getReference().child("groupcirclelastmessages").child(model.getGroupid())
                                                                .setValue(new circleMessage(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() + " joined circle", model.getGroupid(),
                                                                        "", "", "1", "true"));

                                                        viewHolder.confirmDialog.dialog.dismiss();

                                                        Toast.makeText(context, "You leaped into " + groupName, Toast.LENGTH_SHORT).show();

                                                    }
                                                });




                                            }
                                        });















                                        final TextView createdBy = (TextView) dialog.findViewById(R.id.createdBy);




                                        //getting creator phone number
                                        dbRef.child("uid").child(groupCreatedBy).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                final String groupCreator = dataSnapshot.child("phoneNumber").getValue().toString();



                                                if (Objects.equals(groupCreator, myPhoneNumber)){

                                                    dbRef.child("userprofiles").child(myPhoneNumber).addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            if (dataSnapshot.child("name").getValue() == null || Objects.equals(dataSnapshot.child("name").getValue().toString(), "")){

                                                                /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////
                                                                createdBy.setText(groupCreator); // +", " + DateFormat.format("dd-MMM-yy", Long.parseLong(groupCreatedOn)));
                                                                /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////

                                                            } else {

                                                                String myName = dataSnapshot.child("name").getValue().toString();

                                                                /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////
                                                                createdBy.setText("~ " + myName); // + myName +", " + DateFormat.format("dd-MMM-yy", Long.parseLong(groupCreatedOn)));
                                                                /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////
                                                                ///
                                                                /// /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////
                                                                //toolbar_layout.setSubtitle("Created by " + myName +", " + DateFormat.format("dd-MMM-yy", Long.parseLong(groupCreatedOn)));
                                                                /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////


                                                            }




                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });




                                                } else{


                                                    //getting creator name
                                                    dbRef.child("ContactList").child(myUID).child("leapSortedContacts").child(groupCreator).addValueEventListener(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                                            if(dataSnapshot.child("name").getValue() == null || dataSnapshot.child("name").getValue() == ""){
                                                                /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////
                                                                createdBy.setText(groupCreator); // + groupCreator +", " + DateFormat.format("dd-MMM-yy", Long.parseLong(groupCreatedOn)));
                                                                /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////

                                                            } else {


                                                                String mName = dataSnapshot.child("name").getValue().toString();

                                                                /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////

                                                                createdBy.setText(mName); // + mName +", " + DateFormat.format("dd-MMM-yy", Long.parseLong(groupCreatedOn)));

                                                                /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////


                                                            }






                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });

                                                }






                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });









                                        CircleImageView circleImage = (CircleImageView) dialog.findViewById(R.id.circleImage);

                                        StorageReference mGroupStorageRef = mStorage.child("groupCircleProfileImage").child(model.getGroupid()).child(model.getGroupid());
                                        leapUtilities.SquareImageFromFirebase(context, mGroupStorageRef, circleImage);







                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });



                        dialog.show();


                    }
                });



            }
        };





        recyclerView.setAdapter(adapter);



    }









    public static class circleViewHolder extends RecyclerView.ViewHolder {


        ImageView gameImage;
        TextView circleName;
        TextView defaultGame;
        TextView leapInText;

        RelativeLayout publicCircleCard;

        StorageReference gameStorage;

        LeapUtilities leapUtilities;

        ConfirmDialog confirmDialog;

        public circleViewHolder(View itemView) {
            super(itemView);

            leapUtilities = new LeapUtilities();
            confirmDialog = new ConfirmDialog();

            gameImage = (ImageView) itemView.findViewById(R.id.gameImage);
            circleName = (TextView) itemView.findViewById(R.id.circleName);
            defaultGame = (TextView) itemView.findViewById(R.id.defaultGame);
            leapInText = (TextView) itemView.findViewById(R.id.leapInText);
            publicCircleCard = (RelativeLayout) itemView.findViewById(R.id.publicCircleCard);




        }

        public void setCircleName(String circleName) {

            this.circleName.setText(circleName);
        }


        public void setDefaultGame(String defaultGame) {
            this.defaultGame.setText(defaultGame);

            String gameId = defaultGame.replaceAll("\\s+", "").toLowerCase();


            StorageReference mGameImageStorage = FirebaseStorage.getInstance().getReference().child("gameImages").child(gameId + ".jpg");
            leapUtilities.SquareImageFromFirebase(itemView.getContext(), mGameImageStorage, this.gameImage);
        }


    }









}
