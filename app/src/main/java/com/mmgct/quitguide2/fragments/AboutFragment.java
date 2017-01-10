package com.mmgct.quitguide2.fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.fragments.listeners.HeaderCallbacks;

/**
 * Created by 35527 on 12/4/2015.
 */
public class AboutFragment extends BaseFragment {

    private View rootView;
    private HeaderCallbacks mHeaderCallbacks;

    @Override
    public void onAttach(Activity activity) {
        try {
            mHeaderCallbacks = (HeaderCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement HeaderCallbacks");
        }
        super.onAttach(activity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_about, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHeaderCallbacks.showBackBlack();
    }

    @Override
    public void onDestroyView() {
        mHeaderCallbacks.showMain();
        Object o = getTargetFragment();
        if (o instanceof SettingsFragment) {
            Animator animator = AnimatorInflater.loadAnimator(getActivity(), R.animator.oa_slide_right_in);
            animator.setTarget(((SettingsFragment)o).getView());
            animator.start();
        }
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        mHeaderCallbacks = null;
        super.onDetach();
    }

    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
        return enter ? AnimatorInflater.loadAnimator(getActivity(), R.animator.oa_slide_left_in)
                : AnimatorInflater.loadAnimator(getActivity(), R.animator.oa_slide_right_out);
    }
}
