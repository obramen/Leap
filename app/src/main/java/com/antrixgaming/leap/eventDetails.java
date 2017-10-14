package com.antrixgaming.leap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.antrixgaming.leap.LeapClasses.ImageUtils;
import com.antrixgaming.leap.LeapClasses.LeapUtilities;
import com.antrixgaming.leap.LeapClasses.OnlinePressence;
import com.antrixgaming.leap.Models.CreateEvent;
import com.antrixgaming.leap.Models.EventAttenders;
import com.antrixgaming.leap.Models.EventComment;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Objects;

public class eventDetails extends BaseActivity implements ImageUtils.ImageAttachmentListener {

    String myUID;
    String myPhoneNumber;
    DatabaseReference dbRef;
    DatabaseReference eventsDbRef;
    DatabaseReference commentsDbRef;
    DatabaseReference attendersDbRef;
    StorageReference mStorage;
    StorageReference mEventStorage;

    ImageView eventImage;
    TextView eventDay;
    TextView eventMonth;
    TextView eventTitle;
    TextView eventDescription;
    TextView eventPeriod;
    TextView eventByTextView;
    TextView eventLocation;

    TextView attendTextView;
    ImageView leapInButton;

    Button commentButton;
    EditText commentEditText;


    LeapUtilities leapUtilities;

    ImageUtils imageutils;

    FirebaseListAdapter<EventComment> adapter;
    FirebaseListAdapter<EventAttenders> adapter2;



    private Bitmap bitmap;
    private String file_name;

    ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        final String eventID = bundle.getString("eventID");

        dbRef = FirebaseDatabase.getInstance().getReference();
        eventsDbRef = dbRef.child("AnTrixEvents");
        commentsDbRef = dbRef.child("eventComments").child(eventID);
        attendersDbRef = dbRef.child("eventAttenders").child(eventID);
        myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        myPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        leapUtilities = new LeapUtilities();

        mStorage = FirebaseStorage.getInstance().getReference();

        imageutils = new ImageUtils(this);

        progressDialog = new ProgressDialog(this);


        eventImage = (ImageView) findViewById(R.id.eventImage);
        eventDay = (TextView) findViewById(R.id.eventDay);
        eventMonth = (TextView) findViewById(R.id.eventMonth);
        eventTitle = (TextView) findViewById(R.id.eventTitle);
        eventDescription = (TextView) findViewById(R.id.eventDescription);
        eventPeriod = (TextView) findViewById(R.id.eventPeriod);
        eventByTextView = (TextView) findViewById(R.id.eventByTextView);
        eventLocation = (TextView) findViewById(R.id.eventLocation);

        commentButton = (Button) findViewById(R.id.commentButton);
        commentEditText = (EditText) findViewById(R.id.commentEditText);

        attendTextView = (TextView) findViewById(R.id.attendTextView);
        leapInButton = (ImageView) findViewById(R.id.leapInButton);

        final TextView displayedLeaperName = (TextView) findViewById(R.id.displayedLeaperName);


        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(commentEditText.getText().toString())) {
                    //mVerificationField.setError("Cannot be empty.");
                    commentEditText.setHint("Enter comment");
                    commentEditText.setHintTextColor(getResources().getColor(R.color.cherry));
                    return;
                }

                String key = dbRef.child("eventComments").child(eventID).push().getKey();
                dbRef.child("eventComments").child(eventID).child(key).setValue(new EventComment(eventID, key, myPhoneNumber, commentEditText.getText().toString().trim()));

                commentEditText.setText("");
                commentEditText.setHint("Comment...");

            }
        });


        leapInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (Objects.equals(attendTextView.getText().toString(), "Leap in")){
                    dbRef.child("eventAttenders").child(eventID).child(myPhoneNumber).setValue(new EventAttenders(eventID, myPhoneNumber));

                } else if (Objects.equals(attendTextView.getText().toString(), "Leap out")){

                    dbRef.child("eventAttenders").child(eventID).child(myPhoneNumber).removeValue();


                    attendTextView.setText("Leap in");
                    leapInButton.setImageResource(R.drawable.done_check_mark);
                    leapInButton.setBackground(getResources().getDrawable(R.drawable.green_button));


                }



            }
        });


        eventsDbRef.child(eventID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                String mEventTitle = dataSnapshot.child("eventTitle").getValue().toString();
                String mEventDescription = dataSnapshot.child("eventDescription").getValue().toString();
                final String eventBy = dataSnapshot.child("eventBy").getValue().toString();
                long eventStartDate = Long.parseLong(dataSnapshot.child("eventStartDate").getValue().toString());
                long eventEndDate = Long.parseLong(dataSnapshot.child("eventEndDate").getValue().toString());
                String mEventLocation = dataSnapshot.child("eventLocation").getValue().toString();


                getSupportActionBar().setTitle(eventTitle.getText().toString());

                CharSequence mEventDay = android.text.format.DateFormat.format("dd", eventStartDate);
                CharSequence mEventMonth = android.text.format.DateFormat.format("MMM", eventStartDate);

                eventDay.setText(mEventDay);
                eventMonth.setText(mEventMonth);
                eventTitle.setText(mEventTitle);

                eventByTextView.setText(eventBy);
                eventLocation.setText(mEventLocation);

                eventDescription.setText(mEventDescription);


                CharSequence periodStart = android.text.format.DateFormat.format("dd-MMMM", eventStartDate);
                CharSequence periodEnd = android.text.format.DateFormat.format("dd-MMMM", eventEndDate);

                eventPeriod.setText(periodStart + " to " + periodEnd);


                mEventStorage = mStorage.child("events").child(eventBy).child(eventID).child(eventID);
                leapUtilities.SquareImageFromFirebase(eventDetails.this, mEventStorage, eventImage);


                if (Objects.equals(myPhoneNumber, eventBy)) {
                    eventImage.setEnabled(true);
                } else {
                    eventImage.setEnabled(false);
                }


                eventImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        leapUtilities.LeapImageUpload(eventDetails.this, eventImage, mEventStorage);

                    }
                });


                eventByTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(eventDetails.this, leaperProfileActivity.class);
                        intent.putExtra("leaperPhoneNumber", eventByTextView.getText().toString());
                        startActivity(intent);
                    }
                });


                displayedLeaperName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(eventDetails.this, leaperProfileActivity.class);
                        intent.putExtra("leaperPhoneNumber", eventByTextView.getText().toString());
                        startActivity(intent);


                    }
                });




                /////////////////////////////////////////////////////////////////////
                ///////////////////////////////////////////
                //////// GETTING AND SETTING NAMES IN PLACE OF PHONE NUMBER

                ///////////////////////////////////////
                //////////////////   STARTING    ///////////////////////////////


                //// CHECK MY CONTACT LIST IF THIS PERSON IS A CONTACT
                dbRef.child("ContactList").child(myUID).child("leapSortedContacts").child(eventBy)
                        .addValueEventListener(new ValueEventListener() {////////
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (dataSnapshot.child("name").getValue() == null || dataSnapshot.child("name")
                                        .getValue() == "") {///// IF THEY ARE NOT A CONTACT OR THE VALUE IS EMPTY


                                    ///// CHECK THE USERS PROFILES TO SEE IF THEY HAVE AN ENTRY THERE
                                    dbRef.child("userprofiles").child(eventBy).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.child("name").getValue() == null || Objects.equals(dataSnapshot.child("name")
                                                    .getValue().toString(), "")) {///IF THEY DON'T HAVE AN ENTRY USE THEIR PHONE NUMBER

                                                /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////
                                                displayedLeaperName.setText(eventBy);
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

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        Query query = commentsDbRef.orderByChild("commentDate");

        adapter = new FirebaseListAdapter<EventComment>(eventDetails.this, EventComment.class, R.layout.event_comment_list, query) {
            @Override
            protected void populateView(View v, final EventComment model, int position) {

                TextView commentBy = (TextView) v.findViewById(R.id.commentBy);
                TextView commentDate = (TextView) v.findViewById(R.id.commentDate);
                TextView comment = (TextView) v.findViewById(R.id.comment);
                TextView deleteComment = (TextView) v.findViewById(R.id.deleteComment);

                commentBy.setText(model.getCommentBy());
                commentDate.setText(DateFormat.format("dd/MMM/yyyy HH:mm", model.getCommentDate()));
                comment.setText(model.getComment());


                if (Objects.equals(commentBy.getText().toString(), myPhoneNumber)) {
                    deleteComment.setVisibility(v.VISIBLE);
                } else {
                    deleteComment.setVisibility(v.GONE);

                }

                deleteComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        commentsDbRef.child(model.getCommentID()).removeValue();

                    }
                });


            }
        };

        ListView listView = (ListView) findViewById(R.id.list_of_EventComments);
        listView.setAdapter(adapter);








        Query query2 = attendersDbRef.orderByChild("leapInDate");

        adapter2 = new FirebaseListAdapter<EventAttenders>(eventDetails.this, EventAttenders.class, R.layout.event_attenders_list, query2) {
            @Override
            protected void populateView(View v, final EventAttenders model, int position) {

                final TextView attenderName = (TextView) v.findViewById(R.id.attenderName);

                attenderName.setText(model.getAttenderName());

                if (Objects.equals(model.getAttenderName(), myPhoneNumber)) {

                    attendTextView.setText("Leap out");
                    leapInButton.setImageResource(R.drawable.ic_action_cancel);
                    leapInButton.setBackground(getResources().getDrawable(R.drawable.redbutton));

                } else {


                }





                /////////////////////////////////////////////////////////////////////
                ///////////////////////////////////////////
                //////// GETTING AND SETTING NAMES IN PLACE OF PHONE NUMBER

                ///////////////////////////////////////
                //////////////////   STARTING    ///////////////////////////////


                //// CHECK MY CONTACT LIST IF THIS PERSON IS A CONTACT
                dbRef.child("ContactList").child(myUID).child("leapSortedContacts").child(model.getAttenderName())
                        .addValueEventListener(new ValueEventListener() {////////
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (dataSnapshot.child("name").getValue() == null || dataSnapshot.child("name")
                                        .getValue() == "") {///// IF THEY ARE NOT A CONTACT OR THE VALUE IS EMPTY


                                    ///// CHECK THE USERS PROFILES TO SEE IF THEY HAVE AN ENTRY THERE
                                    dbRef.child("userprofiles").child(model.getAttenderName()).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.child("name").getValue() == null || Objects.equals(dataSnapshot.child("name")
                                                    .getValue().toString(), "")) {///IF THEY DON'T HAVE AN ENTRY USE THEIR PHONE NUMBER

                                                /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////
                                                attenderName.setText(model.getAttenderName());
                                                /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////


                                            } else { //// IF THEY HAVE AN ENTRY USE THEIR ENTERED NAME

                                                String myName = dataSnapshot.child("name").getValue().toString();

                                                /////////////////////// ************* KEEP THIS HERE ************ ///////////////////////////
                                                attenderName.setText("~ " + myName);
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
                                    attenderName.setText(mName);
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

        ListView listView2 = (ListView) findViewById(R.id.list_of_EventAttenders);
        listView2.setAdapter(adapter2);






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
    public void image_attachment(int from, String filename, Bitmap file, Uri uri) {
        this.bitmap=file;
        this.file_name=filename;
        eventImage.setImageBitmap(file);

        String path =  Environment.getExternalStorageDirectory() + File.separator + "ImageAttach" + File.separator;
        imageutils.createImage(file,filename,path,false);


        // Get the data from an ImageView as bytes
        eventImage.setDrawingCacheEnabled(true);
        eventImage.buildDrawingCache();
        Bitmap bitmap = eventImage.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        progressDialog.setMessage("Image uploading...");
        progressDialog.show();



        UploadTask uploadTask = mEventStorage.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {


                progressDialog.dismiss();
                Toast.makeText(eventDetails.this, "error encountered, retry", Toast.LENGTH_SHORT).show();

                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                Toast.makeText(eventDetails.this, "Profile picture changed", Toast.LENGTH_SHORT).show();
                Glide.with(eventDetails.this).using(new FirebaseImageLoader()).load(mEventStorage)
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                        .error(R.drawable.antrixlogo1).fitCenter().into(eventImage);
                progressDialog.dismiss();


            }
        });


    }


    //back when return arrow is clicked
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

}
