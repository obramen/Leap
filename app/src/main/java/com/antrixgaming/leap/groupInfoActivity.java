package com.antrixgaming.leap;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class groupInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        final String groupName = bundle.getString("groupName");
        String circleID = bundle.getString("circleID");

        // get the created date and user who created the group
        ValueEventListener listener = FirebaseDatabase.getInstance().getReference().child("groupcircles").child(circleID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String groupCreatedBy = dataSnapshot.child("createdBy").getValue().toString();
                Long groupCreatedOn = Long.parseLong(dataSnapshot.child("createdOn").getValue().toString());
                String groupName = dataSnapshot.child("groupName").getValue().toString();


                /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////
                getSupportActionBar().setTitle(groupName);
                getSupportActionBar().setSubtitle("Created by " + groupCreatedBy +", " + DateFormat.format("dd-MMM-yyyy", groupCreatedOn));
                //getSupportActionBar().show();
                /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }





    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }
}
