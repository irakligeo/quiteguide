package com.mmgct.quitguide2.fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.view.View;

import com.mmgct.quitguide2.R;

/**
 * For all classes in which one if it's actions flips in a new fragment, and where this fragments back
 * animation is a slide down
 * Created by 35527 on 11/25/2015.
 */
public abstract class BaseFlipOutFragment extends BaseNoHeaderFragment {

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

}
