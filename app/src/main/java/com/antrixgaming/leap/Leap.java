package com.antrixgaming.leap;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.antrixgaming.leap.Fragments.chatsFragment;
import com.antrixgaming.leap.Fragments.circlesFragment;
import com.antrixgaming.leap.Fragments.leapsFragment;

import com.antrixgaming.leap.Fragments.userProfileFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import java.util.Date;


public class Leap extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    public final String userPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber().toString();
    public final String UID = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
    private FirebaseAuth mAuth;

    private DatabaseReference leapDatabase;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    TabLayout tabLayout;
    private long messageTime;






    private int[] tabIcons = {
            R.drawable.ic_tab_profile};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leap);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
        messageTime = new Date().getTime();

        /////////////// ADD USER'S UID TO PHONE NUMBERS TABLE TOGETHER WITH UID /////////////////
        /////////// CHECK FOR AVAILABILITY ON TABLE FIRST ////////////////////////
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        // add phone number to table
        dbRef.child("phonenumbers").child(userPhoneNumber).child("uid").setValue(UID);
        // add last login time
        dbRef.child("phonenumbers").child(userPhoneNumber).child("logins").push().setValue(messageTime);
        // add phone number to table
        dbRef.child("uid").child(UID).child("phoneNumber").setValue(userPhoneNumber);



        Toast.makeText(this, "User Newly registered", Toast.LENGTH_LONG).show();
        Toast.makeText(this, "Last login saved", Toast.LENGTH_LONG).show();




        /////////////// ADDING ENDS HEREE /////////////////






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
                    return new chatsFragment();
                case 2:
                    return new circlesFragment();
                case 3:
                    return new leapsFragment();
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
                    return getString(R.string.Chats);
                case 2:
                    return getString(R.string.Circles);
                case 3:
                    return getString(R.string.Leaps);
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
        if (id == R.id.action_leap_settings) {
            return true;
        }
        else if (id == R.id.action_leap_search){

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_wallet) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            finish();
            Intent logoutIntent = new Intent(this, registerLogin.class);
            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(logoutIntent);

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
                            FirebaseAuth.getInstance().signOut();
                            finish();
                            Intent logoutIntent = new Intent(Leap.this, registerLogin.class);
                            logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(logoutIntent);




                        }
                    }

                });
    }
}


