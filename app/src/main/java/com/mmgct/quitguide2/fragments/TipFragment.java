package com.mmgct.quitguide2.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.managers.DbManager;
import com.mmgct.quitguide2.models.Message;

/**
 * Created by 35527 on 11/30/2015.
 */
public class TipFragment extends BaseFlipInFragment {

    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tip, container, false);
        bindListeners();
        showRandTip();
        return rootView;
    }

    private void showRandTip() {
        Message msg = DbManager.getInstance().getRandomTip();
        if (msg != null) {
            TextView txtTip = (TextView) rootView.findViewById(R.id.random_tip);
            txtTip.setText(msg.getMessage());
        }
    }

    private void bindListeners() {
        rootView.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayBackAnimation = false;
                disableTargetAnimation();
                mCallbacks.popBackStack();
                mCallbacks.popBackStack();
                mCallbacks.popBackStack();
            }
        });
    }

    private void disableTargetAnimation() {
        try {
            ((BaseFlipInFragment) getTargetFragment()).disableAnimations();
        } catch (ClassCastException e) {
            throw new ClassCastException("Target fragment must extend BaseFlipInFragment");
        }
    }
}
