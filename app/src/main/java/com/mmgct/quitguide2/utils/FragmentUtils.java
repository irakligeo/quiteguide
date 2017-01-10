package com.mmgct.quitguide2.utils;

import android.app.Fragment;
import android.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 35527 on 1/22/2016.
 */
public class FragmentUtils {

    List<Fragment> fragments = new ArrayList<>();

    public static FragmentManager.OnBackStackChangedListener backstackChangeListener;

    public static FragmentManager.OnBackStackChangedListener getBackstackChangeListener() {
        return backstackChangeListener;
    }

    public static void setBackstackChangeListener(FragmentManager.OnBackStackChangedListener backstackChangeListener) {
        FragmentUtils.backstackChangeListener = backstackChangeListener;
    }
}
