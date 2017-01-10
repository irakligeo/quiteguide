package com.mmgct.quitguide2.models;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by 35527 on 11/19/2015.
 */
public class Mood {

    @DatabaseField (generatedId = true)
    private int id;

    @DatabaseField
    private long date;

    @DatabaseField
    private String type;

    public Mood() {}

    public Mood(long date, String type) {
        this.date = date;
        this.type = type;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
