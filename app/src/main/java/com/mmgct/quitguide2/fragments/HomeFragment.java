package com.mmgct.quitguide2.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewFragment;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mmgct.quitguide2.MainActivity;
import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.fragments.listeners.DrawerCallbacks;
import com.mmgct.quitguide2.managers.DbManager;
import com.mmgct.quitguide2.models.HistoryItem;
import com.mmgct.quitguide2.models.ReasonForQuitting;
import com.mmgct.quitguide2.models.Slip;
import com.mmgct.quitguide2.models.SmokeFreeDay;
import com.mmgct.quitguide2.utils.Common;
import com.mmgct.quitguide2.utils.Constants;
import com.mmgct.quitguide2.utils.ImgUtils;
import com.mmgct.quitguide2.views.notifications.OpenToScreenNavigation;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.sql.SQLException;

/**
 * Created by 35527 on 10/27/2015.
 */
public class HomeFragment extends BaseFragment {

    public static final String TAG = "HomeFragment";

    private TextView mQuitismText;
    private TextView mReasonForQuittingText;
    private ImageView mReasonForQuittingImage;
    private TextView mDaysSmokeFreeNum;
    private TextView mDaysSmokeFreeLbl;
    private View rootView;
    private ReasonForQuitting mDisplayedReasonForQuitting;
    private RelativeLayout mReasonForQuittingContainer;
    private DrawerCallbacks mDrawerCallbacks;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mDrawerCallbacks = (DrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement DrawerCallbacks");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, null, false);

        // Initialize views
        mQuitismText = (TextView) rootView.findViewById(R.id.quitism);
        mReasonForQuittingText = (TextView) rootView.findViewById(R.id.reasons_quitting);
        mReasonForQuittingImage = (ImageView) rootView.findViewById(R.id.img_reason_quitting);
        mDaysSmokeFreeNum = (TextView) rootView.findViewById(R.id.num_days_smokefree);
        mDaysSmokeFreeLbl = (TextView) rootView.findViewById(R.id.lbl_days_smoke_free);
        mReasonForQuittingContainer = (RelativeLayout) rootView.findViewById(R.id.rl_reasons_quitting);

        bindListeners();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            checkForNotificationNavCondition();
        }
    }



    private void checkForNotificationNavCondition() {
        Bundle args = getArguments();
        if (args.getBoolean(OpenToScreenNavigation.STATISTICS, false)) {
            mDrawerCallbacks.openDrawer(DrawerCallbacks.RIGHT);
        }
        else if (args.getBoolean(OpenToScreenNavigation.MANAGE_MY_MOOD, false)) {
            MoodFragment unamimatedMood = new MoodFragment();
            unamimatedMood.disableAnimations();
            args.putBoolean(Constants.ENABLE_ANIMATIONS, true);
            unamimatedMood.setArguments(args);
            mCallbacks.onAddNavigationAction(unamimatedMood, "", true);
        }
        else if (args.getBoolean(OpenToScreenNavigation.TRACK_MY_CRAVINGS, false)) {
            CravingFragment unanimatedCraving = new CravingFragment();
            args.putBoolean(Constants.ENABLE_ANIMATIONS, true);
            unanimatedCraving.setArguments(args);
            mCallbacks.onAddNavigationAction(unanimatedCraving, "", true);
        }
    }

    private void bindListeners() {
        Button btnLearn = (Button) rootView.findViewById(R.id.btn_learn_to_quit);
        ImageButton btnSF = (ImageButton) rootView.findViewById(R.id.btn_smokefree);
        ImageButton btnSlipped = (ImageButton) rootView.findViewById(R.id.btn_slipped);
        ImageButton btnCraving = (ImageButton) rootView.findViewById(R.id.btn_track_craving);
        ImageButton btnMood = (ImageButton) rootView.findViewById(R.id.btn_manage_mood);

        btnLearn.setOnClickListener(new View.OnClickListener() {
            @Override
            public synchronized void onClick(final View v) {
                v.setEnabled(false);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        v.setEnabled(true);
                    }

                    }, 300);
                Bundle args = new Bundle();
                args.putString(WebviewFragment.URL_KEY, "quitguide.html");
                WebviewFragment webViewFragment = new WebviewFragment();
                webViewFragment.setArguments(args);

                mCallbacks.onNavigationAction(webViewFragment, "", false);
            }
        });

        btnSF.setOnClickListener(new View.OnClickListener() {
            @Override
            public synchronized void onClick(final View v) {
                // Save SmokeFreeDay data
                v.setEnabled(false);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        v.setEnabled(true);
                    }
                }, 300);
                SmokeFreeDay sfd;
                DbManager.getInstance().createSmokeFreeDay(sfd = new SmokeFreeDay(Common.millisAtStartOfDay(System.currentTimeMillis())));
                HistoryItem item = new HistoryItem(System.currentTimeMillis(), sfd, HistoryItem.SMK_FREE_DAY);
                DbManager.getInstance().createHistoryItem(item);
                mTrackerHost.send(getString(R.string.category_main), getString(R.string.action_smokefree));
                mTrackerHost.send(getString(R.string.category_main)+"|"+getString(R.string.action_smokefree));
                mCallbacks.onAddNavigationAction(new SFFragment(), "", true);
            }
        });

        btnSlipped.setOnClickListener(new View.OnClickListener() {
            @Override
            public synchronized void onClick(final View v) {
                v.setEnabled(false);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        v.setEnabled(true);
                    }
                }, 300);
                mTrackerHost.send(getString(R.string.category_main), getString(R.string.action_slip));
                mTrackerHost.send(getString(R.string.category_main)+"|"+getString(R.string.action_slip));
                mCallbacks.onAddNavigationAction(new SlippedFragment(), "slipped_fragment", true);
            }
        });

        btnCraving.setOnClickListener(new View.OnClickListener() {
            @Override
            public synchronized void onClick(final View v) {
                v.setEnabled(false);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        v.setEnabled(true);
                    }
                }, 300);
                mTrackerHost.send(getString(R.string.category_main), getString(R.string.action_track_craving));
                mTrackerHost.send(getString(R.string.category_main)+"|"+getString(R.string.action_track_craving));
                mCallbacks.onAddNavigationAction(new CravingFragment(), "", true);
            }
        });

        btnMood.setOnClickListener(new View.OnClickListener() {
            @Override
            public synchronized void onClick(final View v) {
                v.setEnabled(false);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        v.setEnabled(true);
                    }
                }, 300);
                mTrackerHost.send(getString(R.string.category_main), getString(R.string.action_manage_mood));
                mTrackerHost.send(getString(R.string.category_main)+"|"+getString(R.string.action_manage_mood));
                mCallbacks.onAddNavigationAction(new MoodFragment(), "", true);
            }
        });
    }

    public Fragment getFragment() {
        return this;
    }

    public void refresh() {
        new AsyncCaller().execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        new AsyncCaller().execute();

    }


    private class AsyncCaller extends AsyncTask<Void, Void, Object[]>{

        @Override
        protected Object[] doInBackground(Void... voids) {
            DbManager DbInstance = DbManager.getInstance();
            Object[] data = new Object[4];

            // Get random quitism
            data[0] = DbInstance.getRandomQuitism().getMessage();
            // Reasons for quiting
            data[1] = DbInstance.getRandomReason();
            // Smoke free days
            DateTime now = new DateTime(System.currentTimeMillis()).withTimeAtStartOfDay();
            DateTime quitDate = new DateTime(DbInstance.getProfile().getQuitDate()).withTimeAtStartOfDay();
            data[2] = Days.daysBetween(now, quitDate).getDays();

            return data;
        }

        @Override
        protected void onPostExecute(Object[] data) {
            super.onPreExecute();
            // Text for random quitism
            mQuitismText.setText(data[0].toString());

            // Random reason for quitting
            mDisplayedReasonForQuitting = (ReasonForQuitting) data[1];
            if (mDisplayedReasonForQuitting != null) {
                String reasonText = mDisplayedReasonForQuitting.getMessage();
                if (reasonText != null && !reasonText.trim().equals("")) {
                    mReasonForQuittingText.setText(mDisplayedReasonForQuitting.getMessage());
                } else {
                    mReasonForQuittingText.setText(getString(R.string.txt_long_press_to_edit));
                }
                Uri imgPath = null;
                if (mDisplayedReasonForQuitting.getImagePath() != null) {
                    imgPath = Uri.parse(mDisplayedReasonForQuitting.getImagePath());
                }
                if (imgPath != null) {
                    RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams((int)getResources().getDimension(R.dimen.view_height_xsmall), (int)getResources().getDimension(R.dimen.view_width_xsmall));
                    rlp.addRule(RelativeLayout.ALIGN_PARENT_END);
                    rlp.addRule(RelativeLayout.CENTER_VERTICAL);
                    rlp.setMarginEnd((int)getResources().getDimension(R.dimen.spacing_normal));
                    ImgUtils.setImageWithWhiteFrame(getActivity(), mReasonForQuittingImage, imgPath, rlp);
                }
            } else {
                mReasonForQuittingText.setText(getString(R.string.txt_long_press_to_edit));
                mReasonForQuittingImage.setImageResource(android.R.color.transparent);
            }

                // Set  click listener to Reason for Quitting
                if (mReasonForQuittingContainer != null) {
                    mReasonForQuittingContainer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // GA
                            mTrackerHost.send(getString(R.string.category_main), getString(R.string.action_edit_reason_to_quit));
                            mTrackerHost.send(getString(R.string.category_main)+"|"+getString(R.string.action_edit_reason_to_quit));
                            ReasonQuittingFragment frag = null;
                            if (mDisplayedReasonForQuitting != null) {
                                frag = ReasonQuittingFragment.newEditInstance(mDisplayedReasonForQuitting.getId());
                            } else {
                                ReasonForQuitting newReasonForQuitting = new ReasonForQuitting();
                                newReasonForQuitting.setMessage("");
                                DbManager.getInstance().createReasonForQuitting(newReasonForQuitting);
                                frag = ReasonQuittingFragment.newEditInstance(newReasonForQuitting.getId());
                            }
                            frag.setTargetFragment(getFragment(), 0);
                            getActivity().getFragmentManager()
                                    .beginTransaction()
                                    .setCustomAnimations(R.animator.oa_slide_up, R.animator.oa_slide_down, R.animator.oa_slide_up, R.animator.oa_slide_down)
                                    .add(R.id.main_fragment_container, frag, "")
                                    .addToBackStack(null)
                                    .commit();
                            ((MainActivity)getActivity()).hideHeader();
                        }
                    });
                }

            // Smoke free days data
            int daysSmokeFree = (Integer) data[2];
            if (daysSmokeFree > 0) {
                mDaysSmokeFreeLbl.setText(daysSmokeFree == 1 ? "დღე მოწევამდე" : "დღე მოწევამდე");
            } else {
                mDaysSmokeFreeLbl.setText(Math.abs(daysSmokeFree) == 1 ? "დღე მოწევის გარეშე"
                        :"დღე მოწევის გარეშე");
            }
            mDaysSmokeFreeNum.setText(String.valueOf(Math.abs(daysSmokeFree)));
        }
    }
}
