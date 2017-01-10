package com.mmgct.quitguide2.models;

import android.util.Log;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by 35527 on 12/14/2015.
 * Has reference to one of the four history items in the history list section.
 */
public class HistoryItem {

    public static final String SMK_FREE_DAY = "SmokeFreeDay";
    public static final String SLIP = "Slip";
    public static final String CRAVING = "Craving";
    public static final String MOOD = "Mood";
    private static final String TAG = "HistoryItem";

    public HistoryItem() {}
    public HistoryItem(long timestamp, Object item, String type) {
        try {
            switch (type) {
                case (SMK_FREE_DAY):
                    smokeFreeDay = (SmokeFreeDay) item;
                    break;
                case (SLIP):
                    slip = (Slip) item;
                    break;
                case (CRAVING):
                    craving = (Craving) item;
                    break;
                case (MOOD):
                    mood = (Mood) item;
                    break;
            }
        } catch (ClassCastException e) {
            Log.e(TAG, String.format("Error casting %s to %s.", item.getClass().getSimpleName(), type));
            e.printStackTrace();
        }
        date = timestamp;
        this.type = type;
    }

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    Slip slip;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    Craving craving;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    SmokeFreeDay smokeFreeDay;

    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    Mood mood;

    @DatabaseField
    String type;

    @DatabaseField
    long date;

    public Slip getSlip() {
        return slip;
    }

    public void setSlip(Slip slip) {
        this.slip = slip;
    }

    public Craving getCraving() {
        return craving;
    }

    public void setCraving(Craving craving) {
        this.craving = craving;
    }

    public SmokeFreeDay getSmokeFreeDay() {
        return smokeFreeDay;
    }

    public void setSmokeFreeDay(SmokeFreeDay smokeFreeDay) {
        this.smokeFreeDay = smokeFreeDay;
    }

    public Mood getMood() {
        return mood;
    }

    public void setMood(Mood mood) {
        this.mood = mood;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "HistoryItem{" +
                "slip=" + slip +
                ", craving=" + craving +
                ", smokeFreeDay=" + smokeFreeDay +
                ", mood=" + mood +
                ", type='" + type + '\'' +
                ", date=" + date +
                '}';
    }
}
