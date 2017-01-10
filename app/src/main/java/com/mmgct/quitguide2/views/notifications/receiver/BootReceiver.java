package com.mmgct.quitguide2.views.notifications.receiver;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

import com.mmgct.quitguide2.managers.DbManager;
import com.mmgct.quitguide2.models.NotificationHistory;
import com.mmgct.quitguide2.models.RecurringNotification;
import com.mmgct.quitguide2.service.AddGeofencesIntentService;
import com.mmgct.quitguide2.utils.AlarmScheduler;
import com.mmgct.quitguide2.utils.Constants;

/**
 * Created by 35527 on 10/6/2015.
 * This receiver schedules notifications with the alarm manager on boot
 */
public class BootReceiver extends WakefulBroadcastReceiver {

    private static final String TAG = "BootReceiver";


    @Override
    public void onReceive(final Context context, Intent intent) {


        if (DbManager.getInstance() == null || AlarmScheduler.getInstance() == null) {
            DbManager.init(context);
            AlarmScheduler.init(context);
        }

        List<NotificationHistory> scheduledNotifications = DbManager.getInstance().getUndeliveredNotifications();
        List<RecurringNotification> recurringNotifications = DbManager.getInstance().getRecurringNotifications();


                /*--------------------------------- Testing --------------------------------------*
                 * This test case will set all notifications to fire in 60 second intervals       *
                 * Uncomment the following code and reboot the phone to initiate test case        *
                 *--------------------------------------------------------------------------------*/

                /*scheduledNotifications = DbManager.getInstance().getAllNotificationHistory();
                int i = 1;
                for (NotificationHistory noticeHistory : scheduledNotifications) {
                    noticeHistory.setScheduledDeliveryDate(System.currentTimeMillis() + 1000 * 0 * i++);
                    noticeHistory.setActualDeliveryDate(0);
                    DbManager.getInstance().updateNotificationHistory(noticeHistory);
                }*/

                /*-------------------------------- End Testing -----------------------------------*/

        for (NotificationHistory nh : scheduledNotifications) {
            AlarmScheduler.getInstance().scheduleNotification(nh);

            // Logging
            // DateTimeFormatter fmt = DateTimeFormat.forPattern("MMMM dd, yyyy | kk:mm");
            //  Log.v(TAG, String.format("NotificationID: %d \t Notification scheduled time: %s", nh.getNotification().getKey(), fmt.print(nh.getScheduledDeliveryDate())));
        }
        // Schedule recurring timed notificaiton
        for (RecurringNotification recurringNotification : recurringNotifications) {
            AlarmScheduler.getInstance().scheduleRecurringNotification(recurringNotification);
            Log.d(TAG, "Scheduled recurring notification, id: " + recurringNotification.getId());
        }
        // Schedule recurring geotag notifications
        Intent i = new Intent(context, AddGeofencesIntentService.class);
        i.putExtra(AddGeofencesIntentService.FUNCTION_EXTRA_KEY, AddGeofencesIntentService.FUNCTION_TYPE_SET_ALL);
        context.startService(i);

                /*for (NotificationHistory nh : scheduledNotifications) {

                    // Logging
                    DateTimeFormatter fmt = DateTimeFormat.forPattern("MMMM dd, yyyy | kk:mm");
                    Log.v(TAG, String.format("NotificationID: %d \t Notification scheduled time: %s", nh.getNotification().getKey(), fmt.print(nh.getScheduledDeliveryDate())));

                    Intent notificationIntent = new Intent(context, AlarmReceiver.class);
                    notificationIntent.putExtra(Constants.NOTIFICATION_ID_KEY, nh.getNotification().getId());

                    PendingIntent pendingNotification = PendingIntent.getBroadcast(context, nh.getId(), notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, nh.getScheduledDate(), pendingNotification);
                }*/

        Log.v(TAG, "Scheduled all notifications.");
    }
}
