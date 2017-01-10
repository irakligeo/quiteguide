package com.mmgct.quitguide2.fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.fragments.dialogs.TriggerPickerDialog;
import com.mmgct.quitguide2.fragments.listeners.DialogDismissListener;
import com.mmgct.quitguide2.managers.DbManager;
import com.mmgct.quitguide2.models.Craving;
import com.mmgct.quitguide2.models.HistoryItem;
import com.mmgct.quitguide2.models.Slip;
import com.mmgct.quitguide2.utils.Common;

/**
 * Created by 35527 on 11/16/2015.
 */
public class CravingFragment extends BaseFlipOutFragment implements DialogDismissListener {

    public static final String CRAVING_TRIGGER = "craving_trigger";
    private View rootView;
    private int mCravingIntensity = 1; // Default
    private String mTriggerSelection;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.Theme_Orange_QuitGuideTheme);
        LayoutInflater localInflater = inflater.from(contextThemeWrapper);
        rootView = localInflater.inflate(R.layout.fragment_craving, container, false);
        bindListeners();
        return rootView;
    }

    private void bindListeners() {
        rootView.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
                mCallbacks.popBackStack();
            }
        });

        // Seek bar listener
        SeekBar seekBar = (SeekBar) rootView.findViewById(R.id.craving_seekbar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mCravingIntensity = progress;
                TextView cravingIntensityLevel = (TextView) rootView.findViewWithTag(String.valueOf(progress));
                if (cravingIntensityLevel != null) {
                    resetIntensities();
                    cravingIntensityLevel.setTextColor(Color.WHITE);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Trigger select listener

        final Fragment fragment = this;

        View selectTriggerLayout = rootView.findViewById(R.id.craving_trigger_select);
        selectTriggerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TriggerPickerDialog triggerPickerDialog = new TriggerPickerDialog();
                triggerPickerDialog.setTargetFragment(fragment, 0);
                triggerPickerDialog.show(getFragmentManager(), "");
            }
        });

        View contBtn = rootView.findViewById(R.id.btn_continue);
        contBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
                Fragment craving2 = new Craving2Fragment();
                craving2.setTargetFragment(fragment, 0);
                if (mTriggerSelection != null) {
                    Bundle args = new Bundle();
                    args.putString(CRAVING_TRIGGER, mTriggerSelection);
                    craving2.setArguments(args);
                }
                flipOut();
                mCallbacks.onAddNavigationAction(craving2, "", true);
            }
        });
    }

    private void save() {
        // GA
        mTrackerHost.send(getString(R.string.category_craving), getString(R.string.action_craving_level), String.valueOf(mCravingIntensity));
        mTrackerHost.send(getString(R.string.category_craving)+"|"+getString(R.string.action_craving_level)+"|"+String.valueOf(mCravingIntensity));

        Craving craving = new Craving();
        craving.setDate(Common.millisAtStartOfDay(System.currentTimeMillis()));
        craving.setCravingIntensity(mCravingIntensity);
        if (mTriggerSelection != null) {
            craving.setTrigger(DbManager.getInstance().getTriggerByDesc(mTriggerSelection));
        }
        DbManager.getInstance().createCraving(craving);
        DbManager.getInstance().createHistoryItem(new HistoryItem(System.currentTimeMillis(), craving, HistoryItem.CRAVING));
    }
    
    private void resetIntensities() {
        for (int i = 1; i <= 10; i++) {
            TextView cravingIntensity = (TextView) rootView.findViewWithTag(String.valueOf(i)) ;
            if (cravingIntensity != null) {
                cravingIntensity.setTextColor(Color.BLACK);
            }
        }
    }

    @Override
    public void onDialogDismissed(Bundle args) {
        mTriggerSelection = args.getString(TriggerPickerDialog.ARG_KEY);
        if (mTriggerSelection == null || mTriggerSelection.equals("")) {
            ((TextView)rootView.findViewById(R.id.trigger_selection)).setText(getResources().getString(R.string.default_trigger));
        } else {
            ((TextView) rootView.findViewById(R.id.trigger_selection)).setText(mTriggerSelection);
        }
    }


}
