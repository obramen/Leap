package com.antrixgaming.leap.LeapServices;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class ContactService extends IntentService {


    public ContactService() {
        super("CONTACT_SYNC_SERVICE");
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Toast.makeText(this, "Contacts Sync Service Starting", Toast.LENGTH_LONG).show();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        Toast.makeText(this, "Contacts Sync Service Stopped", Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {



        synchronized (this) {


            String myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("phonecontacts").child(myUID);


                // Construct the data source
                ArrayList<getPhoneContacts> arrayOfContacts = new ArrayList<getPhoneContacts>();

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


                        // TODO put error checker here
                        // remove symbols and non digit variables from number
                        Phone_number = Phone_number.replaceAll("[^0-9]", "");

                        getPhoneContacts newContact = new getPhoneContacts(name, Phone_number, contactType);
                        arrayOfContacts.add(newContact);
                        dbRef.child(Phone_number).setValue(newContact);

                    }

                c.close();

                //dbRef.setValue(arrayOfContacts);

            }


        }



    }

    class getPhoneContacts {

        public String mContactName;
        public String mContactPhoneNumber;
        public String mPhoneContactType;

        public getPhoneContacts(String mContactName, String mContactPhoneNumber, String mPhoneContactType) {

            this.mContactName = mContactName;
            this.mContactPhoneNumber = mContactPhoneNumber;
            this.mPhoneContactType = mPhoneContactType;

        }


    }

}
