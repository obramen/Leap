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

import com.antrixgaming.leap.NewClasses.createGroupCircle;
import com.antrixgaming.leap.R;
import com.antrixgaming.leap.activity_one_circle;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class circlesFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_circles, container, false);

        FloatingActionButton chatFab = (FloatingActionButton) view.findViewById(R.id.circle_fab);
        chatFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent openOneChat = new Intent(getActivity(), activity_one_circle.class);
                startActivity(openOneChat);

            }
        });




        ListView listOfMessages = (ListView)view.findViewById(R.id.list_of_groupCircles);

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





        ListView listView = (ListView) view.findViewById(R.id.list_of_groupCircles);
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

                                Intent intent = new Intent(getActivity(), activity_one_circle.class);
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
