package com.mmgct.quitguide2.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.fragments.listeners.DrawerCallbacks;
import com.mmgct.quitguide2.managers.DbManager;
import com.mmgct.quitguide2.models.Profile;

/**
 * Created by 35527 on 11/6/2015.
 */
public class SmokeDataFragment extends SmokeDataIntroFragment {

    private DrawerCallbacks mUpdateCallbacks;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mUpdateCallbacks = (DrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("ERROR: Activity must implement DrawerCallbacks");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mUpdateCallbacks = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_smoke_data, container, false);
        bindListeners();
        setSliders();
        return rootView;
    }

    /**
     * Sets sliders to the right values based on previously saved data
     */
    private void setSliders() {
        // Seek bars
        SeekBar smokeDaysFreq = (SeekBar) rootView.findViewById(R.id.seek_smokedays_frequency);
        SeekBar craveFreq = (SeekBar) rootView.findViewById(R.id.seek_crave_frequency);
        SeekBar numCigs = (SeekBar) rootView.findViewById(R.id.seek_numcigs_frequency);
        SeekBar pricePack = (SeekBar) rootView.findViewById(R.id.seek_price_pack);

        Profile profile = DbManager.getInstance().getProfile();

        smokeDaysFreq.setProgress(profile.getSmokeFreq());
        String tod;
        if ((tod = profile.getTimeOfDay()) != null) {
            if (tod.equals("დილა")) {
                craveFreq.setProgress(0);
            } else if (tod.equals("შუადღე")) {
                craveFreq.setProgress(1);
            } else {
                craveFreq.setProgress(2);
            }

        }
        numCigs.setProgress(profile.getNumDailyCigs());
        pricePack.setProgress((int)(profile.getPricePerPack()*100));
    }

    @Override
    protected void bindListeners() {
        super.bindListeners();
        // Grabbing continue button from the intro layout and changing it to save button
        ImageButton cntBtn = (ImageButton) rootView.findViewById(R.id.btn_continue);
        cntBtn.setImageResource(R.drawable.save_button);
        cntBtn.setContentDescription(getString(R.string.access_save_button));
        cntBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                // Set a filter when clicked
                try {
                    ((ImageButton) v).setColorFilter(R.color.dark_gray);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ((ImageButton) v).setColorFilter(null);
                        }
                    }, 75);
                } catch (ClassCastException e) { e.printStackTrace(); } // Should never happen

                save();
                mUpdateCallbacks.updateStats();
                mCallbacks.popBackStack();
            }
        });
    }
}
