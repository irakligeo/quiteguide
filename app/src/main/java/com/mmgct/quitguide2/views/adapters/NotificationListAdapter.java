package com.mmgct.quitguide2.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.mmgct.quitguide2.views.adapters.item.Item;

import java.util.List;

/**
 * Created by 35527 on 2/24/2016.
 */
public class NotificationListAdapter extends ArrayAdapter<Item> {

    private LayoutInflater mInflater;

    public NotificationListAdapter(Context context, int resource, List<Item> objects) {
        super(context, resource, objects);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getItem(position).getView(mInflater, convertView);
    }

    @Override
    public int getViewTypeCount() {
        return Item.RowType.values().length;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getViewType();
    }

    @Override
    public boolean isEnabled(int position) {
        return getItem(position).isEnabled();
    }
}
