package com.mmgct.quitguide2.fragments;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.view.View;

import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.fragments.dialogs.SlidingDatePickerDiag;
import com.mmgct.quitguide2.managers.DbManager;
import com.mmgct.quitguide2.models.Profile;
import com.mmgct.quitguide2.utils.Common;

/**
 * Created by 35527 on 10/28/2015.
 */
public abstract class QuitDateBaseFragment extends HeaderBaseFragment implements SlidingDatePickerDiag.DatePickerDiagCallback {

    /**
     * Date picker slides up from a bottom dialog
     */
    public void openDatePickerDialog() {
        new SlidingDatePickerDiag().show(getActivity().getFragmentManager(), "");
    }


    /**
     * Callback from SlidingDatePickerDialog
     * @param date - date in the form of a long.
     */
    @Override
    public void onDateSaved(long date) {
        // GA
        mTrackerHost.send(getString(R.string.category_quit_date), getString(R.string.action_quit_date_set), Common.formatTimestamp("MM/dd/yyyy", date));
        mTrackerHost.send(getString(R.string.category_quit_date)+"|"+getString(R.string.action_quit_date_set)+"|"+Common.formatTimestamp("MM/dd/yyyy", date));
        enableShare(date);
        allowContinue();
        // Save to db
        Profile profile = DbManager.getInstance().getProfile();
        profile.setQuitDate(date);
        DbManager.getInstance().updateprofile(profile);
        // Update trophies
        updateTrophies();
        // Update UI
        setDate(date);
    }


    public abstract void allowContinue();
    public abstract void enableShare(long date);
    public abstract void setDate(long date);

}
