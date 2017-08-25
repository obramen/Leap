package com.antrixgaming.leap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;


public class splashScreen extends Activity {

    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_splash_screen);

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {

                //Check if user is already logged in
                //if true, go to app
                //if false go to login / execute logout as well on auth instance

                FirebaseAuth mAuth;
                mAuth = FirebaseAuth.getInstance();

                if(mAuth.getCurrentUser() == null) {
                    // if there's no user, go to login

                    Intent openRegisterLoginIntent = new Intent(splashScreen.this, registerLogin.class);
                    splashScreen.this.startActivity(openRegisterLoginIntent);
                    Toast.makeText(splashScreen.this, "Splash Screen to Login with no user", Toast.LENGTH_LONG).show();

                    splashScreen.this.finish();

                } else {

                    //if there's a user, check if his token is valid

                    mAuth.getCurrentUser().getToken(true)
                            .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                public void onComplete(@NonNull Task<GetTokenResult> task) {
                                    if (task.isSuccessful()) {

                                        //if token is valid open main app
                                        Intent openLeapIntent = new Intent(splashScreen.this, Leap.class);
                                        splashScreen.this.startActivity(openLeapIntent);
                                        Toast.makeText(splashScreen.this, "Splash Screen to Leap with user", Toast.LENGTH_LONG).show();
                                        splashScreen.this.finish();


                                    } else {

                                        //if token is invalid or expired, open login
                                        Intent openRegisterLoginIntent = new Intent(splashScreen.this, registerLogin.class);
                                        splashScreen.this.startActivity(openRegisterLoginIntent);
                                        Toast.makeText(splashScreen.this, "Splash Screen to Login with expired user", Toast.LENGTH_LONG).show();
                                        FirebaseAuth.getInstance().signOut();
                                        splashScreen.this.finish();



                                    }
                                }

                            });



                }







            }





        }, SPLASH_DISPLAY_LENGTH);
    }
}



