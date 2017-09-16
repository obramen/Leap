package com.antrixgaming.leap;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.provider.ContactsContract;
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

import android.widget.TextView;
import android.widget.Toast;

import com.antrixgaming.leap.LeapServices.LeapService;
import com.antrixgaming.leap.NewClasses.circleMessage;
import com.antrixgaming.leap.NewClasses.createGroupCircle;
import com.antrixgaming.leap.NewClasses.getPhoneContacts;
import com.antrixgaming.leap.NewClasses.phoneContactsImport;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class phoneContactList extends AppCompatActivity {


    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    //private ArrayList<String> ContactList;

    public DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
    public String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();


    public String mContactName;
    public String mContactPhoneNumber;
    public String mPhoneContactType;
    public String countryCode = "";



    ///// populate in list view

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
            int id = Integer.parseInt(contacts.mPhoneContactType.trim());
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_contact_list);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FirebaseDatabase.getInstance().getReference().child("uid").child(FirebaseAuth.getInstance()
                .getCurrentUser().getUid()).child("countrycode").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                countryCode = dataSnapshot.getValue().toString();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //getContactRetrievalPermission();
        //gettingPhoneContacts();

        ListView listView = (ListView) findViewById(R.id.phone_ContactList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                TextView newMessagePhoneNumber = (TextView)view.findViewById(R.id.phoneContactStatus);
                String oneCircleSecondUser = newMessagePhoneNumber.getText().toString();
                Intent intent = new Intent(phoneContactList.this, activity_one_chat.class);
                intent.putExtra("oneCircleSecondUser", oneCircleSecondUser);
                startActivity(intent);
                finish();
            }
        });


        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.circleLayout);

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LovelyTextInputDialog(phoneContactList.this, R.style.MyDialogTheme)
                        .setTopColorRes(R.color.colorPrimary)
                        .setTitle(R.string.new_leapers_circle)
                        .setMessage(R.string.new_circle_message)
                        .setIcon(R.drawable.ic_circle_add)
                        .setInputFilter("Enter circle name", new LovelyTextInputDialog.TextFilter() {
                            @Override
                            public boolean check(String text) {
                                //return text.matches("\\w{3,23}\\b");     //("\\w+");
                                return text.matches("^\\w+( +\\w+)*$");     //("\\w+");
                                //return text.matches("\\w+");
                            }

                        })
                        .setCancelable(false)
                        .setNegativeButton("Cancel", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                return;
                            }
                        })
                        .setConfirmButton("Create circle", new LovelyTextInputDialog.OnTextInputConfirmListener() {
                            @Override
                            public void onTextInputConfirmed(String text) {

                                // push and get the key for new group under "groupcircles" table
                                String key = FirebaseDatabase.getInstance().getReference().child("groupcircles").push().getKey();
                                // using the new key, update group info with group creator, group name and group key under same table
                                FirebaseDatabase.getInstance().getReference().child("groupcircles").child(key)
                                        .setValue(new createGroupCircle(FirebaseAuth.getInstance()
                                                .getCurrentUser().getUid(), text, key));
                                // using the new key update members list under same table with the creator of the group setting status to true
                                /// true = admin
                                /// false = not admin
                                FirebaseDatabase.getInstance().getReference().child("groupcircles").child(key).child("members")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue("true");
                                // update the usergroupcirclelist (a list containing all groups a leaper is part of


                                // This list is used to load the circles fragment
                                // First add the group id
                                FirebaseDatabase.getInstance().getReference().child("usergroupcirclelist")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(key).child("groupid").setValue(key);
                                // Next add the group name
                                FirebaseDatabase.getInstance().getReference().child("usergroupcirclelist")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(key).child("groupName").setValue(text);
                                // Lastly add the admin status
                                FirebaseDatabase.getInstance().getReference().child("usergroupcirclelist")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(key).child("admin").setValue("true");
                                FirebaseDatabase.getInstance().getReference().child("usergroupcirclelist")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(key).child("admin").setValue("true");
                                FirebaseDatabase.getInstance().getReference().child("groupcirclelastmessages").child(key)
                                        .setValue(new circleMessage("Welcome to your new circle, add your other leapers now", key,
                                                "Leap Bot", "LEAPBOT"));
                                Toast.makeText(phoneContactList.this, "Circle added", Toast.LENGTH_SHORT).show();


                            }
                        })
                        .show();
            }
        });



    }

    //getting phone contacts from mobile phone
    private void gettingPhoneContacts() {

        //String[] ContactList;


        // Construct the data source
        ArrayList<getPhoneContacts> arrayOfContacts = new ArrayList<getPhoneContacts>();

        // Create the adapter to convert the array to views

        ContactsAdapter adapter = new ContactsAdapter(this, arrayOfContacts);

        ContentResolver cr = getContentResolver();
        // Read Contacts
        Cursor c = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.Contacts._ID,
                        ContactsContract.Contacts.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.RawContacts.ACCOUNT_TYPE,ContactsContract.CommonDataKinds.Phone.TYPE},
                ContactsContract.RawContacts.ACCOUNT_TYPE + " <> 'google' ", null, null);
        if (c.getCount() <= 0) {
            Toast.makeText(this, "No Phone Contact Found..!",
                    Toast.LENGTH_SHORT).show();
        } else {
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



                // TODO put error checker here
                // remove symbols and non digit variables from number
                Phone_number = Phone_number.replaceAll("[^0-9]", "");

                // if number begins with "0", remove it (this shows it's number saved without the country code.
                // Eg. 02423666623 / 0540853936
                int firstDigit = Integer.parseInt(Phone_number.substring(0, 1));
                int secondDigit = Integer.parseInt(Phone_number.substring(0, 2));
                //int thirdDigit = Integer.parseInt(Phone_number.substring(0, 3));
                if ((firstDigit == 0)&&(secondDigit != 00)){
                    Phone_number = Phone_number.substring(1);

                } else if ((firstDigit == 0)&&(secondDigit == 00)) {
                    Phone_number = Phone_number.substring(1);
                    Phone_number = Phone_number.substring(1);

                } else{

                }




                if (Phone_number.length() < 8){
                    //skip short numbers that are possibly not phone numbers
                } else if (Phone_number.length() > 14){
                    // skip potentially too long numbers
                } else if ((Phone_number.length() == 8) || (Phone_number.length() == 9)){



                    Phone_number = countryCode + Phone_number;
                    // Add items to array
                    getPhoneContacts newContact = new getPhoneContacts(name, Phone_number, contactType);

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


                    if (arrayOfContacts.contains(newContact)){


                    }
                    else{
                        adapter.add(newContact);
                        //dbRef.child("contactlist").child(U
                        // ID).child(Phone_number).setValue(newContact);

                    }






                }else if((Phone_number.length() == 11) && (Integer.parseInt(Phone_number.substring(0, 1)) == 1)) {
                    Phone_number = "+" + Phone_number;

                    getPhoneContacts newContact = new getPhoneContacts(name, Phone_number, contactType);

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



                    if (arrayOfContacts.contains(newContact)){


                    }
                    else{
                        adapter.add(newContact);
                        //dbRef.child("contactlist").child(U
                        // ID).child(Phone_number).setValue(newContact);

                    }

                }else if((Phone_number.length() == 11)&& (Integer.parseInt(Phone_number.substring(0, 1)) == 1) &&
                        (Integer.parseInt(Phone_number.substring(0, 2)) == 10)){
                    Phone_number = Phone_number.substring(1);
                    Phone_number = Phone_number.substring(1);
                    Phone_number = "+1" + Phone_number;

                    getPhoneContacts newContact = new getPhoneContacts(name, Phone_number, contactType);

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

                    if (arrayOfContacts.contains(newContact)){


                    }
                    else{
                        adapter.add(newContact);
                        //dbRef.child("contactlist").child(U
                        // ID).child(Phone_number).setValue(newContact);

                    }                }

                else if((Phone_number.length() > 9) && (Phone_number.length() < 14)  && (Phone_number.length() != 11) ) {

                    int CCDGH = Integer.parseInt(Phone_number.substring(0, 3));
                    if (Objects.equals("+" + CCDGH, countryCode))
                    {
                        Phone_number = "+" + Phone_number;

                    } else {

                    }


                    getPhoneContacts newContact = new getPhoneContacts(name, Phone_number, contactType);

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



                    if (arrayOfContacts.contains(newContact)){


                    }
                    else{
                        adapter.add(newContact);
                        //dbRef.child("contactlist").child(U
                        // ID).child(Phone_number).setValue(newContact);

                    }
                    //dbRef.child("contactlist").child(UID).child(Phone_number).setValue(newContact);
                }



                //add phone number to list of contacts in contactlist table in database
                //dbRef.child("contactlist").child(UID).child(Phone_number).setValue("true");
                //dbRef.child("contactlist").child(UID).child(Phone_number).child("fullcontact").child("phonenumber").setValue(Phone_number);
                //dbRef.child("contactlist").child(UID).child(Phone_number).child("fullcontact").child("name").setValue(name);
                //dbRef.child("contactlist").child(UID).child(Phone_number).child("fullcontact").child("contacttype").setValue(contactType);
                //dbRef.child("contactlist").child(UID).child(Phone_number).child("fullcontact").child("leaper").setValue("true");


            }






        }
        c.close();

        ListView listView = (ListView) findViewById(R.id.phone_ContactList);
        //attach adapter to list view
        listView.setAdapter(adapter);



    }





    private void getContactRetrievalPermission(){

        int permissionCheck = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_CONTACTS);

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this, "permission not available, request", Toast.LENGTH_SHORT).show();


            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.READ_CONTACTS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                Toast.makeText(this, "permission request starting", Toast.LENGTH_SHORT).show();


                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        else {

            Toast.makeText(this, "contactt permission already available", Toast.LENGTH_SHORT).show();

            gettingPhoneContacts();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show();

                    gettingPhoneContacts();


                } else {

                    Toast.makeText(this, "permission denied", Toast.LENGTH_SHORT).show();


                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }




    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_phone_contacts, menu);


        ////////////////////////////////// SEARCH BUTTON START /////////////////////////////
        MenuItem searchItem = menu.findItem(R.id.action_contacts_search);
        SearchView searchView =
                (SearchView) MenuItemCompat.getActionView(searchItem);


        // Define the listener
        MenuItemCompat.OnActionExpandListener expandListener = new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Do something when action item collapses

                Toast.makeText(phoneContactList.this, "Collapsed", Toast.LENGTH_SHORT).show();

                return true;  // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Do something when expanded

                Toast.makeText(phoneContactList.this, "Expanded", Toast.LENGTH_SHORT).show();
                return true;  // Return true to expand action view
            }
        };

        // Get the MenuItem for the action item
        //MenuItem actionMenuItem = menu.findItem(R.id.action_contacts_search);

        // Assign the listener to that action item
        //MenuItemCompat.setOnActionExpandListener(actionMenuItem, expandListener);
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
            Intent contactServiceIntent = new Intent(this, LeapService.class);
            startService(contactServiceIntent);
            Toast.makeText(phoneContactList.this, "Refreshing contacts", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);




    }






    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }


}
