package com.antrixgaming.leap.Fragments.LeapFragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.antrixgaming.leap.LeapClasses.LeapUtilities;
import com.antrixgaming.leap.Models.ChatMessage;
import com.antrixgaming.leap.R;
import com.antrixgaming.leap.activity_one_chat;
import com.antrixgaming.leap.selectLeaperContact;
import com.firebase.ui.database.FirebaseListAdapter;
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



public class chatsFragment extends Fragment {

    // Firebase instance variables

    String unReadMessages = "";

    LeapUtilities leapUtilities;

    StorageReference mStorage;
    StorageReference mLeaperStorageRef;
    String myPhoneNumber;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_chat, container, false);

        final String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

        leapUtilities = new LeapUtilities();

        mStorage = FirebaseStorage.getInstance().getReference();

        myPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();








        ListView listView = (ListView) view.findViewById(R.id.list_of_chats);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position,
                                    long id) {


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
                Intent openOneChat = new Intent(getActivity(), selectLeaperContact.class);
                openOneChat.putExtra("SourceActivity", "1");
                startActivity(openOneChat);

            }
        });







        /// READING AND POPULATING USER'S LIST OF INVOLVED CHATS
        ListView listOfMessages = (ListView)view.findViewById(R.id.list_of_chats);
        FirebaseListAdapter<ChatMessage> adapter;
        adapter = new FirebaseListAdapter<ChatMessage>(getActivity(), ChatMessage.class,
                R.layout.chats_list, FirebaseDatabase.getInstance().getReference().child("userchatlist").child(myUID)) {




            @Override
            protected void populateView(final View v, final ChatMessage model, int position) {
                // Get references to the views of message.xml
                TextView messageText = (TextView)v.findViewById(R.id.lastLeaperMessage);
                //TextView messageUser = (TextView)v.findViewById(R.id.message_user);
                TextView phoneNumber = (TextView)v.findViewById(R.id.leaperName);
                TextView messageTime = (TextView)v.findViewById(R.id.lastLeaperMessageTime);
                final CircleImageView leaperImage = (CircleImageView)v.findViewById(R.id.leaper_image);


                if (Objects.equals(myPhoneNumber, model.getSenderPhoneNumber())){

                    mLeaperStorageRef = mStorage.child("leaperProfileImage").child(model.getReceiverPhoneNumber())
                            .child(model.getReceiverPhoneNumber());
                    leapUtilities.CircleImageFromFirebase(getActivity(), mLeaperStorageRef, leaperImage);

                } else if(Objects.equals(myPhoneNumber, model.getReceiverPhoneNumber())) {



                    mLeaperStorageRef = mStorage.child("leaperProfileImage").child(model.getSenderPhoneNumber())
                            .child(model.getSenderPhoneNumber());
                    leapUtilities.CircleImageFromFirebase(getActivity(), mLeaperStorageRef, leaperImage);

                }









                final Query query = FirebaseDatabase.getInstance().getReference().child("userchatpendingquantity").child(myUID)
                            .child(model.getOnecircleid())
                            .child("unreadMessages");
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            final TextView pendingLeaperMessageQuantity = (TextView)v.findViewById(R.id.pendingLeaperMessageQuantity);


                                String unreadMessages = dataSnapshot.getValue().toString();
                                int mUnreadMessages = Integer.parseInt(unreadMessages);

                                if (mUnreadMessages < 1){
                                    pendingLeaperMessageQuantity.setText("");
                                    pendingLeaperMessageQuantity.setVisibility(View.GONE);
                                } else{
                                    pendingLeaperMessageQuantity.setText(unreadMessages);
                                    pendingLeaperMessageQuantity.setVisibility(View.VISIBLE);

                                }




                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });





                // Set their text
                messageText.setText(model.getMessageText()); // set last message as it is
                // Format the date before showing it
                if (DateUtils.isToday(model.getMessageTime())){
                    messageTime.setText(DateFormat.format("HH:mm", model.getMessageTime()));

                }
                else{
                    messageTime.setText(DateFormat.format("dd/MM/yyyy", model.getMessageTime()));
                }

                String loadName = model.getloadname();


                // THIS IS USED TO IDENTIFY WHO THE LAST MESSAGE IS FROM
                // THE LOAD NAME FLAG SHOW WHO SENT THE MESSAGE
                // 1 - MEANS SOMEONE ELSE BUT ME SENT IT HENCE SHOW HIS NAME IN THE THE POPULATED LIST
                // 0 - MEANS I SENT THE MESSAGE AND HENCE DON'T THE SENDER NAME ELSE CHAT LIST WILL REFLECT ME SENDING MESSAGE TO MYSELF
                int x = Integer.parseInt(loadName);



                if (x == 0){
                    //don't change number or name in chat list so that it shows from the other leaper
                    phoneNumber.setText(model.getReceiverPhoneNumber());


                    /// SHOWING LAST ONLINE STATUS FOR CHAT
                    DatabaseReference secondLeaperOnlineStatus = dbRef.child("connections").child(model.getReceiverPhoneNumber());

                    secondLeaperOnlineStatus.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child("statusPermission").getValue() == null){
                                //if unable to retrieve the value, do nothing
                                leaperImage.setBorderColor(getResources().getColor(R.color.grey));
                            }
                            else {

                                String statusPermission = dataSnapshot.child("statusPermission").getValue().toString();
                                if (Objects.equals(statusPermission, "0")){


                                }
                                else if (Objects.equals(statusPermission, "1")){

                                    String currentStatus = dataSnapshot.child("lastOnline").getValue().toString();

                                    if (Objects.equals(currentStatus, "true")){
                                        leaperImage.setBorderColor(getResources().getColor(R.color.green)); /// LEAPER IS ONLINE
                                    } else{
                                        leaperImage.setBorderColor(getResources().getColor(R.color.grey)); /// LEAPER IS OFFLINE

                                    }


                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });






                }

                else if(x == 1){
                    // THIS
                    // change number or name in chat list so it shows as from other leaper
                    phoneNumber.setText(model.getSenderPhoneNumber());


                   /// SHOWING LAST ONLINE STATUS FOR CHAT
                    DatabaseReference secondLeaperOnlineStatus = dbRef.child("connections").child(model.getSenderPhoneNumber());

                    secondLeaperOnlineStatus.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child("statusPermission").getValue() == null){
                                //if unable to retrieve the value, do nothing
                                leaperImage.setBorderColor(getResources().getColor(R.color.grey)); /// LEAPER IS ONLINE
                            }
                            else {

                                String statusPermission = dataSnapshot.child("statusPermission").getValue().toString();
                                if (Objects.equals(statusPermission, "0")){


                                }
                                else if (Objects.equals(statusPermission, "1")){

                                    String currentStatus = dataSnapshot.child("lastOnline").getValue().toString();

                                    if (Objects.equals(currentStatus, "true")){
                                        leaperImage.setBorderColor(getResources().getColor(R.color.green)); /// LEAPER IS ONLINE
                                    } else{
                                        leaperImage.setBorderColor(getResources().getColor(R.color.grey)); /// LEAPER IS OFFLINE

                                    }


                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }

















            }
        };

        listOfMessages.setAdapter(adapter);



        //return inflated layout
        return view;


    }


}







