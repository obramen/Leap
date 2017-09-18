package com.antrixgaming.leap;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.antrixgaming.leap.NewClasses.circleMessage;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.materialdrawer.DrawerBuilder;

import java.util.ArrayList;
import java.util.Date;

public class activity_one_circle extends AppCompatActivity {

    String groupID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_circle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        //final String groupName = bundle.getString("groupName");
        final String circleID = bundle.getString("circleID");
        groupID = circleID;

        final String myPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        // get the created date and user who created the group
        ValueEventListener listener = FirebaseDatabase.getInstance().getReference().child("groupcircles").child(circleID)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String groupCreatedBy = dataSnapshot.child("createdBy").getValue().toString();
                String groupCreatedOn = dataSnapshot.child("createdOn").getValue().toString();
                String groupName = dataSnapshot.child("groupName").getValue().toString();

                /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////
                getSupportActionBar().setTitle(groupName);
                getSupportActionBar().setSubtitle("Created by " + groupCreatedBy +", " + groupCreatedOn);

                /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });












        //groupID.setText(circleID);




        // record user's last opening of the group
        // long lastGroupOpenTime = new Date().getTime();
        // FirebaseDatabase.getInstance().getReference().child("groupcircles").child(circleID).child("members").child(myUid).child("lastOpenTime").setValue(lastGroupOpenTime);

        //// CREATE AN ARRAY FOR THE LIST OF MEMBERS AND UPDATE IT ON DATA CHANGE
        final ArrayList<ArrayList<String >> memberList = new ArrayList<ArrayList<String>>();
        final ArrayList<String> loadedMember = new ArrayList<String>();

        FirebaseDatabase.getInstance().getReference().child("groupcircles").child(circleID).child("members")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            //get member
                            String memberUid = dataSnapshot.getKey();
                            loadedMember.add(memberUid);

                        }
                        memberList.add(loadedMember);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });





        ////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////
        /////////////////////////// NEW MESSAGE START //////////////////////////

        //Send key functions start here
        FloatingActionButton sendFab =
                (FloatingActionButton) findViewById(R.id.send_chat_fab);

        sendFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText input = (EditText) findViewById(R.id.input);

                if (TextUtils.isEmpty(input.getText().toString().trim())) {
                    return;
                }

                // Read the input field and push a new instance
                // of CircleMessage to the Firebase database inside groupcirclemessages table

                //push new messages using Circle ID stored
                String key = FirebaseDatabase.getInstance().getReference().child("groupcirclemessages").child(circleID)
                        .push().getKey();
                FirebaseDatabase.getInstance().getReference().child("groupcirclemessages").child(circleID)
                        .child(key).setValue(new circleMessage(input.getText().toString(), circleID,
                                myPhoneNumber, myUid, "0"));
                FirebaseDatabase.getInstance().getReference().child("groupcircles").child(circleID)
                        .child("lastgroupmessage").setValue(new circleMessage(input.getText().toString(), circleID,
                                myPhoneNumber, myUid, "0"));
                FirebaseDatabase.getInstance().getReference().child("groupcirclemessages").child(circleID)
                        .child(key).child("members").setValue(memberList);
                FirebaseDatabase.getInstance().getReference().child("groupcirclelastmessages").child(circleID)
                        .setValue(new circleMessage(input.getText().toString(), circleID,
                        myPhoneNumber, myUid, "0"));


                // Clear the input
                input.setText("");
            }

        });


        ////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////
        /////////////////////////// NEW MESSAGE END ////////////////////////////


        /////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////
        /////////////////////////// READ DATA START /////////////////////

                ListView listOfMessages = (ListView) findViewById(R.id.list_of_group_messages);

                FirebaseListAdapter<circleMessage> adapter;
                adapter = new FirebaseListAdapter<circleMessage>(activity_one_circle.this, circleMessage.class,
                        R.layout.messages_for_group, FirebaseDatabase.getInstance().getReference().child("groupcirclemessages")
                        .child(circleID)) {
                    @Override
                    protected void populateView(View v, circleMessage model, int position) {

                        // Get references to the views of message.xml
                        TextView messageText = (TextView) v.findViewById(R.id.group_message_text);
                        //TextView messageUser = (TextView)v.findViewById(R.id.group_message_user);
                        TextView phoneNumber = (TextView) v.findViewById(R.id.group_phoneNumber);
                        TextView messageTime = (TextView) v.findViewById(R.id.group_message_time);
                        TextView groupNotificationMessage = (TextView) v.findViewById(R.id.groupNotificationMessage);
                        LinearLayout groupTxtBracketTop = (LinearLayout) v.findViewById(R.id.groupTextBracketTop);
                        RelativeLayout groupTxtBracketBottom = (RelativeLayout) v.findViewById(R.id.groupTextBracketBottom);


                        String messageType = model.getmessageType();

                        switch(messageType){
                            case "0":
                                groupNotificationMessage.setVisibility(v.GONE);
                                groupTxtBracketTop.setVisibility(v.VISIBLE);
                                groupTxtBracketBottom.setVisibility(v.VISIBLE);
                                messageText.setText(model.getMessageText());
                                break;
                            case "1":
                                groupNotificationMessage.setVisibility(v.VISIBLE);
                                groupTxtBracketTop.setVisibility(v.GONE);
                                groupTxtBracketBottom.setVisibility(v.GONE);
                                groupNotificationMessage.setText(model.getMessageText());
                                break;
                        }

                        // Set their text
                        phoneNumber.setText(model.getPhoneNumber());
                        //messageUser.setText(model.getMessageUser());

                        // Format the date before showing it
                        messageTime.setText(DateFormat.format("HH:mm",
                                model.getMessageTime()));

                    }
                };

                listOfMessages.setAdapter(adapter);
            }








        /////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////
        /////////////////////////// READ DATA END ///////////////////////




    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group_chat, menu);

        ////////////////////////////////// SEARCH BUTTON START /////////////////////////////
        MenuItem searchItem = menu.findItem(R.id.action_one_group_chat_memberlist);
        new DrawerBuilder().withActivity(this).build();


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_one_group_chat_memberlist) {

            return true;
        }else if (id == R.id.action_one_group_chat_Group_info) {

            Intent groupInfoIntent = new Intent(this, groupInfoActivity.class);
            groupInfoIntent.putExtra("circleID", groupID);
            startActivity(groupInfoIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);




    }






    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }


}
