package com.mmgct.quitguide2.fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Fragment;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.mmgct.quitguide2.MainActivity;
import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.fragments.dialogs.iOSStyledDecisionDialog;
import com.mmgct.quitguide2.fragments.listeners.DialogDismissListener;
import com.mmgct.quitguide2.fragments.listeners.OnAnimationActionListener;
import com.mmgct.quitguide2.fragments.listeners.OnGeocodeOperationCompleted;
import com.mmgct.quitguide2.utils.Common;
import com.mmgct.quitguide2.utils.Constants;

/**
 * Created by 35527 on 2/22/2016.
 */
public class Slipped2Fragment extends BaseFragment implements DialogDismissListener {

    public static final String TAG = Slipped2Fragment.class.getSimpleName();

    private OnAnimationActionListener mOnAnimationActionListener;
    private View rootView;
    private String mAnimationType = "";

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_slipped2, container, false);
        bindViewListeners();
        return rootView;
    }

    private void bindViewListeners() {
        final OnAnimationActionListener onAnimationActionListener = buildAnimationActionListener();

        // Close btn
        rootView.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });

        // Keep going btn
        rootView.findViewById(R.id.btn_keep_going).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.addClickFilter((ImageButton) v, getResources().getColor(R.color.transparent_black));
                // GA
                mTrackerHost.send(getString(R.string.category_slipped), getString(R.string.action_keep_going));
                mTrackerHost.send(getString(R.string.category_slipped) + "|" + getString(R.string.action_keep_going));
                close();
            }
        });

        // New quit date btn
        final Fragment target = this;
        rootView.findViewById(R.id.btn_new_quitdate).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.addClickFilter((ImageButton) v, getResources().getColor(R.color.transparent_black));
                // GA
                mTrackerHost.send(getString(R.string.category_slipped), getString(R.string.action_new_quit_date_set));
                mTrackerHost.send(getString(R.string.category_slipped) + "|" + getString(R.string.action_new_quit_date_set));
                QuitDateFragment quitDateFragment = new QuitDateFragment();
                Bundle args = new Bundle();
                args.putBoolean(QuitDateFragment.SHOW_CLOSE_BTN_KEY, true);
                quitDateFragment.setArguments(args);
                quitDateFragment.setOnAnimationActionListener(onAnimationActionListener);
                mCallbacks.onAddNavigationAction(quitDateFragment, "", true);
            }
        });

        // Set location and time btns

        Button btnSetLocation = (Button) rootView.findViewById(R.id.btn_help_location);
        Button btnSetTime = (Button) rootView.findViewById(R.id.btn_help_time);

        btnSetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                Common.addClickFilter((Button) v, getResources().getColor(R.color.transparent_black));
                mTrackerHost.send(getString(R.string.category_slipped), getString(R.string.action_location_help));
                mTrackerHost.send(getString(R.string.category_slipped) + "|" + getString(R.string.action_location_help));
                if (!Common.isNotificationEnabled(getActivity())) {
                    promptToChangeNoteSettings();
                    return;
                }
                Location loc = ((MainActivity)getActivity()).getLastKnownLocation();
                if (loc == null) {
                    Log.e(TAG, "Error occurred while retrieving last known location");
                    Toast.makeText(getActivity(), getString(R.string.error_last_location), Toast.LENGTH_SHORT).show();
                    return;
                }

                Common.reverseGeocode(getActivity(), loc.getLatitude(), loc.getLongitude(), new OnGeocodeOperationCompleted() {
                    @Override
                    public void reverseGeocodeCompleted(Address address) {
                        startTipFragmentWithGeotag(address, onAnimationActionListener);
                    }

                    @Override
                    public void geocodeCompleted(Address address) {

                    }
                });


            }
        });

        btnSetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                Common.addClickFilter((Button) v, getResources().getColor(R.color.transparent_black));
                mTrackerHost.send(getString(R.string.category_slipped), getString(R.string.action_time_help));
                mTrackerHost.send(getString(R.string.category_slipped) + "|" + getString(R.string.action_time_help));
                if (!Common.isNotificationEnabled(getActivity())) {
                    promptToChangeNoteSettings();
                    return;
                }
                UserChooseTipFragment chooseTipFragment = new UserChooseTipFragment();
                chooseTipFragment.setOnAnimationActionListener(onAnimationActionListener);
                args.putString(UserChooseTipFragment.TIME_KEY, Common.formatTimestamp("HH:mm", System.currentTimeMillis()));
                args.putInt(UserChooseTipFragment.COLOR_EXTRA_KEY, R.color.fire_engine_red);
                args.putString(UserChooseTipFragment.SCREEN_SEQUENCE_EXTRA_KEY, UserChooseTipFragment.SEQUENCE_TYPE_SLIP);
                chooseTipFragment.setArguments(args);

                mCallbacks.onAddAnimationNavigationAction(chooseTipFragment,
                        HelpTimeFragment.TAG, R.animator.card_flip_left_in,
                        R.animator.card_flip_left_out,
                        R.animator.card_flip_right_in,
                        R.animator.card_flip_right_out,
                        true);
            }
        });
    }

    // Controls animation actions for this fragment
    private OnAnimationActionListener buildAnimationActionListener() {
        return new OnAnimationActionListener() {
            @Override
            public void disableAnimation() {
                mAnimationType = Constants.ANIM_NONE;
                if (mOnAnimationActionListener != null) {
                    mOnAnimationActionListener.disableAnimation();
                }
            }

            @Override
            public void enableAnimation() {
                mAnimationType = "";
            }

            @Override
            public void startTransitionAnimation(boolean enter) {
                if (!Constants.ANIM_NONE.equals(mAnimationType)) {
                    if (enter) {
                        flipIn();
                    } else {
                        flipOut();
                    }
                }
            }
        };
    }

    private void close() {
        if (mOnAnimationActionListener != null) {
            mOnAnimationActionListener.disableAnimation();
        }
        mAnimationType = Constants.ANIM_SLIDE_DOWN;
        mCallbacks.clearBackStack(new HomeFragment());
    }

    public OnAnimationActionListener getOnAnimationActionListener() {
        return mOnAnimationActionListener;
    }

    public void setOnAnimationActionListener(OnAnimationActionListener mOnAnimationActionListener) {
        this.mOnAnimationActionListener = mOnAnimationActionListener;
    }

    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
        if (Constants.ANIM_SLIDE_DOWN.equals(mAnimationType)) {
            return AnimatorInflater.loadAnimator(getActivity(), R.animator.oa_slide_down);
        }
        else if (Constants.ANIM_NONE.equals(mAnimationType)) {
            return AnimatorInflater.loadAnimator(getActivity(), R.animator.oa_instant_disappear);
        }
        else {
            return super.onCreateAnimator(transit, enter, nextAnim);
        }
    }

    private void flipIn() {
        Common.flipIn(getActivity(), rootView);
    }

    private void flipOut() {
        Common.flipOut(getActivity(), rootView);
    }

    private void promptToChangeNoteSettings() {
        iOSStyledDecisionDialog diag = iOSStyledDecisionDialog.newInstance(getString(R.string.header_notification_services),
                getString(R.string.diag_notification_serivces),
                getString(R.string.yes),
                getString(R.string.no));
        diag.setTargetFragment(this, 0);
        diag.show(getActivity().getFragmentManager(), "diag");
    }

    @Override
    public void onDialogDismissed(Bundle args) {
        if (args != null && args.getBoolean(iOSStyledDecisionDialog.BUTTON_LEFT_CALLBACK_KEY, false)) {
            ((MainActivity)getActivity()).openApplicationSettings();
        }
    }

    public void startTipFragmentWithGeotag(Address address, OnAnimationActionListener onAnimationActionListener) {

        Bundle args = new Bundle();
        UserChooseTipFragment chooseTipFragment = UserChooseTipFragment.newInstanceWithRecurringNotification(Common.createRecurringNotification(address.getLatitude(), address.getLongitude(), Common.concatAddressLines(address)));
        chooseTipFragment.setOnAnimationActionListener(onAnimationActionListener);
        args.putBoolean(UserChooseTipFragment.GEOFENCE_KEY, true);
        args.putString(UserChooseTipFragment.SCREEN_SEQUENCE_EXTRA_KEY, UserChooseTipFragment.SEQUENCE_TYPE_SLIP);
        args.putInt(UserChooseTipFragment.COLOR_EXTRA_KEY, R.color.fire_engine_red);
        chooseTipFragment.setArguments(args);

        mCallbacks.onAddAnimationNavigationAction(chooseTipFragment,
                SetLocationFragment.TAG, R.animator.card_flip_left_in,
                R.animator.card_flip_left_out,
                R.animator.card_flip_right_in,
                R.animator.card_flip_right_out,
                true);
    }
}
