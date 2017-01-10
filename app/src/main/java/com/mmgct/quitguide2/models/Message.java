package com.mmgct.quitguide2.models;

import android.util.Log;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 35527 on 10/26/2015.
 */
@DatabaseTable
public class Message {

    private static final String TAG = "Message";

    // JSON field names
    private static final String MESSAGES_ID_KEY = "messageID";
    private static final String MESSAGES_CATEGORY_KEY = "category";
    private static final String MESSAGES_TRIGGER_KEY = "trigger";
    private static final String MESSAGES_CONTENT_KEY = "messageText";

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private int key;

    @DatabaseField
    private String category;

    @DatabaseField
    private String trigger;

    @DatabaseField
    private String message;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", key=" + key +
                ", category='" + category + '\'' +
                ", trigger='" + trigger + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Message message1 = (Message) o;

        if (id != message1.id) return false;
        if (key != message1.key) return false;
        if (category != null ? !category.equals(message1.category) : message1.category != null)
            return false;
        if (trigger != null ? !trigger.equals(message1.trigger) : message1.trigger != null)
            return false;
        return !(message != null ? !message.equals(message1.message) : message1.message != null);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + key;
        result = 31 * result + (category != null ? category.hashCode() : 0);
        result = 31 * result + (trigger != null ? trigger.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }

    public static Message messageFromJSONObject(JSONObject jsonObject) {
        Message message = new Message();

        try {
            message.setKey(jsonObject.getInt(MESSAGES_ID_KEY));
            message.setCategory(jsonObject.getString(MESSAGES_CATEGORY_KEY));
            message.setMessage(jsonObject.getString(MESSAGES_CONTENT_KEY));
            message.setTrigger(jsonObject.optString(MESSAGES_TRIGGER_KEY));
        } catch (JSONException e) {
            Log.e(TAG, "Error occured while ingesting content.", e);
        }

        return message;
    }
}
