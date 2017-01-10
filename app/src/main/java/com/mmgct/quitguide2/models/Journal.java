package com.mmgct.quitguide2.models;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

/**
 * Created by 35527 on 11/30/2015.
 */
public class Journal implements Serializable {

    @DatabaseField (generatedId = true)
    private int id;

    @DatabaseField
    private long date;

    @DatabaseField
    private String title;

    @DatabaseField
    private String entry;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    @Override
    public String toString() {
        return "Journal{" +
                "id=" + id +
                ", date=" + date +
                ", title='" + title + '\'' +
                ", entry='" + entry + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Journal journal = (Journal) o;

        if (id != journal.id) return false;
        if (date != journal.date) return false;
        if (title != null ? !title.equals(journal.title) : journal.title != null) return false;
        return !(entry != null ? !entry.equals(journal.entry) : journal.entry != null);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (int) (date ^ (date >>> 32));
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (entry != null ? entry.hashCode() : 0);
        return result;
    }
}
