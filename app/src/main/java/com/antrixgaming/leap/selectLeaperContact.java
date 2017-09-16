package com.antrixgaming.leap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class selectLeaperContact extends AppCompatActivity {


    private String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("phonecontacts").child(myUID);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_leaper_contact);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ListView listofContacts = (ListView) findViewById(R.id.leaperContactList);

        FirebaseListAdapter<savePhoneContacts> adapter;
        adapter = new FirebaseListAdapter<savePhoneContacts>(this, savePhoneContacts.class,
                R.layout.messages, dbRef) {
            @Override
            protected void populateView(View v, savePhoneContacts model, int position) {


                TextView mPhoneContactName = (TextView) findViewById(R.id.phoneContactName);
                TextView mPhoneContactType = (TextView) findViewById(R.id.phoneContactType);
                TextView mContactPhoneNumber = (TextView) findViewById(R.id.phoneContactStatus);
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

        listofContacts.setAdapter(adapter);
    }



    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    private class savePhoneContacts {

        private String contactName;
        private String contactPhoneNumber;
        private String contactType;



        public savePhoneContacts(String contactName, String contactPhoneNumber, String contactType) {
            this.contactName = contactName;
            this.contactPhoneNumber = contactPhoneNumber;
            this.contactType = contactType;

        }


        public savePhoneContacts(){

        }


        public String getcontactName() {
            return contactName;
        }
        public void setcontactName(String contactName) {
            this.contactName = contactName;
        }


        public String getcontactPhoneNumber() {
            return contactPhoneNumber;
        }
        public void setcontactPhoneNumber(String contactPhoneNumber) {this.contactPhoneNumber = contactPhoneNumber;}


        public String getcontactType() {
            return contactType;
        }
        public void setcontactType(String contactType) {
            this.contactType = contactType;
        }
    }

}







