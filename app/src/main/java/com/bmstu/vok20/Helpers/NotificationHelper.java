package com.bmstu.vok20.Helpers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.bmstu.vok20.MainActivity;
import com.bmstu.vok20.R;

/**
 * Created by qwerty on 15.12.16.
 */

public class NotificationHelper {

    private Context context;
    private int NotificationID;
    private String message;

    public NotificationHelper(Context context, String message) {
        this.context = context;
        this.message = message;
    }

    public void sendNotificationNewMsg() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context.getApplicationContext());
        mBuilder.setSmallIcon(R.drawable.ic_ab_app);
        mBuilder.setContentTitle("You received a new message!");
        mBuilder.setContentText(message);

        Intent resultIntent = new Intent(context.getApplicationContext(), MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context.getApplicationContext());
        stackBuilder.addParentStack(MainActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }

}
