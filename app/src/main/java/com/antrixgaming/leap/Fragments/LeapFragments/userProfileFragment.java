package com.antrixgaming.leap.Fragments.LeapFragments;


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
import android.widget.ImageView;
import android.widget.TextView;

import com.antrixgaming.leap.Fragments.leapHistoryFragment;
import com.antrixgaming.leap.Fragments.openLeapsFragment;
import com.antrixgaming.leap.Fragments.recentLeapsFragment;
import com.antrixgaming.leap.LeapClasses.LeapUtilities;
import com.antrixgaming.leap.Models.CircleMember;
import com.antrixgaming.leap.Models.circleMessage;
import com.antrixgaming.leap.Models.createGroupCircle;
import com.antrixgaming.leap.R;
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
    String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String nickname = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

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
        query = dbRef.child("groupcircles").orderByChild("publicGroup").equalTo("true");
        myPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

        leapUtilities = new LeapUtilities();


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



            }
        };





        recyclerView.setAdapter(adapter);



    }









    public static class circleViewHolder extends RecyclerView.ViewHolder {


        ImageView gameImage;
        TextView circleName;
        TextView defaultGame;
        TextView leapInText;

        StorageReference gameStorage;

        LeapUtilities leapUtilities;

        public circleViewHolder(View itemView) {
            super(itemView);

            leapUtilities = new LeapUtilities();

            gameImage = (ImageView) itemView.findViewById(R.id.gameImage);
            circleName = (TextView) itemView.findViewById(R.id.circleName);
            defaultGame = (TextView) itemView.findViewById(R.id.defaultGame);
            leapInText = (TextView) itemView.findViewById(R.id.leapInText);


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
