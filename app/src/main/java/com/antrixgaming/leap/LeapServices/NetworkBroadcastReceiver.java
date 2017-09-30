package com.antrixgaming.leap.LeapServices;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.antrixgaming.leap.Leap;
import com.antrixgaming.leap.registerLogin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;


public class NetworkBroadcastReceiver extends BroadcastReceiver {

    Snackbar snackbar;

    @Override
    public void onReceive(final  Context context, final Intent intent) {




        boolean status = checkInternetConnection(context);
        Log.d("NetworkReceiver", String.valueOf(status));
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
            Log.d("NetworkReceiver", "connectivity change");



            if(!status ){

                View rootView = ((AppCompatActivity) context).getWindow().getDecorView().findViewById(android.R.id.content);

                snackbar = Snackbar.make(rootView, "No internet connection.", Snackbar.LENGTH_INDEFINITE);
                snackbar.show();


            }else{


                if(snackbar != null) snackbar.dismiss();


                FirebaseAuth mAuth;

                mAuth = FirebaseAuth.getInstance();
                mAuth.getCurrentUser().getToken(true)
                        .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                            public void onComplete(@NonNull Task<GetTokenResult> task) {
                                if (task.isSuccessful()) {
                                    //String idToken = task.getResult().getToken();
                                    // Send token to your backend via HTTPS
                                    // ...

                                } else {

                                    // TODO PUT CHECK FOR INTERNET AVAILABILITY
                                    // TODO APPLY ONLINE STATUS HERE

                                    // Handle error -> task.getException();
                                    FirebaseAuth.getInstance().signOut();

                                    Intent logoutIntent = new Intent(context, registerLogin.class);
                                    logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    context.startActivity(logoutIntent);




                                }
                            }

                        });






            }

        }else  Log.d("NetworkReceiver", "NOT Connectivity change");

    }

    public static boolean checkInternetConnection(Context context) {


        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // ARE WE CONNECTED TO THE NET
        return conMgr.getActiveNetworkInfo() != null
                && conMgr.getActiveNetworkInfo().isAvailable()
                && conMgr.getActiveNetworkInfo().isConnected();

    }

    public boolean isInternetAvailable() {
        try {
            final InetAddress address = InetAddress.getByName("www.google.com");
            return !address.equals("");
        } catch (UnknownHostException e) {
            // Log error
        }
        return false;
    }


    public boolean isConnected() throws InterruptedException, IOException
    {
        String command = "ping -c 1 google.com";
        return (Runtime.getRuntime().exec (command).waitFor() == 0);
    }

}