package com.mmgct.quitguide2.fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.mmgct.quitguide2.BuildConfig;
import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.fragments.dialogs.OkDialog;
import com.mmgct.quitguide2.fragments.dialogs.iOSStyledDecisionDialog;
import com.mmgct.quitguide2.fragments.listeners.DialogDismissListener;
import com.mmgct.quitguide2.fragments.listeners.ExternalImageActivityCallbacks;
import com.mmgct.quitguide2.fragments.listeners.OnAnimationActionListener;
import com.mmgct.quitguide2.fragments.listeners.PictureFragmentCallbacks;
import com.mmgct.quitguide2.managers.DbManager;
import com.mmgct.quitguide2.models.GeoTag;
import com.mmgct.quitguide2.models.Notification;
import com.mmgct.quitguide2.models.RecurringNotification;
import com.mmgct.quitguide2.models.Tip;
import com.mmgct.quitguide2.service.AddGeofencesIntentService;
import com.mmgct.quitguide2.utils.AlarmScheduler;
import com.mmgct.quitguide2.utils.Common;
import com.mmgct.quitguide2.utils.Constants;
import com.mmgct.quitguide2.views.notifications.OpenToScreenNavigation;

import java.io.File;

/**
 * Created by 35527 on 1/22/2016.
 */
public class UserChooseTipFragment extends BaseFragment implements View.OnClickListener, DialogDismissListener, PictureFragmentCallbacks {

    public static final String TAG = UserChooseTipFragment.class.getSimpleName();
    public static final String TIME_KEY = "time_key";
    public static final String GEOFENCE_KEY = "geofence_key";
    public static final String COLOR_EXTRA_KEY = "color_key";
    public static final String BUILD_FROM_EXISTING_KEY = "build_from_existing_notice"; /*If we are displaying and overwriting an old RecurringNotification*/

    public static final String SCREEN_SEQUENCE_EXTRA_KEY = "screen_sequence";   /* For determining the screen sequence of certain operations for this screen */
    public static final String SEQUENCE_TYPE_WHATS_NEW = "seq_whats_new";       /* Coming from What's New screen */
    public static final String SEQUENCE_TYPE_CRAVING = "seq_craving";           /* Coming from Craving screen */
    public static final String SEQUENCE_TYPE_SLIP = "seq_slip";                 /* Coming from Slip screen*/
    public static final String SEQUENCE_TYPE_SETTINGS = "seq_settings";         /* Coming from Settings screen*/
    public static final String SEQUENCE_TYPE_SETTINGS_NEW_TIMED = "seq_new_time";


    private View rootView;
    private EditText mEdtUserTip;
    private ImageView mIvUserImg;
    private String mTime;
    private RecurringNotification mRecurringNotification;
    private boolean mSlideDownAnimation;
    private ExternalImageActivityCallbacks mPictureCallbacks;
    private Uri mPicUri;
    private OnAnimationActionListener mOnAnimationActionListener;
    private String mSequenceType;
    private boolean mIsBuildFromExisting;

    public static UserChooseTipFragment newInstanceWithRecurringNotification(RecurringNotification recurringNotification) {
        UserChooseTipFragment userChooseTipFragment = new UserChooseTipFragment();
        userChooseTipFragment.setRecurringNotification(recurringNotification);
        return userChooseTipFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mPictureCallbacks = (ExternalImageActivityCallbacks) activity;
        } catch (ClassCastException e) {
            Log.e(TAG, "Activity must implement ExternalImageActivityCallbacks", e);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getArguments().containsKey(TIME_KEY)) {
                mTime = getArguments().getString(TIME_KEY);
            }
            mSequenceType = getArguments().getString(SCREEN_SEQUENCE_EXTRA_KEY, "");
        }
        Common.mDisableWhatsNewAnim = false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_user_choose_tip, container, false);
        mEdtUserTip = (EditText) rootView.findViewById(R.id.edt_user_tip);
        mIvUserImg = (ImageView) rootView.findViewById(R.id.iv_user_pic);
        setBackgroundColor();
        TextView tvHeader = (TextView) rootView.findViewById(R.id.header_user_tip);
        tvHeader.setText(getArguments().containsKey(TIME_KEY) ? getString(R.string.header_time_tip) : getString(R.string.header_location_tip));
        bindListeners();
        setupForSequence();
        if (getArguments() != null && getArguments().getBoolean(BUILD_FROM_EXISTING_KEY)) {
            buildFromExisting();
        }
        return rootView;
    }

    private void buildFromExisting() {
        Tip tip = mRecurringNotification.getTip();
        if (tip != null) {
            mEdtUserTip.setText(tip.getContent());
            if (tip.getImgFilePath() != null && !tip.getImgFilePath().trim().equals("")) {
                loadImage(mIvUserImg, mPicUri = Uri.parse(tip.getImgFilePath()));
            }
        }
    }

    private void setupForSequence() {
        if (SEQUENCE_TYPE_SETTINGS.equals(mSequenceType) || SEQUENCE_TYPE_SETTINGS_NEW_TIMED.equals(mSequenceType)){
            rootView.findViewById(R.id.btn_close).setVisibility(View.GONE);
            ((ScrollView)rootView.findViewById(R.id.sv_choose_tip)).setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            mHeaderCallbacks.showBackBlack();
        }
    }

    private void setBackgroundColor() {
        if (getArguments() != null) {
            if (Build.VERSION.SDK_INT >= 23) {
                rootView.setBackgroundColor(getResources().getColor(getArguments().getInt(COLOR_EXTRA_KEY, R.color.dark_blue2), getActivity().getTheme()));
            } else {
                rootView.setBackgroundColor(getResources().getColor(getArguments().getInt(COLOR_EXTRA_KEY, R.color.dark_blue2)));
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mOnAnimationActionListener != null) {
            mOnAnimationActionListener.startTransitionAnimation(false);
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if(mOnAnimationActionListener != null) {
            mOnAnimationActionListener.startTransitionAnimation(true);
        }
    }

    private void bindListeners() {
        ImageButton btnClose = (ImageButton) rootView.findViewById(R.id.btn_close);
        Button btnCustomTip = (Button) rootView.findViewById(R.id.btn_send_custom_tip);
        Button btnRandomTip = (Button) rootView.findViewById(R.id.btn_send_random_tip);

        btnClose.setOnClickListener(this);
        btnCustomTip.setOnClickListener(this);
        btnRandomTip.setOnClickListener(this);
        mIvUserImg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case(R.id.btn_close):
                mHeaderCallbacks.showHeader();
                mHeaderCallbacks.enableDrawers();
                disableAnimationsAndClearBackStack();
                break;
            case(R.id.btn_send_custom_tip):
                Common.addClickFilter((Button)v, getResources().getColor(R.color.transparent_black));
                if (getArguments() != null && getArguments().containsKey(TIME_KEY)) {
                    mTrackerHost.send(getString(R.string.category_notification_tip), getString(R.string.action_save_custom_notification), getString(R.string.label_add_time));
                    mTrackerHost.send(getString(R.string.category_notification_tip) + "|" + getString(R.string.action_save_custom_notification) + "|" + getString(R.string.label_add_time));
                } else {
                    mTrackerHost.send(getString(R.string.category_notification_tip), getString(R.string.action_save_custom_notification), getString(R.string.label_add_location));
                    mTrackerHost.send(getString(R.string.category_notification_tip) + "|" + getString(R.string.action_save_custom_notification) + "|" + getString(R.string.label_add_location));
                }
                if ((mEdtUserTip != null && !mEdtUserTip.getText().toString().trim().equals("")) || mPicUri != null) {
                    if (getArguments() != null && getArguments().getBoolean(BUILD_FROM_EXISTING_KEY)
                            || mIsBuildFromExisting) { // Updating existing
                        updateRecurringNotification();
                    }
                    else { // Creating new
                        Tip tip = new Tip(); // Create tip
                        String userTip = mEdtUserTip.getText().toString();
                        tip.setContent(userTip);
                        tip.setUserCreated(true);
                        if (mPicUri != null) {
                            tip.setImgFilePath(mPicUri.toString());
                        }
                        DbManager.getInstance().createTip(tip);
                        Notification notification = null;

                        if (mTime != null && getArguments().containsKey(TIME_KEY)) { // Recurring timed user tip
                            notification = new Notification();
                            notification = createNotification(userTip, notification);
                            RecurringNotification recurringNotification = createRecurringNotification(tip, notification, false);
                            AlarmScheduler.getInstance().scheduleRecurringNotification(recurringNotification);
                        } else if (mRecurringNotification != null && getArguments().containsKey(GEOFENCE_KEY)) { // Recurring user geofence tip
                            mRecurringNotification.getNotification().setDetail(userTip); // Set notification text
                            mRecurringNotification.setTip(tip); // Set tip
                            persistRecurringNotification(); // Save to db
                            Intent i = new Intent(getActivity(), AddGeofencesIntentService.class);
                            i.putExtra(AddGeofencesIntentService.FUNCTION_EXTRA_KEY, AddGeofencesIntentService.FUNCTION_TYPE_SET_SINGLE); // Flag for setting single geofence
                            i.putExtra(AddGeofencesIntentService.RECURRING_NOTIFICATION_ID_EXTRA_KEY, mRecurringNotification.getId());
                            getActivity().startService(i);
                        }
                    }
                    if (SEQUENCE_TYPE_SETTINGS.equals(mSequenceType)) {
                        mCallbacks.popBackStack();
                    }
                    else if(SEQUENCE_TYPE_SETTINGS_NEW_TIMED.equals(mSequenceType)) {
                        mCallbacks.popBackStack(); // pop two off the stack in this case and go back to settings screen
                        mCallbacks.popBackStack();
                    }
                    else {
                        promptForAnother();
                    }

                } else {
                    new OkDialog().newInstance(getResources().getString(R.string.diag_enter_tip_take_photo)).show(getActivity().getFragmentManager(), "OkDialog");
                }
                break;
            case(R.id.btn_send_random_tip):
                Common.addClickFilter((Button)v, getResources().getColor(R.color.transparent_black));
                if (getArguments() != null && getArguments().containsKey(TIME_KEY)) {
                    mTrackerHost.send(getString(R.string.category_notification_tip), getString(R.string.action_save_random_notification), getString(R.string.label_add_time));
                    mTrackerHost.send(getString(R.string.category_notification_tip) + "|" + getString(R.string.action_save_random_notification) + "|" + getString(R.string.label_add_time));
                } else {
                    mTrackerHost.send(getString(R.string.category_notification_tip), getString(R.string.action_save_random_notification), getString(R.string.label_add_location));
                    mTrackerHost.send(getString(R.string.category_notification_tip) + "|" + getString(R.string.action_save_random_notification) + "|" + getString(R.string.label_add_location));
                }
                Notification notification;
                if (getArguments() != null && getArguments().getBoolean(BUILD_FROM_EXISTING_KEY)) { // Updating an existing notification
                    updateRecurringNotification();
                }
                else {
                    if (mTime != null && getArguments().containsKey(TIME_KEY)) { // Recurring timed random tip
                        notification = new Notification();
                        notification = createNotification("STUB", notification);
                        AlarmScheduler.getInstance().scheduleRecurringNotification(createRecurringNotification(null, notification, true));
                    } else if (mRecurringNotification != null && getArguments().containsKey(GEOFENCE_KEY)) { // Recurring random geofence tip
                        mRecurringNotification.setUseRandTip(true);
                        persistRecurringNotification();
                        Intent i = new Intent(getActivity(), AddGeofencesIntentService.class);
                        i.putExtra(AddGeofencesIntentService.FUNCTION_EXTRA_KEY, AddGeofencesIntentService.FUNCTION_TYPE_SET_SINGLE); // Flag for setting single geofence
                        i.putExtra(AddGeofencesIntentService.RECURRING_NOTIFICATION_ID_EXTRA_KEY, mRecurringNotification.getId());
                        getActivity().startService(i);
                    }
                }

                if (SEQUENCE_TYPE_SETTINGS.equals(mSequenceType)) {
                    mCallbacks.popBackStack();
                }
                else if(SEQUENCE_TYPE_SETTINGS_NEW_TIMED.equals(mSequenceType)) {
                    mCallbacks.popBackStack(); // pop two off the stack in this case and go back to settings screen
                    mCallbacks.popBackStack();
                }
                else {
                    promptForAnother();
                }
                break;
            case(R.id.iv_user_pic):
                Common.addClickFilter((ImageView) v, getResources().getColor(R.color.transparent_black));
                final PictureFragmentCallbacks callback = this;
                AlertDialog dialog = new AlertDialog.Builder(getView().getContext())
                        .setPositiveButton("გადაიღე სურათი", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                if (mPictureCallbacks.getPictureIntent(callback) != null) {
                                    startActivityForResult(mPictureCallbacks.getPictureIntent(callback), ExternalImageActivityCallbacks.REQUEST_CODE_TAKE_PICTURE);
                                }
                            }
                        })
                        .setNegativeButton("ამოირჩიე ალბომიდან", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                startActivityForResult(mPictureCallbacks.getGalleryIntent(), ExternalImageActivityCallbacks.REQUEST_CODE_GALLERY);
                            }
                        }).setIcon(null).show();
                break;
        }
    }

    private void updateRecurringNotification() {
        if (mRecurringNotification.getNotification() != null) {
            // If we allowed changing of time of day
            /*Notification updateNotification = mRecurringNotification.getNotification();
            if (updateNotification != null) {
                updateNotification.setTimeOfDay(mTime);
                DbManager.getInstance().updateNotification(updateNotification);
            }*/
            Tip updateTip = mRecurringNotification.getTip();
            if (updateTip != null) {
                updateTip.setContent(mEdtUserTip.getText().toString());
                if (mPicUri != null) {
                    updateTip.setImgFilePath(mPicUri.toString());
                }
                DbManager.getInstance().updateTip(updateTip);
                DbManager.getInstance().updateRecurringNotification(mRecurringNotification);
            }
            else {
                updateTip = new Tip();
                updateTip.setContent(mEdtUserTip.getText().toString());
                if (mPicUri != null) {
                    updateTip.setImgFilePath(mPicUri.toString());
                }
                DbManager.getInstance().createTip(updateTip);
                mRecurringNotification.setTip(updateTip);
                DbManager.getInstance().updateRecurringNotification(mRecurringNotification);
            }
        }
    }

    private void persistRecurringNotification() {
        GeoTag geoTag = mRecurringNotification.getGeoTag();
        Notification notification = mRecurringNotification.getNotification();

        DbManager.getInstance().createGeoTag(geoTag);
        mRecurringNotification.setGeoTag(geoTag);

        DbManager.getInstance().createNotification(notification);
        mRecurringNotification.setNotification(notification);

        DbManager.getInstance().createRecurringNotification(mRecurringNotification);
        notification.setRecurringNotification(mRecurringNotification); // Work around for cyclical dependency
        DbManager.getInstance().updateNotification(mRecurringNotification.getNotification());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // If image capture was successful set it to ImageView
        if (requestCode == ExternalImageActivityCallbacks.REQUEST_CODE_TAKE_PICTURE
                || requestCode == ExternalImageActivityCallbacks.REQUEST_CODE_GALLERY) {

            final ImageView ivUserPic = (ImageView) rootView.findViewById(R.id.iv_user_pic); // The ImageView that will take the picture

            // If gallery request get the uri
            if (requestCode == ExternalImageActivityCallbacks.REQUEST_CODE_GALLERY && data != null) {
                mPicUri = data.getData();
            }

            /**
             * Otherwise URI will be set in {@link PictureFragmentCallbacks#setImageUri(Uri)}
             */

            if (resultCode == Activity.RESULT_OK && mPicUri != null) {
                loadImage(ivUserPic, mPicUri);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                if (mPicUri != null) {
                    File file = new File(mPicUri.getPath());
                    if (file.exists()) {
                        file.delete();
                    }
                    mPicUri = null;
                }
                if (ivUserPic != null) {
                    resetImgViewToDefault(ivUserPic);
                }
            }
        }
    }

    private void loadImage(final ImageView ivUserPic, final Uri picUri) {
        if (ivUserPic != null) {
            Glide.with(getActivity()).load(picUri).centerCrop().animate(android.R.anim.fade_in).listener(new RequestListener<Uri, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    if (ivUserPic != null) {
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int)getResources().getDimension(R.dimen.view_width_normal), (int)getResources().getDimension(R.dimen.view_height_normal));
                        lp.gravity = Gravity.CENTER_HORIZONTAL;
                        lp.setMargins(0, (int) getResources().getDimension(R.dimen.spacing_large), 0, 0);
                        ivUserPic.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        ivUserPic.setLayoutParams(lp);
                        if (Build.VERSION.SDK_INT >= 21) {
                            ivUserPic.setBackground(getResources().getDrawable(R.drawable.white_frame, getActivity().getTheme()));
                        } else {
                            ivUserPic.setBackground(getResources().getDrawable(R.drawable.white_frame));
                        }
                    }
                    return false;
                }
            }).into(ivUserPic);
    }
    }

    private void resetImgViewToDefault(ImageView ivUserPic) {
        ivUserPic.setScaleType(ImageView.ScaleType.CENTER);
        ivUserPic.setImageResource(R.drawable.ic_camera);
        ivUserPic.setBackgroundResource(R.drawable.white_frame);
    }

    private void promptForAnother() {
        DialogFragment diagOtherTimesPrompt;
        if(getArguments().containsKey(TIME_KEY)) {
            diagOtherTimesPrompt = new iOSStyledDecisionDialog().newInstance(getString(R.string.diag_header_another_time), getString(R.string.diag_another_time), getString(R.string.yes), getString(R.string.no));
        } else {
            diagOtherTimesPrompt = new iOSStyledDecisionDialog().newInstance(getString(R.string.diag_header_another_location), getString(R.string.diag_another_location), getString(R.string.yes), getString(R.string.no));
        }
        diagOtherTimesPrompt.setTargetFragment(this, 0);
        diagOtherTimesPrompt.show(getActivity().getFragmentManager(), "iOSStyledDecisionDialog");
    }

    private Notification createNotification(String userTip, Notification notification) {
        notification.setTimeOfDay(mTime);
        notification.setDetail(userTip);
        notification.setOpenToScreen(OpenToScreenNavigation.TIP);
        notification.setDaysRelativeToLastActivity(-1); // Set these vals to -1 to flag that it is not a notification of these types
        notification.setDaysRelativeToNoProfileSet(-1);
        notification.setDaysRelativeToProgramStart(-1);
        notification.setDaysRelativeToQuitDate(-1);
        return notification;
    }

    private RecurringNotification createRecurringNotification(Tip tip, Notification notification, boolean random) {
        RecurringNotification recurringNotification = new RecurringNotification();
        recurringNotification.setTip(tip);
        recurringNotification.setActive(true);
        recurringNotification.setUseRandTip(random);
        // Cyclical dependency work around
        DbManager.getInstance().createNotification(notification);
        recurringNotification.setNotification(notification);
        DbManager.getInstance().createRecurringNotification(recurringNotification);
        notification.setRecurringNotification(recurringNotification);
        DbManager.getInstance().updateNotification(notification);
        return recurringNotification;
    }

    private void disableAnimationsAndClearBackStack() {
        disableStackedFragmentAnimations();
        mSlideDownAnimation = true;
        mHeaderCallbacks.showHeader();
        mHeaderCallbacks.enableDrawers();
        mCallbacks.clearBackStack(new HomeFragment());
    }


    private void disableStackedFragmentAnimations() {
        if (mOnAnimationActionListener != null) {
            mOnAnimationActionListener.disableAnimation();
        }
        Common.mDisableWhatsNewAnim = true;
    }

    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
        if (mSlideDownAnimation) {
            return AnimatorInflater.loadAnimator(getActivity(), R.animator.oa_slide_down);
        } else {
            return super.onCreateAnimator(transit, enter, nextAnim);
        }
    }

    @Override
    public void onDialogDismissed(Bundle args) {
        boolean geofenceTip = getArguments() != null && getArguments().containsKey(GEOFENCE_KEY);

        if (args.containsKey(iOSStyledDecisionDialog.BUTTON_LEFT_CALLBACK_KEY)) {
            String sequenceType = getArguments() != null ? getArguments().getString(SCREEN_SEQUENCE_EXTRA_KEY, "") : "";
            if (sequenceType.equals(SEQUENCE_TYPE_CRAVING) || sequenceType.equals(SEQUENCE_TYPE_SLIP)) {

                // Animation listener
                OnAnimationActionListener onAnimationActionListener = new OnAnimationActionListener() {
                    @Override
                    public void disableAnimation() {
                        Common.mDisableWhatsNewAnim = true;
                        if (mOnAnimationActionListener != null) {
                            mOnAnimationActionListener.disableAnimation();
                        }
                    }

                    @Override
                    public void enableAnimation() {

                    }

                    @Override
                    public void startTransitionAnimation(boolean enter) {
                        if (!Common.mDisableWhatsNewAnim) {
                            if (enter) {
                                Common.flipIn(getActivity(), rootView);
                                // Clear some views if poping back in
                                if (!mIsBuildFromExisting) {
                                    resetImgViewToDefault(mIvUserImg);
                                    mEdtUserTip.setText("");
                                }
                            } else {
                                Common.flipOut(getActivity(), rootView);
                            }
                        }
                    }
                };
                if (!geofenceTip) {

                    // GA
                    mTrackerHost.send(getString(R.string.category_notification_tip), getString(R.string.action_request_another_notification), getString(R.string.label_add_time));
                    mTrackerHost.send(getString(R.string.category_notification_tip) + "|" + getString(R.string.action_request_another_notification) + "|" + getString(R.string.label_add_time));

                    HelpTimeFragment helpTimeFragment = HelpTimeFragment.newInstance(onAnimationActionListener);
                    helpTimeFragment.setTimeSetListener(new TimeSetListener() {
                        @Override
                        public void setTime(String time) {
                            mTime = time;
                        }
                    });
                    helpTimeFragment.setArguments(getArguments());
                    mCallbacks.onAddAnimationNavigationAction(helpTimeFragment,
                            Constants.CLEAR_BACKSTACK_ON_BACK, R.animator.card_flip_left_in,
                            R.animator.card_flip_left_out,
                            R.animator.card_flip_right_in,
                            R.animator.card_flip_right_out,
                            true);
                }
                else {

                    // GA
                    mTrackerHost.send(getString(R.string.category_notification_tip), getString(R.string.action_request_another_notification), getString(R.string.label_add_location));
                    mTrackerHost.send(getString(R.string.category_notification_tip) + "|" + getString(R.string.action_request_another_notification) + "|" + getString(R.string.label_add_location));

                    SetLocationFragment setLocationFragment = new SetLocationFragment();
                    setLocationFragment.setOnAnimationActionListener(onAnimationActionListener);
                    setLocationFragment.setLocationEventListener(new SetLocationEventListener() {
                        @Override
                        public void locationSelected(RecurringNotification recurringNotification, boolean buildFromExisting) {
                            mRecurringNotification = recurringNotification;
                            if (buildFromExisting) {
                                mIsBuildFromExisting = true;
                                buildFromExisting();
                            }
                        }
                    });
                    setLocationFragment.setArguments(getArguments());
                    mCallbacks.onAddAnimationNavigationAction(setLocationFragment,
                            Constants.CLEAR_BACKSTACK_ON_BACK, R.animator.card_flip_left_in,
                            R.animator.card_flip_left_out,
                            R.animator.card_flip_right_in,
                            R.animator.card_flip_right_out,
                            true);
                }
            }
            else if (sequenceType.equals(SEQUENCE_TYPE_WHATS_NEW)) {
                Common.mDisableWhatsNewAnim = false;
                mCallbacks.popBackStack();
            }
        } else {
            // GA
            mTrackerHost.send(getString(R.string.category_notification_tip), getString(R.string.action_canceled));
            mTrackerHost.send(getString(R.string.category_notification_tip) + "|" + getString(R.string.action_canceled));
            mHeaderCallbacks.showHeader();
            mHeaderCallbacks.enableDrawers();
            disableAnimationsAndClearBackStack();
        }
    }

    @Override
    public void setImageUri(Uri uri) {
        mPicUri = uri;
    }

    public RecurringNotification getRecurringNotification() {
        return mRecurringNotification;
    }

    public void setRecurringNotification(RecurringNotification mRecurringNotification) {
        this.mRecurringNotification = mRecurringNotification;
    }

    public OnAnimationActionListener getOnAnimationActionListener() {
        return mOnAnimationActionListener;
    }

    public void setOnAnimationActionListener(OnAnimationActionListener mOnAnimationActionListener) {
        this.mOnAnimationActionListener = mOnAnimationActionListener;
    }

    public interface TimeSetListener {
        void setTime(String time);
    }

    public interface SetLocationEventListener {
        void locationSelected(RecurringNotification recurringNotification, boolean buildFromExisting);
    }
}

