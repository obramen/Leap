package com.antrixgaming.leap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.antrixgaming.leap.LeapClasses.ImageUtils;
import com.antrixgaming.leap.LeapClasses.LeapUtilities;
import com.antrixgaming.leap.LeapClasses.MenuFAB;
import com.antrixgaming.leap.LeapClasses.OnlinePressence;
import com.antrixgaming.leap.Models.CircleMember;
import com.antrixgaming.leap.Models.savePhoneContacts;
import com.antrixgaming.leap.Models.circleMessage;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class groupInfoActivity extends BaseActivity implements ImageUtils.ImageAttachmentListener  {

    String myUID;
    String myPhoneNumber;

    private StorageReference mStorage;
    private StorageReference mGroupStorageRef;
    private StorageReference mLeaperStorageRef;


    ImageUtils imageutils;


    ProgressDialog progressDialog;


    int loadFlag = 0;
    private String AdminFlag = "false";
    private String TrueAdmin;
    private String FalseAdmin;

    private RelativeLayout adminMenu;


    private Bitmap bitmap;
    private String file_name;


    private MaterialSheetFab materialSheetFab;
    private int statusBarColor;

    MenuFAB fab;
    View sheetView;
    View overlay;
    int sheetColor;
    int fabColor;

    private ImageView groupProfileImage;

    LeapUtilities leapUtilities;
    OnlinePressence onlinePressence;

    String groupCreator;
    DatabaseReference dbRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        leapUtilities = new LeapUtilities();
        onlinePressence = new OnlinePressence();
        mStorage = FirebaseStorage.getInstance().getReference();
        TrueAdmin = "true";
        FalseAdmin = "false";

        dbRef = FirebaseDatabase.getInstance().getReference();

        groupCreator = "";




        myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        myPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();


        // Set material sheet item click listeners
        final LinearLayout groupInfoNewLeaper = (LinearLayout)findViewById(R.id.groupInfoNewLeaper);
        LinearLayout groupInfoNewNotice = (LinearLayout)findViewById(R.id.groupInfoNewNotice);
        groupProfileImage = (ImageView) findViewById(R.id.groupProfileImage);
        imageutils = new ImageUtils(this);
        progressDialog = new ProgressDialog(this);





        fab = (MenuFAB) findViewById(R.id.leapDetailsMenuFAB);
        sheetView = findViewById(R.id.leapDetailsFab_sheet);
        overlay = findViewById(R.id.leapDetailsDimOverlay);
        sheetColor = getResources().getColor(R.color.colorPrimaryDark);
        fabColor = getResources().getColor(R.color.colorPrimaryDark);

        setupFab();


        Bundle bundle = getIntent().getExtras();
        final String groupName = bundle.getString("groupName");
        final String circleID = bundle.getString("circleID");




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
                                                toolbar.setTitle(groupName);
                                                toolbar.setSubtitle("Created by " + groupCreator +", " + DateFormat.format("dd-MMM-yy", Long.parseLong(groupCreatedOn)));
                                                /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////

                                            } else {

                                                String myName = dataSnapshot.child("name").getValue().toString();

                                                /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////
                                                toolbar.setTitle(groupName);
                                                toolbar.setSubtitle("Created by " + myName +", " + DateFormat.format("dd-MMM-yy", Long.parseLong(groupCreatedOn)));
                                                /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////s


                                            }




                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });




                                } else{


                                    //getting creator name
                                    dbRef.child("ContactList").child(myUID).child("leapSortedContacts").child(groupCreator).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            if(dataSnapshot.child("name").getValue() == null || dataSnapshot.child("name").getValue() == ""){
                                                /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////
                                                toolbar.setTitle(groupName);
                                                toolbar.setSubtitle("Created by " + groupCreator +", " + DateFormat.format("dd-MMM-yy", Long.parseLong(groupCreatedOn)));

                                                /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////

                                            } else {


                                                String mName = dataSnapshot.child("name").getValue().toString();

                                                /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////
                                                toolbar.setTitle(groupName);
                                                toolbar.setSubtitle("Created by " + mName +", " + DateFormat.format("dd-MMM-yy", Long.parseLong(groupCreatedOn)));

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






        mGroupStorageRef = mStorage.child("groupCircleProfileImage").child(circleID).child(circleID);
        leapUtilities.SquareImageFromFirebase(groupInfoActivity.this, mGroupStorageRef, groupProfileImage);


        DatabaseReference circleIDRef = FirebaseDatabase.getInstance().getReference()
                .child("groupcirclemembers").child(circleID).child("currentmembers").child(myPhoneNumber);
        circleIDRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                AdminFlag = dataSnapshot.child("admin").getValue().toString();

                if (Objects.equals(AdminFlag, TrueAdmin))
                    fab.setVisibility(View.VISIBLE);
                else if (Objects.equals(AdminFlag, FalseAdmin))
                    fab.setVisibility(View.GONE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });






        groupProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (imageutils.isDeviceSupportCamera())
                    imageutils.imagepicker(1);
                else imageutils.imagepicker(0);


            }
        });





        groupInfoNewLeaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent selectLeapersIntent = new Intent (groupInfoActivity.this, selectLeaperContact.class);
                selectLeapersIntent.putExtra("SourceActivity", "3");
                selectLeapersIntent.putExtra("CircleID", circleID);
                startActivity(selectLeapersIntent);

            }
        });

        groupInfoNewNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {



                new LovelyTextInputDialog(groupInfoActivity.this, R.style.MyDialogTheme)
                        .setTopColorRes(R.color.colorPrimary)
                        .setTitle("New Notice")
                        .setMessage("Enter new message")
                        .setIcon(R.drawable.ic_chat)
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


                                String key = dbRef.child("groupcirclemessages").child(circleID)
                                        .push().getKey();
                                dbRef.child("groupcirclemessages").child(circleID)
                                        .child(key).setValue(new circleMessage(text, circleID,
                                        myPhoneNumber, myUID, "1", "false"));
                                dbRef.child("groupcircles").child(circleID)
                                        .child("lastgroupmessage").setValue(new circleMessage(text, circleID,
                                        myPhoneNumber, myUID, "1", "true"));
                                dbRef.child("groupcirclemessages").child(circleID)
                                        .child(key).child("members").setValue("mmm");
                                dbRef.child("groupcirclelastmessages").child(circleID)
                                        .setValue(new circleMessage(text, circleID,
                                                myPhoneNumber, myUID, "1", "true"));


                                Toast.makeText(groupInfoActivity.this, "Notice sent", Toast.LENGTH_SHORT).show();


                            }
                        })
                        .show();


            }
        });









        final Switch leapStatusSwitch = (Switch) findViewById(R.id.leapStatusSwitch);

        leapStatusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    FirebaseDatabase.getInstance().getReference().child("groupcirclesettings").child(myPhoneNumber)
                            .child(circleID).child("leapStatus").setValue("1");




                    Toast.makeText(groupInfoActivity.this, "Leap status changed", Toast.LENGTH_SHORT).show();
                }else{
                    FirebaseDatabase.getInstance().getReference().child("groupcirclesettings").child(myPhoneNumber)
                            .child(circleID).child("leapStatus").setValue("0");
                    Toast.makeText(groupInfoActivity.this, "Leap status changed", Toast.LENGTH_SHORT).show();

                }
            }
        });

        FirebaseDatabase.getInstance().getReference().child("groupcirclesettings").child(myPhoneNumber)
                .child(circleID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int mleapStatusSwitch = Integer.parseInt(dataSnapshot.child("leapStatus").getValue().toString());

                        if (mleapStatusSwitch == 1)
                            leapStatusSwitch.setChecked(true);
                        else if (mleapStatusSwitch == 0)
                            leapStatusSwitch.setChecked(false);


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
















        ListView listOfContacts = (ListView) findViewById(R.id.list_of_groupInfoLeapers);

        DatabaseReference dbRefLeaper = dbRef.child("groupcirclemembers")
                .child(circleID).child("currentmembers");

        FirebaseListAdapter<CircleMember> adapter;
        adapter = new FirebaseListAdapter<CircleMember>(this, CircleMember.class,
                R.layout.activity_group_info_leaper_list, dbRefLeaper.orderByChild("phoneNumber")) {
            @Override
            protected void populateView(final View v, final CircleMember model, int position) {


                TextView circleLeaperListLeaperName = (TextView) v.findViewById(R.id.circleLeaperListLeaperName);
                final Button circleLeaperListButton = (Button) v.findViewById(R.id.circleLeaperListButton);
                LinearLayout circleLeaperListLayout = (LinearLayout) v.findViewById(R.id.circleLeaperListLayout);
                final CircleImageView leaperImage = (CircleImageView)v.findViewById(R.id.circleLeaperListImage);

                // Populate the data into the template view using the data object
                //circleLeaperListLeaperName.setText(model.getcontactName());
                circleLeaperListLeaperName.setText(model.getPhoneNumber());

                final String leaperPhoneNumber = model.getPhoneNumber();


                mLeaperStorageRef = mStorage.child("leaperProfileImage").child(leaperPhoneNumber).child(leaperPhoneNumber);
                leapUtilities.CircleImageFromFirebase(groupInfoActivity.this, mLeaperStorageRef, leaperImage);






                    final DatabaseReference leapStatus = FirebaseDatabase.getInstance().getReference().child("groupcirclesettings").child(model.getPhoneNumber())
                            .child(circleID);
                    leapStatus.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int mLeapStatus = Integer.parseInt(dataSnapshot.child("leapStatus").getValue().toString());



                            if(Objects.equals(mLeapStatus, 0)){

                                circleLeaperListButton.setVisibility(v.GONE);
                                //leaperImage.setBorderColor(getResources().getColor(R.color.md_red_900));
                            }
                            else if (Objects.equals(mLeapStatus, 1))
                                circleLeaperListButton.setVisibility(v.VISIBLE);
                            //leaperImage.setBorderColor(getResources().getColor(R.color.green));


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



                onlinePressence.circleLeaperOnlinePrescence(groupInfoActivity.this, model.getPhoneNumber(), circleID, leaperImage);









                circleLeaperListButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Intent startNewLeapIntent = new Intent(groupInfoActivity.this, newLeap.class);
                        startNewLeapIntent.putExtra("leapedPhoneNumber", leaperPhoneNumber);
                        startNewLeapIntent.putExtra("SourceActivity", "1");  // to be used to identify that the extras came from here
                        startNewLeapIntent.putExtra("circleID", circleID);
                        startActivity(startNewLeapIntent);

                    }
                });





            }


        };



        listOfContacts.setAdapter(adapter);

        //leapUtilities.justifyListViewHeightBasedOnChildren(listOfContacts);

    }











    @Override
    public void image_attachment(int from, String filename, Bitmap file, Uri uri) {
        this.bitmap=file;
        this.file_name=filename;
        groupProfileImage.setImageBitmap(file);

        String path =  Environment.getExternalStorageDirectory() + File.separator + "ImageAttach" + File.separator;
        imageutils.createImage(file,filename,path,false);


        // Get the data from an ImageView as bytes
        groupProfileImage.setDrawingCacheEnabled(true);
        groupProfileImage.buildDrawingCache();
        Bitmap bitmap = groupProfileImage.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        progressDialog.setMessage("Image uploading...");
        progressDialog.show();



        UploadTask uploadTask = mGroupStorageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {


                progressDialog.dismiss();
                Toast.makeText(groupInfoActivity.this, "error encountered, retry", Toast.LENGTH_SHORT).show();

                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                Toast.makeText(groupInfoActivity.this, "Profile picture changed", Toast.LENGTH_SHORT).show();
                Glide.with(groupInfoActivity.this).using(new FirebaseImageLoader()).load(mGroupStorageRef)
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                        .error(R.drawable.antrixlogo1).centerCrop().into(groupProfileImage);
                progressDialog.dismiss();


            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);
        imageutils.onActivityResult(requestCode, resultCode, data);
    }



    private void setupFab() {



        // Create material sheet FAB
        materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay, sheetColor, fabColor);

        // Set material sheet event listener
        materialSheetFab.setEventListener(new MaterialSheetFabEventListener() {
            @Override
            public void onShowSheet() {
                // Save current status bar color
                statusBarColor = getStatusBarColor();
                // Set darker status bar color to match the dim overlay
                setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            }

            @Override
            public void onHideSheet() {
                // Restore status bar color
                setStatusBarColor(statusBarColor);
            }
        });









    }

    private int getStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getWindow().getStatusBarColor();
        }
        return 0;
    }

    private void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(color);
        }
    }










    @Override
    public void onBackPressed() {
        if (materialSheetFab.isSheetVisible()) {
            materialSheetFab.hideSheet();
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        imageutils.request_permission_result(requestCode, permissions, grantResults);
    }














    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }



}


