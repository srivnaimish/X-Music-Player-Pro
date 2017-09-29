package com.riseapps.xmusic.executor;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.riseapps.xmusic.R;
import com.riseapps.xmusic.view.Activity.MainActivity;

/**
 * Created by naimish on 25/9/17.
 */

public class NotificationUtils extends ContextWrapper {

    private NotificationManager mManager;
    public static final String ANDROID_CHANNEL_ID = "Music";
    public static final String ANDROID_CHANNEL_NAME = "Music Playing Notification";

    public NotificationUtils(Context base) {
        super(base);
        createChannels();
    }

    public void createChannels() {

        // create android channel
        NotificationChannel androidChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            androidChannel = new NotificationChannel(ANDROID_CHANNEL_ID,
                    ANDROID_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            androidChannel.enableLights(true);
            androidChannel.setLightColor(Color.RED);
            androidChannel.setSound(null,null);
            androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            getManager().createNotificationChannel(androidChannel);
        }

    }

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }
    public NotificationCompat.Builder getChannelNotification(PendingIntent pendingIntent, int status, RemoteViews contentView, RemoteViews expandedView, String title, String body) {
        NotificationCompat.Builder mBuilder= new NotificationCompat.Builder(getApplicationContext(),ANDROID_CHANNEL_ID);
                mBuilder.setContentIntent(pendingIntent);
                mBuilder.setContentTitle(title);
                mBuilder.setContentText(body);
                mBuilder.setSmallIcon(R.drawable.ic_notification);
                mBuilder.setAutoCancel(false);
                mBuilder.setChannelId(ANDROID_CHANNEL_ID);
        if (status == 0)
            mBuilder.setOngoing(false);
        else
            mBuilder.setOngoing(true);

                mBuilder.setCustomContentView(contentView);
                mBuilder.setCustomBigContentView(expandedView);

        return mBuilder;
    }

}