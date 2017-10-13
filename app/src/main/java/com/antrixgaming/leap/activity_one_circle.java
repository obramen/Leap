package com.antrixgaming.leap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.antrixgaming.leap.Fragments.CircleFragments.circleLeaperListFragment;
import com.antrixgaming.leap.Fragments.CircleFragments.circleLiveLeapsFragment;
import com.antrixgaming.leap.Fragments.CircleFragments.circleOpenLeapsFragment;
import com.antrixgaming.leap.Models.circleMessage;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Objects;

public class activity_one_circle extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    String groupID;
    DrawerLayout drawer;
    TabLayout tabLayout;

    private int[] tabIcons = {
            R.drawable.ic_action_leapers,
            R.drawable.ic_circle,
            R.drawable.ic_action_live};
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    DatabaseReference dbRef;

    String groupCreator;

    String myUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_circle);
        Toolbar toolbar = (Toolbar) findViewById(R.id.circleListToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //useUpButton(true, toolbar);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();

        dbRef = FirebaseDatabase.getInstance().getReference();
        myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        groupCreator = "";




        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_leapers);

        Bundle bundle = getIntent().getExtras();
        //final String groupName = bundle.getString("groupName");
        final String circleID = bundle.getString("circleID");
        groupID = circleID;

        editor.putString("currentCircleID", groupID);
        editor.apply();


        final String myPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        // get the created date and user who created the group
        dbRef.child("groupcircles").child(circleID)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String groupCreatedBy = dataSnapshot.child("createdBy").getValue().toString();
                final String groupCreatedOn = dataSnapshot.child("createdOn").getValue().toString();
                final String groupName = dataSnapshot.child("groupName").getValue().toString();


                //getting creator phone number
                dbRef.child("uid").child(groupCreatedBy).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        groupCreator = dataSnapshot.child("phoneNumber").getValue().toString();



                        if (Objects.equals(groupCreator, myPhoneNumber)){

                            dbRef.child("userprofiles").child(myPhoneNumber).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.child("name").getValue() == null || Objects.equals(dataSnapshot.child("name").getValue().toString(), "")){

                                        /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////
                                        getSupportActionBar().setTitle(groupName);
                                        getSupportActionBar().setSubtitle("Created by " + groupCreator +", " + DateFormat.format("dd/MMM/yy", Long.parseLong(groupCreatedOn)));
                                        /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////

                                    } else {

                                        String myName = dataSnapshot.child("name").getValue().toString();

                                        /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////
                                        getSupportActionBar().setTitle(groupName);
                                        getSupportActionBar().setSubtitle("Created by " + myName +", " + DateFormat.format("dd/MMM/yy", Long.parseLong(groupCreatedOn)));
                                        /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////s


                                    }




                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });




                        } else{


                            //getting creator name
                            dbRef.child("ContactList").child(myUid).child("leapSortedContacts").child(groupCreator).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.child("name").getValue() == null || dataSnapshot.child("name").getValue() == ""){
                                        /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////
                                        getSupportActionBar().setTitle(groupName);
                                        getSupportActionBar().setSubtitle("Created by " + groupCreator +", " + DateFormat.format("dd-MMM-yy", Long.parseLong(groupCreatedOn)));

                                        /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////

                                    } else {


                                        String mName = dataSnapshot.child("name").getValue().toString();

                                        /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////
                                        getSupportActionBar().setTitle(groupName);
                                        getSupportActionBar().setSubtitle("Created by " + mName +", " + DateFormat.format("dd-MMM-yy", Long.parseLong(groupCreatedOn)));

                                        /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////


                                    }






                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }






                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });





            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




        NavigationView navigationView = (NavigationView) findViewById(R.id.circle_nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        View header = navigationView.getHeaderView(0);

        Button circleLeapListNewLeapButton = (Button) header.findViewById(R.id.circleLeapListNewLeapButton);








        final Switch leapStatusSwitch = (Switch) header.findViewById(R.id.leapStatusSwitch);

        leapStatusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    FirebaseDatabase.getInstance().getReference().child("groupcirclesettings").child(myPhoneNumber)
                            .child(circleID).child("leapStatus").setValue("1");
                    Toast.makeText(activity_one_circle.this, "Leap status changed", Toast.LENGTH_SHORT).show();
                }else{
                    FirebaseDatabase.getInstance().getReference().child("groupcirclesettings").child(myPhoneNumber)
                            .child(circleID).child("leapStatus").setValue("0");
                    Toast.makeText(activity_one_circle.this, "Leap status changed", Toast.LENGTH_SHORT).show();

                }
            }
        });







        //Setting Drawers
        drawer = (DrawerLayout) findViewById(R.id.circle_leaper_list_drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }

            @Override
            public void onDrawerOpened(View drawerView) {





                FirebaseDatabase.getInstance().getReference().child("groupcirclesettings").child(myPhoneNumber)
                        .child(circleID)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                int mleapStatusSwitch = Integer.parseInt(dataSnapshot.child("leapStatus").getValue().toString());

                                if (mleapStatusSwitch == 1)
                                    leapStatusSwitch.setChecked(true);
                                else
                                    leapStatusSwitch.setChecked(false);


                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });













                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();























        circleLeapListNewLeapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent startNewLeapIntent = new Intent(activity_one_circle.this, newLeap.class);
                startNewLeapIntent.putExtra("leapedPhoneNumber", myPhoneNumber);
                startNewLeapIntent.putExtra("SourceActivity", "2");  // to be used to identify that the extras came from here
                startNewLeapIntent.putExtra("circleID", circleID);
                startActivity(startNewLeapIntent);

            }
        });






        //TabLayout tabLayout;
        SectionsPagerAdapter mSectionsPagerAdapter;
        ViewPager mViewPager;


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.circleLeaperListContainer);




        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mSectionsPagerAdapter);

        //Tab layout with tabs
        tabLayout = (TabLayout) findViewById(R.id.circleLeaperListTabs);
        tabLayout.setupWithViewPager(mViewPager);
        setupTabIcons();



        //ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.app_name, R.string.app_name)









        //groupID.setText(circleID);




        // record user's last opening of the group
        // long lastGroupOpenTime = new Date().getTime();
        // FirebaseDatabase.getInstance().getReference().child("groupcircles").child(circleID).child("members").child(myUid).child("lastOpenTime").setValue(lastGroupOpenTime);


        /*
        //// CREATE AN ARRAY FOR THE LIST OF MEMBERS AND UPDATE IT ON DATA CHANGE
        final ArrayList<ArrayList<String >> memberList = new ArrayList<ArrayList<String>>();
        final ArrayList<String> loadedMember = new ArrayList<String>();

        FirebaseDatabase.getInstance().getReference().child("groupcircles").child(circleID).child("members")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            //get member
                            String memberUid = dataSnapshot.getKey();
                            loadedMember.add(memberUid);

                        }
                        memberList.add(loadedMember);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });



          */





        ////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////
        /////////////////////////// NEW MESSAGE START //////////////////////////

        //Send key functions start here
        FloatingActionButton sendFab =
                (FloatingActionButton) findViewById(R.id.send_chat_fab);

        sendFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText input = (EditText) findViewById(R.id.input);

                if (TextUtils.isEmpty(input.getText().toString().trim())) {
                    return;
                }

                // Read the input field and push a new instance
                // of CircleMessage to the Firebase database inside groupcirclemessages table

                //push new messages using Circle ID stored
                String key = FirebaseDatabase.getInstance().getReference().child("groupcirclemessages").child(circleID)
                        .push().getKey();
                FirebaseDatabase.getInstance().getReference().child("groupcirclemessages").child(circleID)
                        .child(key).setValue(new circleMessage(input.getText().toString().trim(), circleID,
                                myPhoneNumber, myUid, "0", "false"));
                FirebaseDatabase.getInstance().getReference().child("groupcircles").child(circleID)
                        .child("lastgroupmessage").setValue(new circleMessage(input.getText().toString().trim(), circleID,
                                myPhoneNumber, myUid, "0", "true"));
                FirebaseDatabase.getInstance().getReference().child("groupcirclemessages").child(circleID)
                        .child(key).child("members").setValue(0);
                FirebaseDatabase.getInstance().getReference().child("groupcirclelastmessages").child(circleID)
                        .setValue(new circleMessage(input.getText().toString().trim(), circleID,
                        myPhoneNumber, myUid, "0", "true"));


                // Clear the input
                input.setText("");
            }

        });


        ////////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////////
        /////////////////////////// NEW MESSAGE END ////////////////////////////


        /////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////
        /////////////////////////// READ DATA START /////////////////////

                ListView listOfMessages = (ListView) findViewById(R.id.list_of_group_messages);

                FirebaseListAdapter<circleMessage> adapter;
                adapter = new FirebaseListAdapter<circleMessage>(activity_one_circle.this, circleMessage.class,
                        R.layout.messages_for_group, FirebaseDatabase.getInstance().getReference().child("groupcirclemessages")
                        .child(circleID)) {
                    @Override
                    protected void populateView(View v, final circleMessage model, int position) {

                        // Get references to the views of message.xml
                        TextView messageText = (TextView) v.findViewById(R.id.group_message_text);
                        //TextView messageUser = (TextView)v.findViewById(R.id.group_message_user);
                        TextView phoneNumber = (TextView) v.findViewById(R.id.group_phoneNumber);
                        TextView messageTime = (TextView) v.findViewById(R.id.group_message_time);
                        TextView groupNotificationMessage = (TextView) v.findViewById(R.id.groupNotificationMessage);
                        LinearLayout groupTxtBracketTop = (LinearLayout) v.findViewById(R.id.groupTextBracketTop);
                        RelativeLayout groupTxtBracketBottom = (RelativeLayout) v.findViewById(R.id.groupTextBracketBottom);
                        final TextView displayedLeaperName = (TextView) v.findViewById(R.id.displayedLeaperName);


                        ///// populate unread message list
                        /*if(Objects.equals(model.getReadFlag(), "false")){
                            model.setReadFlag("true");

                        } */

                        if (model.getmessageType() == null) {

                        } else {
                            String messageType = model.getmessageType();
                            switch (messageType) {
                                case "0":
                                    groupNotificationMessage.setVisibility(v.GONE);
                                    groupTxtBracketTop.setVisibility(v.VISIBLE);
                                    groupTxtBracketBottom.setVisibility(v.VISIBLE);
                                    messageText.setText(model.getMessageText());
                                    break;
                                case "1":
                                    groupNotificationMessage.setVisibility(v.VISIBLE);
                                    groupTxtBracketTop.setVisibility(v.GONE);
                                    groupTxtBracketBottom.setVisibility(v.GONE);
                                    groupNotificationMessage.setText(model.getMessageText());
                                    break;
                            }

                        }

                        // Set their text
                        phoneNumber.setText(model.getPhoneNumber());
                        //messageUser.setText(model.getMessageUser());

                        // Format the date before showing it
                        if (DateUtils.isToday(model.getMessageTime())) {
                            messageTime.setText(DateFormat.format("HH:mm", model.getMessageTime()));

                        } else {
                            messageTime.setText(DateFormat.format("dd/MM/yyyy HH:mm", model.getMessageTime()));
                        }


                        /////////////////////////////////////////////////////////////////////
                        ///////////////////////////////////////////
                        //////// GETTING AND SETTING NAMES IN PLACE OF PHONE NUMBER

                        ///////////////////////////////////////
                        //////////////////   STARTING    ///////////////////////////////


                        //// CHECK MY CONTACT LIST IF THIS PERSON IS A CONTACT
                        dbRef.child("ContactList").child(myUID).child("leapSortedContacts").child(model.getPhoneNumber())
                                .addValueEventListener(new ValueEventListener() {////////
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.child("name").getValue() == null || dataSnapshot.child("name")
                                                .getValue() == "") {///// IF THEY ARE NOT A CONTACT OR THE VALUE IS EMPTY


                                            ///// CHECK THE USERS PROFILES TO SEE IF THEY HAVE AN ENTRY THERE
                                            dbRef.child("userprofiles").child(model.getPhoneNumber()).addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.child("name").getValue() == null || Objects.equals(dataSnapshot.child("name")
                                                            .getValue().toString(), "")) {///IF THEY DON'T HAVE AN ENTRY USE THEIR PHONE NUMBER

                                                        /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////
                                                        displayedLeaperName.setText(model.getPhoneNumber());
                                                        /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////


                                                    } else { //// IF THEY HAVE AN ENTRY USE THEIR ENTERED NAME

                                                        String myName = dataSnapshot.child("name").getValue().toString();

                                                        /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////
                                                        displayedLeaperName.setText("~ " + myName);
                                                        /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////s


                                                    }


                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });


                                        } else {/// IF THEY ARE A CONTACT USE THE SAVED NAME


                                            String mName = dataSnapshot.child("name").getValue().toString();

                                            /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////
                                            displayedLeaperName.setText(mName);
                                            /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////


                                        }


                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });


                        //////////////////   ENDING    ///////////////////////////////
                        ///////////////////////////////////////

                        //////// GETTING AND SETTING NAMES IN PLACE OF PHONE NUMBER
                        ///////////////////////////////////////////
                        /////////////////////////////////////////////////////////////////////


                    }

























                };

                listOfMessages.setAdapter(adapter);
            }








        /////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////
        /////////////////////////// READ DATA END ///////////////////////




    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group_chat, menu);

        ////////////////////////////////// SEARCH BUTTON START /////////////////////////////
        //MenuItem leaperListButton = menu.findItem(R.id.action_one_group_chat_memberlist);





        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if(item != null && id == R.id.action_one_group_chat_memberlist) {
            if (drawer.isDrawerOpen(Gravity.RIGHT)) {
                drawer.closeDrawer(Gravity.RIGHT);
            } else {
                drawer.openDrawer(Gravity.RIGHT);
            }
        }

        if (id == R.id.action_one_group_chat_Group_info) {

            Intent groupInfoIntent = new Intent(this, groupInfoActivity.class);
            groupInfoIntent.putExtra("circleID", groupID);
            startActivity(groupInfoIntent);
            return true;

        }

        return super.onOptionsItemSelected(item);




    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }


    class SectionsPagerAdapter extends FragmentPagerAdapter {




        public SectionsPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new circleLeaperListFragment();
                case 1:
                    return new circleOpenLeapsFragment();
                case 2:
                    return new circleLiveLeapsFragment();
                case 3:
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {

            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            //TextView quantity = new TextView(activity_one_circle.this);
            //quantity.setTextColor(getResources().getColor(R.color.white));
            //quantity.setBackground(getResources().getDrawable(R.drawable.redbutton));

            //quantity.setText("5");

            switch (position) {
                case 0:
                    return "";
                case 1:
                    return "";
                case 2:
                    return "";
                case 3:
                default:
                    return null;

            }
        }
    }

    private void setupTabIcons() {

        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }







    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }






    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.circle_leaper_list_drawer);
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else{
            super.onBackPressed();

        }

    }


    public void useUpButton(boolean value, Toolbar toolbar) {

        drawer = (DrawerLayout) findViewById(R.id.circle_leaper_list_drawer);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
                InputMethodManager inputMethodManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        ActionBar actionBar = getSupportActionBar();
        if (value) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            toggle.setDrawerIndicatorEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        } else {
            toggle.setDrawerIndicatorEnabled(true);
            toggle.setToolbarNavigationClickListener(null);
        }

    }





}
