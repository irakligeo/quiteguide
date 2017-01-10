package com.mmgct.quitguide2.models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

/**
 * Created by 35527 on 12/4/2015.
 */
public class PictureNote {

    @DatabaseField (generatedId = true)
    private int id;

    @DatabaseField
    private String picturePath;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }
}
