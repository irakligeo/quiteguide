package com.mmgct.quitguide2.models;

import com.j256.ormlite.field.DatabaseField;
import com.mmgct.quitguide2.managers.DbManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 35527 on 12/21/2015.
 */
public class Notification {

    private static final String JSON_ID_KEY = "notificationId";
    private static final String JSON_MESSAGE_KEY = "message";
    private static final String JSON_DETAIL_KEY = "detail";
    private static final String JSON_DRT_QUIT_DATE_KEY = "daysRelativeToQuitDate";
    private static final String JSON_DRT_PROGRAM_START_KEY = "daysRelativeToProgramStart";
    private static final String JSON_DRT_LAST_ACTIVITY_KEY = "daysRelativeToLastActivity";
    private static final String JSON_DRT_NO_PROFILE_SET_KEY = "daysRelativeToNoProfileSet";
    private static final String JSON_TIME_OF_DAY_KEY = "timeOfDay";
    private static final String JSON_OPEN_TO_SCREEN_KEY = "openToScreen";
    private static final String JSON_REMINDER_DATE_KEY = "reminderDate";

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private int key;

    @DatabaseField
    private String message;

    @DatabaseField
    private String detail;

    @DatabaseField
    private int daysRelativeToQuitDate;

    @DatabaseField
    private int daysRelativeToProgramStart;

    @DatabaseField
    private int daysRelativeToLastActivity;

    @DatabaseField
    private int daysRelativeToNoProfileSet;

    @DatabaseField
    private String timeOfDay;

    @DatabaseField
    private String openToScreen;

    @DatabaseField
    private String reminderDate;

    @DatabaseField (foreignAutoRefresh = true, foreign = true)
    private NotificationHistory notificationHistory;

    @DatabaseField (foreignAutoRefresh = true, foreign = true)
    private RecurringNotification recurringNotification;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getDaysRelativeToQuitDate() {
        return daysRelativeToQuitDate;
    }

    public void setDaysRelativeToQuitDate(int daysRelativeToQuitDate) {
        this.daysRelativeToQuitDate = daysRelativeToQuitDate;
    }

    public int getDaysRelativeToProgramStart() {
        return daysRelativeToProgramStart;
    }

    public void setDaysRelativeToProgramStart(int daysRelativeToProgramStart) {
        this.daysRelativeToProgramStart = daysRelativeToProgramStart;
    }

    public int getDaysRelativeToLastActivity() {
        return daysRelativeToLastActivity;
    }

    public void setDaysRelativeToLastActivity(int daysRelativeToLastActivity) {
        this.daysRelativeToLastActivity = daysRelativeToLastActivity;
    }

    public int getDaysRelativeToNoProfileSet() {
        return daysRelativeToNoProfileSet;
    }

    public void setDaysRelativeToNoProfileSet(int daysRelativeToNoProfileSet) {
        this.daysRelativeToNoProfileSet = daysRelativeToNoProfileSet;
    }

    public String getTimeOfDay() {
        return timeOfDay;
    }

    public void setTimeOfDay(String timeOfDay) {
        this.timeOfDay = timeOfDay;
    }

    public String getOpenToScreen() {
        return openToScreen;
    }

    public void setOpenToScreen(String openToScreen) {
        this.openToScreen = openToScreen;
    }

    public String getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(String reminderDate) {
        this.reminderDate = reminderDate;
    }

    public NotificationHistory getNotificationHistory() {
        return notificationHistory;
    }

    public void setNotificationHistory(NotificationHistory notificationHistory) {
        this.notificationHistory = notificationHistory;
    }

    public RecurringNotification getRecurringNotification() {
        return recurringNotification;
    }

    public void setRecurringNotification(RecurringNotification recurringNotification) {
        this.recurringNotification = recurringNotification;
    }

    public static void notificationFromJsonObject(JSONObject jo) {
        Notification notification = new Notification();

        try {
            notification.setKey(jo.getInt(JSON_ID_KEY));
            notification.setMessage(jo.getString(JSON_MESSAGE_KEY));
            notification.setDetail(jo.getString(JSON_DETAIL_KEY));
            notification.setDaysRelativeToQuitDate(jo.getInt(JSON_DRT_QUIT_DATE_KEY));
            notification.setDaysRelativeToProgramStart(jo.getInt(JSON_DRT_PROGRAM_START_KEY));
            notification.setDaysRelativeToLastActivity(jo.getInt(JSON_DRT_LAST_ACTIVITY_KEY));
            notification.setDaysRelativeToNoProfileSet(jo.getInt(JSON_DRT_NO_PROFILE_SET_KEY));
            notification.setTimeOfDay(jo.getString(JSON_TIME_OF_DAY_KEY));
            notification.setOpenToScreen(jo.getString(JSON_OPEN_TO_SCREEN_KEY));
            notification.setReminderDate(jo.optString(JSON_REMINDER_DATE_KEY, null));

            DbManager.getInstance().createNotification(notification);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static Notification newNotIngestedInstance() {
        Notification notification = new Notification();
        notification.setDaysRelativeToQuitDate(-1);
        notification.setDaysRelativeToNoProfileSet(-1);
        notification.setDaysRelativeToProgramStart(-1);
        notification.setDaysRelativeToLastActivity(-1);
        return notification;
    }
}