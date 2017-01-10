package com.mmgct.quitguide2.models;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by 35527 on 11/19/2015.
 */
public class Slip {

    @DatabaseField (generatedId = true)
    private int id;

    @DatabaseField
    private long date;

    @DatabaseField (foreign = true, foreignAutoRefresh = true)
    private Content trigger;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public Content getTrigger() {
        return trigger;
    }

    public void setTrigger(Content trigger) {
        this.trigger = trigger;
    }
}
