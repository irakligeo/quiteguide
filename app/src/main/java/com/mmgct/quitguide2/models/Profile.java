package com.mmgct.quitguide2.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by 35527 on 10/26/2015.
 */
@DatabaseTable
public class Profile {


    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private int smokeFreq;

    @DatabaseField
    private int numDailyCigs;

    @DatabaseField
    private String timeOfDay;

    @DatabaseField
    private double pricePerPack;

    @DatabaseField
    private long quitDate;

    @DatabaseField
    private String participantId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSmokeFreq() {
        return smokeFreq;
    }

    public void setSmokeFreq(int smokeFreq) {
        this.smokeFreq = smokeFreq;
    }

    public int getNumDailyCigs() {
        return numDailyCigs;
    }

    public void setNumDailyCigs(int numDailyCigs) {
        this.numDailyCigs = numDailyCigs;
    }

    public String getTimeOfDay() {
        return timeOfDay;
    }

    public void setTimeOfDay(String timeOfDay) {
        this.timeOfDay = timeOfDay;
    }

    public double getPricePerPack() {
        return pricePerPack;
    }

    public void setPricePerPack(double pricePerPack) {
        this.pricePerPack = pricePerPack;
    }

    public long getQuitDate() {
        return quitDate;
    }

    public void setQuitDate(long quitDate) {
        this.quitDate = quitDate;
    }

    public String getParticipantId() {
        return participantId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "smokeFreq=" + smokeFreq +
                ", numDailyCigs=" + numDailyCigs +
                ", timeOfDay=" + timeOfDay +
                ", pricePerPack=" + pricePerPack +
                ", quitDate=" + quitDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Profile profile = (Profile) o;

        if (id != profile.id) return false;
        if (smokeFreq != profile.smokeFreq) return false;
        if (numDailyCigs != profile.numDailyCigs) return false;
        if (Double.compare(profile.pricePerPack, pricePerPack) != 0) return false;
        if (quitDate != profile.quitDate) return false;
        return !(timeOfDay != null ? !timeOfDay.equals(profile.timeOfDay) : profile.timeOfDay != null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id;
        result = 31 * result + smokeFreq;
        result = 31 * result + numDailyCigs;
        result = 31 * result + (timeOfDay != null ? timeOfDay.hashCode() : 0);
        temp = Double.doubleToLongBits(pricePerPack);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (int) (quitDate ^ (quitDate >>> 32));
        return result;
    }

}
