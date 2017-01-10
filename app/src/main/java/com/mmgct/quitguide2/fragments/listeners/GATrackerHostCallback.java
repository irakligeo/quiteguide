package com.mmgct.quitguide2.fragments.listeners;

import com.google.android.gms.analytics.Tracker;

/**
 * Created by 35527 on 1/6/2016.
 */
public interface GATrackerHostCallback {
    Tracker getTracker();
    void send(String category, String action);
    void send(String category, String action, String label);
    void send(String label);
}
