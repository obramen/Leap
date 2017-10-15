package com.antrixgaming.leap;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.antrixgaming.leap.LeapClasses.ImageUtils;
import com.antrixgaming.leap.LeapClasses.LeapUtilities;
import com.antrixgaming.leap.Models.CircleMember;
import com.antrixgaming.leap.Models.UserProfile;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.common.annotation.KeepForSdkWithFieldsAndMethods;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class leaperProfileActivity extends BaseActivity implements ImageUtils.ImageAttachmentListener {

    private String myUID;
    private String myPhoneNumber;
    private CircleImageView profileImage;
    private CircleImageView changeBackgroundImage;
    private CircleImageView changeProfileImage;
    ImageView profileImageBackground;
    private TextView profileLeaperName;

    private StorageReference mStorage;
    private StorageReference mLeaperStorageRef;
    private StorageReference mLeaperProfileStorageRef;

    ProgressDialog progressDialog;

    ImageUtils imageutils;

    private Bitmap bitmap;
    private String file_name;

    LeapUtilities leapUtilities;

    DatabaseReference dbRef;
    DatabaseReference userProfileDbRef;


    int uploadflag = 0;
    int editFlag = 0;

    TextView editProfile;
    Switch leapStatusSwitch;
    Switch statusPermissionSwitch;
    ImageView leaperProfileNewLeap;
    ImageView leaperProfileNewMessage;


    EditText tauntEditText;
    EditText nameEditText;
    Spinner genderEditText;
    EditText psnEditText;
    EditText xboxliveEditText;
    EditText originEditText;
    EditText steamEditText;

    TextView tauntTextView;
    TextView nameTextView;
    TextView genderTextView;
    TextView psnTextView;
    TextView xboxliveTextView;
    TextView originTextView;
    TextView steamTextView;

    Button saveProfileButton;
    Button cancelProfileButton;
    RelativeLayout leapLayout;

    String taunt;
    String name;
    String gender;
    String psn;
    String xboxlive;
    String origin;
    String steam;

    ProgressDialog progressDialogSaveProfile;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaper_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        final String leaperPhoneNumber = bundle.getString("leaperPhoneNumber");
        myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();



        //getSupportActionBar().setTitle(leaperPhoneNumber);
        imageutils = new ImageUtils(this);
        leapUtilities = new LeapUtilities();

        mStorage = FirebaseStorage.getInstance().getReference();
        dbRef = FirebaseDatabase.getInstance().getReference();
        userProfileDbRef = dbRef.child("userprofiles").child(leaperPhoneNumber);
        myPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

        if (Objects.equals(leaperPhoneNumber, myPhoneNumber))
        {
            editFlag = 1;
        } else {
            editFlag = 0;
        }



        profileLeaperName = (TextView) findViewById(R.id.profileLeaperName);
        //profileLeaperName.setText(leaperPhoneNumber);










        /////////////////////////////////////////////////////////////////////
        ///////////////////////////////////////////
        //////// GETTING AND SETTING NAMES IN PLACE OF PHONE NUMBER

        ///////////////////////////////////////
        //////////////////   STARTING    ///////////////////////////////

        if (Objects.equals(leaperPhoneNumber, myPhoneNumber)){ //// IF AM CHECKING MY OWN PROFILE



            //// QUERY USER PROFILES TABLE TO SEE IF I HAVE AN ENTRY IN THERE
            dbRef.child("userprofiles").child(myPhoneNumber).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child("name").getValue() == null || Objects.equals(dataSnapshot.child("name")
                            .getValue().toString(), "")){ /////IF I HAVEN'T SAVED MY PROFILE NAME OR THE NAME IS EMPTY, USE MY PHONE NUMBER

                        /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////
                        getSupportActionBar().setTitle(leaperPhoneNumber);
                        profileLeaperName.setText(leaperPhoneNumber);
                        /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////

                    } else { ////IF I HAVE SAVED MY PROFILE NAME USE IT

                        String myName = dataSnapshot.child("name").getValue().toString();

                        /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////
                        getSupportActionBar().setTitle(myName);
                        profileLeaperName.setText(myName);
                        /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////s


                    }




                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });




        } else { ///////     IF I AM CHECKING SOMEONE ELSE'S PROFILE


            //// CHECK MY CONTACT LIST IF THIS PERSON IS A CONTACT
            dbRef.child("ContactList").child(myUID).child("leapSortedContacts").child(leaperPhoneNumber)
                    .addValueEventListener(new ValueEventListener() {////////
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child("name").getValue() == null || dataSnapshot.child("name")
                            .getValue() == "") {///// IF THEY ARE NOT A CONTACT OR THE VALUE IS EMPTY



                        ///// CHECK THE USERS PROFILES TO SEE IF THEY HAVE AN ENTRY THERE
                        dbRef.child("userprofiles").child(leaperPhoneNumber).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.child("name").getValue() == null || Objects.equals(dataSnapshot.child("name")
                                        .getValue().toString(), "")){///IF THEY DON'T HAVE AN ENTRY USE THEIR PHONE NUMBER

                                    /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////
                                    getSupportActionBar().setTitle(leaperPhoneNumber);
                                    profileLeaperName.setText(leaperPhoneNumber);
                                    /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////


                                } else { //// IF THEY HAVE AN ENTRY USE THEIR ENTERED NAME

                                    String myName = dataSnapshot.child("name").getValue().toString();

                                    /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////
                                    getSupportActionBar().setTitle(myName);
                                    profileLeaperName.setText(myName);
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
                        getSupportActionBar().setTitle(mName);
                        profileLeaperName.setText(mName);
                        /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////


                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }





        //////////////////   ENDING    ///////////////////////////////
        ///////////////////////////////////////

        //////// GETTING AND SETTING NAMES IN PLACE OF PHONE NUMBER
        ///////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////



















        profileImage = (CircleImageView) findViewById(R.id.profileImage);
        changeBackgroundImage = (CircleImageView) findViewById(R.id.changeBackgroundImage);
        profileImageBackground = (ImageView) findViewById(R.id.profileImageBackground);


        leapStatusSwitch = (Switch) findViewById(R.id.leapStatusSwitch);
        statusPermissionSwitch = (Switch) findViewById(R.id.statusPermissionSwitch);
        editProfile = (TextView) findViewById(R.id.editProfle);
        changeProfileImage = (CircleImageView) findViewById(R.id.changeProfileImage);
        leaperProfileNewLeap = (ImageView) findViewById(R.id.leaperProfileNewLeap);
        leaperProfileNewMessage = (ImageView) findViewById(R.id.leaperProfileNewMessage);



        /// FOR EDITING INFORMATION
        tauntEditText = (EditText) findViewById(R.id.tauntEditText);
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        genderEditText = (Spinner) findViewById(R.id.genderEditText);
        psnEditText = (EditText) findViewById(R.id.psnEditText);
        xboxliveEditText = (EditText) findViewById(R.id.xboxliveEditText);
        originEditText = (EditText) findViewById(R.id.originEditText);
        steamEditText = (EditText) findViewById(R.id.steamEditText);

        /// FOR DISPLAYING INFORMATION
        tauntTextView = (TextView) findViewById(R.id.tauntTexView);
        nameTextView = (TextView) findViewById(R.id.nameTextView);
        genderTextView = (TextView) findViewById(R.id.genderTextView);
        psnTextView = (TextView) findViewById(R.id.psnTextView);
        xboxliveTextView = (TextView) findViewById(R.id.xboxliveTextView);
        originTextView = (TextView) findViewById(R.id.originTextView);
        steamTextView = (TextView) findViewById(R.id.steamTextView);

        saveProfileButton = (Button) findViewById(R.id.saveProfileButton);
        cancelProfileButton = (Button) findViewById(R.id.cancelProfileButton);
        leapLayout = (RelativeLayout) findViewById(R.id.leapLayout);







        /// HIDE INFORMATION
        tauntTextView.setVisibility(View.VISIBLE);
        nameTextView.setVisibility(View.VISIBLE);
        genderTextView.setVisibility(View.VISIBLE);
        psnTextView.setVisibility(View.VISIBLE);
        xboxliveTextView.setVisibility(View.VISIBLE);
        originTextView.setVisibility(View.VISIBLE);
        steamTextView.setVisibility(View.VISIBLE);
        editProfile.setVisibility(View.VISIBLE);


        tauntEditText.setVisibility(View.GONE);
        nameEditText.setVisibility(View.GONE);
        genderEditText.setVisibility(View.GONE);
        psnEditText.setVisibility(View.GONE);
        xboxliveEditText.setVisibility(View.GONE);
        originEditText.setVisibility(View.GONE);
        steamEditText.setVisibility(View.GONE);


        saveProfileButton.setVisibility(View.GONE);
        cancelProfileButton.setVisibility(View.GONE);













        progressDialog = new ProgressDialog(this);
        progressDialogSaveProfile = new ProgressDialog(this);

        if (editFlag == 0){

            profileImage.setEnabled(false);
            changeBackgroundImage.setVisibility(View.GONE);
            editProfile.setVisibility(View.GONE);
            leapStatusSwitch.setVisibility(View.GONE);
            leapLayout.setVisibility(View.GONE);
            changeProfileImage.setVisibility(View.GONE);
            leaperProfileNewLeap.setVisibility(View.VISIBLE);
            leaperProfileNewMessage.setVisibility(View.VISIBLE);




        } else if (editFlag == 1){

            profileImage.setEnabled(true);
            changeBackgroundImage.setVisibility(View.VISIBLE);
            editProfile.setVisibility(View.VISIBLE);
            leapStatusSwitch.setVisibility(View.VISIBLE);
            leapLayout.setVisibility(View.VISIBLE);
            changeProfileImage.setVisibility(View.VISIBLE);
            leaperProfileNewLeap.setVisibility(View.GONE);
            leaperProfileNewMessage.setVisibility(View.GONE);

        }






        mLeaperStorageRef = mStorage.child("leaperProfileImage").child(leaperPhoneNumber).child(leaperPhoneNumber);
        leapUtilities.CircleImageFromFirebase(leaperProfileActivity.this, mLeaperStorageRef, profileImage);


        mLeaperProfileStorageRef = mStorage.child("leaperProfileImage").child(leaperPhoneNumber).child("backgroundImage");
        leapUtilities.SquareImageFromFirebase(leaperProfileActivity.this, mLeaperProfileStorageRef, profileImageBackground);












        userProfileDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() == null){

                    dbRef.child("userprofiles").child(leaperPhoneNumber).setValue(new UserProfile(leaperPhoneNumber, "",
                            "", "","",
                            "" , "",
                            "", "",
                            "", ""));

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });








        userProfileDbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                taunt = dataSnapshot.child("taunt").getValue().toString();
                name = dataSnapshot.child("name").getValue().toString();
                gender = dataSnapshot.child("gender").getValue().toString();
                psn = dataSnapshot.child("psn").getValue().toString();
                xboxlive = dataSnapshot.child("xboxlive").getValue().toString();
                origin = dataSnapshot.child("origin").getValue().toString();
                steam = dataSnapshot.child("steam").getValue().toString();



                /// DISPLAYING INFORMATION
                tauntTextView.setText(taunt);
                nameTextView.setText(name);
                genderTextView.setText(gender);
                psnTextView.setText(psn);
                xboxliveTextView.setText(xboxlive);
                originTextView.setText(origin);
                steamTextView.setText(steam);

                tauntEditText.setText(taunt);
                nameEditText.setText(name);
                //genderEditText.(gender);
                psnEditText.setText(psn);
                xboxliveEditText.setText(xboxlive);
                originEditText.setText(origin);
                steamEditText.setText(steam);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });








        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /// HIDE INFORMATION
                tauntTextView.setVisibility(View.GONE);
                nameTextView.setVisibility(View.GONE);
                genderTextView.setVisibility(View.GONE);
                psnTextView.setVisibility(View.GONE);
                xboxliveTextView.setVisibility(View.GONE);
                originTextView.setVisibility(View.GONE);
                steamTextView.setVisibility(View.GONE);
                editProfile.setVisibility(View.GONE);



                /// SHOW EDIT TEXT
                tauntEditText.setVisibility(View.VISIBLE);
                nameEditText.setVisibility(View.VISIBLE);
                genderEditText.setVisibility(View.VISIBLE);
                psnEditText.setVisibility(View.VISIBLE);
                xboxliveEditText.setVisibility(View.VISIBLE);
                originEditText.setVisibility(View.VISIBLE);
                steamEditText.setVisibility(View.VISIBLE);

                saveProfileButton.setVisibility(View.VISIBLE);
                cancelProfileButton.setVisibility(View.VISIBLE);

            }
        });

        saveProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialogSaveProfile.setMessage("Saving information");
                progressDialogSaveProfile.show();

                dbRef.child("userprofiles").child(leaperPhoneNumber).setValue(new UserProfile(leaperPhoneNumber, nameEditText.getText().toString().trim(),
                        tauntEditText.getText().toString().trim(), genderEditText.getSelectedItem().toString(),"" ,
                        "" , "",
                        psnEditText.getText().toString().trim(), xboxliveEditText.getText().toString().trim(),
                        steamEditText.getText().toString().trim(), originEditText.getText().toString().trim()));



                /// HIDE INFORMATION
                tauntTextView.setVisibility(View.VISIBLE);
                nameTextView.setVisibility(View.VISIBLE);
                genderTextView.setVisibility(View.VISIBLE);
                psnTextView.setVisibility(View.VISIBLE);
                xboxliveTextView.setVisibility(View.VISIBLE);
                originTextView.setVisibility(View.VISIBLE);
                steamTextView.setVisibility(View.VISIBLE);
                editProfile.setVisibility(View.VISIBLE);


                tauntEditText.setVisibility(View.GONE);
                nameEditText.setVisibility(View.GONE);
                genderEditText.setVisibility(View.GONE);
                psnEditText.setVisibility(View.GONE);
                xboxliveEditText.setVisibility(View.GONE);
                originEditText.setVisibility(View.GONE);
                steamEditText.setVisibility(View.GONE);


                saveProfileButton.setVisibility(View.GONE);
                cancelProfileButton.setVisibility(View.GONE);

                progressDialogSaveProfile.dismiss();
                Toast.makeText(leaperProfileActivity.this, "Changes saved", Toast.LENGTH_SHORT).show();





            }
        });



        cancelProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {





                /// HIDE INFORMATION
                tauntTextView.setVisibility(View.VISIBLE);
                nameTextView.setVisibility(View.VISIBLE);
                genderTextView.setVisibility(View.VISIBLE);
                psnTextView.setVisibility(View.VISIBLE);
                xboxliveTextView.setVisibility(View.VISIBLE);
                originTextView.setVisibility(View.VISIBLE);
                steamTextView.setVisibility(View.VISIBLE);
                editProfile.setVisibility(View.VISIBLE);


                tauntEditText.setVisibility(View.GONE);
                nameEditText.setVisibility(View.GONE);
                genderEditText.setVisibility(View.GONE);
                psnEditText.setVisibility(View.GONE);
                xboxliveEditText.setVisibility(View.GONE);
                originEditText.setVisibility(View.GONE);
                steamEditText.setVisibility(View.GONE);


                saveProfileButton.setVisibility(View.GONE);
                cancelProfileButton.setVisibility(View.GONE);


            }
        });












        changeBackgroundImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uploadflag = 1;

                 mLeaperProfileStorageRef = mStorage.child("leaperProfileImage").child(leaperPhoneNumber).child("backgroundImage");


                if (imageutils.isDeviceSupportCamera())
                    imageutils.imagepicker(1);
                else imageutils.imagepicker(0);




            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uploadflag = 2;

                mLeaperStorageRef = mStorage.child("leaperProfileImage").child(leaperPhoneNumber).child(leaperPhoneNumber);


                if (imageutils.isDeviceSupportCamera())
                    imageutils.imagepicker(1);
                else imageutils.imagepicker(0);



            }
        });




        leapStatusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    FirebaseDatabase.getInstance().getReference().child("connections").child(myPhoneNumber)
                            .child("leapStatus").setValue("1");
                    //Toast.makeText(leaperProfileActivity.this, "Leap status changed", Toast.LENGTH_SHORT).show();
                }else{
                    FirebaseDatabase.getInstance().getReference().child("connections").child(myPhoneNumber)
                            .child("leapStatus").setValue("0");
                    //Toast.makeText(leaperProfileActivity.this, "Leap status changed", Toast.LENGTH_SHORT).show();

                }
            }
        });

        FirebaseDatabase.getInstance().getReference().child("connections").child(leaperPhoneNumber)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int mPermissiontatusSwitch = Integer.parseInt(dataSnapshot.child("statusPermission").getValue().toString());

                if (mPermissiontatusSwitch == 1)
                    statusPermissionSwitch.setChecked(true);
                else
                    statusPermissionSwitch.setChecked(false);





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


        statusPermissionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    FirebaseDatabase.getInstance().getReference().child("connections").child(myPhoneNumber)
                            .child("statusPermission").setValue("1");
                    Toast.makeText(leaperProfileActivity.this, "Privacy changed", Toast.LENGTH_SHORT).show();
                }else{
                    FirebaseDatabase.getInstance().getReference().child("connections").child(myPhoneNumber)
                            .child("statusPermission").setValue("0");
                    Toast.makeText(leaperProfileActivity.this, "Privacy changed", Toast.LENGTH_SHORT).show();

                }
            }
        });







        leaperProfileNewLeap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Objects.equals(leaperPhoneNumber, myPhoneNumber)){


                } else {

                    Intent startNewLeapIntent = new Intent(leaperProfileActivity.this, newLeap.class);
                    startNewLeapIntent.putExtra("leapedPhoneNumber", leaperPhoneNumber);
                    startNewLeapIntent.putExtra("SourceActivity", "1");  // to be used to identify that the extras came from here
                    startActivity(startNewLeapIntent);

                }

            }
        });


        leaperProfileNewMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(Objects.equals(leaperPhoneNumber, myPhoneNumber)){


                } else {


                    String oneCircleSecondUser = leaperPhoneNumber;
                    Intent newChatIntent = new Intent(leaperProfileActivity.this, activity_one_chat.class);
                    newChatIntent.putExtra("oneCircleSecondUser", oneCircleSecondUser);
                    startActivity(newChatIntent);
                    finish();

                }



            }
        });




















    }






    @Override
    public void image_attachment(int from, String filename, Bitmap file, Uri uri) {

        if (uploadflag == 1){
            this.bitmap=file;
            this.file_name=filename;
            profileImageBackground.setImageBitmap(file);

            String path =  Environment.getExternalStorageDirectory() + File.separator + "ImageAttach" + File.separator;
            imageutils.createImage(file,filename,path,false);


            // Get the data from an ImageView as bytes
            profileImageBackground.setDrawingCacheEnabled(true);
            profileImageBackground.buildDrawingCache();
            Bitmap bitmap = profileImageBackground.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            progressDialog.setMessage("Image uploading...");
            progressDialog.show();



            UploadTask uploadTask = mLeaperProfileStorageRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {


                    progressDialog.dismiss();
                    Toast.makeText(leaperProfileActivity.this, "error encountered, retry", Toast.LENGTH_SHORT).show();

                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    Toast.makeText(leaperProfileActivity.this, "Profile banner changed", Toast.LENGTH_SHORT).show();
                    Glide.with(leaperProfileActivity.this).using(new FirebaseImageLoader()).load(mLeaperProfileStorageRef)
                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                            .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                            .error(R.drawable.profile_picture).fitCenter().into(profileImageBackground);
                    progressDialog.dismiss();


                }
            });


        } else if (uploadflag == 2) {

            this.bitmap=file;
            this.file_name=filename;
            profileImage.setImageBitmap(file);

            String path =  Environment.getExternalStorageDirectory() + File.separator + "ImageAttach" + File.separator;
            imageutils.createImage(file,filename,path,false);


            // Get the data from an ImageView as bytes
            profileImage.setDrawingCacheEnabled(true);
            profileImage.buildDrawingCache();
            Bitmap bitmap = profileImage.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            progressDialog.setMessage("Image uploading...");
            progressDialog.show();



            UploadTask uploadTask = mLeaperStorageRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {


                    progressDialog.dismiss();
                    Toast.makeText(leaperProfileActivity.this, "error encountered, retry", Toast.LENGTH_SHORT).show();

                    // Handle unsuccessful uploads
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    Toast.makeText(leaperProfileActivity.this, "Profile picture changed", Toast.LENGTH_SHORT).show();
                    Glide.with(leaperProfileActivity.this).using(new FirebaseImageLoader()).load(mLeaperStorageRef)
                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                            .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                            .error(R.drawable.profile_picture).centerCrop().into(profileImage);
                    progressDialog.dismiss();


                }
            });


        }








    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        super.onActivityResult(requestCode, resultCode, data);
        imageutils.onActivityResult(requestCode, resultCode, data);
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
