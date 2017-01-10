package com.mmgct.quitguide2.fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
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
import android.widget.TextView;
import android.widget.Toast;

import com.mmgct.quitguide2.MainActivity;
import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.fragments.dialogs.iOSStyledDecisionDialog;
import com.mmgct.quitguide2.fragments.listeners.DialogDismissListener;
import com.mmgct.quitguide2.fragments.listeners.OnAnimationActionListener;
import com.mmgct.quitguide2.fragments.listeners.OnGeocodeOperationCompleted;
import com.mmgct.quitguide2.managers.DbManager;
import com.mmgct.quitguide2.models.GeoTag;
import com.mmgct.quitguide2.models.Message;
import com.mmgct.quitguide2.models.Notification;
import com.mmgct.quitguide2.models.RecurringNotification;
import com.mmgct.quitguide2.utils.Common;
import com.mmgct.quitguide2.utils.Constants;
import com.mmgct.quitguide2.views.notifications.OpenToScreenNavigation;

/**
 * Created by 35527 on 11/17/2015.
 */
public class Craving2Fragment extends BaseFlipInFragment implements DialogDismissListener {

    private static final String TAG = Craving2Fragment.class.getSimpleName();
    private View rootView;
    private String mTriggerSelection;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPlayBackAnimation = true;

        Bundle args = getArguments();
        if (args != null) {
            mTriggerSelection = args.getString(CravingFragment.CRAVING_TRIGGER, null);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_craving2, container, false);
        bindListeners();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void bindListeners() {
        final Fragment target = this;
        TextView craveTip = (TextView) rootView.findViewById(R.id.crave_tip);
        setCraveTipContent(craveTip);

        ImageButton finish = (ImageButton) rootView.findViewById(R.id.btn_crave_finish);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.addClickFilter((ImageButton) v, getResources().getColor(R.color.transparent_black));
                mTrackerHost.send(getString(R.string.category_craving_action), getString(R.string.action_finished));
                mTrackerHost.send(getString(R.string.category_craving_action) + "|" + getString(R.string.action_finished));
                mPlayBackAnimation = false;
                getActivity().getFragmentManager().popBackStack();
                getActivity().getFragmentManager().popBackStack();
            }
        });

        ImageButton journal = (ImageButton) rootView.findViewById(R.id.btn_crave_journal);
        journal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.addClickFilter((ImageButton) v, getResources().getColor(R.color.transparent_black));
                mTrackerHost.send(getString(R.string.category_craving_action), getString(R.string.action_journal));
                mTrackerHost.send(getString(R.string.category_craving_action)+"|"+getString(R.string.action_journal));
                JournalFragment jf = new JournalFragment();
                jf.setAnimationType(Constants.ANIM_FLIP_LEFT_IN);
                jf.setTargetFragment(target, 0);
                Bundle args = new Bundle();
                args.putBoolean(JournalFragment.SHOW_CLOSE_BTN, true);
                jf.setArguments(args);
                flipOut();
                mCallbacks.onAddNavigationAction(jf, "", true);
            }
        });

        Button btnSetLocation = (Button) rootView.findViewById(R.id.btn_help_location);
        Button btnSetTime = (Button) rootView.findViewById(R.id.btn_help_time);
        final OnAnimationActionListener onAnimationActionListener = new OnAnimationActionListener() {
            @Override
            public void disableAnimation() {
                mDisableAnimations = true;
                mPlayBackAnimation = false;
            }

            @Override
            public void enableAnimation() {
                mDisableAnimations = false;
                mPlayBackAnimation = true;
            }

            @Override
            public void startTransitionAnimation(boolean enter) {
                if (!mDisableAnimations) {
                    if (enter) {
                        flipIn();
                    } else {
                        flipOut();
                    }
                }
            }
        };

        btnSetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                Common.addClickFilter((Button) v, getResources().getColor(R.color.transparent_black));
                mTrackerHost.send(getString(R.string.category_craving), getString(R.string.action_location_help));
                mTrackerHost.send(getString(R.string.category_craving) + "|" + getString(R.string.action_location_help));
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
                mTrackerHost.send(getString(R.string.category_craving), getString(R.string.action_time_help));
                mTrackerHost.send(getString(R.string.category_craving) + "|" + getString(R.string.action_time_help));
                if (!Common.isNotificationEnabled(getActivity())) {
                    promptToChangeNoteSettings();
                    return;
                }
                UserChooseTipFragment chooseTipFragment = new UserChooseTipFragment();
                chooseTipFragment.setOnAnimationActionListener(onAnimationActionListener);
                args.putString(UserChooseTipFragment.TIME_KEY, Common.formatTimestamp("HH:mm", System.currentTimeMillis()));
                args.putInt(UserChooseTipFragment.COLOR_EXTRA_KEY, R.color.pumpkin);
                args.putString(UserChooseTipFragment.SCREEN_SEQUENCE_EXTRA_KEY, UserChooseTipFragment.SEQUENCE_TYPE_CRAVING);
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

    public void setCraveTipContent(TextView craveTip) {
        if (craveTip == null)
            return;
        Message msg = null;
        if (mTriggerSelection != null && (msg = DbManager.getInstance().getCraveTipFromTrigger(mTriggerSelection)) != null) {
            craveTip.setText(msg.getMessage());
        } else {
            craveTip.setText(DbManager.getInstance().getRandomCraveTip().getMessage());
        }
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
        args.putString(UserChooseTipFragment.SCREEN_SEQUENCE_EXTRA_KEY, UserChooseTipFragment.SEQUENCE_TYPE_CRAVING);
        args.putInt(UserChooseTipFragment.COLOR_EXTRA_KEY, R.color.pumpkin);
        chooseTipFragment.setArguments(args);

        mCallbacks.onAddAnimationNavigationAction(chooseTipFragment,
                SetLocationFragment.TAG, R.animator.card_flip_left_in,
                R.animator.card_flip_left_out,
                R.animator.card_flip_right_in,
                R.animator.card_flip_right_out,
                true);
    }
}
