package com.antrixgaming.leap;

import android.*;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.antrixgaming.leap.LeapClasses.ImageUtils;
import com.antrixgaming.leap.LeapClasses.LeapUtilities;
import com.antrixgaming.leap.Models.CircleMember;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.firebase.ui.storage.images.FirebaseImageLoader;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class leaperProfileActivity extends BaseActivity implements ImageUtils.ImageAttachmentListener {

    private String myUID;
    private CircleImageView profileImage;
    private TextView profileLeaperName;

    private StorageReference mStorage;
    private StorageReference mLeaperStorageRef;

    ProgressDialog progressDialog;

    ImageUtils imageutils;

    private Bitmap bitmap;
    private String file_name;

    LeapUtilities leapUtilities;

    DatabaseReference dbRef;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaper_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        String leaperPhoneNumber = bundle.getString("leaperPhoneNumber");

        getSupportActionBar().setTitle(leaperPhoneNumber);
        imageutils = new ImageUtils(this);
        leapUtilities = new LeapUtilities();

        mStorage = FirebaseStorage.getInstance().getReference();
        dbRef = FirebaseDatabase.getInstance().getReference();





        profileImage = (CircleImageView) findViewById(R.id.profileImage);
        profileLeaperName = (TextView) findViewById(R.id.profileLeaperName);
        profileLeaperName.setText(leaperPhoneNumber);

        progressDialog = new ProgressDialog(this);



        mLeaperStorageRef = mStorage.child("leaperProfileImage").child(leaperPhoneNumber).child(leaperPhoneNumber);
        leapUtilities.CircleImageFromFirebase(leaperProfileActivity.this, mLeaperStorageRef, profileImage);






        //loadProfileImageFromFirebase();


        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (imageutils.isDeviceSupportCamera())
                    imageutils.imagepicker(1);
                else imageutils.imagepicker(0);


            }
        });


    }


    private void loadProfileImageFromFirebase(){

        Glide.with(leaperProfileActivity.this).using(new FirebaseImageLoader()).load(mLeaperStorageRef)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .error(R.drawable.profile_picture).centerCrop().into(profileImage);

    }



    @Override
    public void image_attachment(int from, String filename, Bitmap file, Uri uri) {
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
