package com.antrixgaming.leap.LeapServices;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.antrixgaming.leap.activity_one_chat;
import com.antrixgaming.leap.leapDetailsActivity;

import java.util.Objects;

public class Open_Notification extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {


        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String SourceActivity = intent.getExtras().getString("SourceActivity");
        String ExtraInfo = intent.getExtras().getString("ExtraInfo");

        manager.cancel(intent.getExtras().getInt("id"));



        /// 1 - ONE ON ONE CHAT
        /// 2 - CIRCLE CHAT
        /// 3 - LEAP
        if (Objects.equals(SourceActivity, "1")){

            Intent newChatIntent = new Intent(context, activity_one_chat.class);
            newChatIntent.putExtra("oneCircleSecondUser", ExtraInfo);
            context.startActivity(newChatIntent);

        } else if (Objects.equals(SourceActivity, "2")){




        } else if (Objects.equals(SourceActivity, "3")){


            Intent openLeapDetails = new Intent(context, leapDetailsActivity.class);
            openLeapDetails.putExtra("leapID", ExtraInfo);
            openLeapDetails.putExtra("sourceActivity", "1");
            context.startActivity(openLeapDetails);
        }



        Toast.makeText(context, "Notification Toasted", Toast.LENGTH_SHORT).show();



    }
}
