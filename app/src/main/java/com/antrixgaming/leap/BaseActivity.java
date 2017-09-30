package com.antrixgaming.leap;

import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.antrixgaming.leap.LeapServices.NetworkBroadcastReceiver;
import com.google.firebase.auth.FirebaseAuth;


public class BaseActivity extends AppCompatActivity{

    NetworkBroadcastReceiver networkChangeReceiver = new NetworkBroadcastReceiver();

    @Override
    protected void onResume() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(networkChangeReceiver, filter);
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(networkChangeReceiver);
        super.onPause();
    }


}
