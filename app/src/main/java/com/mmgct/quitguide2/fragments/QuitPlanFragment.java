package com.mmgct.quitguide2.fragments;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.SplashActivity;
import com.mmgct.quitguide2.fragments.dialogs.DecisionDialog;
import com.mmgct.quitguide2.fragments.listeners.DialogDismissListener;
import com.mmgct.quitguide2.managers.DbManager;
import com.mmgct.quitguide2.models.Notification;
import com.mmgct.quitguide2.utils.AlarmScheduler;

import java.util.List;


/**
 * Created by 35527 on 11/6/2015.
 */
public class QuitPlanFragment extends BaseFragment implements View.OnClickListener, DialogDismissListener {

    private View rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_quit_plan, container, false);
        bindListeners();
        return rootView;
    }

    private void bindListeners() {
        ImageButton quitDate = (ImageButton) rootView.findViewById(R.id.btn_quitplan_quitdate);
        ImageButton reasons = (ImageButton) rootView.findViewById(R.id.btn_quitplan_reasons);
        ImageButton track = (ImageButton) rootView.findViewById(R.id.btn_quitplan_track);
        ImageButton resetProf = (ImageButton) rootView.findViewById(R.id.btn_quitplan_reset_profile);

        quitDate.setOnClickListener(this);
        reasons.setOnClickListener(this);
        track.setOnClickListener(this);
        resetProf.setOnClickListener(this);

    }

    @Override
    public void onClick(final View v) {
        // Set a filter when clicked
        try {
            ((ImageButton) v).setColorFilter(R.color.dark_gray);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((ImageButton) v).setColorFilter(null);
                }
            }, 75);
        } catch (ClassCastException e) { e.printStackTrace(); } // Should never happen
        switch(v.getId()) {
            case(R.id.btn_quitplan_quitdate):
                QuitDateFragment quitDateFragment = new QuitDateFragment();
                Bundle args = new Bundle();
                args.putBoolean(QuitDateFragment.NO_ANIMATION, true);
                quitDateFragment.setArguments(args);
                mCallbacks.onAnimatedNavigationAction(quitDateFragment, "", R.animator.oa_slide_left_in, R.animator.oa_slide_left_out, R.animator.oa_slide_right_in, R.animator.oa_slide_right_out, true);
                break;
            case(R.id.btn_quitplan_reasons):
                ReasonQuittingFragment reasonQuittingFragment = new ReasonQuittingFragment();
                mCallbacks.onAnimatedNavigationAction(reasonQuittingFragment, "", R.animator.oa_slide_left_in, R.animator.oa_slide_left_out, R.animator.oa_slide_right_in, R.animator.oa_slide_right_out, true);
                break;
            case(R.id.btn_quitplan_track):
                mCallbacks.onAnimatedNavigationAction(new SmokeDataFragment(), "", R.animator.oa_slide_left_in, R.animator.oa_slide_left_out, R.animator.oa_slide_right_in, R.animator.oa_slide_right_out, true);
                break;
            case(R.id.btn_quitplan_reset_profile):
                DialogFragment df = DecisionDialog.newInstance("მომხმარებლის წაშლა\n\nყველა თქვენი ისტორია, ჟურნალში განთავსებული ჩანაწერი და ამ აპლიკაციით გადაღებული სურათი წაიშლება. დარწმუნებული ხარ რო გინდა წაშალო შენი პროფილი?");
                df.setTargetFragment(this, 0);
                df.show(getFragmentManager(), "");
                break;
        }
    }

    @Override
    public void onDialogDismissed(Bundle args) {
        if (args != null) {
            String choice = args.getString(DecisionDialog.DECISION_KEY, "null");
            if (choice.equals("yes")) {
                DbManager DbInstance = DbManager.getInstance();
                DbInstance.getState().setIntroMode(true);
                DbInstance.updateState(DbInstance.getState());
                DbInstance.reset();
                deleteAndUnscheduleNotifications();
                Intent intent = new Intent(getActivity().getApplicationContext(), SplashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }
    }

    private void deleteAndUnscheduleNotifications() {
        List<Notification> notifications = DbManager.getInstance().getAllNotifications();
        for (Notification n : notifications) {
            AlarmScheduler.getInstance().removeScheduledNotification(n.getNotificationHistory());
            DbManager.getInstance().deleteNotificationHistory(n.getNotificationHistory());
            n.setNotificationHistory(null);
            DbManager.getInstance().updateNotification(n);
        }
    }

}
