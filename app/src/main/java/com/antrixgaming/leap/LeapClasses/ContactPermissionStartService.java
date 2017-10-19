package com.antrixgaming.leap.LeapClasses;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.antrixgaming.leap.BaseActivity;
import com.antrixgaming.leap.LeapServices.ContactService;

public class ContactPermissionStartService extends BaseActivity{

    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    Context context;


    public void ContactPermissionStartService(Context context){

        this.context = context;

    int permissionCheck = ContextCompat.checkSelfPermission(context,
        android.Manifest.permission.READ_CONTACTS);

        if (ContextCompat.checkSelfPermission(context,
        android.Manifest.permission.READ_CONTACTS)
        != PackageManager.PERMISSION_GRANTED) {

        //Toast.makeText(context, "permission not available, request", Toast.LENGTH_SHORT).show();


        // Should we show an explanation?
        if (ActivityCompat.shouldShowRequestPermissionRationale((BaseActivity) context,
        android.Manifest.permission.READ_CONTACTS)) {

        // Show an explanation to the user *asynchronously* -- don't block
        // this thread waiting for the user's response! After the user
        // sees the explanation, try again to request the permission.

        ActivityCompat.requestPermissions((BaseActivity) context,
        new String[]{android.Manifest.permission.READ_CONTACTS},
        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

        } else {

        // No explanation needed, we can request the permission.

        //Toast.makeText(context, "permission request starting", Toast.LENGTH_SHORT).show();


        ActivityCompat.requestPermissions((BaseActivity) context,
        new String[]{android.Manifest.permission.READ_CONTACTS},
        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
        // app-defined int constant. The callback method gets the
        // result of the request.
        }
        }
        else {

            //Toast.makeText(context, "contactt permission already available", Toast.LENGTH_SHORT).show();

            Intent startContactService = new Intent(context, ContactService.class);
            context.startService(startContactService);
        }

    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    //Toast.makeText(context, "permission granted", Toast.LENGTH_SHORT).show();

                    Intent startContactService = new Intent(context, ContactService.class);
                    context.startService(startContactService);

                } else {

                    //Toast.makeText(context, "permission denied", Toast.LENGTH_SHORT).show();

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }






}
