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

import com.antrixgaming.leap.NewClasses.ChatMessage;
import com.antrixgaming.leap.R;
import com.antrixgaming.leap.activity_one_chat;
import com.antrixgaming.leap.phoneContactList;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


public class chatsFragment extends Fragment {

    // Firebase instance variables

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_chat, container, false);



        ListView listView = (ListView) view.findViewById(R.id.list_of_chats);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position,
                                    long id) {

                Toast.makeText(getContext(), "TOADSTED",
                        Toast.LENGTH_SHORT).show();

                //TODO change +233242366623 to PHONE NUMBER FROM View selected - DONE
                TextView newMessagePhoneNumber = (TextView)view.findViewById(R.id.phoneNumber);
                //String oneCircleSecondUser = newMessagePhoneNumber.getText().toString();
                TextView sendToPhoneNumber = (TextView) view.findViewById(R.id.leaperName);

                Intent intent = new Intent(getActivity(), activity_one_chat.class);
                intent.putExtra("oneCircleSecondUser", sendToPhoneNumber.getText().toString());
                startActivity(intent);
            }
        });



        FloatingActionButton chatFab = (FloatingActionButton) view.findViewById(R.id.chat_fab);
        chatFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Intent openOneChat = new Intent(getActivity(), phoneContactList.class);
                Intent openOneChat = new Intent(getActivity(), phoneContactList.class);
                startActivity(openOneChat);

            }
        });



        ListView listOfMessages = (ListView)view.findViewById(R.id.list_of_chats);
        FirebaseListAdapter<ChatMessage> adapter;
        adapter = new FirebaseListAdapter<ChatMessage>(getActivity(), ChatMessage.class,
                R.layout.chats_list, FirebaseDatabase.getInstance().getReference().child("userchatlist").child(FirebaseAuth.getInstance()
                        .getCurrentUser().getUid())) {


            @Override
            protected void populateView(View v, final ChatMessage model, int position) {
                // Get references to the views of message.xml
                TextView messageText = (TextView)v.findViewById(R.id.lastLeaperMessage);
                //TextView messageUser = (TextView)v.findViewById(R.id.message_user);
                TextView phoneNumber = (TextView)v.findViewById(R.id.leaperName);
                TextView messageTime = (TextView)v.findViewById(R.id.lastLeaperMessageTime);

                // Set their text
                messageText.setText(model.getMessageText()); // set last message as it is
                // Format the date before showing it
                messageTime.setText(DateFormat.format("HH:mm",
                        model.getMessageTime()));
                String loadName = model.getloadname();
                int x = Integer.parseInt(loadName);

                if (x == 0){
                    //don't change number or name in chat list
                    phoneNumber.setText(model.getReceiverPhoneNumber());

                }

                else if(x == 1){
                    // change number or name in chat list
                    phoneNumber.setText(model.getSenderPhoneNumber());

                }






            }
        };

        listOfMessages.setAdapter(adapter);







        //return inflated layout
        return view;


    }


}







