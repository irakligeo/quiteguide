package com.mmgct.quitguide2.fragments;

import android.app.Activity;
import android.os.Bundle;

import com.mmgct.quitguide2.fragments.listeners.HeaderCallbacks;

/**
 * Created by 35527 on 11/9/2015.
 */
public class HeaderBaseFragment extends BaseFragment {
    protected HeaderCallbacks mHeaderCallbacks;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mHeaderCallbacks = (HeaderCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Error activity must implement HeaderCallbacks");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHeaderCallbacks.showBack();
    }

    @Override
    public void onStop() {
        super.onStop();
        mHeaderCallbacks.showMain();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mHeaderCallbacks = null;
    }
}
