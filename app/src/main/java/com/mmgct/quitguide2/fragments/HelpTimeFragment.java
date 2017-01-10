package com.mmgct.quitguide2.fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TimePicker;

import com.mmgct.quitguide2.BuildConfig;
import com.mmgct.quitguide2.MainActivity;
import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.fragments.listeners.OnAnimationActionListener;
import com.mmgct.quitguide2.utils.Common;
import com.mmgct.quitguide2.utils.Constants;

/**
 * Created by 35527 on 1/21/2016.
 */
public class HelpTimeFragment extends BaseFragment implements View.OnClickListener {

    public static final String TAG = HelpTimeFragment.class.getSimpleName();

    private View rootView;
    private boolean mSlideDownAnimation;
    private OnAnimationActionListener mOnAnimationActionListener;
    private UserChooseTipFragment.TimeSetListener mTimeSetListener;
    private String mSequenceType;

    public static HelpTimeFragment newInstance(OnAnimationActionListener onAnimationActionListener) {
        HelpTimeFragment helpTimeFragment = new HelpTimeFragment();
        helpTimeFragment.setOnAnimationActionListener(onAnimationActionListener);
        return helpTimeFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // If coming from Craving or Slip screen sequence, navigation flow changes
            mSequenceType = getArguments().getString(UserChooseTipFragment.SCREEN_SEQUENCE_EXTRA_KEY, "");
            if (UserChooseTipFragment.SEQUENCE_TYPE_CRAVING.equals(mSequenceType)
                    || UserChooseTipFragment.SEQUENCE_TYPE_SLIP.equals(mSequenceType)) {
                try {
                    ((MainActivity) getActivity()).setAlternativeNav(new MainActivity.AlternativeNav() {
                        @Override
                        public void alternativeNavigationAction() {
                            mSlideDownAnimation = true;
                            disableStackedFragmentAnimations();
                        }
                    });
                } catch (ClassCastException e) {
                    throw new ClassCastException("Hosting activity must be MainActivity.");
                }
            }
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ContextThemeWrapper contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.HelpTimeFragmentTheme);
        LayoutInflater localInflater = inflater.from(contextThemeWrapper);
        rootView = localInflater.inflate(R.layout.fragment_help_time, container, false);
        setBackgroundColor();
        bindListeners();
        setupForSequence();
        return rootView;
    }

    private void setupForSequence() {
        if (UserChooseTipFragment.SEQUENCE_TYPE_SETTINGS.equals(mSequenceType) || UserChooseTipFragment.SEQUENCE_TYPE_SETTINGS_NEW_TIMED.equals(mSequenceType)) {
            rootView.findViewById(R.id.btn_close).setVisibility(View.GONE);
            mHeaderCallbacks.showBackNotification();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mOnAnimationActionListener != null) {
            mOnAnimationActionListener.startTransitionAnimation(false);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mOnAnimationActionListener != null) {
            mOnAnimationActionListener.startTransitionAnimation(true);
        }
    }

    private void bindListeners() {
        ImageButton btnClose = (ImageButton) rootView.findViewById(R.id.btn_close);
        Button btnSelectTime = (Button) rootView.findViewById(R.id.btn_select_time);

        btnClose.setOnClickListener(this);
        btnSelectTime.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case(R.id.btn_close):
                mHeaderCallbacks.showHeader();
                mHeaderCallbacks.enableDrawers();
                mSlideDownAnimation = true;
                disableStackedFragmentAnimations();
                mCallbacks.clearBackStack(new HomeFragment());
                break;
            case(R.id.btn_select_time):
                Common.mDisableWhatsNewAnim = false;
                // GA
                mTrackerHost.send(getString(R.string.category_notification_time_select), getString(R.string.action_add_time), getString(R.string.label_time_of_day));
                mTrackerHost.send(getString(R.string.category_notification_time_select) + "|" + getString(R.string.action_add_time) + "|" + getString(R.string.label_time_of_day));
                mSlideDownAnimation = false;
                Common.addClickFilter((Button)v, getResources().getColor(R.color.transparent_black));
                TimePicker timePicker = (TimePicker) rootView.findViewById(R.id.time_picker_help_time);
                if (timePicker != null) {
                    String time = timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute();

                    if (getArguments() != null) {
                        String navSequenceType = getArguments().getString(UserChooseTipFragment.SCREEN_SEQUENCE_EXTRA_KEY, "");

                        // If this screen is coming from Craving or Slip screen
                        if (navSequenceType.equals(UserChooseTipFragment.SEQUENCE_TYPE_SLIP)
                                || navSequenceType.equals(UserChooseTipFragment.SEQUENCE_TYPE_CRAVING)) {
                            if (mTimeSetListener != null) {
                                mTimeSetListener.setTime(time);
                                getActivity().getFragmentManager().popBackStack();
                            }
                        }

                        // If UserChooseTipFragment is not a part of the backstack currently
                        else if (UserChooseTipFragment.SEQUENCE_TYPE_WHATS_NEW.equals(navSequenceType)
                                || UserChooseTipFragment.SEQUENCE_TYPE_SETTINGS.equals(navSequenceType)
                                || UserChooseTipFragment.SEQUENCE_TYPE_SETTINGS_NEW_TIMED.equals(navSequenceType)) {
                            Bundle args = new Bundle();
                            args.putString(UserChooseTipFragment.TIME_KEY, time);
                            args.putString(UserChooseTipFragment.SCREEN_SEQUENCE_EXTRA_KEY, navSequenceType);
                            Fragment timeTipFragment = new UserChooseTipFragment();
                            timeTipFragment.setArguments(args);
                            if (UserChooseTipFragment.SEQUENCE_TYPE_WHATS_NEW.equals(navSequenceType)) {
                                mCallbacks.onAnimatedNavigationAction(timeTipFragment,
                                        UserChooseTipFragment.TAG,
                                        R.animator.card_flip_right_in,
                                        R.animator.card_flip_right_out,
                                        R.animator.card_flip_left_in,
                                        R.animator.card_flip_left_out,
                                        true);
                            }
                            else {
                                mCallbacks.onAnimatedNavigationAction(timeTipFragment,
                                        UserChooseTipFragment.TAG,
                                        R.animator.oa_slide_left_in,
                                        R.animator.oa_slide_left_out,
                                        R.animator.oa_slide_right_in,
                                        R.animator.oa_slide_right_out,
                                        true);
                            }
                        }
                    }
                }
                break;
        }
    }

    private void disableStackedFragmentAnimations() {
        if (mOnAnimationActionListener != null) {
            mOnAnimationActionListener.disableAnimation();
        }
        Common.mDisableWhatsNewAnim = true;
    }


    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
        if (mSlideDownAnimation) {
            return AnimatorInflater.loadAnimator(getActivity(), R.animator.oa_slide_down);
        } else if (Common.mDisableWhatsNewAnim) {
            return AnimatorInflater.loadAnimator(getActivity(), R.animator.oa_instant_disappear);
        } else {
            return super.onCreateAnimator(transit, enter, nextAnim);
        }
    }

    public OnAnimationActionListener getOnAnimationActionListener() {
        return mOnAnimationActionListener;
    }

    public void setOnAnimationActionListener(OnAnimationActionListener onAnimationActionListener) {
        this.mOnAnimationActionListener = onAnimationActionListener;
    }

    public UserChooseTipFragment.TimeSetListener getTimeSetListener() {
        return mTimeSetListener;
    }

    public void setTimeSetListener(UserChooseTipFragment.TimeSetListener mTimeSetListener) {
        this.mTimeSetListener = mTimeSetListener;
    }

    private void setBackgroundColor() {
        if (getArguments() != null) {
            if (BuildConfig.VERSION_CODE >= 23) {
                rootView.setBackgroundColor(getResources().getColor(getArguments().getInt(UserChooseTipFragment.COLOR_EXTRA_KEY, R.color.dark_blue2), getActivity().getTheme()));
            } else {
                rootView.setBackgroundColor(getResources().getColor(getArguments().getInt(UserChooseTipFragment.COLOR_EXTRA_KEY, R.color.dark_blue2)));
            }
        }
    }
}
