package com.mmgct.quitguide2.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.managers.DbManager;
import com.mmgct.quitguide2.models.HistoryItem;
import com.mmgct.quitguide2.models.Mood;
import com.mmgct.quitguide2.utils.Common;

/**
 *
 * Created by 35527 on 11/18/2015.
 */
public class MoodFragment extends BaseFlipOutFragment implements View.OnClickListener {

    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_mood, container, false);
        bindListeners();
        return rootView;
    }

    private void bindListeners() {
        rootView.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getFragmentManager().popBackStack();
            }
        });
        bindFaces((LinearLayout) rootView.findViewById(R.id.layout_face_grid));
    }

    /**
     * Gets all leaves of View tree and set click listener to this if instanceof ImageButton or calls
     * @param vg - Root viewgroup we are recursing through
     */
    private void bindFaces(ViewGroup vg) {
        for (int i = 0; i < vg.getChildCount(); i++) {
            View v = vg.getChildAt(i);
            if (v instanceof ImageButton) {
                v.setOnClickListener(this);
            } else if (v instanceof ViewGroup) {
                bindFaces((ViewGroup) v);
            }
        }
    }

    @Override
    public void onClick(View v) {
        Mood mood;
        switch(v.getId()) {
            case(R.id.btn_happy):
                DbManager.getInstance().createMood(mood = new Mood(Common.millisAtStartOfDay(System.currentTimeMillis()), "happy"));
                createHistoryItem(mood);
                flipOut();
                mTrackerHost.send(getString(R.string.category_mood), getString(R.string.action_type), mood.getType());
                mTrackerHost.send(getString(R.string.category_mood)+"|"+getString(R.string.action_type)+"|"+mood.getType());
                mCallbacks.onAddNavigationAction(newHappySubcreen(), "", true);
                break;
            case(R.id.btn_excited):
                DbManager.getInstance().createMood(mood = new Mood(Common.millisAtStartOfDay(System.currentTimeMillis()), "excited"));
                createHistoryItem(mood);
                flipOut();
                mTrackerHost.send(getString(R.string.category_mood), getString(R.string.action_type), mood.getType());
                mTrackerHost.send(getString(R.string.category_mood)+"|"+getString(R.string.action_type)+"|"+mood.getType());
                mCallbacks.onAddNavigationAction(newHappySubcreen(), "", true);
                break;
            case(R.id.btn_relaxed):
                DbManager.getInstance().createMood(mood = new Mood(Common.millisAtStartOfDay(System.currentTimeMillis()), "relaxed"));
                createHistoryItem(mood);
                flipOut();
                mTrackerHost.send(getString(R.string.category_mood), getString(R.string.action_type), mood.getType());
                mTrackerHost.send(getString(R.string.category_mood)+"|"+getString(R.string.action_type)+"|"+mood.getType());
                mCallbacks.onAddNavigationAction(newHappySubcreen(), "", true);
                break;
            case(R.id.btn_angry):
                DbManager.getInstance().createMood(mood = new Mood(Common.millisAtStartOfDay(System.currentTimeMillis()), "angry"));
                createHistoryItem(mood);
                flipOut();
                mTrackerHost.send(getString(R.string.category_mood), getString(R.string.action_type), mood.getType());
                mTrackerHost.send(getString(R.string.category_mood)+"|"+getString(R.string.action_type)+"|"+mood.getType());
                mCallbacks.onAddNavigationAction(newNotHappySubscreen(), "", true);
                break;
            case(R.id.btn_anxious):
                DbManager.getInstance().createMood(mood = new Mood(Common.millisAtStartOfDay(System.currentTimeMillis()), "anxious"));
                createHistoryItem(mood);
                flipOut();
                mTrackerHost.send(getString(R.string.category_mood), getString(R.string.action_type), mood.getType());
                mTrackerHost.send(getString(R.string.category_mood)+"|"+getString(R.string.action_type)+"|"+mood.getType());
                mCallbacks.onAddNavigationAction(newNotHappySubscreen(), "", true);
                break;
            case(R.id.btn_frustrated):
                DbManager.getInstance().createMood(mood = new Mood(Common.millisAtStartOfDay(System.currentTimeMillis()), "frustrated"));
                createHistoryItem(mood);
                flipOut();
                mTrackerHost.send(getString(R.string.category_mood), getString(R.string.action_type), mood.getType());
                mTrackerHost.send(getString(R.string.category_mood)+"|"+getString(R.string.action_type)+"|"+mood.getType());
                mCallbacks.onAddNavigationAction(newNotHappySubscreen(), "", true);
                break;
            case(R.id.btn_nervous):
                DbManager.getInstance().createMood(mood = new Mood(Common.millisAtStartOfDay(System.currentTimeMillis()), "nervous"));
                createHistoryItem(mood);
                flipOut();
                mTrackerHost.send(getString(R.string.category_mood), getString(R.string.action_type), mood.getType());
                mTrackerHost.send(getString(R.string.category_mood)+"|"+getString(R.string.action_type)+"|"+mood.getType());
                mCallbacks.onAddNavigationAction(newNotHappySubscreen(), "", true);
                break;
            case(R.id.btn_sad):
                DbManager.getInstance().createMood(mood = new Mood(Common.millisAtStartOfDay(System.currentTimeMillis()), "sad"));
                createHistoryItem(mood);
                flipOut();
                mTrackerHost.send(getString(R.string.category_mood), getString(R.string.action_type), mood.getType());
                mTrackerHost.send(getString(R.string.category_mood)+"|"+getString(R.string.action_type)+"|"+mood.getType());
                mCallbacks.onAddNavigationAction(newNotHappySubscreen(), "", true);
                break;
            case(R.id.btn_stressed):
                DbManager.getInstance().createMood(mood = new Mood(Common.millisAtStartOfDay(System.currentTimeMillis()), "stressed"));
                createHistoryItem(mood);
                flipOut();
                mTrackerHost.send(getString(R.string.category_mood), getString(R.string.action_type), mood.getType());
                mTrackerHost.send(getString(R.string.category_mood)+"|"+getString(R.string.action_type)+"|"+mood.getType());
                mCallbacks.onAddNavigationAction(newNotHappySubscreen(), "", true);
                break;
        }
    }

    private void createHistoryItem(Mood mood) {
        DbManager.getInstance().createHistoryItem(new HistoryItem(System.currentTimeMillis(), mood, HistoryItem.MOOD));
    }

    private Fragment newNotHappySubscreen() {
        Fragment f = new MoodSub2Fragment();
        f.setTargetFragment(this, 0);
        return f;
    }

    private Fragment newHappySubcreen() {
        Fragment f = new MoodSub1Fragment();
        f.setTargetFragment(this, 0);
        return f;
    }
}
