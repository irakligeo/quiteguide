package com.mmgct.quitguide2.models;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by 35527 on 12/28/2015.
 */
public class NotificationHistory {

    @DatabaseField (generatedId = true)
    private int id;

    @DatabaseField
    private long scheduledDeliveryDate;

    @DatabaseField
    private long actualDeliveryDate;

    @DatabaseField (foreignAutoRefresh = true, foreign = true)
    private Notification notification;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getScheduledDeliveryDate() {
        return scheduledDeliveryDate;
    }

    public void setScheduledDeliveryDate(long scheduledDeliveryDate) {
        this.scheduledDeliveryDate = scheduledDeliveryDate;
    }

    public long getActualDeliveryDate() {
        return actualDeliveryDate;
    }

    public void setActualDeliveryDate(long actualDeliveryDate) {
        this.actualDeliveryDate = actualDeliveryDate;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    @Override
    public String toString() {
        return "NotificationHistory{" +
                "id=" + id +
                ", scheduledDeliveryDate=" + scheduledDeliveryDate +
                ", actualDeliveryDate=" + actualDeliveryDate +
                ", notification=" + notification +
                '}';
    }
}
