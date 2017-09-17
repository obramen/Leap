package com.antrixgaming.leap.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.antrixgaming.leap.NewClasses.circleMessage;
import com.antrixgaming.leap.NewClasses.createGroupCircle;
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
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;


public class circlesFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_circles, container, false);

        FloatingActionButton circleFAB = (FloatingActionButton) view.findViewById(R.id.circle_fab);
        circleFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new LovelyTextInputDialog(getActivity(), R.style.MyDialogTheme)
                        .setTopColorRes(R.color.colorPrimary)
                        .setTitle(R.string.new_leapers_circle)
                        .setMessage(R.string.new_circle_message)
                        .setIcon(R.drawable.ic_circle_add)
                        .setInputFilter("Enter circle name", new LovelyTextInputDialog.TextFilter() {
                            @Override
                            public boolean check(String text) {
                                //return text.matches("\\w{3,23}\\b");     //("\\w+");
                                return text.matches("^\\w+( +\\w+)*$");     //("\\w+");
                                //return text.matches("\\w+");
                            }

                        })
                        .setCancelable(false)
                        .setNegativeButton("Cancel", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                return;
                            }
                        })
                        .setConfirmButton("Create circle", new LovelyTextInputDialog.OnTextInputConfirmListener() {
                            @Override
                            public void onTextInputConfirmed(String text) {

                                // push and get the key for new group under "groupcircles" table
                                String key = FirebaseDatabase.getInstance().getReference().child("groupcircles").push().getKey();
                                // using the new key, update group info with group creator, group name and group key under same table
                                FirebaseDatabase.getInstance().getReference().child("groupcircles").child(key)
                                        .setValue(new createGroupCircle(FirebaseAuth.getInstance()
                                                .getCurrentUser().getUid(), text, key));
                                // using the new key update members list under same table with the creator of the group setting status to true
                                /// true = admin
                                /// false = not admin
                                FirebaseDatabase.getInstance().getReference().child("groupcircles").child(key).child("members")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue("true");
                                // update the usergroupcirclelist (a list containing all groups a leaper is part of


                                // This list is used to load the circles fragment
                                // First add the group id
                                FirebaseDatabase.getInstance().getReference().child("usergroupcirclelist")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(key).child("groupid").setValue(key);
                                // Next add the group name
                                FirebaseDatabase.getInstance().getReference().child("usergroupcirclelist")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(key).child("groupName").setValue(text);
                                // Lastly add the admin status
                                FirebaseDatabase.getInstance().getReference().child("usergroupcirclelist")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(key).child("admin").setValue("true");
                                FirebaseDatabase.getInstance().getReference().child("usergroupcirclelist")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(key).child("admin").setValue("true");
                                FirebaseDatabase.getInstance().getReference().child("groupcirclelastmessages").child(key)
                                        .setValue(new circleMessage("Welcome to your new circle, add your other leapers now", key,
                                                "Leap Bot", "LEAPBOT"));
                                Toast.makeText(getActivity(), "Circle added", Toast.LENGTH_SHORT).show();


                            }
                        })
                        .show();

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

                try{

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
                                    lastCircleMessageTime.setText(DateFormat.format("HH:mm", mLastCircleMessageTime));
                                    //messageUser.setText(model.getMessageUser());

                                    // Format the date before showing it
                                    //lastCircleMessageTime.setText(DateFormat.format("HH:mm",model.getMessageTime()));
                                    // Format the date before showing it

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


                DatabaseReference circleIDRef = FirebaseDatabase.getInstance().getReference().child("groupcircles").child(circleID.getText().toString());

                circleIDRef.child("members").addValueEventListener(new ValueEventListener() {
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
