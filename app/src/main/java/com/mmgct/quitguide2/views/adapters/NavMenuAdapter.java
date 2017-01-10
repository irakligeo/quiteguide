package com.mmgct.quitguide2.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mmgct.quitguide2.R;

import java.util.List;

/**
 * Created by 35527 on 11/4/2015.
 */
public class NavMenuAdapter extends ArrayAdapter {

    private List<NavMenuItem> mItems;
    private Context mContext;

    public NavMenuAdapter(Context context, int resource, List<NavMenuItem> items) {
        super(context, resource, items);
        mItems = items;
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null || convertView.getTag() == null) {
            LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // Inflate header
            if (position == 0) {
                convertView = layoutInflater.inflate(R.layout.menu_drawer_header, null, false);
            }
            // Inflate row
            else {
                convertView = layoutInflater.inflate(R.layout.menu_drawer_item, null, false);
                holder = new Holder();
                holder.icon = (ImageView) convertView.findViewById(R.id.ic_nav_item);
                holder.itemLbl = (TextView) convertView.findViewById(R.id.lbl_nav_item);
                convertView.setTag(R.id.menu_item_id, mItems.get(position).getItemLabel());
                convertView.setTag(holder);
            }
        } else {
            holder = (Holder) convertView.getTag();
        }
        if (position != 0) {
            NavMenuItem item = mItems.get(position);
            holder.itemLbl.setText(item.getItemLabel());
            holder.icon.setImageResource(item.getImgResId());
        }
        return convertView;
    }

    class Holder {
        private ImageView icon;
        private TextView itemLbl;
    }
}
