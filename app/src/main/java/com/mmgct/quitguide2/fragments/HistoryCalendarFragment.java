package com.mmgct.quitguide2.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.managers.DbManager;
import com.mmgct.quitguide2.models.Slip;
import com.mmgct.quitguide2.utils.Common;
import com.mmgct.quitguide2.views.adapters.GridCalendarAdapter;
import com.p_v.flexiblecalendar.FlexibleCalendarView;
import com.p_v.flexiblecalendar.entity.Event;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by 35527 on 12/9/2015.
 */
public class HistoryCalendarFragment extends BaseFragment {
    private ViewGroup rootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.fragment_calendar_history, container, false);
        initialize();
        return rootView;
    }

    private void initialize() {
        List<Date> dates = new ArrayList<Date>();
        DateTime firstOfMonth = new DateTime(System.currentTimeMillis()).withTimeAtStartOfDay().withDayOfMonth(1);
        DateTime firstOfNextMonth = firstOfMonth.plusMonths(1);
        DateTime previousMonth = firstOfMonth.minusDays(1);
        DateTime nextMonth = firstOfNextMonth;
        DateTime now = new DateTime(System.currentTimeMillis()).withTimeAtStartOfDay();

        ((TextView)rootView.findViewById(R.id.header_history_calendar)).setText(Common.formatTimestamp("MMMMM yyyy", now.getMillis()));

        DbManager dbInstance = DbManager.getInstance();

        while (previousMonth.getDayOfWeek() < 7){
            Date date = new Date();
            Slip slip = dbInstance.getSlipByTime(previousMonth.getMillis());
            if (slip != null) {
                date.setBackground(date.RED_BACKGROUND);
            } else if (dbInstance.getState().getJoinedDate() < previousMonth.getMillis()) {
                date.setBackground(date.GREEN_BACKGROUND);
            }
            date.setDayInMonth(previousMonth.getDayOfMonth());
            dates.add(date);
            previousMonth = previousMonth.minusDays(1);
        }
        Date date = new Date();
        Slip slip = dbInstance.getSlipByTime(previousMonth.getMillis());
        if (slip != null) {
            date.setBackground(date.RED_BACKGROUND);
        } else if (dbInstance.getState().getJoinedDate() < previousMonth.getMillis()) {
            date.setBackground(date.GREEN_BACKGROUND);
        }
        date.setDayInMonth(previousMonth.getDayOfMonth());
        dates.add(date);

        Collections.reverse(dates);

        while (firstOfMonth.getMillis() < firstOfNextMonth.getMillis()) {
            date = new Date();
            if (firstOfMonth.getMillis() <= now.getMillis()) {
                slip = dbInstance.getSlipByTime(firstOfMonth.getMillis());
                if (slip != null) {
                    date.setBackground(date.RED_BACKGROUND);
                } else if (dbInstance.getState().getJoinedDate() <= firstOfMonth.getMillis()) {
                    date.setBackground(date.GREEN_BACKGROUND);
                }
            } else {
                date.setBackground(date.NO_BACKGROUND);
            }
            date.setDayInMonth(firstOfMonth.getDayOfMonth());
            dates.add(date);
            firstOfMonth = firstOfMonth.plusDays(1);
        }
        int remainingDays = 7 - dates.size() % 7;
        int i = 0;
        while (i++ < remainingDays) {
            date = new Date();
            date.setBackground(date.NO_BACKGROUND);
            date.setDayInMonth(nextMonth.getDayOfMonth());
            dates.add(date);
            nextMonth = nextMonth.plusDays(1);
        }

        ((GridView)rootView.findViewById(R.id.grid_view_calendar)).setAdapter(new GridCalendarAdapter(getActivity(), dates));
    }

    public class Date {
        public static final int NO_BACKGROUND = 0;
        public static final int GREEN_BACKGROUND = 1;
        public static final int RED_BACKGROUND = 2;

        private int dayInMonth;
        private int background;

        public int getDayInMonth() {
            return dayInMonth;
        }

        public void setDayInMonth(int dayInMonth) {
            this.dayInMonth = dayInMonth;
        }

        public int getBackground() {
            return background;
        }

        public void setBackground(int background) {
            this.background = background;
        }
    }
}
