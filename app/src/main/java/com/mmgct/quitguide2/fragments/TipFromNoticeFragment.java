package com.mmgct.quitguide2.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.managers.DbManager;
import com.mmgct.quitguide2.models.Notification;
import com.mmgct.quitguide2.models.RecurringNotification;
import com.mmgct.quitguide2.models.Tip;
import com.mmgct.quitguide2.utils.Constants;
import com.mmgct.quitguide2.utils.ImgUtils;

/**
 * Created by 35527 on 1/27/2016.
 */
public class TipFromNoticeFragment extends BaseFragment {

    public static final String TAG = TipFromNoticeFragment.class.getSimpleName();
    public static final String RANDOM_FALLBACK = "random_tip";

    private View rootView;
    private Notification mNotification;
    private boolean mShowRandTip = false;
    private Tip mTip;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mHeaderCallbacks.disableDrawers();
        mHeaderCallbacks.hideHeader();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_tip_from_notice, container, false);
        Bundle args = getArguments();
        // See if we need to fall back to using a random tip
        if (args.getBoolean(RANDOM_FALLBACK, false)) {
            mShowRandTip = true;
        } else if (args.containsKey(Constants.NOTIFICATION_ID_KEY)) {
            mNotification = DbManager.getInstance().getNotificationById(args.getInt(Constants.NOTIFICATION_ID_KEY));
        }
        // If not try and retrieve RecurringNotification
        if (mNotification != null) {
            RecurringNotification recurringNotification = mNotification.getRecurringNotification();
            if (recurringNotification != null) {
                mShowRandTip = recurringNotification.isUseRandTip();
                mTip = recurringNotification.getTip();
                if (mTip == null || (mTip.getImgFilePath() == null && mTip.getContent() == null)) {
                    mShowRandTip = true;
                }
            }
        }
        TextView tvTip = (TextView) rootView.findViewById(R.id.tv_view_tip);
        ImageView ivTip = (ImageView) rootView.findViewById(R.id.iv_view_tip);
        // Show random tip
        if (mShowRandTip) {
            tvTip.setText(DbManager.getInstance().getRandomTip().getMessage());
            ivTip.setVisibility(View.GONE);
        }
        // Show custom tip
        else {
            String stringUri = mTip.getImgFilePath();
            if (stringUri != null) {
                Uri uri = Uri.parse(stringUri);
                ImgUtils.setImageWithWhiteFrame(getActivity(), ivTip, uri, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int)getResources().getDimension(R.dimen.view_big)));
            } else {
                ivTip.setVisibility(View.GONE);
            }
            String userTip = mTip.getContent();
            if (userTip != null) {
                tvTip.setText(userTip);
            }
        }
        bindListeners();
        return rootView;
    }

    private void bindListeners() {
        rootView.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHeaderCallbacks.enableDrawers();
                mHeaderCallbacks.showHeader();
                mCallbacks.onAnimatedNavigationAction(new HomeFragment(),
                        HomeFragment.TAG,
                        R.animator.oa_instant_appear,
                        R.animator.oa_slide_down,
                        R.animator.oa_slide_up,
                        R.animator.oa_slide_down,
                        false);
            }
        });
    }
}
