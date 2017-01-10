package com.mmgct.quitguide2.fragments.listeners;

/**
 * Created by 35527 on 11/6/2015.
 */
public interface DrawerCallbacks {
    final static int LEFT = 0;
    final static int RIGHT = 1;

    void updateStats();
    void openDrawer(int side);
}
