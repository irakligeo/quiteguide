package com.mmgct.quitguide2;


import android.provider.Settings;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;
import com.mmgct.quitguide2.managers.DatabaseHelper;
import com.mmgct.quitguide2.managers.DbManager;
import com.mmgct.quitguide2.models.Content;
import com.mmgct.quitguide2.models.Profile;
import com.mmgct.quitguide2.models.State;
import com.mmgct.quitguide2.utils.AlarmScheduler;
import com.mmgct.quitguide2.utils.Common;
import com.mmgct.quitguide2.utils.DbCreator;

import io.fabric.sdk.android.Fabric;
import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by 35527 on 10/26/2015.
 */
public class Application extends android.app.Application {

    private HashMap<TrackerName, Tracker> mTrackers = new HashMap<>();

    /**
     * Enum used to identify the tracker that needs to be used for tracking.
     * <p/>
     * A single tracker is usually enough for most purposes. In case you do need multiple trackers,
     * storing them all in Application object helps ensure that they are created only once per
     * application instance.
     */
    public enum TrackerName {
        APP_TRACKER // Tracker used only in this app.
        // Examples, add more trackers here
        // GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
        // ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
    }
    @Override
    public void onCreate() {
        super.onCreate();
        // Fabric.with(this, new Crashlytics());
        JodaTimeAndroid.init(this);
        new DbCreator(this);
        DbManager.init(this);
        AlarmScheduler.init(this);

        // Create a state and profile for the application if non-existent
        DatabaseHelper helper = DbManager.getInstance().getHelper();
        try {
            if (helper.getStateDao().countOf() == 0) {
                State state = new State();
                state.setIntroMode(true);
                state.setInTutorial(true);
                state.setIngested(true);  // Set to false to ingest json content on first launch
                                          // we are preloading the sqlLite db for production builds
                state.setJoinedDate(new DateTime(System.currentTimeMillis()).withTimeAtStartOfDay().getMillis());
                state.setLastUpdate(new DateTime(System.currentTimeMillis()).withTimeAtStartOfDay().getMillis());
                state.setAppVersion(BuildConfig.VERSION_CODE);
                DbManager.getInstance().createState(state);
            }
            if (helper.getProfileDao().countOf() == 0) {
                Profile prof = new Profile();
                DbManager.getInstance().createProfile(prof);
            }

            int verNo = DbManager.getInstance().getState().getAppVersion();
            // Upgrade for 2.0.0 - 2.0.2 to 2.1.0
            if (BuildConfig.VERSION_CODE > verNo && verNo <= 9) {
                State state = DbManager.getInstance().getState();
                state.setAppVersion(BuildConfig.VERSION_CODE);
                state.setShowWhatsNewScreen(true);
                DbManager.getInstance().updateState(state);

                // Add two new Content for 2.1 enhancements
                Content thisLocation = new Content();
                Content thisTimeOfDay = new Content();

                thisLocation.setContentDescription("ეს ადგილი");
                thisLocation.setContentType("რა");
                thisTimeOfDay.setContentDescription("ეს დრო");
                thisTimeOfDay.setContentType("რა");

                if (DbManager.getInstance().getTriggerByDesc(thisLocation.getContentDescription()) == null) {
                    DbManager.getInstance().createContent(thisLocation);
                }
                if (DbManager.getInstance().getTriggerByDesc(thisTimeOfDay.getContentDescription()) == null) {
                    DbManager.getInstance().createContent(thisTimeOfDay);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);

            // Set dry run
            // analytics.setDryRun(true);
            // Verbose logging
            // analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);

            // Time interval for which batch reports will be sent
            // Set the dispatch period in seconds, -1 for instantaneous.
            // analytics.setLocalDispatchPeriod(-1);

            Tracker t = null;
            if (trackerId == TrackerName.APP_TRACKER) {
                t = analytics.newTracker(R.xml.analytics);
                // Unique device id
                String deviceId = Settings.Secure.getString(this.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                // Custom dimension for participant id
                String participantId = DbManager.getInstance().getProfile().getParticipantId();

                // Custom dimension for participant id
                t.set("&cd1", participantId == null ? "null" : participantId);
                // Custom dimension for all users
                t.set("&cd2", "ყველა მომხმარებელი");
                // Custom dimension for device id
                t.set("&cd3", deviceId);
                t.set("&cd4", Common.formatTimestamp("yyyy-MM-dd'T'HH:mm:ss'Z'", System.currentTimeMillis()));
                t.set("&uid", deviceId);
            } else {
                throw new RuntimeException("შეცდომა:  "+trackerId+" არ არსებობს.");
            }
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
    }
}
