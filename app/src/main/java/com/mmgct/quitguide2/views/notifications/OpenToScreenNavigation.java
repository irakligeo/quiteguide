package com.mmgct.quitguide2.views.notifications;

/**
 * Created by 35527 on 12/29/2015.
 */
public interface OpenToScreenNavigation {

    // Keys for screen navigation see notifications.json
    String HOME = "HOME";
    String MY_QUIT_PLAN = "MY_QUIT_PLAN";
    String HOW_TO_QUIT = "HOW_TO_QUIT";
    String MANAGE_MY_MOOD = "MANAGE_MY_MOOD";
    String JOURNAL = "JOURNAL";
    String TRACK_MY_CRAVINGS = "TRACK_MY_CRAVINGS";
    String STATISTICS = "STATISTICS";
    String TIP = "TIP";

    void openToScreen(String screenKey);
}
