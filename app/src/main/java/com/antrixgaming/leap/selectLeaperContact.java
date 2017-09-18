package com.antrixgaming.leap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.antrixgaming.leap.LeapClasses.savePhoneContacts;
import com.antrixgaming.leap.LeapClasses.sendNotification;
import com.antrixgaming.leap.NewClasses.getPhoneContacts;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class selectLeaperContact extends AppCompatActivity {


    String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String myPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("ContactList").child(myUID).child("leapSortedContacts");
    String selectedContact = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_leaper_contact);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        final FloatingActionButton addLeaperToGroupFAB = (FloatingActionButton) findViewById(R.id.addLeaperToGroupFAB);
        addLeaperToGroupFAB.setVisibility(View.GONE);
        final RelativeLayout selectLeaperListLayout = (RelativeLayout) findViewById(R.id.selectLeaperListLayout);
        final TextView selectedLeapers = (TextView) findViewById(R.id.selectedLeapers);

        ListView listOfContacts = (ListView) findViewById(R.id.leaperContactList);

        FirebaseListAdapter<savePhoneContacts> adapter;
        adapter = new FirebaseListAdapter<savePhoneContacts>(this, savePhoneContacts.class,
                R.layout.phone_contact_list, dbRef) {
            @Override
            protected void populateView(View v, savePhoneContacts model, int position) {


                TextView mPhoneContactName = (TextView) v.findViewById(R.id.phoneContactName);
                TextView mPhoneContactType = (TextView) v.findViewById(R.id.phoneContactType);
                TextView mContactPhoneNumber = (TextView) v.findViewById(R.id.phoneContactStatus);
                // Populate the data into the template view using the data object
                mPhoneContactName.setText(model.getcontactName());
                mContactPhoneNumber.setText(model.getcontactPhoneNumber());

                //get the contact TYPE digit and assign manually
                int id = Integer.parseInt(model.getcontactType());
                if (id == 1) {
                    mPhoneContactType.setText("HOME");
                } else if (id == 2) {
                    mPhoneContactType.setText("MOBILE");
                } else if (id == 3) {
                    mPhoneContactType.setText("WORK");
                } else if (id == 4) {
                    mPhoneContactType.setText("");
                } else if (id == 5) {
                    mPhoneContactType.setText("");
                } else if (id == 6) {
                    mPhoneContactType.setText("PAGER");
                } else if (id == 7) {
                    mPhoneContactType.setText("OTHER");
                } else if (id == 8) {
                    mPhoneContactType.setText("");
                } else {
                    mPhoneContactType.setText("");
                }

            }


        };

        listOfContacts.setAdapter(adapter);



        // get source intent which determines where this activity was opened from.
        // 1 = chats using newChatFAB
        // 2 = new leap fragment
        // 3 = add leapers to group from groupInfoActivity

        String CircleID = null;
        Bundle bundle = getIntent().getExtras();
        final String SourceActivity = bundle.getString("SourceActivity");
        if(bundle.getString("CircleID") == null){

        } else {
            CircleID = bundle.getString("CircleID");
        }

        final ListView listView = (ListView) findViewById(R.id.leaperContactList);
        final String finalCircleID = CircleID;
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                final TextView returnContact = (TextView)view.findViewById(R.id.phoneContactStatus);
                switch (SourceActivity){
                    case "1":
                        String oneCircleSecondUser = returnContact.getText().toString();
                        Intent newChatIntent = new Intent(selectLeaperContact.this, activity_one_chat.class);
                        newChatIntent.putExtra("oneCircleSecondUser", oneCircleSecondUser);
                        startActivity(newChatIntent);
                        finish();
                        break;
                    case "2":
                        String secondLeaper = returnContact.getText().toString();
                        Intent leaperTwoResult = new Intent();
                        leaperTwoResult.putExtra("SecondLeaper", secondLeaper);
                        setResult(Activity.RESULT_OK, leaperTwoResult);
                        finish();
                        break;
                    case "3":

                        addLeaperToGroupFAB.setVisibility(View.VISIBLE);
                        selectLeaperListLayout.setVisibility(View.VISIBLE);
                        listView.setSelector(android.R.color.darker_gray);

                        selectedLeapers.setText(returnContact.getText().toString());
                        selectedContact = returnContact.getText().toString();
                        break;
                }



            }


        });

        addLeaperToGroupFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 0 - notification status
                // 1 - type of notification

                String key = FirebaseDatabase.getInstance().getReference().child("notifications").child(selectedContact)
                        .push().getKey();
                FirebaseDatabase.getInstance().getReference().child("notifications").child(selectedContact)
                        .child(key).setValue(new sendNotification(key, "CIRCLE INVITATION", finalCircleID, myPhoneNumber, selectedContact,
                        "1", "0"));

                FirebaseDatabase.getInstance().getReference().child("sentnotifications").child(myPhoneNumber)
                        .child(key).setValue(new sendNotification(key, "CIRCLE INVITATION", finalCircleID, myPhoneNumber, selectedContact,
                        "1", "0"));

                Toast.makeText(selectLeaperContact.this, "Invitation sent", Toast.LENGTH_SHORT).show();

                finish();

            }
        });



        listView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
    }







    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }


}







