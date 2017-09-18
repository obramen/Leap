package com.antrixgaming.leap;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.antrixgaming.leap.LeapClasses.sendNotification;
import com.antrixgaming.leap.NewClasses.circleMessage;
import com.antrixgaming.leap.NewClasses.createGroupCircle;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class receivedNotifications extends AppCompatActivity {

    String groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_received_notifications);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




        ListView listOfMessages = (ListView) findViewById(R.id.list_of_notifications);
        FirebaseListAdapter<sendNotification> adapter;
        adapter = new FirebaseListAdapter<sendNotification>(this, sendNotification.class,
                R.layout.notifications_list, FirebaseDatabase.getInstance().getReference().child("notifications").child(FirebaseAuth.getInstance()
                .getCurrentUser().getPhoneNumber())) {


            @Override
            protected void populateView(View v, final sendNotification model, int position) {
                // Get references to the views of message.xml
                TextView notificationsInviteType = (TextView)v.findViewById(R.id.notificationInviteType);
                TextView notificationsInviteTime = (TextView)v.findViewById(R.id.notificationInviteTime);
                final TextView notificationsInviteBy = (TextView)v.findViewById(R.id.notificationInviteBy);
                TextView notificationsInviteMessage = (TextView)v.findViewById(R.id.notificationInviteMessage);
                TextView notificationConfirmText = (TextView)v.findViewById(R.id.notificationConfirmText);
                Button notificationLeapInButton = (Button) v.findViewById(R.id.notificationLeapInButton);
                Button notificationLeapOutButton = (Button) v.findViewById(R.id.notificationLeapOutButton);


                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

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
                switch(messageFormat){
                    case "1":
                        notificationsInviteBy.setText("Invitation from " + model.getinviteBy());
                        DatabaseReference query = dbRef.child("groupcircles").child(circleID);
                        query.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String mGroupName = dataSnapshot.child("groupName").getValue().toString();
                                groupName = mGroupName;
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        notificationsInviteMessage.setText("You have been invited to join " + groupName);
                        break;

                }


                // Set their text
                notificationsInviteType.setText(model.geinviteType());
                // Format the date before showing it
                notificationsInviteTime.setText(DateFormat.format("dd-MMM-yyyy, HH:mm",
                        model.getinviteTime()));


                notificationLeapInButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // using the new key update members list under same table with the creator of the group setting status to true
                        /// true = admin
                        /// false = not admin
                        FirebaseDatabase.getInstance().getReference().child("groupcircles").child(circleID).child("members")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue("false");
                        // update the usergroupcirclelist (a list containing all groups a leaper is part of


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
                        FirebaseDatabase.getInstance().getReference().child("groupcirclemessages").child(circleID)
                                .child(circleID).setValue(new circleMessage(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() + " joined circle",
                                circleID, "", "", "1"));
                        FirebaseDatabase.getInstance().getReference().child("groupcircles").child(circleID)
                                .child("lastgroupmessage").setValue(new circleMessage(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() + " joined circle",
                                circleID, "", "", "1"));
                        //FirebaseDatabase.getInstance().getReference().child("groupcirclemessages").child(circleID)
                                //.child(circleID).child("members").setValue(memberList);
                        FirebaseDatabase.getInstance().getReference().child("groupcirclelastmessages").child(circleID)
                                .setValue(new circleMessage(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() + " joined circle", circleID,
                                        "", "", ""));



                        FirebaseDatabase.getInstance().getReference().child("notifications").child(FirebaseAuth.getInstance()
                                .getCurrentUser().getPhoneNumber()).child(model.getNotificationID()).child("notificationStatus").setValue("1");
                        FirebaseDatabase.getInstance().getReference().child("sentnotifications").child(model.getinviteBy()).child(model.getNotificationID())
                                .child("notificationStatus").setValue("1");
                        Snackbar.make(v, "Invitation Accepted", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();                    }
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