package com.antrixgaming.leap;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;

import com.antrixgaming.leap.Fragments.chatsFragment;
import com.antrixgaming.leap.Fragments.circlesFragment;
import com.antrixgaming.leap.Fragments.leapsFragment;

import com.antrixgaming.leap.Fragments.userProfileFragment;
import com.antrixgaming.leap.LeapServices.ContactService;
import com.antrixgaming.leap.LeapServices.LeapService;
import com.antrixgaming.leap.NewClasses.circleMessage;
import com.antrixgaming.leap.NewClasses.createGroupCircle;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;


import java.util.Date;


public class Leap extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    //TODO CHANGE ALL FIREBASE INSTANCES TO dbRef

    public final String userPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
    public final String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private FirebaseAuth mAuth;

    private DatabaseReference leapDatabase;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    TabLayout tabLayout;
    private long loginTime;


    private int[] tabIcons = {
            R.drawable.ic_tab_profile};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leap);

        Intent startContactService = new Intent(this, ContactService.class);
        //startService(startContactService);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
        loginTime = new Date().getTime();

        // get bundled country code from registerLogin activity
        Bundle bundle = getIntent().getExtras();
        String countryCode = bundle.getString("countryCode");
        String countryCodeStatus = bundle.getString("countryCodeStatus");


        /////////////// ADD USER'S UID TO PHONE NUMBERS TABLE TOGETHER WITH UID /////////////////
        /////////// CHECK FOR AVAILABILITY ON TABLE FIRST ////////////////////////
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        // add phone number to phone numbers table with section named as user's phone number
        dbRef.child("phonenumbers").child(userPhoneNumber).child("uid").setValue(UID);
        // add last login time under phone numbers table with section logins
        dbRef.child("phonenumbers").child(userPhoneNumber).child("logins").push().setValue(loginTime);
        // add phone number under uid table
        dbRef.child("uid").child(UID).child("phoneNumber").setValue(userPhoneNumber);
        // add country code under uid table
        dbRef.child("users").child(userPhoneNumber).setValue(userPhoneNumber);

        int x = Integer.parseInt(countryCodeStatus);
        if (x == 1) {

            dbRef.child("uid").child(UID).child("countrycode").setValue("+" + countryCode);

        } else {

        }
        /////////////// ADDING ENDS HERE /////////////////


        //initialize database reference
        leapDatabase = FirebaseDatabase.getInstance().getReference();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.leapsContainer);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        //Setting Drawers
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Tab layout with tabs
        tabLayout = (TabLayout) findViewById(R.id.mainTabs);
        tabLayout.setupWithViewPager(mViewPager);
        setupTabIcons();


        //Set Username text in top nav
        TextView txtProfileName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.profileFullName);
        txtProfileName.setText(userPhoneNumber);



        Button leapOutButton = (Button) findViewById(R.id.leapOutButton);
        leapOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                finish();
                Intent logoutIntent = new Intent(Leap.this, registerLogin.class);
                logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(logoutIntent);
            }
        });

    }



    // Pager adapter initialize and import of fragments
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new userProfileFragment();
                case 1:
                    return new leapsFragment();
                case 2:
                    return new chatsFragment();
                case 3:
                    return new circlesFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "";
                case 1:
                    return getString(R.string.Leaps);
                case 2:
                    return getString(R.string.Chats);
                case 3:
                    return getString(R.string.Circles);
                default:
                    return null;

            }
        }
    }

    private void setupTabIcons() {

        tabLayout.getTabAt(0)
                .setIcon(tabIcons[0]);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.leap, menu);

        return true;
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.nav_notifications) {

            Intent openNotificationsIntent = new Intent(this, receivedNotifications.class);
            startActivity(openNotificationsIntent);

            return true;
        }
        else if (id == R.id.action_new_circle){

               new LovelyTextInputDialog(this, R.style.MyDialogTheme)
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
                                            "Leap Bot", "LEAPBOT", "0"));
                            Toast.makeText(Leap.this, "Circle added", Toast.LENGTH_SHORT).show();


                        }
                    })
                    .show();


            return true;

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_notifications) {

            Intent openNotificationsIntent = new Intent(this, receivedNotifications.class);
            startActivity(openNotificationsIntent);

        } else if (id == R.id.nav_cancelled_leaps) {

        }  else if (id == R.id.nav_pending_leaps) {

        } else if (id == R.id.nav_declined_leaps) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;



    }




    //minimize leap when back is pressed
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }  else if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
        {
            this.moveTaskToBack(true);
            return true;
        }

        else {
            super.onBackPressed();
        }


        return super.onKeyDown(keyCode, event);




    }




    @Override
    protected void onResume() {

        FirebaseAuth mAuth;

        //Recheck for user availability in database
        super.onResume();
        mAuth = FirebaseAuth.getInstance();
        mAuth.getCurrentUser().getToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            //String idToken = task.getResult().getToken();
                            // Send token to your backend via HTTPS
                            // ...


                            Toast.makeText(Leap.this, "This works, already signed in", Toast.LENGTH_LONG).show();


                        } else {
                            // Handle error -> task.getException();
                            //FirebaseAuth.getInstance().signOut();
                            //finish();
                            //Intent logoutIntent = new Intent(Leap.this, registerLogin.class);
                            //logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            //startActivity(logoutIntent);




                        }
                    }

                });
    }
}


