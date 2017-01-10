package com.mmgct.quitguide2.managers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.mmgct.quitguide2.models.Award;
import com.mmgct.quitguide2.models.Content;
import com.mmgct.quitguide2.models.Craving;
import com.mmgct.quitguide2.models.GeoTag;
import com.mmgct.quitguide2.models.HistoryItem;
import com.mmgct.quitguide2.models.Journal;
import com.mmgct.quitguide2.models.Message;
import com.mmgct.quitguide2.models.Mood;
import com.mmgct.quitguide2.models.Note;
import com.mmgct.quitguide2.models.Notification;
import com.mmgct.quitguide2.models.NotificationHistory;
import com.mmgct.quitguide2.models.PictureNote;
import com.mmgct.quitguide2.models.Profile;
import com.mmgct.quitguide2.models.ReasonForQuitting;
import com.mmgct.quitguide2.models.RecurringNotification;
import com.mmgct.quitguide2.models.Slip;
import com.mmgct.quitguide2.models.SmokeFreeDay;
import com.mmgct.quitguide2.models.State;
import com.mmgct.quitguide2.models.Tip;

import java.sql.SQLException;

/**
 * Created by 35527 on 10/26/2015.
 */

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "quitguide.db";
    private static final String TAG = "DbManager";
    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 3;

    private Dao<Message, Integer> messageDao = null;
    private Dao<Content, Integer> contentDao = null;
    private Dao<Award, Integer> awardDao = null;
    private Dao<State, Integer> stateDao = null;
    private Dao<Profile, Integer> profileDao = null;
    private Dao<ReasonForQuitting, Integer> reasonForQuittingDao = null;
    private Dao<SmokeFreeDay, Integer> smokeFreeDayDao = null;
    private Dao<Slip, Integer> slipDao = null;
    private Dao<Craving, Integer> cravingDao = null;
    private Dao<Mood, Integer> moodDao = null;
    private Dao<Note, Integer> noteDao = null;
    private Dao<Journal, Integer> journalDao = null;
    private Dao<PictureNote, Integer> pictureNoteDao = null;
    private Dao<HistoryItem, Integer> historyItemDao = null;
    private Dao<Notification, Integer> notificationDao = null;
    private Dao<NotificationHistory, Integer> notificationHistoryDao = null;
    private Dao<Tip, Integer> tipDao = null;
    private Dao<GeoTag, Integer> geoTagDao = null;
    private Dao<RecurringNotification, Integer> recurringNotificationDao = null;

    public DatabaseHelper(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Message.class);
            TableUtils.createTable(connectionSource, Content.class);
            TableUtils.createTable(connectionSource, Award.class);
            TableUtils.createTable(connectionSource, State.class);
            TableUtils.createTable(connectionSource, Profile.class);
            TableUtils.createTable(connectionSource, ReasonForQuitting.class);
            TableUtils.createTable(connectionSource, SmokeFreeDay.class);
            TableUtils.createTable(connectionSource, Slip.class);
            TableUtils.createTable(connectionSource, Craving.class);
            TableUtils.createTable(connectionSource, Mood.class);
            TableUtils.createTable(connectionSource, Note.class);
            TableUtils.createTable(connectionSource, Journal.class);
            TableUtils.createTable(connectionSource, PictureNote.class);
            TableUtils.createTable(connectionSource, HistoryItem.class);
            TableUtils.createTable(connectionSource, Notification.class);
            TableUtils.createTable(connectionSource, NotificationHistory.class);
            TableUtils.createTable(connectionSource, Tip.class);
            TableUtils.createTable(connectionSource, GeoTag.class);
            TableUtils.createTable(connectionSource, RecurringNotification.class);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {

    }

    @Override
    public void close() {
        super.close();
        messageDao = null;
        contentDao = null;
        awardDao = null;
        stateDao = null;
        profileDao = null;
        reasonForQuittingDao = null;
        smokeFreeDayDao = null;
        slipDao = null;
        cravingDao = null;
        moodDao = null;
        noteDao = null;
        journalDao = null;
        pictureNoteDao = null;
        historyItemDao = null;
        notificationDao = null;
        notificationHistoryDao = null;
        tipDao = null;
        geoTagDao = null;
        recurringNotificationDao = null;
    }

    public Dao<Message, Integer> getMessageDao() {
        if (messageDao == null) {
            try {
                messageDao = getDao(Message.class);
            } catch (SQLException e) {
                Log.e(TAG, "Error while retrieving Message DAO", e);
                e.printStackTrace();
            }
        }
        return messageDao;
    }

    public Dao<Content, Integer> getContentDao() {
        if (contentDao == null) {
            try {
                contentDao = getDao(Content.class);
            } catch (SQLException e) {
                Log.e(TAG, "Error while retrieving Content DAO", e);
                e.printStackTrace();
            }
        }
        return contentDao;
    }

    public Dao<Award, Integer> getAwardDao() {
        if (awardDao == null) {
            try {
                awardDao = getDao(Award.class);
            } catch (SQLException e) {
                Log.e(TAG, "Error while retrieving Award DAO", e);
                e.printStackTrace();
            }
        }
        return awardDao;
    }

    public Dao<State, Integer> getStateDao() {
        if (stateDao == null) {
            try {
                stateDao = getDao(State.class);
            } catch (SQLException e) {
                Log.e(TAG, "Error while retrieving State DAO", e);
                e.printStackTrace();
            }
        }
        return stateDao;
    }

    public Dao<Profile, Integer> getProfileDao() {
        if (profileDao == null) {
            try {
                profileDao = getDao(Profile.class);
            } catch (SQLException e) {
                Log.e(TAG, "Error while retrieving Profile DAO", e);
                e.printStackTrace();
            }
        }
        return profileDao;
    }

    public Dao<ReasonForQuitting, Integer> getReasonForQuittingDao() {
        if (reasonForQuittingDao == null) {
            try {
                reasonForQuittingDao = getDao(ReasonForQuitting.class);
            } catch (SQLException e) {
                Log.e(TAG, "Error while retrieving ReasonForQuitting Dao", e);
                e.printStackTrace();
            }
        }
        return reasonForQuittingDao;
    }

    public Dao<SmokeFreeDay, Integer> getSmokeFreeDayDao() {
        if (smokeFreeDayDao == null) {
            try {
                smokeFreeDayDao = getDao(SmokeFreeDay.class);
            } catch (SQLException e) {
                Log.e(TAG, "Error while retrieving SmokeFreeDay Dao", e);
                e.printStackTrace();
            }
        }
        return smokeFreeDayDao;
    }

    public Dao<Slip, Integer> getSlipDao() {
        if (slipDao == null) {
            try {
                slipDao = getDao(Slip.class);
            } catch (SQLException e) {
                Log.e(TAG, "Error while retrieving Slip Dao", e);
                e.printStackTrace();
            }
        }
        return slipDao;
    }

    public Dao<Craving, Integer> getCravingDao() {
        if (cravingDao == null) {
            try {
                cravingDao = getDao(Craving.class);
            } catch (SQLException e) {
                Log.e(TAG, "Error while retrieving Craving Dao", e);
                e.printStackTrace();
            }
        }
        return cravingDao;
    }

    public Dao<Mood, Integer> getMoodDao() {
        if (moodDao == null) {
            try {
                moodDao = getDao(Mood.class);
            } catch (SQLException e) {
                Log.e(TAG, "Error while retrieving Mood Dao", e);
                e.printStackTrace();
            }
        }
        return moodDao;
    }

    public Dao<Note, Integer> getNoteDao(){
        if (noteDao == null) {
            try {
                noteDao = getDao(Note.class);
            } catch (SQLException e) {
                Log.e(TAG, "Error while retrieving Note Dao", e);
                e.printStackTrace();
            }
        }
        return noteDao;
    }

    public Dao<Journal, Integer> getJournalDao() {
        if (journalDao == null) {
            try {
                journalDao = getDao(Journal.class);
            } catch (SQLException e) {
                Log.e(TAG, "Error retrieving Journal Dao", e);
                e.printStackTrace();
            }
        }
        return journalDao;
    }

    public Dao<PictureNote, Integer> getPictureNoteDao() {
        if (pictureNoteDao == null) {
            try {
                pictureNoteDao = getDao(PictureNote.class);
            } catch (SQLException e) {
                Log.e(TAG, "Error retrieving Picture Note Dao", e);
                e.printStackTrace();
            }
        }
        return pictureNoteDao;
    }

    public Dao<HistoryItem, Integer> getHistoryItemDao() {
        if (historyItemDao == null) {
            try {
                historyItemDao = getDao(HistoryItem.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return historyItemDao;
    }

    public Dao<Notification, Integer> getNotificationDao() {
        if (notificationDao == null) {
            try {
                notificationDao = getDao(Notification.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return notificationDao;
    }

    public Dao<NotificationHistory, Integer> getNotificationHistoryDao() {
        if (notificationHistoryDao == null) {
            try {
                notificationHistoryDao = getDao(NotificationHistory.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return notificationHistoryDao;
    }

    public Dao<Tip, Integer> getTipDao() {
        if (tipDao == null) {
            try {
                tipDao = getDao(Tip.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return tipDao;
    }

    public Dao<GeoTag, Integer> getGeoTagDao() {
        if (geoTagDao == null) {
            try {
                geoTagDao = getDao(GeoTag.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return geoTagDao;
    }

    public Dao<RecurringNotification, Integer> getRecurringNotificationDao() {
        if (recurringNotificationDao == null) {
            try {
                recurringNotificationDao = getDao(RecurringNotification.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return recurringNotificationDao;
    }
}
