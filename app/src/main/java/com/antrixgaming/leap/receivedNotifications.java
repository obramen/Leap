package com.antrixgaming.leap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.antrixgaming.leap.LeapClasses.sendNotification;
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
                TextView notificationsInviteBy = (TextView)v.findViewById(R.id.notificationInviteBy);
                TextView notificationsInviteMessage = (TextView)v.findViewById(R.id.notificationInviteMessage);
                Button notificationLeapInButton = (Button) v.findViewById(R.id.notificationLeapInButton);
                Button notificationLeapOutButton = (Button) v.findViewById(R.id.notificationLeapOutButton);


                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();


                // Case for invite type and invite group
                // 1 - Circle invite // Invite to join + Circle name (Load using circle ID)
                String messageFormat = model.getinviteMessage();
                switch(messageFormat){
                    case "1":
                        notificationsInviteBy.setText("Invitation from " + model.getinviteBy());
                        String circleID = model.getcircleID();
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
