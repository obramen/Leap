package com.antrixgaming.leap;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class groupInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        String groupName = bundle.getString("groupName");
        String circleID = bundle.getString("circleID");
        String createdBy = bundle.getString("circleCreatedBy");
        String createdOn = bundle.getString("circleCreatedOn");

        getSupportActionBar().setTitle(groupName);
        getSupportActionBar().setSubtitle("Created by " + createdBy +", " + createdOn);
        //ActionBar().setSubtitle("Created by " + createdBy +", " + createdOn);




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
