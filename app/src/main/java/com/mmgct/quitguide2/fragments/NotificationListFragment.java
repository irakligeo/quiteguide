package com.mmgct.quitguide2.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.mmgct.quitguide2.MainActivity;
import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.managers.DbManager;
import com.mmgct.quitguide2.models.RecurringNotification;
import com.mmgct.quitguide2.utils.Common;
import com.mmgct.quitguide2.views.adapters.NotificationListAdapter;
import com.mmgct.quitguide2.views.adapters.item.Item;
import com.mmgct.quitguide2.views.adapters.item.NotificationHeader;
import com.mmgct.quitguide2.views.adapters.item.NotificationItem;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 35527 on 2/24/2016.
 */
public class NotificationListFragment extends BaseFragment {
    private View rootView;
    private ListView mListView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_notification_list, container, false);
        bindViewListeners();
        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        // If coming from settings
        if (getArguments() != null
                && UserChooseTipFragment.SEQUENCE_TYPE_SETTINGS.equals(getArguments().getString(UserChooseTipFragment.SCREEN_SEQUENCE_EXTRA_KEY))){
            try {
                ((ManageNotificationsFragment)getTargetFragment()).setTabToDisplay(ManageNotificationsFragment.LIST);
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }
    }

    private void bindViewListeners() {
        mListView = (ListView) rootView.findViewById(R.id.lv_notification);
        mListView.setAdapter(buildNotificationAdapter());

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // All other view besides Notification item should be disabled
                if (parent.getItemAtPosition(position) instanceof  NotificationItem) {
                    NotificationItem item = (NotificationItem) parent.getItemAtPosition(position);
                    view.setBackgroundColor(Color.parseColor("#64000000"));
                    RecurringNotification recurringNotification = DbManager.getInstance().getRecurringNotificationById(item.getRecurringNotificationId());
                    boolean isGeotag = recurringNotification.getGeoTag() != null;

                    Bundle chooseTipArgs = new Bundle();
                    chooseTipArgs.putBoolean(UserChooseTipFragment.BUILD_FROM_EXISTING_KEY, true);
                    if (isGeotag) {
                        // GA
                        mTrackerHost.send(getString(R.string.category_notification_map_list), getString(R.string.action_selection_location_item));
                        mTrackerHost.send(getString(R.string.category_notification_map_list) + "|" + getString(R.string.action_selection_location_item));
                        chooseTipArgs.putBoolean(UserChooseTipFragment.GEOFENCE_KEY, true);
                    }
                    else if (recurringNotification.getNotification() != null) {
                        // GA
                        mTrackerHost.send(getString(R.string.category_notification_map_list), getString(R.string.action_select_time_item));
                        mTrackerHost.send(getString(R.string.category_notification_map_list) + "|" + getString(R.string.action_select_time_item));
                        chooseTipArgs.putString(UserChooseTipFragment.TIME_KEY, recurringNotification.getNotification().getOpenToScreen());
                    }
                    chooseTipArgs.putString(UserChooseTipFragment.SCREEN_SEQUENCE_EXTRA_KEY, UserChooseTipFragment.SEQUENCE_TYPE_SETTINGS);
                    UserChooseTipFragment geoTipFrag = UserChooseTipFragment.newInstanceWithRecurringNotification(recurringNotification);
                    geoTipFrag.setArguments(chooseTipArgs);

                    mCallbacks.onAnimatedNavigationAction(geoTipFrag,
                            UserChooseTipFragment.TAG,
                            R.animator.oa_slide_left_in,
                            R.animator.oa_slide_left_out,
                            R.animator.oa_slide_right_in,
                            R.animator.oa_slide_right_out,
                            true);
                }
            }
        });
    }

    private ListAdapter buildNotificationAdapter() {
        NotificationItem.hostingActivity = new WeakReference<>((MainActivity) getActivity());
        List<Item> lvItems = new ArrayList<>();
        List<RecurringNotification> recurringNotifications = DbManager.getInstance().getRecurringNotifications();

        lvItems.add(new NotificationHeader(getString(R.string.quitguide_notifications)));
        lvItems.add(new NotificationItem(getString(R.string.tips_to_help_quit), getString(R.string.helpful_messages), R.drawable.ic_quitguide_book, DbManager.getInstance().getState().isAllowNotification(), true));
        lvItems.add(new NotificationHeader(getString(R.string.custom_notifications)));
        for (RecurringNotification recurringNotification : recurringNotifications) {
            final boolean isGeoTagged = recurringNotification.getGeoTag() != null;

            lvItems.add(new NotificationItem(
                    isGeoTagged ? recurringNotification.getGeoTag().getAddress() : getString(R.string.daily_msg) +" "+ Common.formatMilitaryToStandardTime(recurringNotification.getNotification().getTimeOfDay()),
                    getString(R.string.message)+ " " + (recurringNotification.getTip() == null ? getString(R.string.quitguide) : recurringNotification.getTip().getContent()),
                    isGeoTagged ? R.drawable.ic_pin_black : R.drawable.ic_time_indicator,
                    recurringNotification.isActive(),
                    recurringNotification.getId()));
        }
        return new NotificationListAdapter(getActivity(), 0, lvItems);
    }
}
