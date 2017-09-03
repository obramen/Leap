package com.antrixgaming.leap.Fragments;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.antrixgaming.leap.NewClasses.ChatMessage;
import com.antrixgaming.leap.NewClasses.createGroupCircle;
import com.antrixgaming.leap.R;
import com.antrixgaming.leap.activity_one_circle;
import com.antrixgaming.leap.groupInfoActivity;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

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






        ListView listOfMessages = (ListView)view.findViewById(R.id.list_of_chats);

        FirebaseListAdapter<createGroupCircle> adapter;
        adapter = new FirebaseListAdapter<createGroupCircle>(getActivity(), createGroupCircle.class,
                R.layout.circle_list, FirebaseDatabase.getInstance().getReference().child("usergroupcirclelist")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

            @Override
            protected void populateView(View v, createGroupCircle model, int position) {


                // Get references to the views of message.xml
                TextView circleName = (TextView)v.findViewById(R.id.circleName);
                TextView circleID = (TextView)v.findViewById(R.id.circleID);
                TextView circleCreatedBy = (TextView)v.findViewById(R.id.circleCreatedBy);
                TextView circleCreatedOn = (TextView)v.findViewById(R.id.circleCreatedOn);
                //TextView messageUser = (TextView)v.findViewById(R.id.message_user);
                TextView lastCircleMessageBy = (TextView)v.findViewById(R.id.lastCircleMessageBy);
                TextView messageTime = (TextView)v.findViewById(R.id.lastCircleMessageTime);

                // Set their text
                circleName.setText(model.getGroupName());
                circleID.setText(model.getGroupid());
                circleCreatedBy.setText(model.getCreatedBy());

                // Format the date before showing it
                circleCreatedOn.setText(DateFormat.format("dd-MMM-YY",model.getCreatedOn()));
            }
        };

        listOfMessages.setAdapter(adapter);





        ListView listView = (ListView) view.findViewById(R.id.list_of_chats);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                TextView circleID = (TextView)view.findViewById(R.id.circleID);
                TextView circleName = (TextView)view.findViewById(R.id.circleName);
                TextView circleCreatedBy = (TextView)view.findViewById(R.id.circleCreatedBy);
                TextView circleCreatedOn = (TextView)view.findViewById(R.id.circleCreatedOn);



                Intent intent = new Intent(getActivity(), groupInfoActivity.class);
                intent.putExtra("circleID", circleID.getText().toString());
                intent.putExtra("groupName", circleName.getText().toString());
                intent.putExtra("circleCreatedBy", circleCreatedBy.getText().toString());
                intent.putExtra("circleCreatedOn", circleCreatedOn.getText().toString());
                startActivity(intent);
            }
        });




        return view;

    }

}
