package com.mmgct.quitguide2.models;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by 35527 on 11/19/2015.
 */
public class SmokeFreeDay {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private long date;

    // Required no arg constructor
    public SmokeFreeDay() {}

    public SmokeFreeDay(long date) {
        this.date = date;
    }

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
}
