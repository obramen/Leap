package com.antrixgaming.leap.LeapClasses;


import android.Manifest;
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

public class PermissionRequest extends BaseActivity{

    public static final int RESULT_CODE = 1;






    Context context;
    String PERMISSION;


    public void Request(Context context, String PERMISSION){

        this.context = context;
        this.PERMISSION = PERMISSION;


        int permissionCheck = ContextCompat.checkSelfPermission(context,
                PERMISSION);

        if (ContextCompat.checkSelfPermission(context,
                PERMISSION)
                != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(context, PERMISSION + " not available, request", Toast.LENGTH_SHORT).show();


            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale((BaseActivity) context,
                    PERMISSION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                ActivityCompat.requestPermissions((BaseActivity) context,
                        new String[]{PERMISSION},
                        RESULT_CODE);

            } else {

                // No explanation needed, we can request the permission.

                Toast.makeText(context, PERMISSION + " request starting", Toast.LENGTH_SHORT).show();


                ActivityCompat.requestPermissions((BaseActivity) context,
                        new String[]{PERMISSION},
                        RESULT_CODE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        else {

            Toast.makeText(context, PERMISSION + " already available", Toast.LENGTH_SHORT).show();

            //Intent startContactService = new Intent(context, ContactService.class);
            //context.startService(startContactService);
        }

    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RESULT_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                    Toast.makeText(context, "permission granted", Toast.LENGTH_SHORT).show();

                    //Intent startContactService = new Intent(context, ContactService.class);
                    //context.startService(startContactService);

                } else {

                    Toast.makeText(context, "permission denied", Toast.LENGTH_SHORT).show();

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
