package com.mmgct.quitguide2.views.adapters.item;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.views.adapters.NotificationItemHolder;
import com.mmgct.quitguide2.views.adapters.item.Item;

/**
 * Created by 35527 on 2/24/2016.
 */
public class NotificationHeader implements Item {

    private final String name;

    public NotificationHeader(String name) {
        this.name = name;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public int getViewType() {
        return RowType.HEADER_ITEM.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView) {
        View v;

        if (convertView == null) {
            v = inflater.inflate(R.layout.header_notification, null);
            NotificationItemHolder holder = new NotificationItemHolder();
            holder.tv1 = (TextView) v.findViewById(R.id.header_notification);
            v.setTag(holder);
        }
        else {
            v = convertView;
        }

        NotificationItemHolder holder = (NotificationItemHolder)v.getTag();
        holder.tv1.setText(name);

        return v;
    }
}
