package com.example.eatathome.Rider.Helper;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import com.example.eatathome.R;

public class NotificationHelperRider extends ContextWrapper {

    private static final String EatatHome_ID = "com.example.eatathome.Rider";
    private static final String EatatHome_Name = "Rider";

    private NotificationManager manager;

    public NotificationHelperRider(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) //only working this if api is 26 or higher
            createChannel();
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel iDeliveryChannel = new NotificationChannel(EatatHome_ID,
                EatatHome_Name,
                NotificationManager.IMPORTANCE_DEFAULT);
        iDeliveryChannel.enableLights(false);
        iDeliveryChannel.enableVibration(true);
        iDeliveryChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(iDeliveryChannel);
    }

    public NotificationManager getManager() {
        if(manager == null)
            manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public android.app.Notification.Builder getiDeliveryChannelNotification
            (String title, String body, PendingIntent contentIntent, Uri soundUri)
    {
        return new android.app.Notification.Builder(getApplicationContext(), EatatHome_ID)
                .setContentIntent(contentIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_local_shipping_black_24dp)
                .setSound(soundUri)
                .setAutoCancel(false);
    }
}
