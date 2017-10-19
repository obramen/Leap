package com.antrixgaming.leap;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.antrixgaming.leap.Models.CircleMember;
import com.antrixgaming.leap.Models.sendNotification;
import com.antrixgaming.leap.Models.circleMessage;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class receivedNotifications extends BaseActivity {

    String groupName;
    String myPhoneNumber;
    String myUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_received_notifications);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();




        ListView listOfMessages = (ListView) findViewById(R.id.list_of_notifications);
        FirebaseListAdapter<sendNotification> adapter;
        adapter = new FirebaseListAdapter<sendNotification>(this, sendNotification.class,
                R.layout.notifications_list, FirebaseDatabase.getInstance().getReference().child("notifications").child(FirebaseAuth.getInstance()
                .getCurrentUser().getPhoneNumber()).orderByChild("index")) {


            @Override
            protected void populateView(final View v, final sendNotification model, int position) {
                // Get references to the views of message.xml
                TextView notificationsInviteType = (TextView)v.findViewById(R.id.notificationInviteType);
                TextView notificationsInviteTime = (TextView)v.findViewById(R.id.notificationInviteTime);
                final TextView notificationsInviteBy = (TextView)v.findViewById(R.id.notificationInviteBy);
                final TextView notificationsInviteMessage = (TextView)v.findViewById(R.id.notificationInviteMessage);
                TextView notificationConfirmText = (TextView)v.findViewById(R.id.notificationConfirmText);
                Button notificationLeapInButton = (Button) v.findViewById(R.id.notificationLeapInButton);
                Button notificationLeapOutButton = (Button) v.findViewById(R.id.notificationLeapOutButton);
                final TextView displayedLeaperName = (TextView)v.findViewById(R.id.displayedLeaperName);



                final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

                final String circleID = model.getcircleID();

                // 0 - unattended
                // 1 - accepted
                // 2 - declined
                switch(model.getnotificationStatus()){
                    case "0":
                        notificationConfirmText.setVisibility(View.GONE);
                        notificationLeapInButton.setVisibility(View.VISIBLE);
                        notificationLeapOutButton.setVisibility(View.VISIBLE);
                        break;
                    case "1":
                        notificationConfirmText.setVisibility(View.VISIBLE);
                        notificationConfirmText.setText("ACCEPTED");
                        notificationLeapInButton.setVisibility(View.GONE);
                        notificationLeapOutButton.setVisibility(View.GONE);
                        break;
                    case "2":
                        notificationConfirmText.setVisibility(View.VISIBLE);
                        notificationConfirmText.setText("DECLINED");
                        notificationLeapInButton.setVisibility(View.GONE);
                        notificationLeapOutButton.setVisibility(View.GONE);
                        break;
                }




                // Case for invite type and invite group
                // 1 - Circle invite // Invite to join + Circle name (Load using circle ID)
                String messageFormat = model.getinviteMessage();
                if (messageFormat == "1") {
                    notificationsInviteBy.setText("Invitation from " + model.getinviteBy());
                    FirebaseDatabase.getInstance().getReference().child("groupcirclenames").child(circleID)
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    String circleName = dataSnapshot.child("circleName").getValue().toString();
                                    groupName = dataSnapshot.child("circleName").getValue().toString();


                                    notificationsInviteMessage.setText("You have been invited to join " + circleName);

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });



                }


                // Set their text
                notificationsInviteType.setText(model.geinviteType());
                // Format the date before showing it
                notificationsInviteTime.setText(DateFormat.format("dd-MMM-yyyy, HH:mm",
                        model.getinviteTime()));
                notificationsInviteBy.setText(model.getinviteBy());


                notificationLeapInButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // using the new key update members list under same table with the creator of the group setting status to true
                        /// true = admin
                        /// false = not admin
                        FirebaseDatabase.getInstance().getReference().child("groupcirclemembers").child(circleID).child("currentmembers").child(myPhoneNumber)
                                .setValue(new CircleMember(myPhoneNumber,"false","1"));
                        // update the usergroupcirclelist (a list containing all groups a leaper is part of
                        FirebaseDatabase.getInstance().getReference().child("groupcirclesettings").child(myPhoneNumber).child(circleID).child("leapStatus").setValue("1");


                        // This list is used to load the circles fragment
                        // First add the group id
                        FirebaseDatabase.getInstance().getReference().child("usergroupcirclelist")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(circleID).child("groupid").setValue(circleID);
                        // Next add the group name
                        FirebaseDatabase.getInstance().getReference().child("usergroupcirclelist")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(circleID).child("groupName").setValue(groupName);
                        // Lastly add the admin status
                        FirebaseDatabase.getInstance().getReference().child("usergroupcirclelist")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(circleID).child("admin").setValue("false");


                        //push new messages using Circle ID stored
                        /// 1 - shows it's a notification message // 0 - normal message
                        String key = FirebaseDatabase.getInstance().getReference().child("groupcirclemessages").child(circleID)
                                .push().getKey();
                        FirebaseDatabase.getInstance().getReference().child("groupcirclemessages").child(circleID)
                                .child(key).setValue(new circleMessage(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() + " joined circle",
                                circleID, "", "", "1", "false"));
                        FirebaseDatabase.getInstance().getReference().child("groupcircles").child(circleID)
                                .child("lastgroupmessage").setValue(new circleMessage(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() + " joined circle",
                                circleID, "", "", "1", "true"));
                        //FirebaseDatabase.getInstance().getReference().child("groupcirclemessages").child(circleID)
                                //.child(circleID).child("members").setValue(memberList);
                        FirebaseDatabase.getInstance().getReference().child("groupcirclelastmessages").child(circleID)
                                .setValue(new circleMessage(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() + " joined circle", circleID,
                                        "", "", "1", "true"));



                        FirebaseDatabase.getInstance().getReference().child("notifications").child(FirebaseAuth.getInstance()
                                .getCurrentUser().getPhoneNumber()).child(model.getNotificationID()).child("notificationStatus").setValue("1");
                        FirebaseDatabase.getInstance().getReference().child("sentnotifications").child(model.getinviteBy()).child(model.getNotificationID())
                                .child("notificationStatus").setValue("1");
                        Snackbar.make(v, "Invitation Accepted", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();




                    }
                });



                notificationLeapOutButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseDatabase.getInstance().getReference().child("notifications").child(FirebaseAuth.getInstance()
                                .getCurrentUser().getPhoneNumber()).child(model.getNotificationID()).child("notificationStatus").setValue("2");
                        FirebaseDatabase.getInstance().getReference().child("sentnotifications").child(model.getinviteBy()).child(model.getNotificationID())
                                .child("notificationStatus").setValue("2");
                        Snackbar.make(v, "Invitation Declined", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                    }
                });












                /////////////////////////////////////////////////////////////////////
                ///////////////////////////////////////////
                //////// GETTING AND SETTING NAMES IN PLACE OF PHONE NUMBER

                ///////////////////////////////////////
                //////////////////   STARTING    ///////////////////////////////




                //// CHECK MY CONTACT LIST IF THIS PERSON IS A CONTACT
                dbRef.child("ContactList").child(myUID).child("leapSortedContacts").child(model.getinviteBy())
                        .addValueEventListener(new ValueEventListener() {////////
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (dataSnapshot.child("name").getValue() == null || dataSnapshot.child("name")
                                        .getValue() == "") {///// IF THEY ARE NOT A CONTACT OR THE VALUE IS EMPTY



                                    ///// CHECK THE USERS PROFILES TO SEE IF THEY HAVE AN ENTRY THERE
                                    dbRef.child("userprofiles").child(model.getinviteBy()).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.child("name").getValue() == null || Objects.equals(dataSnapshot.child("name")
                                                    .getValue().toString(), "")){///IF THEY DON'T HAVE AN ENTRY USE THEIR PHONE NUMBER

                                                /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////
                                                displayedLeaperName.setText(model.getinviteBy());
                                                /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////


                                            } else { //// IF THEY HAVE AN ENTRY USE THEIR ENTERED NAME

                                                String myName = dataSnapshot.child("name").getValue().toString();

                                                /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////
                                                displayedLeaperName.setText("~ " + myName);
                                                /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////s


                                            }




                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });







                                } else {/// IF THEY ARE A CONTACT USE THE SAVED NAME


                                    String mName = dataSnapshot.child("name").getValue().toString();

                                    /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////
                                    displayedLeaperName.setText(mName);
                                    /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////


                                }


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });







                //////////////////   ENDING    ///////////////////////////////
                ///////////////////////////////////////

                //////// GETTING AND SETTING NAMES IN PLACE OF PHONE NUMBER
                ///////////////////////////////////////////
                /////////////////////////////////////////////////////////////////////










            }
        };

        listOfMessages.setAdapter(adapter);

    }


    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
