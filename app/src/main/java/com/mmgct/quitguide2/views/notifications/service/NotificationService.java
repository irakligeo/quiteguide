package com.mmgct.quitguide2.views.notifications.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.SplashActivity;
import com.mmgct.quitguide2.managers.DbManager;
import com.mmgct.quitguide2.models.Notification;
import com.mmgct.quitguide2.models.NotificationHistory;
import com.mmgct.quitguide2.models.RecurringNotification;
import com.mmgct.quitguide2.utils.Common;
import com.mmgct.quitguide2.utils.Constants;

import org.joda.time.DateTime;

/**
 * Created by 35527 on 12/28/2015.
 */
public class NotificationService extends IntentService {

    private static final String TAG = NotificationService.class.getSimpleName();

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public NotificationService(String name) {
        super(name);
    }

    public NotificationService(){
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        int notificationId;

        Log.d(TAG, "Notification service received intent");

        // Get the id of the notification from the Intent
        if (intent.getExtras() != null && intent.getExtras().containsKey(Constants.NOTIFICATION_ID_KEY)) {
            notificationId = intent.getIntExtra(Constants.NOTIFICATION_ID_KEY, -1);
        } else { return; }

        if (DbManager.getInstance() == null)
            DbManager.init(getApplicationContext());

        Notification notification = DbManager.getInstance().getNotificationById(notificationId);
        NotificationHistory notificationHistory = notification.getNotificationHistory();

        /*---------------------------------Recurring Notifications--------------------------------*/

        boolean isUserNotificationActive = false;

        RecurringNotification recNotification = notification.getRecurringNotification();
        if (notificationHistory == null && recNotification != null) {
            if (!(isUserNotificationActive = recNotification.isActive())) {
                Log.i(TAG, "Would have sent recurring notification "+ recNotification.getId() + " but it is disabled");
                return;
            }
            Log.d(TAG, "Building recurring notification...");
            if (recNotification.getGeoTag() == null) { // Daily timed notification
                // Check for a valid time since this should be a recurring time of day notification
                if (notification.getTimeOfDay() == null || !notification.getTimeOfDay().matches("\\d{1,2}:\\d{1,2}")) {
                    return;
                }
                notificationHistory = new NotificationHistory();
                String[] time = notification.getTimeOfDay().split(":");
                notificationHistory.setScheduledDeliveryDate(new DateTime(System.currentTimeMillis())
                        .withTimeAtStartOfDay()
                        .withHourOfDay(Integer.parseInt(time[0]))
                        .withMinuteOfHour(Integer.parseInt(time[1]))
                        .getMillis());
                if (recNotification.isUseRandTip()) {
                    Log.d(TAG, "Fetching random tip...");
                    notification.setDetail(getString(R.string.notification_txt_random));
                } else {
                    Log.d(TAG, "Sending user tip: " + notification.getDetail());
                }
            } else {                                    // Geofence notification
                if (recNotification.isUseRandTip()) {
                    Log.d(TAG, "Fetching random tip...");
                    notification.setDetail(getString(R.string.notification_txt_random));
                } else {
                    Log.d(TAG, "Sending user tip: " + notification.getDetail());
                }
                notificationHistory = new NotificationHistory();
                notificationHistory.setScheduledDeliveryDate(System.currentTimeMillis());
            }
        }

        /*----------------------------------------------------------------------------------------*/

        // Return if notifications are turned off
        if (!isUserNotificationActive && !DbManager.getInstance().getState().isAllowNotification()) {
            Log.v(TAG, "Notifications are off, would have fired notification "+notification.getId());
            return;
        }

       // Log.d(TAG, "id= "+ notificationHistory.getId() + " actualDate= " + notificationHistory.getActualDeliveryDate() +" schedDate= "+ notificationHistory.getScheduledDeliveryDate());

        // return if notification has already been delivered
        if (notificationHistory.getActualDeliveryDate() > 0)
            return;

        // If it is in the past, return and don't push notification
        // Comment this out for all past scheduled notifications to get pushed
        // Give it a 50 minutes to fire before skipping it

        if (notificationHistory.getScheduledDeliveryDate() < System.currentTimeMillis()-50*60*1000)
            return;

        Context context = getApplicationContext();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(context, SplashActivity.class);
        notificationIntent.putExtra(Constants.NOTIFICATION_OPEN_TO_SCREEN_KEY, notification.getOpenToScreen());
        notificationIntent.putExtra(Constants.NOTIFICATION_ID_KEY, notification.getId());
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // PendingIntent pendingIntent = PendingIntent.getActivity(context, notificationHistory.getId(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, notification.getId(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        android.app.Notification noticeNotification = new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle(getString(R.string.notification_title))
                .setSmallIcon(R.drawable.ic_notification)
                .setContentText(notification.getDetail())
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
        notificationManager.notify(notification.getId(), noticeNotification);

        Log.d(TAG, "Sent notification " + notification.toString());

        notificationHistory.setActualDeliveryDate(System.currentTimeMillis());
        DbManager.getInstance().updateNotificationHistory(notificationHistory);
    }


}
