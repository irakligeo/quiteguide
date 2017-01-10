package com.mmgct.quitguide2.fragments;

import android.app.Fragment;
import android.media.audiofx.BassBoost;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mmgct.quitguide2.R;

/**
 * Created by 35527 on 2/28/2016.
 */
public class MapWrapperFragment extends BaseFragment {

    private View rootView;
    private Fragment fragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_map_wrapper, container, false);
        getChildFragmentManager().beginTransaction().replace(R.id.container_map_wrapper, fragment, SetLocationFragment.MAP_TAG).commit();
        return rootView;
    }


    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }
}
