package com.mmgct.quitguide2.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mmgct.quitguide2.MainActivity;
import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.managers.DbManager;
import com.mmgct.quitguide2.models.Award;
import com.mmgct.quitguide2.models.Notification;
import com.mmgct.quitguide2.models.State;
import com.mmgct.quitguide2.utils.AlarmScheduler;
import com.mmgct.quitguide2.utils.Common;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by 35527 on 11/3/2015.
 */
public class TutorialFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tutorial, null, false);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DbManager DbInstance = DbManager.getInstance();
                State state = DbInstance.getInstance().getState();
                state.setIntroMode(false);
                DbInstance.getInstance().updateState(state);
                removeIntroNotifications();
                scheduleQuitDateNotifications();

                Award quitDateSetAward = DbInstance.getInstance().getAwardByKey(1);
                quitDateSetAward.setAwarded(true);
                DbInstance.getInstance().updateAward(quitDateSetAward);
                Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        return rootView;
    }

    private void scheduleQuitDateNotifications() {
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

    /**
     * Removes notifications relating to no profile set
     */
    private void removeIntroNotifications() {
        List<Notification> dRTNoProfileSetNotifications = DbManager.getInstance().getDRTNoProfileSetNotifications();
        for (Notification n : dRTNoProfileSetNotifications) {
            AlarmScheduler.getInstance().removeScheduledNotification(n.getNotificationHistory());
            DbManager.getInstance().deleteNotificationHistory(n.getNotificationHistory());
            n.setNotificationHistory(null);
            DbManager.getInstance().updateNotification(n);
        }
    }
}
