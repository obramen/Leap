package com.antrixgaming.leap.LeapClasses;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.antrixgaming.leap.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class LeapUtilities {

    private Context context;
    private StorageReference storageReference;
    private CircleImageView circleImageView;
    private ImageView imageView;
    private String returnedValue;
    private String childOne;
    private String childTwo;
    private String childThree;
    private String childFour;
    private String childFive;
    private String returnedChildValue;
    private DatabaseReference databaseReference;





    public void CircleImageFromFirebase(Context context, StorageReference storageReference, CircleImageView circleImageView){

        this.context = context;
        this.storageReference = storageReference;
        this.circleImageView = circleImageView;





        Glide.with(context).using(new FirebaseImageLoader()).load(storageReference)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .error(R.drawable.profile_picture).centerCrop().into(circleImageView);


/*
        Picasso.with(context).load(storageReference.getDownloadUrl().toString()).networkPolicy(NetworkPolicy.OFFLINE)
                .error(R.drawable.profile_picture)
                //.placeholder(R.drawable.profile_picture)
                //.centerCrop()

                .into(circleImageView);

*/

    }




    public void SquareImageFromFirebase(Context context, StorageReference storageReference, ImageView imageView){

        this.context =context;
        this.storageReference = storageReference;
        this.imageView = imageView;

        Glide.with(context).using(new FirebaseImageLoader()).load(storageReference)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                .error(R.drawable.profile_picture).centerCrop().into(imageView);

    }


    public Drawable buildCounterDrawable(long count, int backgroundImageId) {
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





    public static void justifyListViewHeightBasedOnChildren (ListView listView) {

        ListAdapter adapter = listView.getAdapter();

        if (adapter == null) {
            return;
        }
        ViewGroup vg = listView;
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View listItem = adapter.getView(i, null, vg);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams par = listView.getLayoutParams();
        par.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(par);
        listView.requestLayout();
    }











    public void ReturnFirebaseValue(DatabaseReference databaseReference){

        this.databaseReference = databaseReference;

        databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                returnedValue = dataSnapshot.getValue().toString();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public String returnedValue(){return returnedValue;}







    public void ReturnFirebaseChildValue(DatabaseReference databaseReference, final String childOne) {

        this.databaseReference = databaseReference;
        this.childOne = childOne;


        databaseReference.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                returnedChildValue = dataSnapshot.child(childOne).getValue().toString();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void ReturnFirebaseChildValue(DatabaseReference databaseReference, final String childOne, final String childTwo) {

        this.databaseReference = databaseReference;
        this.childOne = childOne;
        this.childTwo = childTwo;




        databaseReference.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                returnedChildValue = dataSnapshot.child(childOne).child(childTwo).getValue().toString();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    public String getReturnedChildValue() {

        databaseReference.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                returnedChildValue = dataSnapshot.child(childOne).child(childTwo).getValue().toString();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        return returnedChildValue;
    }

    public void ReturnFirebaseChildValue(String childOne, String childTwo, String childThree) {

        this.childOne = childOne;
        this.childTwo = childTwo;
        this.childThree = childThree;

        this.databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child(childOne).child(childTwo).addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                returnedChildValue = dataSnapshot.child("uid").getValue().toString();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    public void ReturnFirebaseChildValue(DatabaseReference databaseReference, final String childOne, final String childTwo,
                                         final String childThree, final String childFour) {

        this.databaseReference = databaseReference;
        this.childOne = childOne;
        this.childTwo = childTwo;
        this.childThree = childThree;
        this.childFour = childFour;


        databaseReference.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                returnedChildValue = dataSnapshot.child(childOne).child(childTwo).child(childThree).child(childFour).getValue().toString();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void ReturnFirebaseChildValue(DatabaseReference databaseReference, final String childOne, final String childTwo,
                                         final String childThree, final String childFour, final String childFive) {

        this.databaseReference = databaseReference;
        this.childOne = childOne;
        this.childTwo = childTwo;
        this.childThree = childThree;
        this.childFour = childFour;
        this.childFive = childFive;


        databaseReference.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                returnedChildValue = dataSnapshot.child(childOne).child(childTwo).child(childThree).child(childFour).child(childFive).getValue().toString();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }






    public void setReturnedChildValue(String returnedChildValue){this.returnedChildValue = returnedChildValue;}










}
