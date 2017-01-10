package com.mmgct.quitguide2.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mmgct.quitguide2.IntroActivity;
import com.mmgct.quitguide2.MainActivity;
import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.managers.DbManager;
import com.mmgct.quitguide2.models.Profile;

/**
 * Created by 35527 on 11/3/2015.
 */
public class SmokeDataIntroFragment extends SmokeDataBaseFragment {

    private static final String TAG = "SmokeDataIntro";
    private int mSmokeFreq, mNumDailyCigs;
    String mTimeOfDay;
    private double mPricePerPack;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_intro_smoke_data, null, false);
        bindListeners();
        return rootView;
    }

    /**
     * Sets click listeners on View objects.
     */
    protected void bindListeners() {
        ImageButton continueBtn = (ImageButton) rootView.findViewById(R.id.btn_continue);
        // Seek bars
        SeekBar smokeDaysFreq = (SeekBar) rootView.findViewById(R.id.seek_smokedays_frequency);
        SeekBar craveFreq = (SeekBar) rootView.findViewById(R.id.seek_crave_frequency);
        SeekBar numCigs = (SeekBar) rootView.findViewById(R.id.seek_numcigs_frequency);
        SeekBar pricePack = (SeekBar) rootView.findViewById(R.id.seek_price_pack);
        // Labels
        final TextView smokeDaysVal = (TextView) rootView.findViewById(R.id.val_smokedays_frequency);
        final TextView craveVal = (TextView) rootView.findViewById(R.id.val_crave_frequency);
        final TextView numCigsVal = (TextView) rootView.findViewById(R.id.val_numcigs_frequency);
        final TextView pricePackVal = (TextView) rootView.findViewById(R.id.val_price_pack);


        if (continueBtn != null) {
            continueBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    save();
                    mCallbacks.onAnimatedNavigationAction(new TutorialFragment(), "", R.animator.card_flip_left_in, R.animator.card_flip_left_out, R.animator.card_flip_right_in, R.animator.card_flip_right_out, true);
                }
            });
        }
        if (smokeDaysFreq != null) {
            smokeDaysFreq.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (progress == 7) {
                        mSmokeFreq = progress;
                        smokeDaysVal.setText("ყოველდღე");
                    } else {
                        smokeDaysVal.setText(String.format("%d %s", mSmokeFreq = progress, progress == 1 ? "დღე კვირაში" : "დღეები კვირაში"));
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }
        if (craveFreq != null) {
            craveFreq.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (progress == 0) {
                        craveVal.setText(mTimeOfDay = "დილა");
                    } else if (progress == 1) {
                        craveVal.setText(mTimeOfDay = "შუადღე");
                    } else {
                        craveVal.setText(mTimeOfDay = "საღამო");
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }
        if (numCigs != null) {
            numCigs.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    numCigsVal.setText(String.valueOf(mNumDailyCigs = progress));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }
        if (pricePack != null) {
            pricePack.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    mPricePerPack = (double) progress / 100;
                    pricePackVal.setText(String.format("$%.2f", mPricePerPack));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }
    }

    @Override
    protected void save() {
        // Default mTimeOfDay to 'Morning'
        if (mTimeOfDay == null) {
            mTimeOfDay = "დილა";
        }

        Profile profile = DbManager.getInstance().getProfile();

        // Google analytics
        if (profile.getNumDailyCigs() != mNumDailyCigs) {
            mTrackerHost.send(getString(R.string.category_track), getString(R.string.action_number_of_cigs), String.valueOf(mNumDailyCigs));
            mTrackerHost.send(getString(R.string.category_track)+"|"+getString(R.string.action_number_of_cigs)+"|"+String.valueOf(mNumDailyCigs));
        }
        if (profile.getSmokeFreq() != mSmokeFreq) {
            mTrackerHost.send(getString(R.string.category_track), getString(R.string.action_number_of_smoking_days), String.valueOf(mSmokeFreq));
            mTrackerHost.send(getString(R.string.category_track)+"|"+getString(R.string.action_number_of_smoking_days)+"|"+String.valueOf(mSmokeFreq));
        }
        if (profile.getPricePerPack() != mPricePerPack) {
            mTrackerHost.send(getString(R.string.category_track), getString(R.string.action_price_per_pack), String.valueOf(mPricePerPack));
            mTrackerHost.send(getString(R.string.category_track)+"|"+getString(R.string.action_price_per_pack)+"|"+String.valueOf(mPricePerPack));
        }
        if (profile.getTimeOfDay() == null || !profile.getTimeOfDay().equals(mTimeOfDay)) {
            mTrackerHost.send(getString(R.string.category_track), getString(R.string.action_time_of_day), mTimeOfDay);
            mTrackerHost.send(getString(R.string.category_track)+"|"+getString(R.string.action_time_of_day)+"|"+mTimeOfDay);
        }

        profile.setNumDailyCigs(mNumDailyCigs);
        profile.setPricePerPack(mPricePerPack);
        profile.setTimeOfDay(mTimeOfDay);
        profile.setSmokeFreq(mSmokeFreq);
        DbManager.getInstance().updateprofile(profile);

}
}
