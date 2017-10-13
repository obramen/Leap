package com.antrixgaming.leap;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.antrixgaming.leap.LeapClasses.ImageUtils;
import com.antrixgaming.leap.LeapClasses.LeapUtilities;
import com.antrixgaming.leap.Models.CreateEvent;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.DateFormat;
import java.text.Format;
import java.util.Objects;

public class events extends BaseActivity implements ImageUtils.ImageAttachmentListener  {

    String myUID;
    String myPhoneNumber;
    DatabaseReference dbRef;
    DatabaseReference eventsDbRef;
    StorageReference mStorage;
    StorageReference mEventStorage;
    FirebaseListAdapter<CreateEvent> adapter;

    ImageView eventImage;
    TextView eventDay;
    TextView eventMonth;
    TextView eventTitle;
    TextView eventDescription;
    TextView eventID;

    LeapUtilities leapUtilities;

    ImageUtils imageutils;



    private Bitmap bitmap;
    private String file_name;

    ProgressDialog progressDialog;

    FloatingActionButton events_fab;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbRef = FirebaseDatabase.getInstance().getReference();
        eventsDbRef = dbRef.child("AnTrixEvents");
        myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        myPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        leapUtilities = new LeapUtilities();

        mStorage = FirebaseStorage.getInstance().getReference();

        imageutils = new ImageUtils(this);

        progressDialog = new ProgressDialog(this);

        events_fab = (FloatingActionButton)findViewById(R.id.events_fab);

        if (Objects.equals(myPhoneNumber, "+233242366623")){
            events_fab.setVisibility(View.VISIBLE);
        }
        else{
            events_fab.setVisibility(View.VISIBLE);
        }



        events_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent openNewEventIntent = new Intent(events.this, new_event.class);
                startActivity(openNewEventIntent);

            }
        });




        adapter = new FirebaseListAdapter<CreateEvent> (this, CreateEvent.class, R.layout.events_list,
                eventsDbRef.orderByChild("eventStartDate")){

            @Override
            protected void populateView(View v, CreateEvent model, int position) {

                eventImage = (ImageView) v.findViewById(R.id.eventImage);
                eventDay = (TextView)v.findViewById(R.id.eventDay);
                eventMonth = (TextView)v.findViewById(R.id.eventMonth);
                eventTitle = (TextView)v.findViewById(R.id.eventTitle);
                eventDescription = (TextView)v.findViewById(R.id.eventDescription);
                eventID = (TextView)v.findViewById(R.id.eventID);

                eventID.setText(model.getEventID());


                if (Objects.equals(myPhoneNumber, "+233242366623")){
                    eventImage.setEnabled(true);
                }
                else{
                    eventImage.setEnabled(false);
                }



                String mEventTitle = model.getEventTitle();
                String mEventDescription = model.getEventDescription();


                CharSequence mEventDay = android.text.format.DateFormat.format("dd", model.getEventStartDate());
                CharSequence mEventMonth = android.text.format.DateFormat.format("MMM", model.getEventStartDate());

                eventDay.setText(mEventDay);
                eventMonth.setText(mEventMonth);
                eventTitle.setText(mEventTitle);



                mEventStorage = mStorage.child("events").child(model.getEventBy()).child(model.getEventID()).child(model.getEventID());
                leapUtilities.SquareImageFromFirebase(events.this, mEventStorage, eventImage);



                if (android.text.format.DateFormat.format("dd-MM-yyyy", model.getEventStartDate()) ==
                        android.text.format.DateFormat.format("dd-MM-yyyy", model.getEventEndDate())){

                    eventDescription.setText(mEventDescription);

                } else {

                    CharSequence mEventEndDay = android.text.format.DateFormat.format("dd", model.getEventStartDate());
                    CharSequence mEventEndMonth = android.text.format.DateFormat.format("MMM", model.getEventStartDate());

                    mEventDescription = mEventDay + " " + mEventMonth + " - " + mEventEndDay + " " + mEventEndMonth + ": " + mEventDescription;

                    eventDescription.setText(mEventDescription);


                }





                eventImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent eventDetailsIntent = new Intent(events.this, eventDetails.class);
                        eventDetailsIntent.putExtra("eventID", eventID.getText().toString());
                        startActivity(eventDetailsIntent);
                    }
                });




            }
        };


        ListView listView = (ListView)findViewById(R.id.list_of_events);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                eventID = (TextView)view.findViewById(R.id.eventID);

                Intent eventDetailsIntent = new Intent(events.this, eventDetails.class);
                eventDetailsIntent.putExtra("eventID", eventID.getText().toString());
                startActivity(eventDetailsIntent);

            }
        });






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
                Toast.makeText(events.this, "error encountered, retry", Toast.LENGTH_SHORT).show();

                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                Toast.makeText(events.this, "Profile picture changed", Toast.LENGTH_SHORT).show();
                Glide.with(events.this).using(new FirebaseImageLoader()).load(mEventStorage)
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
