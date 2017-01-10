package com.mmgct.quitguide2.fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.View;

import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.fragments.BaseFragment;
import com.mmgct.quitguide2.fragments.listeners.HeaderCallbacks;
import com.mmgct.quitguide2.utils.Constants;

/**
 * Created by 35527 on 11/11/2015.
 */
public class BaseNoHeaderFragment extends BaseFragment {

    private HeaderCallbacks mHeaderCallbacks;
    private boolean mDisableAnimations;



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
    public void onStop() {
        super.onStop();
        mHeaderCallbacks.showHeader();
        mHeaderCallbacks.enableDrawers();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mHeaderCallbacks = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        mHeaderCallbacks.hideHeader();
        mHeaderCallbacks.disableDrawers();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null && getArguments().getBoolean(Constants.ENABLE_ANIMATIONS, false)) {
            enableAnimations();
        }
    }

    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
        if (mDisableAnimations) {
            return super.onCreateAnimator(transit, enter, 0);
        }

        if (nextAnim == R.animator.card_flip_right_out || nextAnim == R.animator.card_flip_right_in){
            return AnimatorInflater.loadAnimator(getActivity(), nextAnim);
        }

        return enter ? AnimatorInflater.loadAnimator(getActivity(), R.animator.oa_slide_up)
                : AnimatorInflater.loadAnimator(getActivity(), R.animator.oa_slide_down);
    }

    public void disableAnimations(){
        mDisableAnimations = true;
    }
    public void enableAnimations() {
        mDisableAnimations = false;
    }

    public boolean isAnimationDisabled() {
        return mDisableAnimations;
    }
}
