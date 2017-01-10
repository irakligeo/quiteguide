package com.mmgct.quitguide2.views.notifications.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.mmgct.quitguide2.utils.Constants;
import com.mmgct.quitguide2.views.notifications.service.NotificationService;

/**
 * Created by 35527 on 12/28/2015.
 */
public class AlarmReceiver extends BroadcastReceiver {


    private static final String TAG = AlarmReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle args = intent.getExtras();
        Log.d(TAG, "Notification id= "+args.getInt(Constants.NOTIFICATION_ID_KEY, -1));
        if (args.containsKey(Constants.NOTIFICATION_ID_KEY)) {
            Log.d(TAG, "Received alarm broadcast");
            int notificationId = args.getInt(Constants.NOTIFICATION_ID_KEY);
            Intent notificationService = new Intent(context, NotificationService.class);
            notificationService.putExtra(Constants.NOTIFICATION_ID_KEY, notificationId);
            context.startService(notificationService); // Start notification service
        }
    }
}
