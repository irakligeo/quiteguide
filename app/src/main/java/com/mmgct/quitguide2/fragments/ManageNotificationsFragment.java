package com.mmgct.quitguide2.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mmgct.quitguide2.MainActivity;
import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.fragments.dialogs.iOSStyledDecisionDialog;
import com.mmgct.quitguide2.fragments.listeners.DialogDismissListener;
import com.mmgct.quitguide2.utils.Common;

/**
 * Created by 35527 on 2/24/2016.
 */
public class ManageNotificationsFragment extends BaseFragment implements View.OnClickListener {

    public static final String TAG = ManageNotificationsFragment.class.getSimpleName();
    public static final String MAP = "map";
    public static final String LIST = "list";
    public static final String SEQUENCE_EXTRA_KEY = "manage_notifications_seq_extra_key"; /*Header changes based on where this fragment is inflated from*/
    public static final String SEQUENCE_FROM_SETTINGS = "from_settings";
    public static final String SEQUENCE_FROM_DRAWER = "from_drawer";

    private View rootView;
    private Button mBtnMap, mBtnList;
    private int mSelectedToggle;
    private String tabToDisplay = ""; // Keeps track of which view to display on popBackstackEnter
    private String mSequenceType;
    private SetLocationFragment mLocationFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Common.mDisableWhatsNewAnim = false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_manage_notifications, container, false);
        mSelectedToggle = R.id.btn_map_view;
        mBtnMap = (Button) rootView.findViewById(R.id.btn_map_view);
        mBtnList = (Button) rootView.findViewById(R.id.btn_list_view);
        if (getArguments() != null) {
            mSequenceType = getArguments().getString(SEQUENCE_EXTRA_KEY, "");
        }
        if (tabToDisplay.equals(LIST)) {
            showListFragment(); // initialize with list view
            toggle(R.id.btn_list_view);
        }
        else {
            toggle(R.id.btn_map_view);
            showSetLocationFragment(); // initialize with map view
        }
        bindListeners();
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHeaderCallbacks.showMain();
    }

    private void bindListeners() {
        mBtnMap.setOnClickListener(this);
        mBtnList.setOnClickListener(this);

        View ibtnPlus = null;
        if (SEQUENCE_FROM_SETTINGS.equals(mSequenceType)) {
            ibtnPlus = mHeaderCallbacks.getHeaderLayout(R.id.header_notifications_with_back_plus).findViewById(R.id.ic_plus);
        }
        else if (SEQUENCE_FROM_DRAWER.equals(mSequenceType)) {
            ibtnPlus = mHeaderCallbacks.getHeaderLayout(R.id.lyt_header_loc_tod_drawer_items).findViewById(R.id.ic_loc_tod_plus);
        }

        if(ibtnPlus != null) {
           ibtnPlus.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   iOSStyledDecisionDialog dialog = iOSStyledDecisionDialog.newInstance(getString(R.string.header_add_notification), getString(R.string.content_add_notification), getString(R.string.location), getString(R.string.time));
                   dialog.setOnDismissListener(new DialogDismissListener() {
                       @Override
                       public void onDialogDismissed(Bundle args) {
                           if (args != null && args.containsKey(iOSStyledDecisionDialog.BUTTON_LEFT_CALLBACK_KEY)) {
                               showSetLocationFragment();
                           } else {
                               Fragment helpTimeFragment = new HelpTimeFragment();
                               args.putString(UserChooseTipFragment.SCREEN_SEQUENCE_EXTRA_KEY, UserChooseTipFragment.SEQUENCE_TYPE_SETTINGS_NEW_TIMED);
                               helpTimeFragment.setArguments(args);

                               mCallbacks.onAnimatedNavigationAction(helpTimeFragment,
                                       HelpTimeFragment.TAG,
                                       R.animator.oa_slide_left_in,
                                       R.animator.oa_slide_left_out,
                                       R.animator.oa_slide_right_in,
                                       R.animator.oa_slide_right_out,
                                       true);
                           }
                       }
                   });
                   dialog.show(getFragmentManager(), "dialog");
               }
           });
        }
    }

    private void showSetLocationFragment() {
        toggle(R.id.btn_map_view);
        SetLocationFragment setLocationFragment = new SetLocationFragment();
        setLocationFragment.setTargetFragment(this, 0);
        Bundle args = new Bundle();
        args.putString(UserChooseTipFragment.SCREEN_SEQUENCE_EXTRA_KEY, UserChooseTipFragment.SEQUENCE_TYPE_SETTINGS);
        args.putString(SEQUENCE_EXTRA_KEY, mSequenceType);
        setLocationFragment.setArguments(args);
        if (SEQUENCE_FROM_SETTINGS.equals(mSequenceType)) {
            mHeaderCallbacks.showBackNotification();
        }
        else if (SEQUENCE_FROM_DRAWER.equals(mSequenceType)) {
            mHeaderCallbacks.hideAllHeaderViews();
            ViewGroup vg = mHeaderCallbacks.getHeaderLayout(R.id.lyt_header_loc_tod_drawer_items);
            vg.setVisibility(View.VISIBLE);
            vg.findViewById(R.id.ic_loc_tod_plus).setVisibility(View.GONE);
        }

        getChildFragmentManager().beginTransaction().replace(R.id.frag_container_notifications, setLocationFragment).commit();

        // getActivity().getFragmentManager().beginTransaction().add(R.id.main_fragment_container, setLocationFragment, "manage_notifications").commit();

        // mCallbacks.onNavigationAction(setLocationFragment, SetLocationFragment.TAG, false);

      //  getChildFragmentManager().beginTransaction().replace(R.id.frag_container_notifications, new JournalFragment()).commit();
    }

    private void showListFragment() {
        toggle(R.id.btn_list_view);
        Fragment frag = new NotificationListFragment();
        frag.setTargetFragment(this, 0);
        Bundle args = new Bundle();
        args.putString(SEQUENCE_EXTRA_KEY, mSequenceType);
        args.putString(UserChooseTipFragment.SCREEN_SEQUENCE_EXTRA_KEY, UserChooseTipFragment.SEQUENCE_TYPE_SETTINGS);
        frag.setArguments(args);
        if (SEQUENCE_FROM_SETTINGS.equals(mSequenceType)) {
            mHeaderCallbacks.showBackWithPlus();
        }
        else if (SEQUENCE_FROM_DRAWER.equals(mSequenceType)) {
            mHeaderCallbacks.hideAllHeaderViews();
            ViewGroup vg = mHeaderCallbacks.getHeaderLayout(R.id.lyt_header_loc_tod_drawer_items);
            vg.setVisibility(View.VISIBLE);
        }
        getChildFragmentManager().beginTransaction().replace(R.id.frag_container_notifications, frag).commit();
        //getFragmentManager().beginTransaction().replace(R.id.frag_container_notifications, frag).commit();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == mSelectedToggle) {
            return;
        }
        switch (id) {
            case(R.id.btn_map_view):
                showSetLocationFragment();
                break;
            case(R.id.btn_list_view):
                showListFragment();
                break;
        }
    }

    private void toggle(int selectedId) {
        mSelectedToggle = selectedId;
        if (selectedId == R.id.btn_map_view) {
            // GA
            mTrackerHost.send(getString(R.string.category_notification_map_list), getString(R.string.action_map_selected));
            mTrackerHost.send(getString(R.string.category_notification_map_list) + "|" + getString(R.string.action_map_selected));
            if (Build.VERSION.SDK_INT >= 23) {
                mBtnList.setBackground(getResources().getDrawable(R.drawable.shape_right_round_rect_white, getActivity().getTheme()));
                mBtnList.setTextColor(getResources().getColor(R.color.black, getActivity().getTheme()));
                mBtnMap.setBackground(getResources().getDrawable(R.drawable.shape_left_round_rect_black, getActivity().getTheme()));
                mBtnMap.setTextColor(getResources().getColor(R.color.white, getActivity().getTheme()));
            } else {
                mBtnList.setTextColor(getResources().getColor(R.color.black));
                mBtnList.setBackground(getResources().getDrawable(R.drawable.shape_right_round_rect_white));
                mBtnMap.setBackground(getResources().getDrawable(R.drawable.shape_left_round_rect_black));
                mBtnMap.setTextColor(getResources().getColor(R.color.white));
            }
        } else {
            // GA
            mTrackerHost.send(getString(R.string.category_notification_map_list), getString(R.string.action_list_selected));
            mTrackerHost.send(getString(R.string.category_notification_map_list) + "|" + getString(R.string.action_list_selected));
            if (Build.VERSION.SDK_INT >= 23) {
                mBtnList.setBackground(getResources().getDrawable(R.drawable.shape_right_round_rect_black, getActivity().getTheme()));
                mBtnList.setTextColor(getResources().getColor(R.color.white, getActivity().getTheme()));
                mBtnMap.setBackground(getResources().getDrawable(R.drawable.shape_left_round_rect_white, getActivity().getTheme()));
                mBtnMap.setTextColor(getResources().getColor(R.color.black, getActivity().getTheme()));
            } else {
                mBtnList.setTextColor(getResources().getColor(R.color.white));
                mBtnList.setBackground(getResources().getDrawable(R.drawable.shape_right_round_rect_black));
                mBtnMap.setBackground(getResources().getDrawable(R.drawable.shape_left_round_rect_white));
                mBtnMap.setTextColor(getResources().getColor(R.color.black));
            }
        }
    }

    public String getTabToDisplay() {
        return tabToDisplay;
    }

    public void setTabToDisplay(String tabToDisplay) {
        this.tabToDisplay = tabToDisplay;
    }

    public SetLocationFragment getLocationFragment() {
        return mLocationFragment;
    }

    public void setLocationFragment(SetLocationFragment mLocationFragment) {
        this.mLocationFragment = mLocationFragment;
    }
}
