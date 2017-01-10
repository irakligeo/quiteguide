package com.mmgct.quitguide2.fragments.listeners;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by 35527 on 11/9/2015.
 */
public interface HeaderCallbacks {
    void showBack();
    void showBackBlack();
    void showMain();
    void slideHeaderUp();
    void slideHeaderDown();
    void hideHeader();
    void showHeader();
    void disableDrawers();
    void enableDrawers();
    void showSave();
    void setOnSaveListener(OnSaveListener saveListener);
    void showBackWithPlus();
    void hideAllHeaderViews();
    void showBackNotification();
    ViewGroup getHeaderLayout(int layoutId);

      abstract class OnSaveListener {
        public abstract void onSaved();
    }
}
