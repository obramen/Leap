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
import android.widget.ListView;
import android.widget.TextView;

import com.antrixgaming.leap.NewClasses.ChatMessage;
import com.antrixgaming.leap.R;
import com.antrixgaming.leap.activity_one_circle;
import com.firebase.ui.database.FirebaseListAdapter;
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


        FirebaseListAdapter<ChatMessage> adapter;
        adapter = new FirebaseListAdapter<ChatMessage>(getActivity(), ChatMessage.class,
                R.layout.circle_list, FirebaseDatabase.getInstance().getReference()) {

            @Override
            protected void populateView(View v, ChatMessage model, int position) {
                // Get references to the views of message.xml
                TextView messageText = (TextView)v.findViewById(R.id.lastCircleMessage);
                //TextView messageUser = (TextView)v.findViewById(R.id.message_user);
                TextView phoneNumber = (TextView)v.findViewById(R.id.circleName);
                TextView messageTime = (TextView)v.findViewById(R.id.lastCircleMessageTime);

                // Set their text
                messageText.setText(model.getMessageText());
                phoneNumber.setText(model.getSenderPhoneNumber());

                // Format the date before showing it
                messageTime.setText(DateFormat.format("HH:mm",
                        model.getMessageTime()));





            }
        };

        listOfMessages.setAdapter(adapter);


        return view;

    }

}
