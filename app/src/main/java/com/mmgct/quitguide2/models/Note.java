package com.mmgct.quitguide2.models;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by 35527 on 11/30/2015.
 */
public class Note {

    public Note (){}

    public Note(long date, String note) {
        this.date = date;
        this.note = note;
    }

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private long date;

    @DatabaseField
    private String note;

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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
