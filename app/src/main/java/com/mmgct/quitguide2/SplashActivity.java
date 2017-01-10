package com.mmgct.quitguide2;

import android.app.Activity;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mmgct.quitguide2.managers.DbManager;
import com.mmgct.quitguide2.models.Notification;
import com.mmgct.quitguide2.models.NotificationHistory;
import com.mmgct.quitguide2.models.Profile;
import com.mmgct.quitguide2.models.State;
import com.mmgct.quitguide2.utils.AlarmScheduler;
import com.mmgct.quitguide2.utils.Common;
import com.mmgct.quitguide2.utils.Constants;
import com.mmgct.quitguide2.utils.ContentIngestor;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.joda.time.DateTime;

import java.util.List;


/**
 * Created by 35527 on 10/26/2015.
 */
public class SplashActivity extends Activity {

    private static final String TAG = "SplashActivity";

    private State mState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mState = DbManager.getInstance().getState();
        setContentView(R.layout.activity_splash);
        new AsyncCaller().execute();
    }

    /**
     * Ingests JSON content, and stores it in local app db
     */
    private void ingestContent() {
        ContentIngestor contentIngestor = new ContentIngestor(getApplicationContext());
        contentIngestor.ingestMessageJSON();
        contentIngestor.ingestContentJSON();
        contentIngestor.ingestNotificationJSON();
    }



    private class AsyncCaller extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (!mState.isIngested()) {
                ingestContent();
                mState.setIngested(true);
                DbManager.getInstance().updateState(mState);
            }

            // If the app has not already been opened today send analytics
            DateTime lastOpened = new DateTime(mState.getAppLastOpenedTimestamp()).withTimeAtStartOfDay();
            DateTime today = new DateTime(System.currentTimeMillis()).withTimeAtStartOfDay();
            if (!lastOpened.equals(today)){
                mState.setAppLastOpenedTimestamp(today.getMillis());
                int incrDaysUsed = mState.getTotalNumberOfDaysUsed()+1;
                mState.setTotalNumberOfDaysUsed(incrDaysUsed);
                DbManager.getInstance().updateState(mState);
                Tracker t = ((Application) getApplication()).getTracker(Application.TrackerName.APP_TRACKER);

                t.send(new HitBuilders.EventBuilder()
                                .setCategory(getString(R.string.category_quit_plan))
                                .setAction(getString(R.string.action_another_day_in_app))
                                .setLabel(String.valueOf(incrDaysUsed))
                                .build());
                t.send(new HitBuilders.EventBuilder()
                        .setCategory(getString(R.string.category_reports))
                        .setAction(getString(R.string.action_events_triggered))
                        .setLabel(getString(R.string.category_quit_plan) + "|" + getString(R.string.action_another_day_in_app) + "|" + incrDaysUsed)
                        .build());
            }

            // Schedule actualDate, daysRelativeToNoProfileSet, daysRelativeToLastActivity local notifications here
            DateTime now = new DateTime(System.currentTimeMillis()).withTimeAtStartOfDay();
            List<Notification> actualDates = DbManager.getInstance().getActualDateNotifications();
            for (Notification n : actualDates) {

                String[] parseDate = n.getReminderDate().split("/");
                String[] parseTime = n.getTimeOfDay().split(":");

                int month = Integer.parseInt(parseDate[0]);
                int dayOfMonth = Integer.parseInt(parseDate[1]);
                int hour = Integer.parseInt(parseTime[0]);
                int min = Integer.parseInt(parseTime[1]);

                int year = Common.getScheduleYear(month, dayOfMonth);

                DateTime scheduleDate = new DateTime(year, month, dayOfMonth, hour, min);

                if (n.getNotificationHistory() == null) {
                    Common.createNewNotificationHistory(n, scheduleDate);
                } else {
                    Common.updateNotificationHistory(n.getNotificationHistory(), scheduleDate);
                }
                AlarmScheduler.getInstance().scheduleNotification(n.getNotificationHistory());
            }

            List<Notification> lastActivityNotifications = DbManager.getInstance().getDRTLastActivityNotifications();
            for (Notification n : lastActivityNotifications) {

                DateTime scheduleDate = calcDaysFromNow(n.getDaysRelativeToLastActivity(), n.getTimeOfDay());

                if (n.getNotificationHistory() == null) {
                    Common.createNewNotificationHistory(n, scheduleDate);
                } else {
                    Common.updateNotificationHistory(n.getNotificationHistory(), scheduleDate);
                }
                AlarmScheduler.getInstance().scheduleNotification(n.getNotificationHistory());
            }

            // Only schedule these if intro mode is active still
            if (DbManager.getInstance().getState().isIntroMode()) {
                List<Notification> dRTNoProfileSetNotifications = DbManager.getInstance().getDRTNoProfileSetNotifications();
                for (Notification n : dRTNoProfileSetNotifications) {
                    DateTime scheduleDate = calcDaysFromNow(n.getDaysRelativeToNoProfileSet(), n.getTimeOfDay());

                    if (n.getNotificationHistory() == null) {
                        Common.createNewNotificationHistory(n, scheduleDate);
                    } else {
                        Common.updateNotificationHistory(n.getNotificationHistory(), scheduleDate);
                    }
                    AlarmScheduler.getInstance().scheduleNotification(n.getNotificationHistory());
                }
            }

            return null;
        }

        /**
         *
         * @param daysFromNow - days from now
         * @param time - time in the format 00:00 - 23:59
         */
        private DateTime calcDaysFromNow(int daysFromNow, String time) {
            int hour, min;
            String[] parseTime = time.split(":");
            hour = Integer.parseInt(parseTime[0]);
            min = Integer.parseInt(parseTime[1]);
            return new DateTime(System.currentTimeMillis())
                    .withTimeAtStartOfDay()
                    .plusDays(daysFromNow)
                    .plusHours(hour)
                    .plusMinutes(min);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (mState.isIntroMode()) {
                Intent intent = new Intent(getApplicationContext(), IntroActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                if (getIntent().getExtras() != null) {
                    intent.putExtras(getIntent().getExtras());
                }
                startActivity(intent);
            } else {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                if (getIntent().getExtras() != null) {
                    intent.putExtras(getIntent().getExtras());
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }
    }

}
