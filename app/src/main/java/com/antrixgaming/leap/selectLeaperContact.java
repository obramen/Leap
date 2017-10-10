package com.antrixgaming.leap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.antrixgaming.leap.LeapClasses.ContactPermissionStartService;
import com.antrixgaming.leap.LeapClasses.LeapUtilities;
import com.antrixgaming.leap.LeapServices.ContactService;
import com.antrixgaming.leap.LeapServices.LeapService;
import com.antrixgaming.leap.Models.getPhoneContacts;
import com.antrixgaming.leap.Models.savePhoneContacts;
import com.antrixgaming.leap.Models.sendNotification;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class selectLeaperContact extends BaseActivity {

    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    String myUID;
    String myPhoneNumber;
    DatabaseReference dbRef;
    String selectedContact = null;

    LeapUtilities leapUtilities;
    StorageReference mStorage;
    StorageReference mLeaperStorageRef;
    List<String> selectedNumbers;

    ContactPermissionStartService contactPermissionStartService;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_leaper_contact);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);






        myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        myPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        dbRef = FirebaseDatabase.getInstance().getReference().child("ContactList").child(myUID).child("leapSortedContacts");

        mStorage = FirebaseStorage.getInstance().getReference();
        leapUtilities = new LeapUtilities();

        selectedNumbers = new ArrayList<>();

        contactPermissionStartService = new ContactPermissionStartService();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && this != null) {
           contactPermissionStartService.ContactPermissionStartService(this);

        } else {
            contactPermissionStartService.ContactPermissionStartService(this);
        }


        final FloatingActionButton addLeaperToGroupFAB = (FloatingActionButton) findViewById(R.id.addLeaperToGroupFAB);
        addLeaperToGroupFAB.setVisibility(View.GONE);
        final RelativeLayout selectLeaperListLayout = (RelativeLayout) findViewById(R.id.selectLeaperListLayout);
        final TextView selectedLeapers = (TextView) findViewById(R.id.selectedLeapers);

        ListView listOfContacts = (ListView) findViewById(R.id.leaperContactList);

        FirebaseListAdapter<savePhoneContacts> adapter;

        adapter = new FirebaseListAdapter<savePhoneContacts>(this, savePhoneContacts.class,
                R.layout.phone_contact_list, dbRef.orderByChild("name")) {  //dbRef.orderByChild("name")
            @Override
            protected void populateView(View v, savePhoneContacts model, int position) {


                TextView mPhoneContactName = (TextView) v.findViewById(R.id.phoneContactName);
                TextView mPhoneContactType = (TextView) v.findViewById(R.id.phoneContactType);
                TextView mContactPhoneNumber = (TextView) v.findViewById(R.id.phoneContactStatus);
                // Populate the data into the template view using the data object
                mPhoneContactName.setText(model.getcontactName());
                mContactPhoneNumber.setText(model.getcontactPhoneNumber());
                CircleImageView contact_list_image = (CircleImageView)v.findViewById(R.id.contact_list_image);



                mLeaperStorageRef = mStorage.child("leaperProfileImage").child(model.getcontactPhoneNumber()).child(model.getcontactPhoneNumber());
                leapUtilities.CircleImageFromFirebase(selectLeaperContact.this, mLeaperStorageRef, contact_list_image);




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



                        TextView mContactPhoneNumber = (TextView) view.findViewById(R.id.phoneContactStatus);
                        CircleImageView selectIndicator = (CircleImageView) view.findViewById(R.id.selectIndicator);
                        LinearLayout phoneContactToSelect = (LinearLayout) view.findViewById(R.id.phoneContactToSelect);

                        addLeaperToGroupFAB.setVisibility(View.VISIBLE);
                        selectLeaperListLayout.setVisibility(View.VISIBLE);
                        listView.setSelector(android.R.color.darker_gray);


                        if (selectedNumbers.contains(mContactPhoneNumber.getText().toString())){

                            selectedNumbers.remove(mContactPhoneNumber.getText().toString());
                            selectIndicator.setVisibility(View.GONE);
                            phoneContactToSelect.setBackgroundColor(ActivityCompat.getColor(selectLeaperContact.this, R.color.white));

                        }else {
                            selectedNumbers.add(mContactPhoneNumber.getText().toString());
                            selectIndicator.setVisibility(View.VISIBLE);
                            phoneContactToSelect.setBackgroundColor(ActivityCompat.getColor(selectLeaperContact.this, R.color.selectorcolor));

                        }


                        selectedLeapers.setText(selectedNumbers.toString());
                        selectedContact = returnContact.getText().toString();

                        CircleImageView selectedCircleImage = new CircleImageView(selectLeaperContact.this);
                        selectedCircleImage.setImageResource(R.drawable.profile_picture);
                        selectedCircleImage.isShown();

                        if (selectedNumbers.size() > 0){
                            addLeaperToGroupFAB.setVisibility(View.VISIBLE);
                            selectLeaperListLayout.setVisibility(View.VISIBLE);
                            listView.setSelector(android.R.color.darker_gray);

                        }else{
                            addLeaperToGroupFAB.setVisibility(View.GONE);
                            selectLeaperListLayout.setVisibility(View.GONE);
                        }


                        break;
                }



            }


        });

        addLeaperToGroupFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 0 - notification status
                // 1 - type of notification



                for(int x = 0; x < selectedNumbers.size(); x++ ){
                    String key = FirebaseDatabase.getInstance().getReference().child("notifications").child(selectedNumbers.get(x))
                            .push().getKey();
                    FirebaseDatabase.getInstance().getReference().child("notifications").child(selectedNumbers.get(x))
                            .child(key).setValue(new sendNotification(key, "CIRCLE INVITATION", finalCircleID, myPhoneNumber, selectedNumbers.get(x),
                            "1", "0"));

                    FirebaseDatabase.getInstance().getReference().child("sentnotifications").child(myPhoneNumber)
                            .child(key).setValue(new sendNotification(key, "CIRCLE INVITATION", finalCircleID, myPhoneNumber, selectedNumbers.get(x),
                            "1", "0"));

                    Toast.makeText(selectLeaperContact.this, "Invitation sent", Toast.LENGTH_SHORT).show();

                    finish();
                }



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
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_phone_contacts, menu);


        ////////////////////////////////// SEARCH BUTTON START /////////////////////////////
        MenuItem searchItem = menu.findItem(R.id.action_contacts_search);
        SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(searchItem);

        ////////////////////////////////// SEARCH BUTTON END /////////////////////////////








        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_contacts_search) {
            return true;
        }
        else if (id == R.id.action_contacts_refresh) {
            //Intent intent = getIntent();
            //finish();
            //startActivity(intent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && this != null) {
                //getContactRetrievalPermission();
            } else {
                //Intent startContactService = new Intent(this, ContactService.class);
                //startService(startContactService);
            }
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







