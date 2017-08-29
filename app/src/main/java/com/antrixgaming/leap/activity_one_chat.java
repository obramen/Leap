package com.antrixgaming.leap;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.ShareActionProvider;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.antrixgaming.leap.NewClasses.ChatMessage;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class activity_one_chat extends AppCompatActivity {


    String oneCircleFirstUserPhoneNumber;
    String  oneCircleFirstUserUid;
    String oneCircleSecondUserPhoneNumber;
    String oneCircleSecondUserUid;
    String oneCircleUid;
    DatabaseReference oneCircleSecondUserUidReference;
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    String numberA;
    String numberB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_chat);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //final FirebaseDatabase database = FirebaseDatabase.getInstance();
        //DatabaseReference ref = database.getReference("/phonenumbers/" + "233242100903" +"/uid");
        //Post post = dataSnapshot.getValue(Post.class);

        //DatabaseReference seconduseruid = FirebaseDatabase.getReference("phonenumbers");


        // One Circle first user details ***phone number and UID***
        oneCircleFirstUserPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
         oneCircleFirstUserUid= FirebaseAuth.getInstance().getCurrentUser().getUid();

        // One Circle second user details ***phone number and UID***
        Bundle bundle = getIntent().getExtras();
        oneCircleSecondUserPhoneNumber = bundle.getString("oneCircleSecondUser"); //phone number

        //Reference path to oneCircleSecondUserUid
        oneCircleSecondUserUidReference = FirebaseDatabase.getInstance()
                                            .getReference("phonenumbers").child("+233242100903")
                                            .child("uid");



        oneCircleSecondUserUidReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Log.i(TAG, dataSnapshot.getValue(String.class);


                //get UID from datareference for second User to chat with
                oneCircleSecondUserUid = dataSnapshot.getValue().toString();

                //TODO compare the two UIDs in order to produce numberA and numberB

                if(oneCircleFirstUserUid.compareTo(oneCircleSecondUserUid)<0){
                    numberA = oneCircleFirstUserUid;
                    numberB = oneCircleSecondUserUid;


                } else if(oneCircleFirstUserUid.compareTo(oneCircleSecondUserUid)>0){
                    numberA = oneCircleSecondUserUid;
                    numberB = oneCircleFirstUserUid;
                }

                else if(oneCircleFirstUserUid.compareTo(oneCircleSecondUserUid)==0){
                    numberA = oneCircleFirstUserUid;
                    numberB = oneCircleFirstUserUid;
                }



                oneCircleUid = numberA + numberB;




                dbRef.child("onecircles").child(oneCircleUid).child("numberA").setValue(numberA);
                dbRef.child("onecircles").child(oneCircleUid).child("numberB").setValue(numberB);

                Toast.makeText(activity_one_chat.this, "oneCircleUID is " + oneCircleUid, Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Log.w(TAG, "onCancelled", databaseError.toException());
            }

        });




        getSupportActionBar().setTitle(oneCircleSecondUserPhoneNumber);
        getSupportActionBar().setIcon(R.drawable.ic_menu_camera);


        ////// INFORMATION FOR THE ONE CIRCLE /////
        ////// ONE CIRCLE ID IS FORMED BY CONCATENATING Uid OF BOTH USERS /////




        FloatingActionButton sendFab =
                (FloatingActionButton)findViewById(R.id.send_chat_fab);

        sendFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText input = (EditText)findViewById(R.id.input);

                if (TextUtils.isEmpty(input.getText().toString().trim())) {
                    return;
                }

                // Read the input field and push a new instance
                // of ChatMessage to the Firebase database inside onecirclemessages table
                String key = dbRef.child("onecirclemessages")
                        .push().getKey();

                dbRef.child("onecirclemessages").child(key)
                        .setValue(new ChatMessage(input.getText().toString(),oneCircleUid,
                                oneCircleFirstUserPhoneNumber, oneCircleFirstUserUid)
                        );
                //get the just sent messageid and add to messages with UID of sender attached
                dbRef.child("onecircles").child(oneCircleUid).child("messages").child(key)
                        .setValue(oneCircleFirstUserUid);


                // Clear the input
                input.setText("");
            }
        });



        ListView listOfMessages = (ListView)findViewById(R.id.list_of_messages);


        FirebaseListAdapter<ChatMessage> adapter;
            adapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
                    R.layout.messages, FirebaseDatabase.getInstance().getReference().child("onecirclemessages")) {

                @Override
                protected void populateView(View v, ChatMessage model, int position) {
                    // Get references to the views of message.xml
                    TextView messageText = (TextView)v.findViewById(R.id.message_text);
                    //TextView messageUser = (TextView)v.findViewById(R.id.message_user);
                    TextView phoneNumber = (TextView)v.findViewById(R.id.phoneNumber);
                    TextView messageTime = (TextView)v.findViewById(R.id.message_time);

                    // Set their text
                    messageText.setText(model.getMessageText());
                    phoneNumber.setText(model.getPhoneNumber());
                    //messageUser.setText(model.getMessageUser());

                    // Format the date before showing it
                    messageTime.setText(DateFormat.format("HH:mm",
                            model.getMessageTime()));





                }
            };

            listOfMessages.setAdapter(adapter);






    }




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
        if (id == R.id.action_one_chat_search) {
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

