package com.mmgct.quitguide2.managers;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
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
import com.mmgct.quitguide2.utils.Constants;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 35527 on 10/26/2015.
 */
public class DbManager {

    private static final String TAG = "DbManager";
    private static DbManager instance;
    private DatabaseHelper helper;

    private Profile mProfile;
    private State mState;

    public static void init(Context ctx) {
        if (instance == null) {
            instance = new DbManager(ctx);
        }
    }

    public static DbManager getInstance() {
        return instance;
    }

    public DbManager(Context ctx) {
        helper = new DatabaseHelper(ctx);
    }

    /*------------------------------------ Create methods --------------------------------------- */

    // Creates an application state object, should not be called again after first use
    public void createState(State state) {
        try {
            if (helper.getStateDao().countOf() > 0)
                throw new RuntimeException("Previous state exists.");
            helper.getStateDao().create(state);
        } catch (SQLException e) {
            Log.e(TAG, "Error creating award " + state, e);
        }
    }

    // Creates an user profile object, should not be called again after first use
    public void createProfile(Profile profile) {
        try {
            if (helper.getProfileDao().countOf() > 0)
                throw new RuntimeException("Previous state exists.");
            helper.getProfileDao().create(profile);
        } catch (SQLException e) {
            Log.e(TAG, "Error creating award " + profile, e);
        }
    }

    public void createMessage(Message message) {
        try {
            helper.getMessageDao().create(message);
        } catch (SQLException e) {
            Log.e(TAG, "Error creating message " + message, e);
        }
    }

    public void createContent(Content content) {
        try {
            helper.getContentDao().create(content);
        } catch (SQLException e) {
            Log.e(TAG, "Error creating content " + content, e);
        }
    }

    public void createAward(Award award) {
        try {
            helper.getAwardDao().create(award);
        } catch (SQLException e) {
            Log.e(TAG, "Error creating award " + award, e);
        }
    }

    public void createReasonForQuitting(ReasonForQuitting reason) {
        try {
            helper.getReasonForQuittingDao().create(reason);
        } catch (SQLException e) {
            Log.e(TAG, "Error creating ReasonForQuitting " + reason, e);
            e.printStackTrace();
        }
    }

    public void createSmokeFreeDay(SmokeFreeDay smokeFreeDay) {
        try {
            helper.getSmokeFreeDayDao().create(smokeFreeDay);
        } catch (SQLException e) {
            Log.e(TAG, "Error creating SmokeFreeDay " + smokeFreeDay, e);
            e.printStackTrace();
        }
    }

    public void createSlip(Slip slip) {
        try {
            helper.getSlipDao().create(slip);
        } catch (SQLException e) {
            Log.e(TAG, "Error creating Slip " + slip, e);
            e.printStackTrace();
        }
    }

    public void createCraving(Craving craving) {
        try {
            helper.getCravingDao().create(craving);
        } catch (SQLException e) {
            Log.e(TAG, "Error creating Craving " + craving, e);
            e.printStackTrace();
        }
    }

    public void createMood(Mood mood) {
        try {
            helper.getMoodDao().create(mood);
        } catch (SQLException e) {
            Log.e(TAG, "Error creating Mood " + mood, e);
            e.printStackTrace();
        }
    }

    public void createNote(Note note) {
        try {
            helper.getNoteDao().create(note);
        } catch (SQLException e) {
            Log.e(TAG, "Error creating Note: " + note, e);
            e.printStackTrace();
        }
    }

    public void createJournal(Journal journal) {
        try {
            helper.getJournalDao().create(journal);
        } catch (SQLException e) {
            Log.e(TAG, "Error creating Journal: " + journal.toString(), e);
            e.printStackTrace();
        }
    }

    public void createPictureNote(PictureNote pictureNote) {
        try {
            helper.getPictureNoteDao().create(pictureNote);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createHistoryItem(HistoryItem item) {
        try {
            helper.getHistoryItemDao().create(item);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createNotification(Notification notification) {
        try {
            helper.getNotificationDao().create(notification);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createNotificationHistory(NotificationHistory notificationHistory) {
        try {
            helper.getNotificationHistoryDao().create(notificationHistory);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createTip(Tip tip) {
        try {
            helper.getTipDao().create(tip);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createGeoTag(GeoTag geoTag) {
        try {
            helper.getGeoTagDao().create(geoTag);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createRecurringNotification(RecurringNotification recurringNotification) {
        try {
            helper.getRecurringNotificationDao().create(recurringNotification);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    /*------------------------------------ Update methods --------------------------------------- */

    public void updateState(State state) {
        try {
            helper.getStateDao().update(state);
        } catch (SQLException e) {
            Log.e(TAG, "Error updating state " + state, e);
        }
    }

    public void updateprofile(Profile profile) {
        try {
            helper.getProfileDao().update(profile);
        } catch (SQLException e) {
            Log.e(TAG, "Error updating profile " + profile, e);
        }
    }

    public void updateAward(Award award) {
        try {
            helper.getAwardDao().update(award);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateJournal(Journal journal) {
        try {
            helper.getJournalDao().update(journal);
        } catch (SQLException e) {
            Log.e(TAG, "Error updating journal: " + journal.toString(), e);
            e.printStackTrace();
        }
    }

    public void updateNotification(Notification notification) {
        try {
            helper.getNotificationDao().update(notification);
        } catch (SQLException e) {
            Log.e(TAG, "Error updating notification: " + notification.toString(), e);
            e.printStackTrace();
        }
    }

    public void updateNotificationHistory(NotificationHistory notificationHistory) {
        try {
            helper.getNotificationHistoryDao().update(notificationHistory);
        } catch (SQLException e) {
            Log.e(TAG, "Error updating notification history: " + notificationHistory.toString(), e);
            e.printStackTrace();
        }
    }

    public void updateReasonForQuitting(ReasonForQuitting reasonForQuitting) {
        try {
            getHelper().getReasonForQuittingDao().update(reasonForQuitting);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateRecurringNotification(RecurringNotification recurringNotification) {
        try {
            getHelper().getRecurringNotificationDao().update(recurringNotification);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTip(Tip tip) {
        try {
            getHelper().getTipDao().update(tip);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*------------------------------------ Accessor methods ------------------------------------- */

    public State getState() {
        if (mState == null) {
            try {
                List<State> theState = helper.getStateDao().queryForAll();
                if (theState.size() > 0) {
                    mState = theState.get(0);
                } else {
                    throw new RuntimeException("Application state does not exist");
                }
            } catch (SQLException e) {
                Log.e(TAG, "Error retrieving state.", e);
            }
        }
        return mState;
    }

    public Profile getProfile() {
        if (mProfile == null) {
            try {
                List<Profile> theProfile = helper.getProfileDao().queryForAll();
                if (theProfile.size() > 0) {
                    mProfile = theProfile.get(0);
                } else {
                    throw new RuntimeException("Application profile does not exist");
                }
            } catch (SQLException e) {
                Log.e(TAG, "Error retrieving profile.", e);
            }
        }
        return mProfile;
    }

    public List<HistoryItem> getAllHistoryItems() {
        List<HistoryItem> historyItems = null;
        try {
            historyItems = helper.getHistoryItemDao()
                    .queryBuilder().orderBy("date", false).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return historyItems;
    }

    public List<Message> getAllMessages() {
        List<Message> messages = null;
        try {
            messages = helper.getMessageDao().queryForAll();
        } catch (SQLException e) {
            Log.e(TAG, "Error retrieving messages ", e);
        }
        return messages;
    }

    public List<Content> getContent() {
        List<Content> content = null;
        try {
            content = helper.getContentDao().queryForAll();
        } catch (SQLException e) {
            Log.e(TAG, "Error retrieving content ", e);
        }
        return content;
    }

    public List<Content> getAllTriggers() {
        List<Content> content = null;
        try {
            QueryBuilder qb = helper.getContentDao().queryBuilder();
            qb.where().eq("contentType", "What");
            content = qb.query();
        } catch (SQLException e) {
            Log.e(TAG, "Error retrieving content ", e);
        }
        return content;
    }

    public List<Award> getAwards() {
        List<Award> awards = null;
        try {
            awards = helper.getAwardDao().queryForAll();
        } catch (SQLException e) {
            Log.e(TAG, "Error retrieving awards ", e);
        }
        return awards;
    }

    public List<Journal> getAllJournals() {
        List<Journal> journals = null;
        try {
            QueryBuilder<Journal, Integer> qb = getHelper().getJournalDao().queryBuilder();
            qb.orderBy("date", false);
            journals = qb.query();
        } catch (SQLException e) {
            Log.e(TAG, "Error retrieving journals", e);
            e.printStackTrace();
        }
        return journals;
    }

    public Award getAwardByKey(int key) {
        List<Award> award = null;

        QueryBuilder<Award, Integer> qb = getHelper().getAwardDao().queryBuilder();
        try {
            qb.where().eq("key", 1);
            award = qb.query();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return award.get(0);
    }

    public Message getRandomQuitism() {
        QueryBuilder<Message, Integer> qb = getHelper().getMessageDao().queryBuilder();
        Message msg = null;
        try {
            qb.where().eq("category", "Quitism");
            List<Message> messages = qb.query();
            int random = (int) (Math.random() * messages.size());
            msg = messages.get(random);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return msg;
    }

    public Message getRandomCraveTip() {
        QueryBuilder<Message, Integer> qb = getHelper().getMessageDao().queryBuilder();
        Message msg = null;
        try {
            qb.where().eq("category", "CraveTip");
            List<Message> messages = qb.query();
            int random = (int) (Math.random() * messages.size());
            msg = messages.get(random);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return msg;
    }

    public Message getCraveTipFromTrigger(String trigger) {
        QueryBuilder<Message, Integer> qb = getHelper().getMessageDao().queryBuilder();
        Message msg = null;
        try {
            qb.where().eq("category", "CraveTip").and().eq("trigger", trigger);
            List<Message> messages = qb.query();

            if (messages.size() > 0) {
                int len = messages.size();
                int rand = (int) (Math.random() * len);
                msg = messages.get(rand);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Unable to retrieve CraveTip for Trigger " + trigger.toString(), e);
            e.printStackTrace();
        }
        return msg;
    }

    /**
     * @return returns a random reason for quitting or null. Value must be checked.
     */
    @Nullable
    public ReasonForQuitting getRandomReason() {
        ReasonForQuitting reason = null;
        try {
            List<ReasonForQuitting> reasons = getHelper().getReasonForQuittingDao().queryForAll();
            int numOfReasons = reasons.size();
            if (numOfReasons > 0) {
                int random = (int) (Math.random() * reasons.size());
                reason = reasons.get(random);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reason;
    }

    public Note getRandomNote() {
        Note note = null;
        try {
            List<Note> notes = getHelper().getNoteDao().queryForAll();
            int numOfNotes = notes.size();
            if (numOfNotes > 0) {
                int rand = (int) (Math.random() * numOfNotes);
                note = notes.get(rand);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error retrieving random note.", e);
            e.printStackTrace();
        }
        return note;
    }

    public Message getRandomTip() {
        Message msg = null;
        try {
            QueryBuilder<Message, Integer> qb = getHelper().getMessageDao().queryBuilder();
            qb.where().eq("category", "Distraction");
            List<Message> messages = qb.query();
            if (messages.size() > 0) {
                int len = messages.size();
                int rand = (int) (Math.random() * len);
                msg = messages.get(rand);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Unable to retrieve random tip.", e);
            e.printStackTrace();
        }
        return msg;
    }

    public PictureNote getRandomPictureNote() {
        PictureNote pictureNote = null;
        try {
            List<PictureNote> pictureNotes = getHelper().getPictureNoteDao().queryForAll();
            Log.d(TAG, "pictureNotes size= " + pictureNotes.size());
            if (pictureNotes.size() > 0) {
                int rand = (int) (pictureNotes.size() * Math.random());
                pictureNote = pictureNotes.get(rand);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pictureNote;
    }

    /**
     * Reset all awarded trophies
     */
    public void unaward() {
        List<Award> trophies = getAwards();

        for (Award trophy : trophies) {
            trophy.setAwarded(false);
            updateAward(trophy);
        }
    }

    public Content getTriggerByDesc(String desc) {
        QueryBuilder<Content, Integer> qb = getHelper().getContentDao().queryBuilder();
        List<Content> trigger = null;
        try {
            qb.where().eq("contentDescription", desc);
            trigger = qb.query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return trigger == null || trigger.size() <= 0 ? null : trigger.get(0);
    }

    public Slip getSlipByTime(long millis) {
        QueryBuilder<Slip, Integer> qb = getHelper().getSlipDao().queryBuilder();
        List<Slip> slips = null;
        try {
            slips = qb.where().eq("date", millis).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return slips == null || slips.size() == 0 ? null : slips.get(0);
    }


    /**
     * Method returns a String of counts for the different mood button presses for the plot graph
     *
     * @return vals - mood values
     */
    public String getPlotValues() {
        StringBuilder vals = new StringBuilder();
        Dao<Mood, Integer> moodDao = getHelper().getMoodDao();
        QueryBuilder<Mood, Integer> qb = moodDao.queryBuilder();
        try {
            vals.append(qb.where().eq("type", "ნერვიული").countOf());
            vals.append(",");
            vals.append(qb.where().eq("type", "ბრაზიანი").countOf());
            vals.append(",");
            vals.append(qb.where().eq("type", "სტრესული").countOf());
            vals.append(",");
            vals.append(qb.where().eq("type", "დასვენებული").countOf());
            vals.append(",");
            vals.append(qb.where().eq("type", "შეშფოთებული").countOf());
            vals.append(",");
            vals.append(qb.where().eq("type", "აღფრთოვანებული").countOf());
            vals.append(",");
            vals.append(qb.where().eq("type", "იმედგაცრუებული").countOf());
            vals.append(",");
            vals.append(qb.where().eq("type", "მოწყენილი").countOf());
            vals.append(",");
            vals.append(qb.where().eq("type", "ბედნიერი").countOf());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vals.toString();
    }


    public List<BarGraphItem> getBarItems() {

        List<BarGraphItem> items = new ArrayList();
        Dao<Craving, Integer> cravingDao = getHelper().getCravingDao();
        Dao<Slip, Integer> slipDao = getHelper().getSlipDao();
        QueryBuilder<Craving, Integer> cravingQb = cravingDao.queryBuilder();
        QueryBuilder<Slip, Integer> slipQb = slipDao.queryBuilder();
        List<Content> triggers = getAllTriggers();
        try {
            for (Content trigger : triggers) {
                BarGraphItem item = new BarGraphItem(
                        trigger.getContentDescription(),
                        String.valueOf(cravingQb.where().eq("trigger_id", getTriggerByDesc(trigger.getContentDescription())).countOf()
                                + slipQb.where().eq("trigger_id", getTriggerByDesc(trigger.getContentDescription())).countOf()));
                addBarItem(items, item);
            }
            // Add no trigger selected to list
            BarGraphItem item = new BarGraphItem(
                    Constants.NO_TRIGGER,
                    String.valueOf(cravingQb.where().isNull("trigger_id").countOf()
                            + slipQb.where().isNull("trigger_id").countOf()));
            addBarItem(items, item);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    private void addBarItem(List<BarGraphItem> items, BarGraphItem item) {
        if (Integer.parseInt(item.getValue()) > 0) {
            items.add(item);
        }
    }

    public ReasonForQuitting getReasonById(int id){
        ReasonForQuitting reasonForQuitting = null;
        try {
            reasonForQuitting = getHelper().getReasonForQuittingDao().queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reasonForQuitting;
    }

    // ------------------------------ Notificaitons --------------------------------

    public Notification getNotificationById(int id) {
        Notification nh = null;
        try {
            nh = getHelper().getNotificationDao().queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nh;
    }

    public List<NotificationHistory> getUndeliveredNotifications() {
        List<NotificationHistory> notificationHistoryList = null;
        QueryBuilder<NotificationHistory, Integer> qb = getHelper().getNotificationHistoryDao().queryBuilder();
        try {
            notificationHistoryList = qb.where().eq("actualDeliveryDate", 0).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notificationHistoryList;
    }

    public List<NotificationHistory> getAllNotificationHistory() {
        List<NotificationHistory> notificationHistoryList = null;
        try {
            notificationHistoryList = getHelper().getNotificationHistoryDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notificationHistoryList;
    }

    public List<Notification> getActualDateNotifications() {
        List<Notification> notifications = null;
        QueryBuilder<Notification, Integer> qb = getHelper().getNotificationDao().queryBuilder();
        try {
            notifications = qb.where().isNotNull("reminderDate").query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }

    public List<Notification> getDRTQuitDateNotifications() {
        List<Notification> notifications = null;
        QueryBuilder<Notification, Integer> qb = getHelper().getNotificationDao().queryBuilder();
        try {
            notifications = qb.where().ge("daysRelativeToQuitDate", 0).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }

    public List<Notification> getDRTNoProfileSetNotifications() {
        List<Notification> notifications = null;
        QueryBuilder<Notification, Integer> qb = getHelper().getNotificationDao().queryBuilder();
        try {
            notifications = qb.where().ge("daysRelativeToNoProfileSet", 0).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }

    public List<Notification> getDRTLastActivityNotifications() {
        List<Notification> notifications = null;
        QueryBuilder<Notification, Integer> qb = getHelper().getNotificationDao().queryBuilder();
        try {
            notifications = qb.where().ge("daysRelativeToLastActivity", 0).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }

    public List<Notification> getAllNotifications() {
        List<Notification> notifications = null;
        try {
            notifications = getHelper().getNotificationDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }

    public List<RecurringNotification> getRecurringNotifications() {
        List<RecurringNotification> recurringNotifications = null;
        try {
            recurringNotifications = helper.getRecurringNotificationDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recurringNotifications;
    }

    public RecurringNotification getRecurringNotificationById(int id) {
        RecurringNotification recurringNotification = null;
        try {
            recurringNotification = helper.getRecurringNotificationDao().queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recurringNotification;
    }

    public List<RecurringNotification> getRecurringGeofenceNotifications() {
        List<RecurringNotification> recurringGeofenceNotifications = null;

        QueryBuilder<RecurringNotification, Integer> qb = getHelper().getRecurringNotificationDao().queryBuilder();
        try {
            recurringGeofenceNotifications = qb.where().isNotNull("geoTag_id").query();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return recurringGeofenceNotifications;
    }

    public List<RecurringNotification> getRecurringTimedNotifications() {
        List<RecurringNotification> recurringGeofenceNotifications = null;

        QueryBuilder<RecurringNotification, Integer> qb = getHelper().getRecurringNotificationDao().queryBuilder();
        try {
            recurringGeofenceNotifications = qb.where().isNull("geoTag_id").query();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return recurringGeofenceNotifications;
    }


    public List<GeoTag> getAllGeoTags() {
        List<GeoTag> geoTags = null;
        try {
            geoTags = helper.getGeoTagDao().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return geoTags;
    }
    /* --------------------------------------- Delete ------------------------------------------- */

    public void reset() {
        // Delete
        Dao<ReasonForQuitting, Integer> reasonForQuittingDao = getHelper().getReasonForQuittingDao();
        DeleteBuilder<ReasonForQuitting, Integer> deleteBuilder = reasonForQuittingDao.deleteBuilder();
        try {
            deleteBuilder.where().ge("id", 0);
            reasonForQuittingDao.delete(deleteBuilder.prepare());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Delete notifications
       /* Dao<NotificationHistory, Integer> notificationHistoryDao = getHelper().getNotificationHistoryDao();
        DeleteBuilder<NotificationHistory, Integer> nhDeleteBuilder = notificationHistoryDao.deleteBuilder();
        try {
            nhDeleteBuilder.where().ge("id", 0);
            notificationHistoryDao.delete(nhDeleteBuilder.prepare());
            Log.v(TAG, "All notifications deleted");
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
        // Reset
        unaward();
    }

    public void deleteJournal(Journal j) {
        Dao<Journal, Integer> journalDao = getHelper().getJournalDao();
        try {
            journalDao.delete(j);
        } catch (SQLException e) {
            Log.e(TAG, "Error deleting journal " + j.toString(), e);
            e.printStackTrace();
        }
    }

    public void deleteNotificationHistory(NotificationHistory nh) {
        if (nh != null) {
            try {
                getHelper().getNotificationHistoryDao().delete(nh);
            } catch (SQLException e) {
                Log.e(TAG, "Error deleting notification history " + nh.toString(), e);
                e.printStackTrace();
            }
        }
    }

    public void deleteReasonForQuitting(ReasonForQuitting reasonForQuitting) {
        if (reasonForQuitting != null) {
            try {
                getHelper().getReasonForQuittingDao().delete(reasonForQuitting);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Maybe remove this method and force all database interaction to go through the manager

    public DatabaseHelper getHelper() {
        return helper;
    }

    /**
     * Class represents a bar graph item
     */

    public class BarGraphItem {

        private String label;
        private String value;

        public BarGraphItem(String label, String value) {
            this.label = label;
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

}
