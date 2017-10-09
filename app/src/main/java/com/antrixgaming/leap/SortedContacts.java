package com.antrixgaming.leap;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.antrixgaming.leap.BaseActivity;
import com.antrixgaming.leap.LeapClasses.ContactPermissionStartService;
import com.antrixgaming.leap.LeapClasses.LeapUtilities;
import com.antrixgaming.leap.LeapServices.ContactService;
import com.antrixgaming.leap.LeapServices.LeapService;
import com.antrixgaming.leap.Models.getPhoneContacts;
import com.antrixgaming.leap.Models.savePhoneContacts;
import com.antrixgaming.leap.Models.sendNotification;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class SortedContacts extends BaseActivity {

    String countryCode;

    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    ArrayList<getPhoneContacts> arrayOfContacts;
    ContactsAdapter adapter1;

    String myUID;
    String myPhoneNumber;
    DatabaseReference dbRef;
    String selectedContact = null;

    LeapUtilities leapUtilities;
    StorageReference mStorage;
    StorageReference mLeaperStorageRef;
    List<String> selectedNumbers;
    List<String> doubleChecker;

    ContactPermissionStartService contactPermissionStartService;

    ListView listOfContacts;

    TextView mPhoneContactName;
    TextView mPhoneContactType;
    TextView mContactPhoneNumber;

    DatabaseReference rootdbRef;

    SharedPreferences sharedPreferences;

    ProgressDialog progressDialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_leaper_contact);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);






        myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        myPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

        rootdbRef = FirebaseDatabase.getInstance().getReference();
        dbRef = FirebaseDatabase.getInstance().getReference().child("ContactList").child(myUID);

        mStorage = FirebaseStorage.getInstance().getReference();
        leapUtilities = new LeapUtilities();

        selectedNumbers = new ArrayList<>();
        doubleChecker = new ArrayList<>();

        progressDialog = new ProgressDialog(this);

        //contactPermissionStartService = new ContactPermissionStartService();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && this != null) {
            //contactPermissionStartService.ContactPermissionStartService(this);

        } else {
            //contactPermissionStartService.ContactPermissionStartService(this);
        }



        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        countryCode = sharedPreferences.getString("countryCode", null);


        sortCotact();





        final FloatingActionButton addLeaperToGroupFAB = (FloatingActionButton) findViewById(R.id.addLeaperToGroupFAB);
        addLeaperToGroupFAB.setVisibility(View.GONE);
        final RelativeLayout selectLeaperListLayout = (RelativeLayout) findViewById(R.id.selectLeaperListLayout);
        final TextView selectedLeapers = (TextView) findViewById(R.id.selectedLeapers);






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
                        Intent newChatIntent = new Intent(SortedContacts.this, activity_one_chat.class);
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
                            phoneContactToSelect.setBackgroundColor(ActivityCompat.getColor(SortedContacts.this, R.color.white));

                        }else {
                            selectedNumbers.add(mContactPhoneNumber.getText().toString());
                            selectIndicator.setVisibility(View.VISIBLE);
                            phoneContactToSelect.setBackgroundColor(ActivityCompat.getColor(SortedContacts.this, R.color.selectorcolor));

                        }


                        selectedLeapers.setText(selectedNumbers.toString());
                        selectedContact = returnContact.getText().toString();

                        CircleImageView selectedCircleImage = new CircleImageView(SortedContacts.this);
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

                    Toast.makeText(SortedContacts.this, "Invitation sent", Toast.LENGTH_SHORT).show();

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







    private void sortCotact(){




        progressDialog.setMessage("Loading contacts...");
        progressDialog.show();



        FirebaseDatabase.getInstance().getReference().child("uid").child(myUID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        countryCode = dataSnapshot.child("countrycode").getValue().toString();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });





        // Construct the data source
        arrayOfContacts = new ArrayList<getPhoneContacts>();
        adapter1 = new ContactsAdapter(this, arrayOfContacts);


        // Create the adapter to convert the array to views

        ContentResolver cr = getContentResolver();


        // Read Contacts
        Cursor c = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.Contacts._ID,
                        ContactsContract.Contacts.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.RawContacts.ACCOUNT_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE},
                ContactsContract.RawContacts.ACCOUNT_TYPE + " <> 'google' ", null, null);
        if(c.getCount()<=0)

        {

        } else

        {
            while (c.moveToNext()) {


                String Phone_number = c
                        .getString(c
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)); //Phone number
                final String name = c
                        .getString(c
                                .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)); //Name of contact
                final String contactType = c
                        .getString(c
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));


                // SAVE THE RAW STATE OF THE PHONE NUMBER
                final String rawPhoneNumber =  Phone_number;

                /////////////////////////////////////////////////////////////////////////////////////////
                /////////////////////////////////////////////////////////////////////////////////////////
                /////////////////////////////////////////////////////////////////////////////////////////
                /////////////////////////////////////////////////////////////////////////////////////////
                ///////////////////// FORMATTING PHONE NUMBER BEGINS HERE ///////////////////////////////

                /// STRINGS FOR CHECKING
                String noPlus = null;
                String noZeroZero = null;
                String goodLenth = null;





                // CHECK IF NUMBER BEGINS WITH "+"
                String plusCheck = Phone_number.substring(0, 1);
                if (Objects.equals(plusCheck, "+")){
                    //PROCEED TO SAVE AS IT IS
                    Phone_number = Phone_number.replaceAll("[^0-9]", "");
                    Phone_number = "+" + Phone_number;
                } else {
                    // used to proceed to next check
                    noPlus = Phone_number;
                }


                if (noPlus == null){

                }else {
                    // remove symbols and non digit variables from number
                    noPlus = noPlus.replaceAll("[^0-9]", "");


                    // CHECK IF NUMBER BEGINS WITH 00 and followed by another number
                    String twoZerosCheck = noPlus.substring(0, 2);
                    if (Objects.equals(twoZerosCheck, "00")){
                        String ZeroPhoneNumber = noPlus.substring(2);
                        String thirdZero = ZeroPhoneNumber.substring(0,1);
                        if (Objects.equals(thirdZero, "0")){
                            // used to proceed to next check
                            noZeroZero = Phone_number;
                        } else{
                            noPlus = noPlus.substring(2);
                            noPlus = "+" + noPlus;
                            Phone_number = noPlus;
                        }

                    }else {
                        // used to proceed to next check
                        noZeroZero = noPlus;
                    }
                }









                // MAKE EXCEPTIONS FOR SAINT HALENA & NIUE
                //if ((Objects.equals(countryCode, "+290")) || (Objects.equals(countryCode, "+683"))){

                // PHONE NUMBER LENGTH 4
                //PUT PROCEED HERE

                //}

                // MAKE EXCEPTION FOR AUSTRIA
                //if (Objects.equals(countryCode, "+43")){

                // PHONE NUMBER LENGTH 13
                // PUT PROCEED HERE



                //}


                if (noZeroZero == null){

                } else {
                    int numberLength = noZeroZero.length();

                    // ELIMINATE TOO SHORT AND TOO LONG NUMBERS
                    if (numberLength < 8 || numberLength > 15){
                        // do nothing. number too short or too long
                        Phone_number = null;
                    } else {
                        // CHECK FOR COUNTRY CODE AVAILABILITY
                        String firstDigit = noZeroZero.substring(0, 1);
                        String secondDigit = noZeroZero.substring(0, 2);
                        String thirdDigit = noZeroZero.substring(0, 3);
                        if ((Objects.equals(firstDigit, countryCode)) || (Objects.equals(secondDigit, countryCode)) || (Objects.equals(thirdDigit, countryCode))) {
                            Phone_number = "+" + Phone_number;
                        } else // check for locally saved number without zero
                            if (numberLength > 10){
                                Phone_number = "+" + noZeroZero;
                            }else if ((Objects.equals(firstDigit, "0")) && (!Objects.equals(secondDigit, "00"))) {
                                noZeroZero = noZeroZero.substring(1);
                                Phone_number = countryCode + noZeroZero;
                            } else {
                                Phone_number = null;
                            }
                    }

                }


                ///////////////////// FORMATTING PHONE NUMBER BEGINS HERE ///////////////////////////////
                /////////////////////////////////////////////////////////////////////////////////////////
                /////////////////////////////////////////////////////////////////////////////////////////
                /////////////////////////////////////////////////////////////////////////////////////////
                /////////////////////////////////////////////////////////////////////////////////////////



                ///////////////////////// SAVING THE PHONE NUMBER TO DATABASE ////////////////////////////
                ///////////////////////// SAVING THE PHONE NUMBER TO DATABASE ////////////////////////////
                ///////////////////////// SAVING THE PHONE NUMBER TO DATABASE ////////////////////////////

                if (Phone_number == null){

                } else if(Objects.equals(Phone_number, myPhoneNumber)){


                } else {


                    if (doubleChecker.contains(Phone_number)){


                    }else {
                        getPhoneContacts newContact = new getPhoneContacts(name, Phone_number, contactType);
                        doubleChecker.add(Phone_number);
                        //arrayOfContacts.add(newContact);



                        Collections.sort(arrayOfContacts, new Comparator<getPhoneContacts>(){
                            public int compare(getPhoneContacts obj1, getPhoneContacts obj2) {
                                // ## Ascending order




                                return obj1.mContactName.compareToIgnoreCase(obj2.mContactName); // To compare string values
                                // return Integer.valueOf(obj1.empId).compareTo(obj2.empId); // To compare integer values

                                // ## Descending order
                                // return obj2.firstName.compareToIgnoreCase(obj1.firstName); // To compare string values
                                // return Integer.valueOf(obj2.empId).compareTo(obj1.empId); // To compare integer values
                            }
                        });

                        adapter1.add(newContact);



                    }





                }
                ///////////////////////// SAVING THE PHONE NUMBER TO DATABASE ////////////////////////////
                ///////////////////////// SAVING THE PHONE NUMBER TO DATABASE ////////////////////////////
                ///////////////////////// SAVING THE PHONE NUMBER TO DATABASE ////////////////////////////


            }

            c.close();

            //dbRef.setValue(arrayOfContacts);

        }

        listOfContacts = (ListView) findViewById(R.id.leaperContactList);
        listOfContacts.setAdapter(adapter1);
        progressDialog.dismiss();

        dbRef.child("phoneSortedContacts").setValue(arrayOfContacts);



    }












    public static class ContactsAdapter extends ArrayAdapter<getPhoneContacts> {
        public ContactsAdapter(Context context, ArrayList<getPhoneContacts> contacts) {
            super(context, 0, contacts);
        }



        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position

            final getPhoneContacts contacts = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.phone_contact_list, parent, false);
            }
            // Lookup view for data population
            TextView mPhoneContactName = (TextView) convertView.findViewById(R.id.phoneContactName);
            TextView mPhoneContactType = (TextView) convertView.findViewById(R.id.phoneContactType);
            TextView mContactPhoneNumber = (TextView) convertView.findViewById(R.id.phoneContactStatus);
            // Populate the data into the template view using the data object
            mPhoneContactName.setText(contacts.mContactName);
            mContactPhoneNumber.setText(contacts.mContactPhoneNumber);

            //get the contact TYPE digit and assign manually
            int id = Integer.parseInt(contacts.mPhoneContactType);
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

            //phoneContactType.setText(contacts.mPhoneContactType);
            // Return the completed view to render on screen



            return convertView;
        }


    }






    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }


}







