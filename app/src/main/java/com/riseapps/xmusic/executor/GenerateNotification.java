package com.riseapps.xmusic.executor;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.riseapps.xmusic.R;
import com.riseapps.xmusic.view.MainActivity;

/**
 * Created by naimish on 12/3/17.
 */

public class GenerateNotification {
    Context context;
    public void generateNotification(Context context,int id) {
        this.context=context;
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 2,intent, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(false);
        mBuilder.setOngoing(true);
        mBuilder.setSmallIcon(R.drawable.ic_play);
                // Set Ticker Messag
        mBuilder.setTicker("Music Playing");
        Notification notification = mBuilder.build();
        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notification_content_layout);

        contentView.setTextViewText(R.id.track, "Song Name");
        contentView.setTextViewText(R.id.artist,"Artist");
        contentView.setImageViewResource(R.id.imageView,R.drawable.empty);
        notification.contentView = contentView;

        RemoteViews expandedView =
                new RemoteViews(context.getPackageName(), R.layout.notification_expand);
        expandedView.setTextViewText(R.id.track, "Song Name");
        expandedView.setTextViewText(R.id.artist,"Artist");
        expandedView.setImageViewResource(R.id.imageView,R.drawable.empty);
        notification.bigContentView = expandedView;

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(id, notification);
    }

    public void cancelNotification(int id){
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(id);
    }


}

