package com.mmgct.quitguide2.fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.View;

import com.google.android.gms.analytics.Tracker;
import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.fragments.listeners.GATrackerHostCallback;
import com.mmgct.quitguide2.fragments.listeners.HeaderCallbacks;
import com.mmgct.quitguide2.fragments.listeners.NavCallbacks;
import com.mmgct.quitguide2.managers.DbManager;
import com.mmgct.quitguide2.models.Award;
import com.mmgct.quitguide2.models.State;
import com.mmgct.quitguide2.utils.Common;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by 35527 on 10/26/2015.
 */
public abstract class BaseFragment extends Fragment {

    protected NavCallbacks mCallbacks;
    protected GATrackerHostCallback mTrackerHost;
    protected HeaderCallbacks mHeaderCallbacks;
    private State mState;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavCallbacks");
        }
        try {
            mTrackerHost = (GATrackerHostCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement GATrackerHostCallback");
        }
        try {
            mHeaderCallbacks = (HeaderCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement HeaderCallbacks");
        }
    }

    public void setGrayedOut(boolean grayedOut) {
        if (getView() == null)
            return;
        Paint grayscalePaint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        grayscalePaint.setColorFilter(new ColorMatrixColorFilter(cm));
        if (grayedOut) {
            getView().setLayerType(View.LAYER_TYPE_HARDWARE, grayscalePaint);
        } else {
            getView().setLayerType(View.LAYER_TYPE_NONE, null);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Award trophies for today if applicable
        DbManager DbInstance = DbManager.getInstance();
        State state = DbInstance.getInstance().getState();
        DateTime today = new DateTime(System.currentTimeMillis()).withTimeAtStartOfDay();
        if (state.getLastUpdate() < today.getMillis()) {
            state.setLastUpdate(today.getMillis());
            DbInstance.updateState(state);
            updateTrophies();
        }
    }

    protected void updateTrophies() {
        DbManager dbInstance = DbManager.getInstance();
        int daysSmokeFree = Common.daysFromNow(dbInstance.getProfile().getQuitDate());
        List<Award> trophies = dbInstance.getInstance().getAwards();
        if (daysSmokeFree < 0) {
            daysSmokeFree *= -1;
            for (Award trophy : trophies) {
                if (daysSmokeFree >= trophy.getReqNumber()){
                    trophy.setAwarded(true);
                    dbInstance.updateAward(trophy);
                } else {
                    trophy.setAwarded(false);
                    dbInstance.updateAward(trophy);
                }
            }
        } else {
            for (Award trophy : trophies) {
                trophy.setAwarded(false);
                dbInstance.updateAward(trophy);
            }
            Award trophy = dbInstance.getAwardByKey(1);
            trophy.setAwarded(true);
            dbInstance.updateAward(trophy);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.mCallbacks = null;
        this.mState = null;
        this.mHeaderCallbacks = null;
    }


}
