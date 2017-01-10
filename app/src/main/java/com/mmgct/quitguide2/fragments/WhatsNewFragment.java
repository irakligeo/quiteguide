package com.mmgct.quitguide2.fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.mmgct.quitguide2.MainActivity;
import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.fragments.dialogs.OkDialog;
import com.mmgct.quitguide2.fragments.dialogs.iOSStyledDecisionDialog;
import com.mmgct.quitguide2.fragments.listeners.DialogDismissListener;
import com.mmgct.quitguide2.managers.DbManager;
import com.mmgct.quitguide2.models.State;
import com.mmgct.quitguide2.utils.Common;
import com.mmgct.quitguide2.utils.Constants;

/**
 * Created by 35527 on 1/20/2016.
 */
public class WhatsNewFragment extends BaseFragment implements View.OnClickListener, DialogDismissListener {

    public static final String TAG = WhatsNewFragment.class.getSimpleName();

    private View rootView;
    private boolean mLoaded;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_whats_new, container, false);
        bindListeners();
        State state = DbManager.getInstance().getState();
        state.setShowWhatsNewScreen(false);
        DbManager.getInstance().updateState(state);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mLoaded) {
            mHeaderCallbacks.hideHeader();
            mHeaderCallbacks.disableDrawers();
            mLoaded = true;
        }
    }


    private void bindListeners() {
        Button btnHelpLocation = (Button) rootView.findViewById(R.id.btn_help_location);
        Button btnHelpTime = (Button) rootView.findViewById(R.id.btn_help_time);
        ImageButton btnClose = (ImageButton) rootView.findViewById(R.id.btn_close);

        btnHelpLocation.setOnClickListener(this);
        btnHelpTime.setOnClickListener(this);
        btnClose.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Bundle args = new Bundle();
        switch(v.getId()) {
            case(R.id.btn_help_location):
                mTrackerHost.send(getString(R.string.category_whats_new_notifications), getString(R.string.action_location_help));
                mTrackerHost.send(getString(R.string.category_whats_new_notifications) + "|" + getString(R.string.action_location_help));

                Common.addClickFilter((Button) v, getResources().getColor(R.color.transparent_black));
                if (!Common.isNotificationEnabled(getActivity())) {
                    promptToChangeNoteSettings();
                    return;
                }
                Fragment setLocationFragment = new SetLocationFragment();
                args.putString(UserChooseTipFragment.SCREEN_SEQUENCE_EXTRA_KEY, UserChooseTipFragment.SEQUENCE_TYPE_WHATS_NEW);
                setLocationFragment.setArguments(args);

               /* MapWrapperFragment wrapper = new MapWrapperFragment();
                wrapper.setFragment(setLocationFragment);*/

                /*ManageNotificationsFragment manageNotificationsFragment = new ManageNotificationsFragment();
                manageNotificationsFragment.setLocationFragment((SetLocationFragment)setLocationFragment);*/

                mCallbacks.onAnimatedNavigationAction(setLocationFragment,
                        SetLocationFragment.TAG,R.animator.card_flip_right_in,
                        R.animator.card_flip_right_out,
                        R.animator.card_flip_left_in,
                        R.animator.card_flip_left_out,
                        true);
                break;
            case(R.id.btn_help_time):
                mTrackerHost.send(getString(R.string.category_whats_new_notifications), getString(R.string.action_time_help));
                mTrackerHost.send(getString(R.string.category_whats_new_notifications) + "|" + getString(R.string.action_time_help));

                Common.addClickFilter((Button) v, getResources().getColor(R.color.transparent_black));
                if (!Common.isNotificationEnabled(getActivity())) {
                    promptToChangeNoteSettings();
                    return;
                }
                Fragment helpTimeFragment = new HelpTimeFragment();
                args.putString(UserChooseTipFragment.SCREEN_SEQUENCE_EXTRA_KEY, UserChooseTipFragment.SEQUENCE_TYPE_WHATS_NEW);
                helpTimeFragment.setArguments(args);
                mCallbacks.onAnimatedNavigationAction(helpTimeFragment,
                        HelpTimeFragment.TAG,R.animator.card_flip_right_in,
                        R.animator.card_flip_right_out,
                        R.animator.card_flip_left_in,
                        R.animator.card_flip_left_out,
                        true);
                break;
            case(R.id.btn_close):
                mTrackerHost.send(getString(R.string.category_whats_new_notifications), getString(R.string.action_canceled));
                mTrackerHost.send(getString(R.string.category_whats_new_notifications) + "|" + getString(R.string.action_canceled));

                mHeaderCallbacks.showHeader();
                mHeaderCallbacks.enableDrawers();
                mCallbacks.onAnimatedNavigationAction(new HomeFragment(),
                        HomeFragment.TAG,
                        R.animator.oa_instant_appear,
                        R.animator.oa_slide_down,
                        R.animator.oa_slide_up,
                        R.animator.oa_slide_down,
                        false);
                break;
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


    public void setDisableAnimations(boolean mDisableAnimations) {
        Common.mDisableWhatsNewAnim = mDisableAnimations;
    }

    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
        if (Common.mDisableWhatsNewAnim) {
            return AnimatorInflater.loadAnimator(getActivity(), R.animator.oa_instant_disappear);
        } else {
            return super.onCreateAnimator(transit, enter, nextAnim);
        }
    }

    @Override
    public void onDialogDismissed(Bundle args) {
        if (args != null && args.getBoolean(iOSStyledDecisionDialog.BUTTON_LEFT_CALLBACK_KEY, false)) {
            ((MainActivity)getActivity()).openApplicationSettings();
        }
    }
}
