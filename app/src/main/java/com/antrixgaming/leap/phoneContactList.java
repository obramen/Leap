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
import android.widget.ListView;

import android.widget.TextView;
import android.widget.Toast;

import com.antrixgaming.leap.NewClasses.getPhoneContacts;
import java.util.ArrayList;

public class phoneContactList extends AppCompatActivity {


    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    //private ArrayList<String> ContactList;



    public String mContactName;
    public String mContactPhoneNumber;
    public String mPhoneContactType;





    public class ContactsAdapter extends ArrayAdapter<getPhoneContacts> {
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
            TextView phoneContactType = (TextView) convertView.findViewById(R.id.phoneContactType);
            TextView mContactPhoneNumber = (TextView) convertView.findViewById(R.id.phoneContactStatus);
            // Populate the data into the template view using the data object
            mPhoneContactName.setText(contacts.mContactName);
            mContactPhoneNumber.setText(contacts.mContactPhoneNumber);


            //get the contact TYPE digit and assign manually
            int id = Integer.parseInt(contacts.mPhoneContactType.trim());
            if (id == 1) {
                phoneContactType.setText("HOME");
            } else if (id == 2) {
                phoneContactType.setText("MOBILE");
            } else if (id == 3) {
                phoneContactType.setText("WORK");
            } else if (id == 4) {
                phoneContactType.setText("");
            } else if (id == 5) {
                phoneContactType.setText("");
            } else if (id == 6) {
                phoneContactType.setText("PAGER");
            } else if (id == 7) {
                phoneContactType.setText("OTHER");
            } else if (id == 8) {
                phoneContactType.setText("");
            } else {
                phoneContactType.setText("");
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

        getContactRetrievalPermission();
        gettingPhoneContacts();

        ListView listView = (ListView) findViewById(R.id.phone_ContactList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                TextView newMessagePhoneNumber = (TextView)view.findViewById(R.id.phoneContactStatus);
                String oneCircleSecondUser = newMessagePhoneNumber.getText().toString();
                Intent intent = new Intent(phoneContactList.this, activity_one_chat.class);
                //String message = "abc";
                intent.putExtra("oneCircleSecondUser", oneCircleSecondUser);
                startActivity(intent);
            }
        });



    }

    //getting phone contacts from mobile phone
    private void gettingPhoneContacts() {

        String[] ContactList;


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
                String name = c
                        .getString(c
                                .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)); //Name of contact
                String contactType = c
                        .getString(c
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));




                // Add items to array
                getPhoneContacts newContact = new getPhoneContacts(name, Phone_number, contactType);

                //attach array to adapter
                adapter.add(newContact);

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
            Intent intent = getIntent();
            finish();
            startActivity(intent);
            Toast.makeText(phoneContactList.this, "Contacts Refreshed", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);




    }






    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }


}
