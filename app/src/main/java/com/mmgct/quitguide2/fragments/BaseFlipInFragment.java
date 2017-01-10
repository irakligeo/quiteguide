package com.mmgct.quitguide2.fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.os.Bundle;
import android.view.View;

import com.mmgct.quitguide2.R;

/**
 * For fragments that's enter animation is flip in, back animation is flip out, and otherwise slides
 * down
 * Created by 35527 on 11/25/2015.
 */
public abstract class BaseFlipInFragment extends BaseFragment {

    protected boolean mPlayBackAnimation;
    protected boolean mDisableAnimations;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPlayBackAnimation = true;
    }

    @Override
    public void onDestroyView() {
        if (getTargetFragment() != null && getTargetFragment() instanceof BaseFlipOutFragment) {
            BaseFlipOutFragment flipOutFragment = (BaseFlipOutFragment) getTargetFragment();
            if (mPlayBackAnimation) {
                flipOutFragment.flipIn();
            } else {
                flipOutFragment.disableAnimations();
            }
        } else if (getTargetFragment() != null && getTargetFragment() instanceof BaseFlipInFragment) {
            BaseFlipInFragment flipInFragment = (BaseFlipInFragment) getTargetFragment();
            if (mPlayBackAnimation) {
                flipInFragment.flipIn();
            } else {
                flipInFragment.disableAnimations();
            }
        }
        super.onDestroyView();
    }

    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
        if (nextAnim != 0 || mDisableAnimations) {
            // return super.onCreateAnimator(transit, enter, nextAnim);
            return AnimatorInflater.loadAnimator(getActivity(), R.animator.oa_instant_disappear);
        } else if (enter) {
            return AnimatorInflater.loadAnimator(getActivity(), R.animator.card_flip_left_in);
        } else if (mPlayBackAnimation) {
            return AnimatorInflater.loadAnimator(getActivity(), R.animator.card_flip_right_out);
        } else {
            return AnimatorInflater.loadAnimator(getActivity(), R.animator.oa_slide_down);
        }
    }

    public void flipOut() {
        View v = getView();
        if (v != null) {
            Animator flipOut = AnimatorInflater.loadAnimator(getActivity(), R.animator.card_flip_left_out);
            flipOut.setTarget(v);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(flipOut);
            animatorSet.start();
        }
    }

    public void flipIn() {
        View v = getView();
        if (v != null) {
            Animator flipOut = AnimatorInflater.loadAnimator(getActivity(), R.animator.card_flip_right_in);
            flipOut.setTarget(v);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(flipOut);
            animatorSet.start();
        }
    }

    public void disableAnimations(){
        mDisableAnimations = true;
        mPlayBackAnimation = false;
    }
}
