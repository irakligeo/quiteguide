package com.mmgct.quitguide2.fragments;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.fragments.dialogs.SlidingDatePickerDiag;
import com.mmgct.quitguide2.fragments.dialogs.OkDialog;
import com.mmgct.quitguide2.fragments.dialogs.iOSStyledDecisionDialog;
import com.mmgct.quitguide2.fragments.listeners.DialogDismissListener;
import com.mmgct.quitguide2.managers.DbManager;
import com.mmgct.quitguide2.models.State;
import com.mmgct.quitguide2.utils.Common;

import org.joda.time.DateTime;
import org.joda.time.Days;

/**
 * Created by 35527 on 10/29/2015.
 */
public class QuitDateIntroFragment extends QuitDateBaseFragment implements DialogDismissListener {

    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_intro_quit_date, container, false);
        bindListeners();
        // Gray filter
        ImageButton share = (ImageButton) rootView.findViewById(R.id.btn_share);
        share.setColorFilter(getResources().getColor(R.color.transparent_black));
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // GA
        mTrackerHost.send(getString(R.string.category_start_pages), getString(R.string.action_welcome_screen_viewed));
        mTrackerHost.send(getString(R.string.category_start_pages)+"|"+getString(R.string.action_welcome_screen_viewed));
        promptUserToAcceptNotifications();
    }

    private void promptUserToAcceptNotifications() {
        DialogFragment df = iOSStyledDecisionDialog.newInstance(getResources().getString(R.string.header_dialog_notification),
                getResources().getString(R.string.content_dialog_notification),
                getResources().getString(R.string.btn_left_dialog_notification),
                getResources().getString(R.string.btnRight_dialog_notification));
        df.setCancelable(false);
        df.setTargetFragment(this, 0);
        df.show(getFragmentManager(), "");
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
        rootView.findViewById(R.id.btn_continue)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OkDialog.newInstance("უპს!\n\nშენ არ გაქვს შერჩეული მოწევისთვის თავის დანებების დღე.")
                                .show(getFragmentManager(), "");
                    }
                });
    }

    @Override
    public void setDate(long date) {
        Button quitDate = (Button) rootView.findViewById(R.id.btn_quit_date);
        if (quitDate != null) {
            quitDate.setText(Common.formatTimestamp("MM/dd/yyyy", date));
        }
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
                sendIntent.putExtra(Intent.EXTRA_TEXT, String.format("%s %s%s", daysBetween <= 0 ? "მოწევას თავი დავანებე" : "მე ვაპირებ მოწევისთვის თავის დანებებას", Common.formatTimestamp("MM/dd/yyyy", date), "!"));
                sendIntent.setType("text/plain");
                // GA
                mTrackerHost.send(getString(R.string.category_quit_date), getString(R.string.action_share_quit_date));
                mTrackerHost.send(getString(R.string.category_quit_date)+"|"+getString(R.string.action_share_quit_date));
                startActivity(sendIntent);
            }
        });
    }

    @Override
    public void allowContinue() {
        rootView.findViewById(R.id.btn_continue)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCallbacks.onAnimatedNavigationAction(new ReasonQuittingIntroFragment(), "", R.animator.card_flip_left_in, R.animator.card_flip_left_out, R.animator.card_flip_right_in, R.animator.card_flip_right_out, true);
                    }
                });
    }

    @Override
    public void onDialogDismissed(Bundle args) {
        if (args == null) {
            return;
        } else if (args.getBoolean(iOSStyledDecisionDialog.BUTTON_LEFT_CALLBACK_KEY, false)) {
            State state = DbManager.getInstance().getState();
            state.setAllowNotification(false);
            DbManager.getInstance().updateState(state);
        } else if (args.getBoolean(iOSStyledDecisionDialog.BUTTON_RIGHT_CALLBACK_KEY, false)) {
            State state = DbManager.getInstance().getState();
            state.setAllowNotification(true);
            DbManager.getInstance().updateState(state);
        } else
            ; // Nothing
    }
}
