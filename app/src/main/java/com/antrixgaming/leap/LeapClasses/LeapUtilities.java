package com.antrixgaming.leap.LeapClasses;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.antrixgaming.leap.R;
import com.antrixgaming.leap.events;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.R.attr.data;

public class LeapUtilities extends AppCompatActivity implements ImageUtils.ImageAttachmentListener {

    private Context context;
    private StorageReference storageReference;
    private CircleImageView circleImageView;
    private ImageView imageView;
    private DatabaseReference databaseReference;




    private Bitmap bitmap;
    private String file_name;

    ProgressDialog progressDialog;



    ImageUtils imageutils;

    private String timestamp;









    public void CircleImageFromFirebase(Context context, StorageReference storageReference, CircleImageView circleImageView, String timestamp){

        this.context = context;
        this.storageReference = storageReference;
        this.circleImageView = circleImageView;
        //this.timestamp = timestamp;





        Glide.with(context).using(new FirebaseImageLoader()).load(storageReference)
                .skipMemoryCache(true)
                //.diskCacheStrategy(DiskCacheStrategy.RESULT)
                .signature(new StringSignature(String.valueOf(timestamp)))
                .error(R.drawable.ic_camera)
                .centerCrop()
                .into(circleImageView);


/*
        Picasso.with(context).load(storageReference.getDownloadUrl().toString()).networkPolicy(NetworkPolicy.OFFLINE)
                .error(R.drawable.profile_picture)
                //.placeholder(R.drawable.profile_picture)
                //.centerCrop()

                .into(circleImageView);

*/

    }




    public void SquareImageFromFirebase(Context context, StorageReference storageReference, ImageView imageView, String timestampB){

        this.context =context;
        this.storageReference = storageReference;
        this.imageView = imageView;

        Glide.with(context).using(new FirebaseImageLoader()).load(storageReference)
                .skipMemoryCache(true)
                //.diskCacheStrategy(DiskCacheStrategy.RESULT)
                .signature(new StringSignature(String.valueOf(timestampB)))
                .error(R.drawable.ic_camera)
                .fitCenter()
                .into(imageView);

    }


    public Drawable buildCounterDrawable(long count, int backgroundImageId, Context context) {
        this.context = context;

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.notification_layout, null);
        view.setBackgroundResource(backgroundImageId);

        if (count == 0) {
            View counterTextPanel = view.findViewById(R.id.counterValuePanel);
            counterTextPanel.setVisibility(View.GONE);
        } else {
            TextView textView = (TextView) view.findViewById(R.id.count);
            textView.setText("" + count);
        }

        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return new BitmapDrawable(context.getResources(), bitmap);
    }












    public void LeapImageUpload(final Context context, final ImageView imageView, final StorageReference storageReference){


        progressDialog = new ProgressDialog(context);

        this.imageView = imageView;
        this.storageReference = storageReference;

        imageutils = new ImageUtils((AppCompatActivity) context);


        if (imageutils.isDeviceSupportCamera())
            imageutils.imagepicker(1);
        else imageutils.imagepicker(0);


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
    public void image_attachment(final int from, String filename, Bitmap file, Uri uri) {


        this.bitmap=file;
        this.file_name=filename;
        imageView.setImageBitmap(file);

        String path =  Environment.getExternalStorageDirectory() + File.separator + "ImageAttach" + File.separator;
        imageutils.createImage(file,filename,path,false);


        // Get the data from an ImageView as bytes
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = imageView.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        progressDialog.setMessage("Image uploading...");
        progressDialog.show();



        UploadTask uploadTask = storageReference.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {


                progressDialog.dismiss();
                Toast.makeText(context, "error encountered, retry", Toast.LENGTH_SHORT).show();

                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                Toast.makeText(context, "Profile picture changed", Toast.LENGTH_SHORT).show();
                Glide.with(context).using(new FirebaseImageLoader()).load(storageReference)
                        .diskCacheStrategy(DiskCacheStrategy.RESULT)
                        .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                        .error(R.drawable.antrixlogo1).fitCenter().into(imageView);
                progressDialog.dismiss();


            }
        });


    }








}
