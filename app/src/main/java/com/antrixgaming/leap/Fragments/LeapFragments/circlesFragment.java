package com.antrixgaming.leap.Fragments.LeapFragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.antrixgaming.leap.LeapClasses.LeapUtilities;
import com.antrixgaming.leap.Models.CircleMember;
import com.antrixgaming.leap.Models.circleMessage;
import com.antrixgaming.leap.Models.createGroupCircle;
import com.antrixgaming.leap.R;
import com.antrixgaming.leap.activity_one_circle;
import com.antrixgaming.leap.groupInfoActivity;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class circlesFragment extends Fragment {

    int unReadMessages = 0;

    LeapUtilities leapUtilities;

    StorageReference mStorage;
    StorageReference mLeaperStorageRef;
    CircleImageView circleImageView;



    String myPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
    String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_circles, container, false);


        leapUtilities = new LeapUtilities();
        mStorage = FirebaseStorage.getInstance().getReference();



        FloatingActionButton circleFAB = (FloatingActionButton) view.findViewById(R.id.circle_fab);
        circleFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });




        final ListView listOfMessages = (ListView)view.findViewById(R.id.list_of_groupCircles);

        FirebaseListAdapter<createGroupCircle> adapter;
        adapter = new FirebaseListAdapter<createGroupCircle>(getActivity(), createGroupCircle.class,
                R.layout.circle_list, FirebaseDatabase.getInstance().getReference().child("usergroupcirclelist")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

            @Override
            protected void populateView(final View v, createGroupCircle model, int position) {
                // Get references to the views of message_for_group.xml
                TextView circleName = (TextView)v.findViewById(R.id.circleName);
                TextView circleID = (TextView)v.findViewById(R.id.circleID);
                // Set their text
                circleName.setText(model.getGroupName());
                circleID.setText(model.getGroupid());



                mLeaperStorageRef = mStorage.child("groupCircleProfileImage").child(model.getGroupid()).child(model.getGroupid());
                circleImageView = (CircleImageView)v.findViewById(R.id.circle_image_1);

                leapUtilities.CircleImageFromFirebase(getActivity(), mLeaperStorageRef, circleImageView);



                try{

                    ////GETTING THE NUMBER OF UNREAD MESSAGES FOR A PARTICULAR CIRCLE AND PUSHING IT TO THE QUANTITY OF UNREAD MESSAGES
                    FirebaseListAdapter<circleMessage> adapter2;
                    adapter2 = new FirebaseListAdapter<circleMessage>(getActivity(), circleMessage.class, R.layout.circle_list,
                            FirebaseDatabase.getInstance().getReference().child("groupcirclemessages").child(circleID.getText().toString())) {
                        @Override
                        protected void populateView(View v, circleMessage model, int position) {

                            TextView pendingMessageQuantity = (TextView)v.findViewById(R.id.pendingMessageQuantity);


                            /*if (Objects.equals(model.getReadFlag(), "false")){
                                unReadMessages +=1;
                                pendingMessageQuantity.setText(unReadMessages);

                            }*/


                        }
                    };


                    /// GET DATA FROM THE LAST MESSAGE SENT TO GROUP (GROUPCIRCLELASTMESSAGES) TABLE AND FIT THE DETAILS TO VIEWS
                    FirebaseDatabase.getInstance().getReference().child("groupcirclelastmessages").child(circleID.getText().toString())
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    String mLastCircleMessageText = dataSnapshot.child("messageText").getValue().toString();
                                    Long mLastCircleMessageTime = Long.parseLong(dataSnapshot.child("messageTime").getValue().toString());
                                    String mLastCircleMessageUser = dataSnapshot.child("phoneNumber").getValue().toString();

                                    TextView lastCircleMessage = (TextView) v.findViewById(R.id.lastCircleMessage);
                                    TextView lastCircleMessageBy = (TextView)v.findViewById(R.id.lastCircleMessageBy);
                                    TextView lastCircleMessageTime = (TextView) v.findViewById(R.id.lastCircleMessageTime);

                                    // Set their text
                                    lastCircleMessage.setText(mLastCircleMessageText);
                                    lastCircleMessageBy.setText(mLastCircleMessageUser + ": ");
                                    //lastCircleMessageTime.setText(DateFormat.format("HH:mm", mLastCircleMessageTime));
                                    //messageUser.setText(model.getMessageUser());

                                    // Format the date before showing it
                                    //lastCircleMessageTime.setText(DateFormat.format("HH:mm",model.getMessageTime()));
                                    // Format the date before showing it

                                    if (DateUtils.isToday(mLastCircleMessageTime)){
                                        lastCircleMessageTime.setText(DateFormat.format("HH:mm", mLastCircleMessageTime));

                                    }
                                    else{
                                        lastCircleMessageTime.setText(DateFormat.format("dd/MM/yyyy", mLastCircleMessageTime));
                                    }



                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }

                catch(Exception e){


                }






            }
        };

        listOfMessages.setAdapter(adapter);





        final ListView listView = (ListView) view.findViewById(R.id.list_of_groupCircles);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                listView.setSelector(android.R.color.darker_gray);
                return true;
            }


        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position,
                                    long id) {

                final TextView circleID = (TextView)view.findViewById(R.id.circleID);
                final TextView circleName = (TextView)view.findViewById(R.id.circleName);


                DatabaseReference circleIDRef = FirebaseDatabase.getInstance().getReference().child("groupcirclemembers").child(circleID.getText().toString()).child("currentmembers");

                circleIDRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            long numberOfMembers = dataSnapshot.getChildrenCount();
                            final TextView circleCreatedBy = (TextView)view.findViewById(R.id.circleCreatedBy);
                            final TextView circleCreatedOn = (TextView)view.findViewById(R.id.circleCreatedOn);

                            if (numberOfMembers > 1){

                                Intent intent = new Intent(getActivity(), activity_one_circle.class);
                                intent.putExtra("circleID", circleID.getText().toString());
                                intent.putExtra("groupName", circleName.getText().toString());
                                startActivity(intent);

                            }else {

                                Intent intent = new Intent(getActivity(), groupInfoActivity.class);
                                intent.putExtra("circleID", circleID.getText().toString());
                                intent.putExtra("groupName", circleName.getText().toString());
                                startActivity(intent);
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                });



            }
        });




        return view;

    }

}
