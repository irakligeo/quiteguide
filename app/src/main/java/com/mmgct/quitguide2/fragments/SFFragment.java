package com.mmgct.quitguide2.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.fragments.listeners.DrawerCallbacks;

/**
 * Created by 35527 on 11/9/2015.
 */
public class SFFragment extends BaseNoHeaderFragment {

    private View rootView;
    private DrawerCallbacks mDrawerCallbacks;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mDrawerCallbacks = (DrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Error activity must implement DrawerCallbacks");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_sf, container, false);
        bindListeners();
        return rootView;
    }

    private void bindListeners() {
        final Fragment thisFragment = this;
        rootView.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTrackerHost.send(getString(R.string.category_smoke_free), getString(R.string.action_closed));
                mTrackerHost.send(getString(R.string.category_smoke_free)+"|"+getString(R.string.action_closed));
                mCallbacks.popBackStack();
            }
        });
        rootView.findViewById(R.id.sf_btn_history).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTrackerHost.send(getString(R.string.category_smoke_free), getString(R.string.action_view_history));
                mTrackerHost.send(getString(R.string.category_smoke_free)+"|"+getString(R.string.action_view_history));
                disableAnimations();
                FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.animator.card_flip_left_in, R.animator.card_flip_left_out);
                ft.hide(thisFragment);
                ft.commit();

                Bundle args = new Bundle();
                args.putBoolean(HistoryFragment.SHOW_CLOSE_BTN, true);
                HistoryFragment historyFragment = new HistoryFragment();
                historyFragment.setArguments(args);
                FragmentTransaction ft2 = getActivity().getFragmentManager().beginTransaction();
                ft2.setCustomAnimations(R.animator.card_flip_left_in, R.animator.card_flip_left_out, R.animator.oa_slide_up, R.animator.oa_slide_down);
                ft2.add(R.id.main_fragment_container, historyFragment);
                ft2.addToBackStack("");
                ft2.commit();
            }
        });
        rootView.findViewById(R.id.sf_btn_stats).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTrackerHost.send(getString(R.string.category_smoke_free), getString(R.string.action_view_stats));
                mTrackerHost.send(getString(R.string.category_smoke_free)+"|"+getString(R.string.action_view_stats));
                mCallbacks.popBackStack();
                mDrawerCallbacks.openDrawer(DrawerCallbacks.RIGHT);
            }
        });
    }
}
