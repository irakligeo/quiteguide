package com.mmgct.quitguide2.models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Arrays;

/**
 * Created by 35527 on 10/30/2015.
 */
@DatabaseTable
public class ReasonForQuitting {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String imagePath;

    @DatabaseField
    private String message;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
