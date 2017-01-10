package com.mmgct.quitguide2.fragments;

import android.app.Fragment;
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
 * Created by 35527 on 2/26/2016.
 * Very Similar to NotificationListFragment it just only shows time notifications
 */
public class TimeNotificationListFragment extends BaseFragment {

    private View rootView;
    private ListView mListView;
    private View mIBPlus;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_notification_list, container, false);
        mHeaderCallbacks.hideAllHeaderViews();
        ViewGroup headerLayout = mHeaderCallbacks.getHeaderLayout(R.id.lyt_header_loc_tod_drawer_items);
        headerLayout.setVisibility(View.VISIBLE);
        mIBPlus = headerLayout.findViewById(R.id.ic_loc_tod_plus);
        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        bindViewListeners();
        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        mHeaderCallbacks.showMain();
    }

    private void bindViewListeners() {
        if (mIBPlus != null) {
            mIBPlus.setVisibility(View.VISIBLE);
            mIBPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle args = new Bundle();
                    Fragment helpTimeFragment = new HelpTimeFragment();
                    args.putString(UserChooseTipFragment.SCREEN_SEQUENCE_EXTRA_KEY, UserChooseTipFragment.SEQUENCE_TYPE_SETTINGS_NEW_TIMED);
                    helpTimeFragment.setArguments(args);
                    Common.mDisableWhatsNewAnim = false;
                    mCallbacks.onAnimatedNavigationAction(helpTimeFragment,
                            HelpTimeFragment.TAG,
                            R.animator.oa_slide_left_in,
                            R.animator.oa_slide_left_out,
                            R.animator.oa_slide_right_in,
                            R.animator.oa_slide_right_out,
                            true);
                }
            });
        }

        mListView = (ListView) rootView.findViewById(R.id.lv_notification);
        mListView.setAdapter(buildNotificationAdapter());

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // All other view besides Notification item should be disabled
                if (parent.getItemAtPosition(position) instanceof NotificationItem) {
                    NotificationItem item = (NotificationItem) parent.getItemAtPosition(position);
                    view.setBackgroundColor(Color.parseColor("#64000000"));
                    RecurringNotification recurringNotification = DbManager.getInstance().getRecurringNotificationById(item.getRecurringNotificationId());
                    boolean isGeotag = recurringNotification.getGeoTag() != null;

                    Bundle chooseTipArgs = new Bundle();
                    chooseTipArgs.putBoolean(UserChooseTipFragment.BUILD_FROM_EXISTING_KEY, true);
                    if (isGeotag) {
                        chooseTipArgs.putBoolean(UserChooseTipFragment.GEOFENCE_KEY, true);
                    }
                    else if (recurringNotification.getNotification() != null) {
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
        List<RecurringNotification> recurringNotifications = DbManager.getInstance().getRecurringTimedNotifications();

        lvItems.add(new NotificationHeader(getString(R.string.time_base_notifications)));
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
