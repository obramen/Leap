package com.antrixgaming.leap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.FirebaseDatabase;


public class splashScreen extends Activity {

    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_splash_screen);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        FirebaseDatabase.getInstance().getReference().keepSynced(true);




        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {


                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    // User is signed in

                    Intent openLeapIntent = new Intent(splashScreen.this, Leap.class);
                    //code status is used to tell the Leap activity where the extras is coming from.
                    // the number "0" is used to signal that the intent is coming from splash screen activity.
                    // Hence it comes with no country code attached to it
                    openLeapIntent.putExtra("countryCodeStatus", "0");
                    openLeapIntent.putExtra("countryCode", "0");

                    splashScreen.this.startActivity(openLeapIntent);
                    splashScreen.this.finish();


                } else {
                    // No user is signed in
                    Intent openRegisterLoginIntent = new Intent(splashScreen.this, registerLogin.class);
                    splashScreen.this.startActivity(openRegisterLoginIntent);
                    FirebaseAuth.getInstance().signOut();
                    splashScreen.this.finish();
                }







                /*




                //Check if user is already logged in
                //if true, go to app
                //if false go to login / execute logout as well on auth instance

                FirebaseAuth mAuth;
                mAuth = FirebaseAuth.getInstance();

                if(mAuth.getCurrentUser() == null) {
                    // if there's no user, go to login

                    Intent openRegisterLoginIntent = new Intent(splashScreen.this, registerLogin.class);
                    splashScreen.this.startActivity(openRegisterLoginIntent);
                    splashScreen.this.finish();

                } else {


                    //if there's a user, check if his token is valid

                    mAuth.getCurrentUser().getToken(true)
                            .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                public void onComplete(@NonNull Task<GetTokenResult> task) {
                                    if (task.isSuccessful()) {

                                        //TODO add intent extras for selected country code country code picker
                                                                                //if token is valid open main app
                                        Intent openLeapIntent = new Intent(splashScreen.this, Leap.class);
                                        //code status is used to tell the Leap activity where the extras is coming from.
                                        // the number "0" is used to signal that the intent is coming from splash screen activity.
                                        // Hence it comes with no country code attached to it
                                        openLeapIntent.putExtra("countryCodeStatus", "0");
                                        openLeapIntent.putExtra("countryCode", "0");

                                        splashScreen.this.startActivity(openLeapIntent);
                                        splashScreen.this.finish();


                                    } else {

                                        //if token is invalid or expired, open login
                                        Intent openRegisterLoginIntent = new Intent(splashScreen.this, registerLogin.class);
                                        splashScreen.this.startActivity(openRegisterLoginIntent);
                                        FirebaseAuth.getInstance().signOut();
                                        splashScreen.this.finish();



                                    }
                                }

                            });



                }

                */







            }





        }, SPLASH_DISPLAY_LENGTH);
    }


}



