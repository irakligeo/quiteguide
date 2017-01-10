package com.mmgct.quitguide2.fragments.listeners;

import android.app.Fragment;
import android.os.Bundle;

/**
 * Created by 35527 on 10/26/2015.
 */
public interface NavCallbacks {

    void onNavigationAction(Fragment fragment, String tag, boolean addToStack);
    void onAnimatedNavigationAction(Fragment fragment, String tag, int enterId, int exitId, int popEnterId, int popExitId, boolean addToStack);
    void onNoPopAnimatedNavigationAction(Fragment fragment, String tag, int enterId, int exitId, boolean addToStack);
    void onAddAnimationNavigationAction(Fragment fragment, String tag, int enterId, int exitId, int popEnterId, int popExitId, boolean addToStack);
    void onAddNavigationAction(Fragment fragment, String tag, boolean addToStack);
    void onFragmentFinished(String tag, Bundle bundle);
    void popBackStack();
    void clearBackStack(Fragment loadAfterClear);
}
