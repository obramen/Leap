package com.antrixgaming.leap;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.util.TimeUtils;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.Button;

import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.antrixgaming.leap.Fragments.LeapFragments.chatsFragment;
import com.antrixgaming.leap.Fragments.LeapFragments.circlesFragment;
import com.antrixgaming.leap.Fragments.LeapFragments.leapsFragment;

import com.antrixgaming.leap.Fragments.LeapFragments.userProfileFragment;
import com.antrixgaming.leap.LeapClasses.ConfirmDialog;
import com.antrixgaming.leap.LeapClasses.ContactPermissionStartService;
import com.antrixgaming.leap.LeapClasses.LeapUtilities;
import com.antrixgaming.leap.LeapClasses.GetFirebaseInfo;
import com.antrixgaming.leap.Models.CircleMember;
import com.antrixgaming.leap.Models.circleMessage;
import com.antrixgaming.leap.Models.createGroupCircle;
import com.bumptech.glide.util.Util;
import com.futuremind.recyclerviewfastscroll.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;


import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import br.com.goncalves.pugnotification.notification.PugNotification;
import de.hdodenhof.circleimageview.CircleImageView;


public class Leap extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener{



    String countryCode;

    TextView displayedLeaperName;

    FirebaseAuth.AuthStateListener mAuthListener;

    GetFirebaseInfo getFirebaseInfo;







    // NOTIFICATIONS
    long count = 0;
    //MenuItem menuItem;
    int loadFlag = 0;
    int pushNFlag = 0;

    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;
    private int notification_id;
    private RemoteViews remoteViews;
    private Context context;






    //TODO CHANGE ALL FIREBASE INSTANCES TO dbRef

    String userPhoneNumber;
    String UID;
    private FirebaseAuth mAuth;
    String myPhoneNumber;

    StorageReference mStorage;
    StorageReference mLeaperStorageRef;
    String myUID;

    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;



    private DatabaseReference leapDatabase;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    TabLayout tabLayout;
    private long loginTime;

    CircleImageView profileImage;

    public String deviceOnlinekey = null;


    private int[] tabIcons = {
            R.drawable.ic_tab_profile};

    LeapUtilities leapUtilities;

    SharedPreferences sharedPreferences;

    ContactPermissionStartService contactPermissionStartService;

    TextView BugReport;
    TextView AboutLeap;
    TextView inviteFriends;

    ConfirmDialog confirmDialog;

    //TextView notificationTextView;
    //TextView notificationTextView2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leap);

        //contactPermissionStartService = new ContactPermissionStartService();
        //contactPermissionStartService.ContactPermissionStartService(Leap.this);

        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        //qFirebaseDatabase.getInstance().getReference().keepSynced(true);



        leapUtilities = new LeapUtilities();
        confirmDialog = new ConfirmDialog();
        getFirebaseInfo = new GetFirebaseInfo();

        context = this;

        userPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        myPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();


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
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
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

            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("countryCode", "+" + countryCode);
            editor.apply();


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
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        //Setting Drawers
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);




        displayedLeaperName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.displayedLeaperName);




        final FloatingActionButton leapFAB = (FloatingActionButton)findViewById(R.id.leapFAB);
        final CircleImageView profileImageFAB = (CircleImageView) findViewById(R.id.profile_image);


        final View header = navigationView.getHeaderView(0);
        profileImage = (CircleImageView)header.findViewById(R.id.profile_image);

        mStorage = FirebaseStorage.getInstance().getReference();
        mLeaperStorageRef = mStorage.child("leaperProfileImage").child(myPhoneNumber).child(myPhoneNumber);

        //leapUtilities.CircleImageFromFirebase(this, mLeaperStorageRef, profileImage);
        //leapUtilities.CircleImageFromFirebase(this, mLeaperStorageRef, profileImageFAB);





        FirebaseDatabase.getInstance().getReference().child("profileImageTimestamp").child(myPhoneNumber)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChildren()){

                            String timestamp = dataSnapshot.child(myPhoneNumber).getValue().toString();

                            mStorage = FirebaseStorage.getInstance().getReference();
                            mLeaperStorageRef = mStorage.child("leaperProfileImage").child(myPhoneNumber).child(myPhoneNumber);

                            leapUtilities.CircleImageFromFirebase(Leap.this, mLeaperStorageRef, profileImage, timestamp);
                            leapUtilities.CircleImageFromFirebase(Leap.this, mLeaperStorageRef, profileImageFAB, timestamp);


                        } else {
                            FirebaseDatabase.getInstance().getReference().child("profileImageTimestamp").child(myPhoneNumber)
                                    .child(myPhoneNumber).setValue(new Date().getTime());
                        }



                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });










        //Tab layout with tabs
        tabLayout = (TabLayout) findViewById(R.id.mainTabs);
        tabLayout.setupWithViewPager(mViewPager);
        setupTabIcons();





        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(Gravity.START);
                Intent intent = new Intent(Leap.this, leaperProfileActivity.class);
                intent.putExtra("leaperPhoneNumber", myPhoneNumber);
                startActivity(intent);
            }
        });


        profileImageFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Leap.this, leaperProfileActivity.class);
                intent.putExtra("leaperPhoneNumber", myPhoneNumber);
                startActivity(intent);
            }


        });















        final TextView notificationTextView = (TextView) findViewById(R.id.notificationTextView);
        final TextView notificationTextView2 = (TextView) findViewById(R.id.notificationTextView2);









        leapFAB.setVisibility(View.INVISIBLE);
        profileImageFAB.setVisibility(View.VISIBLE);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {


                switch (tab.getPosition()){
                    case 0: // PROFILE FRAGMENT
                        leapFAB.setVisibility(View.INVISIBLE);
                        profileImageFAB.setVisibility(View.VISIBLE);
                        break;
                    case 1: // LEAPS FRAGMENT
                        profileImageFAB.setVisibility(View.INVISIBLE);
                        leapFAB.setVisibility(View.VISIBLE);
                        leapFAB.setImageResource(android.R.drawable.ic_input_add);
                        break;
                    case 2: // CHATS FRAGMENT
                        profileImageFAB.setVisibility(View.INVISIBLE);
                        leapFAB.setImageResource(android.R.drawable.stat_notify_chat);
                        leapFAB.setVisibility(View.VISIBLE);



                        break;
                    case 3: // CIRCLES FRAGMENT
                        profileImageFAB.setVisibility(View.INVISIBLE);
                        leapFAB.setImageResource(R.drawable.ic_circle_add);
                        leapFAB.setVisibility(View.VISIBLE);
                        break;
                    default:
                        profileImageFAB.setVisibility(View.INVISIBLE);
                        leapFAB.setImageResource(android.R.drawable.ic_input_add);
                        leapFAB.setVisibility(View.INVISIBLE);
                        break;
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                leapFAB.setVisibility(View.INVISIBLE);
                profileImageFAB.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0: // PROFILE FRAGMENT
                        profileImageFAB.setVisibility(View.VISIBLE);
                        leapFAB.setVisibility(View.INVISIBLE);
                        break;
                    case 1: // LEAPS FRAGMENT
                        profileImageFAB.setVisibility(View.INVISIBLE);
                        leapFAB.setVisibility(View.VISIBLE);
                        leapFAB.setImageResource(android.R.drawable.ic_input_add);
                        break;
                    case 2: // CHATS FRAGMENT
                        profileImageFAB.setVisibility(View.INVISIBLE);
                        leapFAB.setImageResource(android.R.drawable.stat_notify_chat);
                        leapFAB.setVisibility(View.VISIBLE);
                        break;
                    case 3: // CIRCLES FRAGMENT
                        profileImageFAB.setVisibility(View.INVISIBLE);
                        leapFAB.setImageResource(R.drawable.ic_circle_add);
                        leapFAB.setVisibility(View.VISIBLE);
                        break;
                    default:
                        break;
                }

            }
        });







        leapFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {


                //TabLayout.Tab tab = tabLayout.getTabAt(int position);

                int tabPosition = tabLayout.getSelectedTabPosition();

                switch (tabPosition){


                    case 0: // PROFILE FRAGMENT
                        // nothing here yet
                        break;


                    case 1: // LEAPS FRAGMENT
                        Intent startNewLeapIntent = new Intent(Leap.this, newLeap.class);
                        startActivity(startNewLeapIntent);
                        break;



                    case 2:
                        //Intent openOneChat = new Intent(getActivity(), phoneContactList.class);
                        Intent openOneChat = new Intent(Leap.this, selectLeaperContact.class);
                        //Intent openOneChat = new Intent(Leap.this, SortedContacts.class);
                        openOneChat.putExtra("SourceActivity", "1");
                        startActivity(openOneChat);
                        break;



                    case 3: // CIRCLES FRAGMENT
                        new LovelyTextInputDialog(Leap.this, R.style.MyDialogTheme)
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
                                        dbRef.child("groupcircles").child(key)
                                                .setValue(new createGroupCircle(FirebaseAuth.getInstance()
                                                        .getCurrentUser().getUid(), text, key));

                                        // Save group UID and name in different list
                                        dbRef.child("groupcirclenames").child(key)
                                                .child("circleID").setValue(key);
                                        dbRef.child("groupcirclenames").child(key)
                                                .child("circleName").setValue(text);




                                        // using the new key create members list under "circlemembers" table with the creator of the group setting status to true
                                        /// true = admin
                                        /// false = not admin
                                        /// 1 - shows it's a notification message // 0 - normal message
                                        dbRef.child("groupcirclemembers").child(key).child("currentmembers").child(myPhoneNumber)
                                                .setValue(new CircleMember(myPhoneNumber,"true","1"));
                                        dbRef.child("groupcirclesettings").child(myPhoneNumber).child(key).child("leapStatus").setValue("1");

                                        // This list is used to load the circles fragment
                                        // First add the group id
                                        dbRef.child("usergroupcirclelist")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(key).child("groupid").setValue(key);
                                        // Next add the group name
                                        dbRef.child("usergroupcirclelist")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(key).child("groupName").setValue(text);
                                        // Lastly add the admin status
                                        dbRef.child("usergroupcirclelist")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(key).child("admin").setValue("true");
                                        dbRef.child("groupcirclelastmessages").child(key)
                                                .setValue(new circleMessage("Welcome to your new circle, add your other leapers now", key,
                                                        "Leap Bot", "LEAPBOT", "1", "true"));
                                        Snackbar.make(v, "New circle added", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null).show();


                                    }
                                })
                                .show();
                        break;


                    default:
                        break;



                }
            }
        });








        //Set Username text in top nav
        TextView txtProfileName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.profileFullName);
        txtProfileName.setText(userPhoneNumber);





        inviteFriends = (TextView)findViewById(R.id.inviteFriends);
        inviteFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "Leap - you got leaped");
                    String sAux = "\nDownload the Leap app now and connect with gamers, share your passion and compete on a whole new level. Connect now and get leaped! \n";
                    sAux = sAux + "https://play.google.com/store/apps/details?id="+ getPackageName() + " \n\n";
                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(i, "choose one"));
                } catch(Exception e) {
                    //e.toString();
                }
            }
        });







        /////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////
        //////// GETTING AND SETTING NAMES IN PLACE OF PHONE NUMBER

        ///////////////////////////////////////
        //////////////////   STARTING    ///////////////////////////////




        //// CHECK MY CONTACT LIST IF THIS PERSON IS A CONTACT
        dbRef.child("ContactList").child(myUID).child("leapSortedContacts").child(userPhoneNumber)
                .addValueEventListener(new ValueEventListener() {////////
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.child("name").getValue() == null || dataSnapshot.child("name")
                                .getValue() == "") {///// IF THEY ARE NOT A CONTACT OR THE VALUE IS EMPTY



                            ///// CHECK THE USERS PROFILES TO SEE IF THEY HAVE AN ENTRY THERE
                            dbRef.child("userprofiles").child(userPhoneNumber).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.child("name").getValue() == null || Objects.equals(dataSnapshot.child("name")
                                            .getValue().toString(), "")){///IF THEY DON'T HAVE AN ENTRY USE THEIR PHONE NUMBER

                                        /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////
                                        displayedLeaperName.setText(userPhoneNumber);
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








        displayedLeaperName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Leap.this, leaperProfileActivity.class);
                intent.putExtra("leaperPhoneNumber", myPhoneNumber);
                startActivity(intent);

            }
        });












        Button leapOutButton = (Button) findViewById(R.id.leapOutButton);
        leapOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // since I can connect from multiple devices, we store each connection instance separately
                // any time that connectionsRef's value is null (i.e. has no children) I am offline
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference myConnectionsRef = database.getReference().child("connections").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());

                // stores the timestamp of my last disconnect (the last time I was seen online)
                final DatabaseReference lastOnlineRef = database.getReference().child("connections").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("lastOnline");

                // when this device disconnects, remove it
                dbRef.child("connections").child(FirebaseAuth.getInstance().getCurrentUser()
                        .getPhoneNumber()).child(deviceOnlinekey).removeValue();

                // when I disconnect, update the last time I was seen online
                lastOnlineRef.setValue(ServerValue.TIMESTAMP);

                // add this device to my connections list
                // this value could contain info about the device or a timestamp too


                FirebaseAuth.getInstance().signOut();
                Intent logoutIntent = new Intent(Leap.this, registerLogin.class);
                logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(logoutIntent);


            }
        });

        BugReport = (TextView)findViewById(R.id.BugReport);
        BugReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                drawer.closeDrawer(Gravity.START);


                new LovelyTextInputDialog(Leap.this, R.style.MyDialogTheme)
                        .setTopColorRes(R.color.colorPrimary)
                        .setTitle("Bug Report")
                        .setMessage("Enter report")
                        .setIcon(R.drawable.ic_chat)
                        .setInputFilter("Enter message", new LovelyTextInputDialog.TextFilter() {
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
                        .setConfirmButton("Send", new LovelyTextInputDialog.OnTextInputConfirmListener() {
                            @Override
                            public void onTextInputConfirmed(String text) {


                                String key = dbRef.child("bugreports").push().getKey();
                                dbRef.child("bugreports").child(key)
                                        .child("reportDate").setValue(new Date().getTime());
                                dbRef.child("bugreports").child(key)
                                        .child("reporter").setValue(myUID);
                                dbRef.child("bugreports").child(key)
                                        .child("message").setValue(text);


                                Toast.makeText(Leap.this, "Bug report sent successfully", Toast.LENGTH_SHORT).show();


                            }
                        })
                        .show();
            }
        });


        AboutLeap = (TextView)findViewById(R.id.aboutLeap);
        AboutLeap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent aboutLeapIntent = new Intent(Leap.this, about_leap.class);
                startActivity(aboutLeapIntent);

                drawer.closeDrawer(Gravity.START);

            }
        });

















        //////////////////////////// RESET ONLINE STATUS NODE
        dbRef.child("connections").child(myPhoneNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String leapStatus = null;
                String statusPermission = null;

                /// BEFORE ANYTHING, CHECK AND REMOVE REDUNDANT ENTRIES BY RESETTING ENTIRE NODE. HOWEVER GET AVAILABLE VALUES FIRST
                if(dataSnapshot.child("leapStatus").getValue() == null){
                } else {
                    leapStatus = dataSnapshot.child("leapStatus").getValue().toString();
                }

                if(dataSnapshot.child("statusPermission").getValue() == null) {

                }else {

                    statusPermission = dataSnapshot.child("statusPermission").getValue().toString();
                }



                //// PROCEED TO DELETE THE NODE
                dbRef.child("connections").child(myPhoneNumber).removeValue();

                /// RESTORE THE VALUES IF THEY WEREN'T NULL AT THE POINT OF RETRIEVING
                if (leapStatus == null){
                } else{
                    dbRef.child("connections").child(myPhoneNumber).child("leapStatus").setValue(leapStatus);
                }

                if (statusPermission == null){
                    dbRef.child("connections").child(myPhoneNumber).child("lastOnline").setValue("true");
                    dbRef.child("connections").child(myPhoneNumber).child("statusPermission").setValue("1"); // 1 - for allow last see // default value if nothing set in settings
                    dbRef.child("connections").child(myPhoneNumber).child("leapStatus").setValue("1");
                } else{
                    dbRef.child("connections").child(myPhoneNumber).child("lastOnline").setValue("true");
                    dbRef.child("connections").child(myPhoneNumber).child("statusPermission").setValue(statusPermission);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //////////////////////////// RESET ONLINE STATUS NODE END















        // since I can connect from multiple devices, we store each connection instance separately
        // any time that connectionsRef's value is null (i.e. has no children) I am offline
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myConnectionsRef = database.getReference().child("connections").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());

        // stores the timestamp of my last disconnect (the last time I was seen online)
        final DatabaseReference lastOnlineRef = database.getReference().child("connections").child(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()).child("lastOnline");

        final DatabaseReference connectedRef = database.getReference().child(".info").child("connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {


                    DatabaseReference con = myConnectionsRef.push();
                    deviceOnlinekey = con.getKey().toString();

                    // when this device disconnects, remove it
                    con.onDisconnect().removeValue();

                    // when I disconnect, update the last time I was seen online
                    lastOnlineRef.onDisconnect().setValue(ServerValue.TIMESTAMP);

                    // add this device to my connections list
                    // this value could contain info about the device or a timestamp too
                    con.setValue(Boolean.TRUE);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled at .info/connected");
            }
        });








        final int versionCode = BuildConfig.VERSION_CODE;
        dbRef.child("connections").child(myPhoneNumber).child("myAppVersion").setValue(versionCode);
        //editor.apply();


        dbRef.child("APP").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                int currentVersion = Integer.parseInt(dataSnapshot.child("currentVersion").getValue().toString());

                if (versionCode == currentVersion){



                    dbRef.child("patchnotes").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.hasChildren()){



                                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Leap.this);
                                final SharedPreferences.Editor editor = sharedPreferences.edit();
                                String patchNotesReadTime = sharedPreferences.getString("patchNotesReadTime", null);

                                String title = dataSnapshot.child("title").getValue().toString();
                                String message = dataSnapshot.child("message").getValue().toString();
                                final String time = dataSnapshot.child("time").getValue().toString();


                                if(Objects.equals(patchNotesReadTime, time)){
                                    /// PATCH READ


                                } else {


                                    /// PATCH NOT READ
                                    confirmDialog.NewConfirmDialog(context, title, message, "Read later", "Don't show again");
                                    confirmDialog.confirmAccept.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            confirmDialog.dialog.dismiss();


                                        }
                                    });
                                    confirmDialog.confirmCancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dbRef.child("connections").child(myPhoneNumber).child("patchnotes").setValue("true");
                                            confirmDialog.dialog.dismiss();
                                            editor.putString("patchNotesReadTime", time);
                                            editor.apply();


                                        }
                                    });





                                }









                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });














                } else {



                    dbRef.child("patchnotes").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.hasChildren()){



                                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Leap.this);
                                final SharedPreferences.Editor editor = sharedPreferences.edit();

                                final long nowTime = new Date().getTime();
                                final long inOneWeek = nowTime + TimeUnit.DAYS.toMillis(6);

                                final long patchNotesUpdateTime = sharedPreferences.getLong("patchNotesUpdateTime", nowTime);


                                String title = dataSnapshot.child("title").getValue().toString();
                                String message = dataSnapshot.child("message").getValue().toString();
                                final String time = dataSnapshot.child("time").getValue().toString();


                                if (nowTime > patchNotesUpdateTime) {



                                    /// PATCH NOT READ
                                    confirmDialog.NewConfirmDialog(context, title, message, "Update Now", "Update Later");
                                    confirmDialog.confirmAccept.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            confirmDialog.dialog.dismiss();

                                            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                            try {
                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                            }
                                            catch (android.content.ActivityNotFoundException anfe) {
                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                                            }

                                        }
                                    });
                                    confirmDialog.confirmCancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dbRef.child("connections").child(myPhoneNumber).child("patchnotes").setValue("true");
                                            confirmDialog.dialog.dismiss();
                                            editor.putLong("patchNotesUpdateTime", inOneWeek);
                                            editor.apply();





                                        }
                                    });



                                } else if (nowTime == patchNotesUpdateTime) {






                                    /// PATCH NOT READ
                                    confirmDialog.NewConfirmDialog(context, title, message, "Update Now", "Update Later");
                                    confirmDialog.confirmAccept.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            confirmDialog.dialog.dismiss();

                                            final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                                            try {
                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                            }
                                            catch (android.content.ActivityNotFoundException anfe) {
                                                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
                                            }

                                        }
                                    });
                                    confirmDialog.confirmCancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dbRef.child("connections").child(myPhoneNumber).child("patchnotes").setValue("true");
                                            confirmDialog.dialog.dismiss();
                                            editor.putLong("patchNotesUpdateTime", inOneWeek);
                                            editor.apply();





                                        }
                                    });


                                }













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

































        ////// NOTIFICATIONS




        FirebaseDatabase.getInstance().getReference().child("notifications").child(myPhoneNumber).orderByChild("notificationStatus")
                .equalTo("0").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {



                count = dataSnapshot.getChildrenCount();

                if (loadFlag == 0){
                    loadFlag = 1;
                } else if (loadFlag == 1){



                    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                    setSupportActionBar(toolbar);


                    //Setting Drawers
                    final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                            Leap.this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                    drawer.setDrawerListener(toggle);
                    toggle.syncState();

                    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                    navigationView.setNavigationItemSelectedListener(Leap.this);







                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });







        FirebaseDatabase.getInstance().getReference().child("notifications").child(myPhoneNumber).orderByChild("notificationStatus")
                .equalTo("0").limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                if (dataSnapshot.hasChildren()){


                    if (pushNFlag == 0){
                        pushNFlag = 1;
                    } else if (pushNFlag == 1){



                        if (dataSnapshot.hasChildren()){
                            String notificationID = (String) dataSnapshot.child("notificationID").getValue();
                            String inviteBy = (String) dataSnapshot.child("inviteBy").getValue();
                            String circleID = (String) dataSnapshot.child("circleID").getValue();
                            Bundle bundle = new Bundle();
                            bundle.putString("notificationID", notificationID);

                            getFirebaseInfo.GetLeaperName(inviteBy, notificationTextView);
                            String nLeaperName = notificationTextView.getText().toString();
                            getFirebaseInfo.GetCircleName(circleID, notificationTextView2);
                            String circleName = notificationTextView2.getText().toString();


                            if ((Objects.equals(circleName, "Name") || (Objects.equals(nLeaperName, "Name")))){



                                PugNotification.with(context)
                                        .load()
                                        .title("Circle Invitation")
                                        .message("You've received a circle invite from " + inviteBy)
                                        .bigTextStyle("You've received a circle invite from " + inviteBy)
                                        .smallIcon(R.mipmap.ic_leap)
                                        .largeIcon(R.mipmap.ic_leap)
                                        .flags(Notification.DEFAULT_ALL)
                                        .click(receivedNotifications.class, bundle)
                                        .autoCancel(true)
                                        .simple()
                                        .build();

                            } else {


                                PugNotification.with(context)
                                        .load()
                                        .title("Circle Invitation")
                                        .message("You've been invited by " + nLeaperName + " to join " + circleName)
                                        .bigTextStyle("You've been invited by " + nLeaperName + " to join " + circleName)
                                        .smallIcon(R.mipmap.ic_leap)
                                        .largeIcon(R.mipmap.ic_leap)
                                        .flags(Notification.DEFAULT_ALL)
                                        .click(receivedNotifications.class, bundle)
                                        .autoCancel(true)
                                        .simple()
                                        .build();

                            }




                        }



                    }



                }



            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
                    return "Leaps";
                case 2:
                    return "Chats";
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

        MenuItem menuItem = menu.findItem(R.id.nav_leap_notifications);
        menuItem.setIcon(leapUtilities.buildCounterDrawable(count, R.drawable.ic_bell, context));


        return true;
    }











    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.nav_leap_notifications) {



            Intent openNotificationsIntent = new Intent(this, receivedNotifications.class);
            startActivity(openNotificationsIntent);


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

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);

        }  else if (id == R.id.nav_leap_events){
            Intent openNotificationsIntent = new Intent(this, events.class);
            startActivity(openNotificationsIntent);

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);

        }


        return true;
    }


    //minimize leap when back is pressed
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mViewPager = (ViewPager) findViewById(R.id.leapsContainer);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            this.moveTaskToBack(true);
            return true;
        } else if ((keyCode == event.KEYCODE_BACK) && (mViewPager.getCurrentItem() != 0)){
                mViewPager.setCurrentItem(0);
        }else {
            //super.onBackPressed();
        }



        mViewPager.setCurrentItem(0);
        return super.onKeyDown(keyCode, event);


    }


    @Override
    protected void onResume() {
        super.onResume();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // Sign in logic here.
                }
                else {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(Leap.this, registerLogin.class);
                    startActivity(intent);

                }
            }
        };

        super.onResumeFragments();
    }

    @Override
    protected void onResumeFragments() {

         mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // Sign in logic here.
                }
                else {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(Leap.this, registerLogin.class);
                    startActivity(intent);

                }
            }
        };

        super.onResumeFragments();
    }
}


