package com.mmgct.quitguide2.models;


import com.j256.ormlite.field.DatabaseField;

/**
 * Created by 35527 on 1/25/2016.
 */
public class RecurringNotification {

    @DatabaseField (generatedId = true)
    private int id;

    @DatabaseField (foreign = true, foreignAutoRefresh = true)
    private Notification notification;

    @DatabaseField (foreign = true, foreignAutoRefresh = true)
    private GeoTag geoTag;

    @DatabaseField (foreign = true, foreignAutoRefresh = true)
    private Tip tip;

    @DatabaseField
    private boolean active;

    @DatabaseField
    private boolean useRandTip;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public GeoTag getGeoTag() {
        return geoTag;
    }

    public void setGeoTag(GeoTag geoTag) {
        this.geoTag = geoTag;
    }

    public Tip getTip() {
        return tip;
    }

    public void setTip(Tip tip) {
        this.tip = tip;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isUseRandTip() {
        return useRandTip;
    }

    public void setUseRandTip(boolean useRandTip) {
        this.useRandTip = useRandTip;
    }

    @Override
    public String toString() {
        return "RecurringNotification{" +
                "id=" + id +
                ", notification=" + notification +
                ", geoTag=" + geoTag +
                ", tip=" + tip +
                ", active=" + active +
                ", useRandTip=" + useRandTip +
                '}';
    }

}
