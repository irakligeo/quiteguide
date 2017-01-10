package com.mmgct.quitguide2.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mmgct.quitguide2.managers.DbManager;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by 35527 on 10/26/2015.
 */
public class DbCreator extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "quitguide.db";
    private static final int DATABASE_VERSION = 3;
    public static final String TAG = DbCreator.class.getSimpleName();

    public DbCreator(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            // Necessary for the database use.
            getReadableDatabase();
            close();
    }

    public DbCreator(Context context, boolean overwriteDb) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // Necessary for the database use.
        if (overwriteDb) {
            setForcedUpgrade();
        }
        getReadableDatabase();
        close();
    }
}