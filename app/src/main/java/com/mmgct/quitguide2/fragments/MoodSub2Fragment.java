package com.mmgct.quitguide2.fragments;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.utils.Constants;

/**
 * Created by 35527 on 11/25/2015.
 */
public class MoodSub2Fragment extends BaseFlipInFragment {

    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_mood_sub_2, container, false);
        bindListeners();
        return rootView;
    }

    private void bindListeners() {
        final Fragment target = this;

        rootView.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayBackAnimation = false;
                mTrackerHost.send(getString(R.string.category_mood), getString(R.string.action_negative_mood), getString(R.string.label_closed));
                mTrackerHost.send(getString(R.string.category_mood)+"|"+getString(R.string.action_negative_mood)+"|"+getString(R.string.label_closed));
                mCallbacks.popBackStack();
                mCallbacks.popBackStack();
            }
        });

        rootView.findViewById(R.id.btn_mood2_call_friend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTrackerHost.send(getString(R.string.category_mood), getString(R.string.action_negative_mood), getString(R.string.label_call_friend));
                mTrackerHost.send(getString(R.string.category_mood)+"|"+getString(R.string.action_negative_mood)+"|"+getString(R.string.label_call_friend));
                showContacts();
            }
        });

        rootView.findViewById(R.id.btn_mood2_read_notes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment f = new ReadNotesFragment();
                f.setTargetFragment(target, 0);
                flipOut();
                mTrackerHost.send(getString(R.string.category_mood), getString(R.string.action_negative_mood), getString(R.string.label_read_notes));
                mTrackerHost.send(getString(R.string.category_mood)+"|"+getString(R.string.action_negative_mood)+"|"+getString(R.string.label_read_notes));
                mCallbacks.onAddNavigationAction(f, "", true);
            }
        });

        rootView.findViewById(R.id.btn_mood2_tips).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment f = new TipFragment();
                f.setTargetFragment(target, 0);
                flipOut();
                mTrackerHost.send(getString(R.string.category_mood), getString(R.string.action_negative_mood), getString(R.string.label_tips_distractions));
                mTrackerHost.send(getString(R.string.category_mood)+"|"+getString(R.string.action_negative_mood)+"|"+getString(R.string.label_tips_distractions));
                mCallbacks.onAddNavigationAction(f, "", true);
            }
        });

        ImageButton journal = (ImageButton) rootView.findViewById(R.id.btn_mood2_journal);
        journal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JournalFragment jf = new JournalFragment();
                jf.setAnimationType(Constants.ANIM_FLIP_LEFT_IN);
                jf.setTargetFragment(target, 0);
                Bundle args = new Bundle();
                args.putBoolean(JournalFragment.SHOW_CLOSE_BTN, true);
                jf.setArguments(args);
                flipOut();
                mTrackerHost.send(getString(R.string.category_mood), getString(R.string.action_negative_mood), getString(R.string.label_journal));
                mTrackerHost.send(getString(R.string.category_mood)+"|"+getString(R.string.action_negative_mood)+"|"+getString(R.string.label_journal));
                mCallbacks.onAddNavigationAction(jf, "", true);
            }
        });
    }

    void showContacts()
    {
//        Intent i = new Intent();
//        i.setComponent(new ComponentName("com.android.contacts", "com.android.contacts.DialtactsContactsEntryActivity"));
//        i.setAction("android.intent.action.MAIN");
//        i.addCategory("android.intent.category.LAUNCHER");
//        i.addCategory("android.intent.category.DEFAULT");
//        startActivity(i);

        Intent i = new Intent();
        i.setAction(Intent.ACTION_VIEW);
        i.setData(Uri.parse("content://contacts/people/"));
        startActivity(i);
    }
}
