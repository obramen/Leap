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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.antrixgaming.leap.LeapClasses.ImageUtils;
import com.antrixgaming.leap.LeapClasses.LeapUtilities;
import com.antrixgaming.leap.LeapClasses.OnlinePressence;
import com.antrixgaming.leap.Models.CreateEvent;
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

    Button commentButton;
    EditText commentEditText;


    LeapUtilities leapUtilities;

    ImageUtils imageutils;

    FirebaseListAdapter<EventComment> adapter;



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
        myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        myPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        leapUtilities = new LeapUtilities();

        mStorage = FirebaseStorage.getInstance().getReference();

        imageutils = new ImageUtils(this);

        progressDialog = new ProgressDialog(this);


        eventImage = (ImageView) findViewById(R.id.eventImage);
        eventDay = (TextView)findViewById(R.id.eventDay);
        eventMonth = (TextView)findViewById(R.id.eventMonth);
        eventTitle = (TextView)findViewById(R.id.eventTitle);
        eventDescription = (TextView)findViewById(R.id.eventDescription);
        eventPeriod = (TextView)findViewById(R.id.eventPeriod);
        eventByTextView = (TextView)findViewById(R.id.eventByTextView);
        eventLocation = (TextView)findViewById(R.id.eventLocation);

        commentButton = (Button) findViewById(R.id.commentButton);
        commentEditText = (EditText)findViewById(R.id.commentEditText);




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
                dbRef.child("eventComments").child(eventID).child(key) .setValue(new EventComment(eventID, key, myPhoneNumber, commentEditText.getText().toString().trim()));

                commentEditText.setText("");
                commentEditText.setHint("Comment...");

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



                if (Objects.equals(myPhoneNumber, eventBy)){
                    eventImage.setEnabled(true);
                }
                else{
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

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });













        commentsDbRef = dbRef.child("eventComments").child(eventID);
        Query query = commentsDbRef.orderByChild("commentDate");

        adapter = new FirebaseListAdapter<EventComment>(eventDetails.this, EventComment.class, R.layout.event_comment_list, query) {
            @Override
            protected void populateView(View v, final EventComment model, int position) {

                TextView commentBy = (TextView) v.findViewById(R.id.commentBy);
                TextView commentDate = (TextView) v.findViewById(R.id.commentDate);
                TextView comment = (TextView)v.findViewById(R.id.comment);
                TextView deleteComment = (TextView)v.findViewById(R.id.deleteComment);

                commentBy.setText(model.getCommentBy());
                commentDate.setText(DateFormat.format("dd/MMM/yyyy HH:MM", model.getCommentDate()));
                comment.setText(model.getComment());


                if (Objects.equals(commentBy.getText().toString(), myPhoneNumber)){
                    deleteComment.setVisibility(v.VISIBLE);
                } else{
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
