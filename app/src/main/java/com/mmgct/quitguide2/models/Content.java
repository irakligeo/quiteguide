package com.mmgct.quitguide2.models;

import android.util.Log;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Represents a content record.
 *
 * Created by 35527 on 10/26/2015.
 */
@DatabaseTable
public class Content implements Serializable {

    private static final String TAG = "Content";

    // JSON field names
    private static final String CONTENT_DESC_KEY = "contentDescription";
    private static final String CONTENT_TYPE_KEY = "contentType";

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String contentDescription;

    @DatabaseField
    private String contentType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContentDescription() {
        return contentDescription;
    }

    public void setContentDescription(String contentDescription) {
        this.contentDescription = contentDescription;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public String toString() {
        return "Content{" +
                "id=" + id +
                ", contentDescription='" + contentDescription + '\'' +
                ", contentType='" + contentType + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Content content = (Content) o;

        if (id != content.id) return false;
        if (contentDescription != null ? !contentDescription.equals(content.contentDescription) : content.contentDescription != null)
            return false;
        return !(contentType != null ? !contentType.equals(content.contentType) : content.contentType != null);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (contentDescription != null ? contentDescription.hashCode() : 0);
        result = 31 * result + (contentType != null ? contentType.hashCode() : 0);
        return result;
    }

    public static Content contentFromJSONObject(JSONObject jsonObject) {
        Content content = new Content();

        try {
            content.setContentDescription(jsonObject.getString(CONTENT_DESC_KEY));
            content.setContentType(jsonObject.getString(CONTENT_TYPE_KEY));
        } catch (JSONException e) {
            Log.e(TAG, "Error occured while ingesting content.", e);
        }

        return content;
    }
}
