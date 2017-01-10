package com.mmgct.quitguide2.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.fragments.HistoryCalendarFragment;

import java.util.List;

/**
 * Created by 35527 on 12/10/2015.
 */
public class GridCalendarAdapter extends BaseAdapter {

    List<HistoryCalendarFragment.Date> mDates;
    Context mContext;

    public GridCalendarAdapter (Context context, List<HistoryCalendarFragment.Date> dates) {
        mDates = dates;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mDates.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_calendar_cell, parent, false);
            TextView day = (TextView)convertView.findViewById(R.id.day_calendar_cell);
            holder = new Holder();
            holder.setCell((RelativeLayout)convertView);
            holder.setDay(day);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        setBackground(convertView, position);
        holder.getDay().setText(String.valueOf(mDates.get(position).getDayInMonth()));
        return convertView;
    }

    private void setBackground(View convertView, int position) {
        switch(mDates.get(position).getBackground()) {
            case(HistoryCalendarFragment.Date.GREEN_BACKGROUND):
                convertView.setBackgroundResource(R.drawable.bg_green_cal_cell);
                break;
            case(HistoryCalendarFragment.Date.RED_BACKGROUND):
                convertView.setBackgroundResource(R.drawable.bg_red_cal_cell);
        }
    }

    private class Holder {
        private TextView day;
        private RelativeLayout cell;

        public TextView getDay() {
            return day;
        }

        public void setDay(TextView day) {
            this.day = day;
        }

        public RelativeLayout getCell() {
            return cell;
        }

        public void setCell(RelativeLayout cell) {
            this.cell = cell;
        }
    }
}
