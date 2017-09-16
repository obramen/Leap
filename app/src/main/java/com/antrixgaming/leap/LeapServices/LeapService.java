package com.antrixgaming.leap.LeapServices;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;


public class LeapService extends Service {

    final class leapServiceClass implements Runnable{

        int serviceID;
        leapServiceClass(int serviceID){

            this.serviceID = serviceID;
        }

        @Override
        public void run() {

            int i = 0;
            synchronized (this)
            {
                while (i<10){
                    try{

                        wait(1500);
                        i++;
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
                stopSelf(serviceID);

            }
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        Toast.makeText(this, "Contacts refreshing", Toast.LENGTH_LONG).show();
        Thread thread = new Thread(new leapServiceClass(startId));
        thread.start();
        return  START_STICKY;


    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Toast.makeText(this, "Contacts refreshed", Toast.LENGTH_LONG).show();


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {


        return null;
    }
}
