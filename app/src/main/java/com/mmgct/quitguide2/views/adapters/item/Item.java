package com.mmgct.quitguide2.views.adapters.item;

import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by 35527 on 2/24/2016.
 */
public interface Item {
    public enum RowType {
        LIST_ITEM, HEADER_ITEM
    }


    boolean isEnabled();
    int getViewType();
    View getView(LayoutInflater inflater, View convertView);
}