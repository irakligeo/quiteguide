package com.mmgct.quitguide2.models;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by 35527 on 1/25/2016.
 */
public class GeoTag {

    @DatabaseField (generatedId = true)
    private int id;

    @DatabaseField
    private String address;

    @DatabaseField
    private double lat;

    @DatabaseField
    private double lon;

    @DatabaseField
    private long dateCreated;

    public GeoTag(double lat, double lon, String address, long dateCreated) {
        this.lat = lat; this.lon = lon; this.dateCreated = dateCreated; this.address = address;
    }

    // Required no arg constructor for ormlite
    public GeoTag() {
        super();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
