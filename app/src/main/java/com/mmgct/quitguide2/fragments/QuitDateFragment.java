package com.mmgct.quitguide2.fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.fragments.dialogs.SlidingDatePickerDiag;
import com.mmgct.quitguide2.fragments.listeners.DrawerCallbacks;
import com.mmgct.quitguide2.fragments.listeners.OnAnimationActionListener;
import com.mmgct.quitguide2.managers.DbManager;
import com.mmgct.quitguide2.models.Notification;
import com.mmgct.quitguide2.utils.AlarmScheduler;
import com.mmgct.quitguide2.utils.Common;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.List;


/**
 * Created by 35527 on 11/6/2015.
 */
public class QuitDateFragment extends QuitDateBaseFragment {

    private View rootView;
    private DrawerCallbacks mUpdateCallbacks;
    private boolean mPlayBackAnimation, mPlayNoAnimation;
    private OnAnimationActionListener mOnAnimationActionListener;

    public static final String SHOW_CLOSE_BTN_KEY = "close_btn";
    public static final String NO_ANIMATION = "no_anim";

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPlayBackAnimation = true;
        mPlayNoAnimation = false;
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
        if (mOnAnimationActionListener != null) {
            mOnAnimationActionListener.startTransitionAnimation(true);
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
        rootView = inflater.inflate(R.layout.fragment_quit_date, container, false);
        bindListeners();
        ImageButton share = (ImageButton) rootView.findViewById(R.id.btn_share);
        share.setColorFilter(getResources().getColor(R.color.transparent_black));
        long quitDate = DbManager.getInstance().getProfile().getQuitDate();
        if (quitDate > 0) {
            Button quitDateButton = (Button) rootView.findViewById(R.id.btn_quit_date);
            if (quitDateButton != null) {
                quitDateButton.setText(Common.formatTimestamp("MM/dd/yyyy", quitDate));
            }
            enableShare(quitDate);
        }

        Bundle args = getArguments();
        if (args != null) {
            if (args.getBoolean(SHOW_CLOSE_BTN_KEY, false)) {
                addCloseButton();
            }
            else if(args.getBoolean(NO_ANIMATION, false)) {
                rootView.findViewById(R.id.btn_close).setVisibility(View.GONE);
                mPlayNoAnimation = true;
            }
        }

        return rootView;
    }

    private Fragment getSelf(){
        return this;
    }

    protected void bindListeners() {
        rootView.findViewById(R.id.btn_quit_date)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public synchronized void onClick(View view) {
                        Common.shortDisable(view, 300);
                        SlidingDatePickerDiag slidingDatePickerDiag = new SlidingDatePickerDiag();
                        slidingDatePickerDiag.setTargetFragment(getSelf(), 0);
                        slidingDatePickerDiag.show(getActivity().getFragmentManager(), "");
                    }
                });
    }

    @Override
    public void setDate(long date) {
        Button quitDate = (Button) rootView.findViewById(R.id.btn_quit_date);
        if (quitDate != null) {
            quitDate.setText(Common.formatTimestamp("MM/dd/yyyy", date));
        }
        scheduleQuitDateNotifications();
        mUpdateCallbacks.updateStats();
    }

    private void scheduleQuitDateNotifications() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Notification> dRTQuitDateNotifications = DbManager.getInstance().getDRTQuitDateNotifications();
                for(Notification n : dRTQuitDateNotifications) {
                    String[] parseTime = n.getTimeOfDay().split(":");

                    DateTime scheduleDate = new DateTime(DbManager.getInstance().getProfile().getQuitDate())
                            .withTimeAtStartOfDay()
                            .plusDays(n.getDaysRelativeToQuitDate())
                            .plusHours(Integer.parseInt(parseTime[0]))
                            .plusMinutes(Integer.parseInt(parseTime[1]));

                    if (n.getNotificationHistory() == null) {
                        Common.createNewNotificationHistory(n, scheduleDate);
                    } else {
                        Common.updateNotificationHistory(n.getNotificationHistory(), scheduleDate);
                    }
                    AlarmScheduler.getInstance().scheduleNotification(n.getNotificationHistory());
                }
            }
        }).start();
    }

    @Override
    public void enableShare(final long date) {
        ImageButton share = (ImageButton) rootView.findViewById(R.id.btn_share);
        share.setColorFilter(null);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public synchronized void onClick(View v) {
                Common.shortDisable(v, 300);
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                // Check to see if date is in the future or the past
                int daysBetween = Days.daysBetween(new DateTime(System.currentTimeMillis()).withTimeAtStartOfDay(), new DateTime(date).withTimeAtStartOfDay()).getDays();
                sendIntent.putExtra(Intent.EXTRA_TEXT, String.format("%s %s%s", daysBetween <= 0 ? "I quit smoking on" : "I am going to quit smoking on", Common.formatTimestamp("MM/dd/yyyy", date), "!"));
                sendIntent.setType("text/plain");
                mTrackerHost.send(getString(R.string.category_quit_date), getString(R.string.action_share_quit_date));
                mTrackerHost.send(getString(R.string.category_quit_date) + "|" + getString(R.string.action_share_quit_date));
                startActivity(sendIntent);
            }
        });
    }

    /**
     * No continue button
     */
    @Override
    public void allowContinue() {
    }

    public void addCloseButton() {
        View closeBtn = rootView.findViewById(R.id.btn_close);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayBackAnimation = false;
                if (mOnAnimationActionListener != null) {
                    mOnAnimationActionListener.disableAnimation();
                }
                mCallbacks.clearBackStack(new HomeFragment());
            }
        });
    }

    @Override
    public void onDestroyView() {
        /*if (getTargetFragment() != null && getTargetFragment() instanceof SlippedFragment) {
            SlippedFragment slippedFragment = (SlippedFragment) getTargetFragment();
            if (mPlayBackAnimation) {
                slippedFragment.flipIn();
            } else {
                slippedFragment.disableAnimations();
            }
            }*/
        super.onDestroyView();
    }

    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
        if (nextAnim != 0 || mPlayNoAnimation) {
            return super.onCreateAnimator(transit, enter, nextAnim);
        }
        else if (enter) {
            return AnimatorInflater.loadAnimator(getActivity(), R.animator.card_flip_left_in);
        } else if (mPlayBackAnimation) {
            return AnimatorInflater.loadAnimator(getActivity(), R.animator.card_flip_right_out);
        } else {
            return AnimatorInflater.loadAnimator(getActivity(), R.animator.oa_slide_down);
        }
    }

    public OnAnimationActionListener getOnAnimationActionListener() {
        return mOnAnimationActionListener;
    }

    public void setOnAnimationActionListener(OnAnimationActionListener mOnAnimationActionListener) {
        this.mOnAnimationActionListener = mOnAnimationActionListener;
    }
}
