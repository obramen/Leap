package com.antrixgaming.leap;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.antrixgaming.leap.Models.ChatMessage;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.Objects;

public class activity_one_chat extends BaseActivity {


    private String oneCircleFirstUserPhoneNumber;
    private String  oneCircleFirstUserUid;
    private String oneCircleSecondUserPhoneNumber;
    private String oneCircleSecondUserUid;
    public String oneCircleUid;
    private DatabaseReference oneCircleSecondUserUidReference;
    private String numberA;
    private String numberB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_chat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar();

        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

        final TextView secondUid = (TextView) findViewById(R.id.secondUid);
        final TextView circleid = (TextView) findViewById(R.id.circleUid);

        // One Circle first user details ***phone number and UID***
        oneCircleFirstUserPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        oneCircleFirstUserUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // One Circle second user details ***phone number and UID***
        Bundle bundle = getIntent().getExtras();
        oneCircleSecondUserPhoneNumber = bundle.getString("oneCircleSecondUser"); //phone number
        /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////
        getSupportActionBar().setTitle(oneCircleSecondUserPhoneNumber);
        /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////


        ///////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////////////////////////////////
        /////////////////////////// CREATE CIRCLE START ///////////////////////

        //Reference path to oneCircleSecondUserUid
        oneCircleSecondUserUidReference = dbRef.child("phonenumbers").child(oneCircleSecondUserPhoneNumber).child("uid");


        if (oneCircleFirstUserPhoneNumber.compareTo(oneCircleSecondUserPhoneNumber) < 0) {
            numberA = oneCircleFirstUserPhoneNumber;
            numberB = oneCircleSecondUserPhoneNumber;
        } else if (oneCircleFirstUserPhoneNumber.compareTo(oneCircleSecondUserPhoneNumber) > 0) {
            numberA = oneCircleSecondUserPhoneNumber;
            numberB = oneCircleFirstUserPhoneNumber;
        } else if (Objects.equals(oneCircleFirstUserPhoneNumber, oneCircleSecondUserPhoneNumber)) {
            numberA = oneCircleFirstUserPhoneNumber;
            numberB = oneCircleFirstUserPhoneNumber;
        }

        //number to save as onecircle uid
        oneCircleUid = numberA + numberB;
        circleid.setText(oneCircleUid);


        dbRef.child("onecircles").child(oneCircleUid).child("numberA").setValue(numberA);
        dbRef.child("onecircles").child(oneCircleUid).child("numberB").setValue(numberB);



        //final String oneCircleUid = oneCircleFirstUserPhoneNumber + oneCircleSecondUserPhoneNumber;
        //circleid.setText(oneCircleUid);




        //oneCircleSecondUserUidReferenceA = FirebaseDatabase.getInstance().getReference().child("uid").orderByChild("phoneNumber").


        long lastSeenTimeWithMe = new Date().getTime();
        oneCircleSecondUserUidReference.getParent().child("lastseenwith" + oneCircleFirstUserUid).setValue(lastSeenTimeWithMe);

        /// add seen with time to ensure data change for value event listener
        oneCircleSecondUserUidReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.i(TAG, dataSnapshot.getValue(String.class);

                //get UID from datareference for second User to chat with
                oneCircleSecondUserUid = dataSnapshot.getValue().toString();

                secondUid.setText(oneCircleSecondUserUid);

                //compare two UIDs and generate circle uid Number A should be smaller than Number B

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Log.w(TAG, "onCancelled", databaseError.toException());
            }


        });


        ////// this function returns running of the code to the value event listener above to get secondUid
        oneCircleSecondUserUid = secondUid.getText().toString();



        /////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////
        ///////////////////////////CREATE CIRCLE END //////////////////


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
                // of ChatMessage to the Firebase database inside onecirclemessages table
                // false shows message not read

                //first push and store the generated key for the message
                String key = FirebaseDatabase.getInstance().getReference().child(oneCircleUid).child("onecirclsemessages")
                        .push().getKey();
                //next update the message using the key stored
                FirebaseDatabase.getInstance().getReference().child("onecirclemessages").child(oneCircleUid).child(key)
                        .setValue(new ChatMessage(input.getText().toString().trim(), oneCircleUid,
                                oneCircleFirstUserPhoneNumber, oneCircleSecondUserPhoneNumber, oneCircleFirstUserUid, "0", "false"));
                //get the just sent messageid and add to one circle with UID of sender attached
                //FirebaseDatabase.getInstance().getReference().child("onecircles").child(oneCircleUid).child("onecirclemessages").child(key).setValue(oneCircleFirstUserUid);


                //save into first user's chat list ----- this uses the "chatlist" table to populate list of chats
                ///////////////////------USE THE CHATMESSAGE CLASS-------/////////////////////
                /////////////////////// THE PHONE NUMBER FIELD IS SET TO THE RECEIVER'S PHONE
                /// /////////////////// NUMBER SO AS TO SHOW IN FIRST USER'S CHAT LIST AS WHO IT WAS SENT TO
                FirebaseDatabase.getInstance().getReference().child("userchatlist").child(oneCircleFirstUserUid).child(oneCircleUid)
                        .setValue(new ChatMessage(input.getText().toString().trim(), oneCircleUid,
                                oneCircleFirstUserPhoneNumber, oneCircleSecondUserPhoneNumber, oneCircleFirstUserUid, "0", "true"));
                //// tell the data where to be loaded with sender name or not. this affects how it appears in chat list
                //dbRef.child("userchatlist").child(oneCircleFirstUserUid).child(oneCircleUid).child("loadname")
                //       .setValue("0"); // 0 means false so don't load it


                ///////////////////------USE THE CHATMESSAGE CLASS-------/////////////////////
                /////////////////////// THE PHONE NUMBER FIELD IS SET TO THE SENDER'S PHONE NUMBER SO AS TO
                /////////////////////// SHOW IN THE CHAT LIST OF THE SECOND USER SO AS TO SHOW WHO SENT THE MESSAGE
                //save into second user's chat list ----- this uses the "chatlist" table to populate list of chats
                /////////////////////// NOTE THIS WILL SHOW IN DATABASE AS SENDER'S NUMBER
                FirebaseDatabase.getInstance().getReference().child("userchatlist").child(oneCircleSecondUserUid).child(oneCircleUid)
                        .setValue(new ChatMessage(input.getText().toString().trim(), oneCircleUid,
                                oneCircleFirstUserPhoneNumber, oneCircleSecondUserPhoneNumber, oneCircleFirstUserUid, "1", "true"));
                //update with sender number / sender name to user chatList location
                //dbRef.child("userchatlist").child(oneCircleSecondUserUid).child(oneCircleUid).child("loadname")
                //        .setValue("1"); // 1 means true so load it



                // increase unread messages for second leaper


                final Query query = FirebaseDatabase.getInstance().getReference().child("userchatpendingquantity").child(oneCircleSecondUserUid).child(oneCircleUid)
                        .child("unreadMessages");
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {




                        if(dataSnapshot.getValue() == null){
                            FirebaseDatabase.getInstance().getReference().child("userchatpendingquantity").child(oneCircleSecondUserUid).child(oneCircleUid)
                                    .child("unreadMessages").setValue("1");
                            FirebaseDatabase.getInstance().getReference().child("userchatpendingquantity").child(oneCircleFirstUserUid).child(oneCircleUid)
                                    .child("unreadMessages").setValue("0");
                            query.removeEventListener(this);
                        }

                        if(dataSnapshot.getValue() != null) {

                            String unreadMessages = dataSnapshot.getValue().toString();

                            int mUnreadMessages = Integer.parseInt(unreadMessages);

                            String newQuantity = String.valueOf(mUnreadMessages + 1);

                            FirebaseDatabase.getInstance().getReference().child("userchatpendingquantity").child(oneCircleSecondUserUid).
                                    child(oneCircleUid).child("unreadMessages").setValue(newQuantity);

                            query.removeEventListener(this);

                        }




                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });




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


        //TODO optimize sorting to 200 messages for fast listing

        // TODO PUT CATCH EXCEPTION HERE FOR ERROR HANDLING

        final String mCircleID = circleid.getText().toString();
        ListView listOfMessages = (ListView) findViewById(R.id.list_of_messages);

        FirebaseListAdapter<ChatMessage> adapter;
        adapter = new FirebaseListAdapter<ChatMessage>(activity_one_chat.this, ChatMessage.class,
                R.layout.messages, FirebaseDatabase.getInstance().getReference().child("onecirclemessages")
                .child(mCircleID)) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {


                //RelativeLayout sentMessageLayout = (RelativeLayout)v.findViewById(R.id.sentMessageLayout);
                //RelativeLayout receivedMessageLayout = (RelativeLayout)v.findViewById(R.id.receivedMessageLayout);

                // Get references to the views of message.xml
                TextView messageText = (TextView) v.findViewById(R.id.message_text);
                //TextView messageUser = (TextView)v.findViewById(R.id.message_user);
                TextView phoneNumber = (TextView) v.findViewById(R.id.phoneNumber);
                TextView messageTime = (TextView) v.findViewById(R.id.message_time);





                    // Set their text
                    messageText.setText(model.getMessageText());
                    phoneNumber.setText(model.getSenderPhoneNumber());
                    //messageUser.setText(model.getMessageUser());

                    // Format the date before showing it
                    messageTime.setText(DateFormat.format("HH:mm",
                            model.getMessageTime()));



                    FirebaseDatabase.getInstance().getReference().child("userchatpendingquantity").child(oneCircleFirstUserUid).
                            child(oneCircleUid).child("unreadMessages").setValue("0");






                }
            };

            listOfMessages.setAdapter(adapter);




        /// SHOWING LAST ONLINE STATUS FOR CHAT
        DatabaseReference secondLeaperOnlineStatus = dbRef.child("connections").child(oneCircleSecondUserPhoneNumber);

        secondLeaperOnlineStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("statusPermission").getValue() == null){
                    //if unable to retrieve the value, do nothing
                }
                else {
                    String statusPermission = dataSnapshot.child("statusPermission").getValue().toString();
                    switch(statusPermission){

                        case "0":  // 0 - false // don't allow last seen
                            break;
                        case "1":  // 1 - true  // allow last seen
                            String currentStatus = dataSnapshot.child("lastOnline").getValue().toString();

                            if (currentStatus == "true"){
                                getSupportActionBar().setSubtitle("Online");
                            } else{

                                if ((DateFormat.format("dd-MM-yyyy", Long.parseLong(currentStatus))  == (DateFormat.format("dd-MM-yyyy", new Date().getTime())))){
                                    getSupportActionBar().setSubtitle("Today, " + DateFormat.format("HH:mm", Long.parseLong(currentStatus)));

                                } else {

                                    getSupportActionBar().setSubtitle(DateFormat.format("dd-MMM-yyyy, HH:mm", Long.parseLong(currentStatus)));

                                }



                            }
                            break;
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





    }






            /////////////////////////////////////////////////////////////////
            /////////////////////////////////////////////////////////////////
            /////////////////////////// READ DATA END ///////////////////////


        @Override
        public boolean onCreateOptionsMenu(final Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_one_chat, menu);

            ////////////////////////////////// SEARCH BUTTON START /////////////////////////////
            MenuItem searchItem = menu.findItem(R.id.action_one_chat_search);
            SearchView searchView =
                    (SearchView) MenuItemCompat.getActionView(searchItem);


            // Define the listener
            MenuItemCompat.OnActionExpandListener expandListener = new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    // Do something when action item collapses

                    return true;  // Return true to collapse action view
                }

                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    // Do something when expanded

                    return true;  // Return true to expand action view
                }
            };

            // Get the MenuItem for the action item
            //MenuItem actionMenuItem = menu.findItem(R.id.action_contacts_search);

            // Assign the listener to that action item
            //MenuItemCompat.setOnActionExpandListener(actionMenuItem, expandListener);
            ////////////////////////////////// SEARCH BUTTON END /////////////////////////////


            ////////////////////////////////// SHARE BUTTON START /////////////////////////////
            //MenuItem shareItem = menu.findItem(R.id.action_one_chat_share);
            //ShareActionProvider myShareActionProvider =
              //      (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
            //Intent myShareIntent = new Intent(Intent.ACTION_SEND);
            //myShareIntent.setType("text/*");
            //myShareIntent.putExtra(Intent.EXTRA_STREAM, "Text");

            //myShareActionProvider.setShareIntent(myShareIntent);

            // Image has changed! Update the intent:
            //myShareIntent.putExtra(Intent.EXTRA_STREAM, "My new Text");
            //myShareActionProvider.setShareIntent(myShareIntent);
            ////////////////////////////////// SHARE BUTTON END /////////////////////////////

            return super.onCreateOptionsMenu(menu);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_one_chat_new_leap) {
                Intent startNewLeapIntent = new Intent(this, newLeap.class);
                startNewLeapIntent.putExtra("leapedPhoneNumber", oneCircleSecondUserPhoneNumber);
                startNewLeapIntent.putExtra("SourceActivity", "2");  // to be used to identify that the extras came from here
                startActivity(startNewLeapIntent);
                return true;
            }
            else if (id == R.id.action_one_chat_leap_history) {
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

