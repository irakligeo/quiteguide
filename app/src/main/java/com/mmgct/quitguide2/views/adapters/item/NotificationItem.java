package com.mmgct.quitguide2.views.adapters.item;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.mmgct.quitguide2.MainActivity;
import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.managers.DbManager;
import com.mmgct.quitguide2.models.RecurringNotification;
import com.mmgct.quitguide2.models.State;
import com.mmgct.quitguide2.views.adapters.NotificationItemHolder;

import java.lang.ref.WeakReference;

/**
 * Created by 35527 on 2/24/2016.
 */
public class NotificationItem implements Item {

    public static WeakReference<MainActivity> hostingActivity; /*Required for google analytics*/

    private static final String TAG = NotificationItem.class.getSimpleName();
    private String header, content;
    private int iconResId, recurringNotificationId;
    private boolean checked;
    private boolean isQuitGuideItem;
    private boolean isEnabled;

    public NotificationItem(String header, String content, int iconResId, boolean checked, int recurringNotificationId) {
        this.header = header;
        this.content = content;
        this.iconResId = iconResId;
        this.checked = checked;
        this.recurringNotificationId = recurringNotificationId;
    }

    public NotificationItem(String header, String content, int iconResId, boolean checked, boolean isQuitGuideItem) {
        this.header = header;
        this.content = content;
        this.iconResId = iconResId;
        this.checked = checked;
        this.isQuitGuideItem = isQuitGuideItem;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public int getViewType() {
        return RowType.LIST_ITEM.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView) {
        View v;

        if (convertView == null) {
            v = inflater.inflate(R.layout.lv_item_notification, null);
            NotificationItemHolder holder = new NotificationItemHolder();
            holder.tv1 = (TextView) v.findViewById(R.id.tv_header);
            holder.tv2 = (TextView) v.findViewById(R.id.tv_content);
            holder.iv = (ImageView) v.findViewById(R.id.iv_icon);
            holder.sw = (Switch) v.findViewById(R.id.sw_notif);
            v.setTag(holder);
        } else {
            v = convertView;
        }
        NotificationItemHolder holder = (NotificationItemHolder) v.getTag();
        holder.tv1.setText(header);
        holder.tv2.setText(content);
        holder.sw.setChecked(checked);
        if (!isQuitGuideItem) {
            isEnabled = true;
            holder.sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    checked = isChecked;
                    if (hostingActivity != null && hostingActivity.get() != null) {
                        MainActivity main = hostingActivity.get();
                        if (checked) {
                            // GA
                            main.send(main.getString(R.string.category_notification_map_list), main.getString(R.string.action_enable_notification_item));
                            main.send(main.getString(R.string.category_notification_map_list) + "|" + main.getString(R.string.action_enable_notification_item));
                        } else {
                            main.send(main.getString(R.string.category_notification_map_list), main.getString(R.string.action_disable_notification_item));
                            main.send(main.getString(R.string.category_notification_map_list) + "|" + main.getString(R.string.action_disable_notification_item));
                        }
                    }
                    RecurringNotification rn = DbManager.getInstance().getRecurringNotificationById(recurringNotificationId);
                    rn.setActive(isChecked);
                    DbManager.getInstance().updateRecurringNotification(rn);
                }
            });
        } else {
            isEnabled = false;
            holder.sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    checked = isChecked;
                    State state = DbManager.getInstance().getState();
                    state.setAllowNotification(checked);
                    DbManager.getInstance().updateState(state);
                }
            });
        }
        holder.iv.setImageResource(iconResId);
        return v;
    }

    public int getRecurringNotificationId() {
        return recurringNotificationId;
    }
}
