package com.mmgct.quitguide2.utils;

import android.content.Context;
import android.util.Log;

import com.mmgct.quitguide2.managers.DbManager;
import com.mmgct.quitguide2.models.Award;
import com.mmgct.quitguide2.models.Content;
import com.mmgct.quitguide2.models.Message;
import com.mmgct.quitguide2.models.Notification;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by 35527 on 10/26/2015.
 */
public class ContentIngestor {

    private static final String TAG = "Content Ingestor";

    private static final String MESSAGES_KEY = "Msg";
    private static final String CONTENT_KEY = "Content";
    private static final String AWARD_KEY = "Award";
    private static final String NOTIFICATION_KEY = "Notification";

    private Context mContext;
    private DbManager mDbManager;

    public ContentIngestor(Context context) {
        mContext = context;
        mDbManager = DbManager.getInstance();
    }

    public String loadJSONFromAsset(String content) {

        String json;

        try {

            InputStream is = mContext.getAssets().open(content);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public void ingestMessageJSON() {
        String json = loadJSONFromAsset(Constants.MESSAGE_FILE_NAME);

        try {
            JSONObject rootJsonObject = new JSONObject(json);
            JSONArray messages = rootJsonObject.getJSONArray(MESSAGES_KEY);

            for (int i = 0; i < messages.length(); i++) {
                Message msg = Message.messageFromJSONObject(
                        messages.getJSONObject(i));

                mDbManager.createMessage(msg);
            }

        } catch (JSONException e) {
            Log.e(TAG, "Error ingesting message json.", e);
        }
        Log.v(TAG, "Ingested all messages.");
    }

    public void ingestContentJSON() {
        String json = loadJSONFromAsset(Constants.CONTENT_FILE_NAME);

        try {
            JSONObject rootJsonObject = new JSONObject(json);

            // Ingest content array
            JSONArray content = rootJsonObject.getJSONArray(CONTENT_KEY);
            for (int i = 0; i < content.length(); i++) {
                Content c = Content.contentFromJSONObject(
                        content.getJSONObject(i));
                mDbManager.createContent(c);
            }
            // Ingest award array
            JSONArray awards = rootJsonObject.getJSONArray(AWARD_KEY);
            for (int j = 0; j < awards.length(); j++) {
                Award award = Award.awardFromJSONObject(awards.getJSONObject(j));

                mDbManager.createAward(award);
            }


        } catch (JSONException e) {
            Log.e(TAG, "Error ingesting message json.", e);
        }
        Log.v(TAG, "Ingested all content.");
    }

    public void ingestNotificationJSON() {
        String json = loadJSONFromAsset(Constants.NOTIFICATION_FILE_NAME);
        try {
            JSONObject rootJsonObject = new JSONObject(json);
            JSONArray notifications = rootJsonObject.getJSONArray(NOTIFICATION_KEY);

            for (int i = 0; i < notifications.length(); i++) {
                Notification.notificationFromJsonObject(notifications.getJSONObject(i));
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error ingesting message json.", e);
        }
        Log.v(TAG, "Ingested notifications.");
    }
}
