package com.antrixgaming.leap.LeapClasses;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;

import com.antrixgaming.leap.BaseActivity;
import com.antrixgaming.leap.Leap;
import com.antrixgaming.leap.R;

import static android.content.Context.NOTIFICATION_SERVICE;


public class LeapNotify extends BaseActivity{


    private NotificationCompat.Builder builder;
    private NotificationManager notificationManager;
    private int notification_id;
    private RemoteViews remoteViews;
    private Context context;
    String SourceActivity;
    String ExtraInfo;
    String NotifyTitle;
    String NotifyMessage;
    View button;



    public void notify (String SourceActivity, String NotifyTitle, String NotifyMessage, String ExtraInfo){
        context = this;
        this.SourceActivity = SourceActivity;
        this.NotifyTitle = NotifyTitle;
        this.NotifyMessage = NotifyMessage;
        this.ExtraInfo = ExtraInfo;





        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        remoteViews = new RemoteViews(getPackageName(), R.layout.custom_notification);

        remoteViews.setImageViewResource(R.id.notificationIcon, R.mipmap.ic_leap);
        remoteViews.setTextViewText(R.id.notificationTitle, NotifyTitle);
        remoteViews.setTextViewText(R.id.notificationMessage, NotifyMessage);


        notification_id = (int) System.currentTimeMillis();
        Intent openNotification = new Intent("Open_Notification");
        openNotification.putExtra("id", notification_id);
        openNotification.putExtra("SourceActivity", SourceActivity);
        openNotification.putExtra("ExtraInfo", ExtraInfo);


        PendingIntent p_openNotification = PendingIntent.getBroadcast(context, 123, openNotification, 0);
        remoteViews.setOnClickPendingIntent(R.id.notificationButton, p_openNotification);




        Intent notification_intent = new Intent(context, Leap.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notification_intent, 0);

        builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_leap)
                .setAutoCancel(true)
                .setCustomBigContentView(remoteViews)
                .setContentIntent(pendingIntent);

        notificationManager.notify(notification_id, builder.build());





    }

    public LeapNotify(){

    }


    public void notifyClick(View button){

        this.button = button;

        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent notification_intent = new Intent(context, Leap.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notification_intent, 0);

                builder = new NotificationCompat.Builder(context);
                builder.setSmallIcon(R.mipmap.ic_leap)
                        .setAutoCancel(true)
                        .setCustomBigContentView(remoteViews)
                        .setContentIntent(pendingIntent);

                notificationManager.notify(notification_id, builder.build());
            }
        });
    }





}
