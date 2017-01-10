package com.mmgct.quitguide2.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.mmgct.quitguide2.managers.DbManager;
import com.mmgct.quitguide2.models.Content;
import com.mmgct.quitguide2.models.NotificationHistory;
import com.mmgct.quitguide2.models.RecurringNotification;
import com.mmgct.quitguide2.views.notifications.receiver.AlarmReceiver;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 35527 on 12/28/2015.
 * This class schedules notification ids to the system Alarm Service
 */
public class AlarmScheduler {

    private static final String TAG = "AlarmScheduler";
    private Context mContext;
    private AlarmManager mAlarmManager;
    static AlarmScheduler mInstance;

    public static void init(Context context) {
        if (mInstance == null) {
            mInstance = new AlarmScheduler(context);
        }
    }

    private AlarmScheduler(Context context) {
        mContext = context;
        mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
    }

    public static AlarmScheduler getInstance() {
        return mInstance;
    }

    public synchronized void scheduleRecurringNotification(RecurringNotification recurringNotification) {
        if (recurringNotification == null || recurringNotification.getNotification() == null) {
            return;
        }

        Intent intent = new Intent(mContext, AlarmReceiver.class);
        intent.putExtra(Constants.NOTIFICATION_ID_KEY,
                recurringNotification.getNotification().getId()); // Pass the notification id to the intent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, recurringNotification.getNotification().getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Log.d(TAG, "notificationId= "+ recurringNotification.getNotification().getId());

        // Hours and minutes when repeating alarms are supposed to be fired
        String time = recurringNotification.getNotification().getTimeOfDay();
        if (time == null) {
            return;
        }
        int hour = Integer.parseInt(time.substring(0, time.indexOf(":")));
        int minute = Integer.parseInt(time.substring(time.indexOf(":")+1));

        // If the time is passed already today set it for tomorrow at the selected time
        DateTime now = new DateTime(System.currentTimeMillis());
        DateTime notificationDeliveryTime = now.withTime(hour, minute, 0, 0);
        if (now.isAfter(notificationDeliveryTime)) {
            notificationDeliveryTime = notificationDeliveryTime.plusDays(1);
        }

        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                notificationDeliveryTime.getMillis(),
                AlarmManager.INTERVAL_DAY, // ms in day
                pendingIntent);

        Log.v(TAG, "Recurring Notification " + recurringNotification.getId()
                + " scheduled for " + notificationDeliveryTime.getMonthOfYear()
                +"/"+notificationDeliveryTime.getDayOfMonth()
                +"/"+notificationDeliveryTime.getYear()
                +"T"+notificationDeliveryTime.getHourOfDay()
                +":"+notificationDeliveryTime.getMinuteOfHour());
    }

    public synchronized void scheduleNotification(NotificationHistory nh) {

        if (nh == null || nh.getNotification() == null)
            return;

        Intent intent = new Intent(mContext, AlarmReceiver.class);
        intent.putExtra(Constants.NOTIFICATION_ID_KEY,
                nh.getNotification().getId()); // Pass the notification id to the intent
        /*intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                Intent.FLAG_ACTIVITY_SINGLE_TOP);*/ // So that onNewIntent() will get called

        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, nh.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Testing

        mAlarmManager.set(AlarmManager.RTC_WAKEUP, nh.getScheduledDeliveryDate(), pendingIntent);
        Log.v(TAG, "Notification " + nh.getNotification().getKey()
                + " scheduled for " + Common.formatTimestamp("MMMM, dd yyyy @ HH:mm:ss", nh.getScheduledDeliveryDate()));

    }

    public synchronized void removeScheduledRecurringNotification (RecurringNotification recurringNotification) {
        if (recurringNotification == null) {
            return;
        }
        Intent intent = new Intent(mContext, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, recurringNotification.getId(), intent, PendingIntent.FLAG_CANCEL_CURRENT);
        mAlarmManager.cancel(pendingIntent);
    }

    public synchronized void removeScheduledNotification(NotificationHistory nh) {
        if (nh == null || nh.getNotification() == null)
            return;

        Intent intent = new Intent(mContext, AlarmReceiver.class);
        intent.putExtra(Constants.NOTIFICATION_ID_KEY, nh.getNotification().getId());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, nh.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarmManager.cancel(pendingIntent);
        Log.v(TAG, String.format("Removed notification %d.", nh.getNotification().getKey()));
    }
}
