package com.antrixgaming.leap;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.antrixgaming.leap.LeapClasses.LeapUtilities;
import com.antrixgaming.leap.Models.CreateEvent;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.Format;

public class events extends AppCompatActivity {

    String myUID;
    String myPhoneNumber;
    DatabaseReference dbRef;
    DatabaseReference eventsDbRef;
    StorageReference mStorage;
    StorageReference mEventStorage;
    FirebaseListAdapter<CreateEvent> adapter;

    ImageView eventImage;
    TextView eventDay;
    TextView eventMonth;
    TextView eventTitle;
    TextView eventDescription;
    TextView eventID;

    LeapUtilities leapUtilities;








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbRef = FirebaseDatabase.getInstance().getReference();
        eventsDbRef = dbRef.child("AnTrixEvents");
        myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        myPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        leapUtilities = new LeapUtilities();

        mStorage = FirebaseStorage.getInstance().getReference();




        adapter = new FirebaseListAdapter<CreateEvent> (this, CreateEvent.class, R.layout.events_list,
                eventsDbRef.orderByChild("eventStartDate")){

            @Override
            protected void populateView(View v, CreateEvent model, int position) {

                eventImage = (ImageView) v.findViewById(R.id.eventImage);
                eventDay = (TextView)v.findViewById(R.id.eventDay);
                eventMonth = (TextView)v.findViewById(R.id.eventMonth);
                eventTitle = (TextView)v.findViewById(R.id.eventTitle);
                eventDescription = (TextView)v.findViewById(R.id.eventDescription);
                eventID = (TextView)v.findViewById(R.id.eventID);

                String mEventTitle = model.getEventTitle();
                String mEventDescription = model.getEventDescription();


                CharSequence mEventDay = android.text.format.DateFormat.format("dd", model.getEventStartDate());
                CharSequence mEventMonth = android.text.format.DateFormat.format("MMM", model.getEventStartDate());

                eventDay.setText(mEventDay);
                eventMonth.setText(mEventMonth);
                eventTitle.setText(mEventTitle);



                mEventStorage = mStorage.child("events").child(model.getEventBy()).child(model.getEventID()).child(model.getEventID());
                leapUtilities.SquareImageFromFirebase(events.this, mEventStorage, eventImage);



                if (android.text.format.DateFormat.format("dd-MM-yyyy", model.getEventStartDate()) ==
                        android.text.format.DateFormat.format("dd-MM-yyyy", model.getEventEndDate())){

                    eventDescription.setText(mEventDescription);

                } else {

                    CharSequence mEventEndDay = android.text.format.DateFormat.format("dd", model.getEventStartDate());
                    CharSequence mEventEndMonth = android.text.format.DateFormat.format("MMM", model.getEventStartDate());

                    mEventDescription = mEventDay + " " + mEventMonth + "-" + mEventEndDay + " " + mEventEndMonth + ":" + mEventDescription;

                    eventDescription.setText(mEventDescription);


                }


            }
        };


        ListView listView = (ListView)findViewById(R.id.list_of_events);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                eventID = (TextView)findViewById(R.id.eventID);

                Intent eventDetailsIntent = new Intent();
                eventDetailsIntent.putExtra("eventID", eventID.getText().toString());
                startActivity(eventDetailsIntent);

            }
        });






    }
}
