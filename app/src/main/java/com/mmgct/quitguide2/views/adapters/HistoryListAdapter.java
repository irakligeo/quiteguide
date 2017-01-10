package com.mmgct.quitguide2.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.fragments.HistoryListFragment;

import java.util.List;

/**
 * Created by 35527 on 12/14/2015.
 */
public class HistoryListAdapter extends BaseAdapter {

    private List<HistoryListFragment.ListItem> items;
    private Context mContext;

    public HistoryListAdapter(Context context, List<HistoryListFragment.ListItem> items) {
        this.items = items;
        mContext = context;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        if (items != null && position < items.size())
            return items.get(position);
        else
            return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_list_history, parent, false);
            holder = new Holder();
            holder.setHeader((TextView)convertView.findViewById(R.id.item_header));
            holder.setTrigger((TextView) convertView.findViewById(R.id.item_trigger));
            holder.setDate((TextView) convertView.findViewById(R.id.item_date));
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }
        HistoryListFragment.ListItem item = items.get(position);
        holder.getHeader().setText(item.getHeader());
        if (item.getTrigger() != null) {
            holder.getTrigger().setVisibility(View.VISIBLE);
            holder.getTrigger().setText(item.getTrigger());
        } else {
            holder.getTrigger().setVisibility(View.GONE);
        }
        holder.getDate().setText(item.getDate());

        return convertView;
    }

    private class Holder {
        TextView header, trigger, date;

        public TextView getHeader() {
            return header;
        }

        public void setHeader(TextView header) {
            this.header = header;
        }

        public TextView getTrigger() {
            return trigger;
        }

        public void setTrigger(TextView trigger) {
            this.trigger = trigger;
        }

        public TextView getDate() {
            return date;
        }

        public void setDate(TextView date) {
            this.date = date;
        }
    }
}
