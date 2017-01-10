package com.mmgct.quitguide2.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.managers.DbManager;
import com.mmgct.quitguide2.models.ReasonForQuitting;
import com.mmgct.quitguide2.utils.ImgUtils;

/**
 * Created by 35527 on 10/28/2015.
 */
public class ReasonQuittingIntroFragment extends ReasonQuittingBaseFragment {

    private static final String TAG = "ReasonQuittingIntro";
    protected View rootView;
    private String mReasonDesc;



    public String getReasonDesc() {
        return mReasonDesc;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_intro_reason_quitting, null, false);
        bindListeners();
        return rootView;
    }

    /**
     * Sets click listeners on View objects.
     */
    protected void bindListeners() {
        ImageButton upload = (ImageButton) rootView.findViewById(R.id.btn_upload_photo);
        ImageButton continueBtn = (ImageButton) rootView.findViewById(R.id.btn_continue);
        // Configure edit text
        final EditText userReason = (EditText) rootView.findViewById(R.id.reason_for_quitting_desc);


        userReason.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            /**
             * This listener is designed to close the soft keyboard when enter is typed.
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (userReason != null) {
                    if (s.length() > 1 && s.charAt(s.length() - 1) == '\n') {
                        userReason.setText(s.subSequence(0, s.length() - 1));
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(userReason.getWindowToken(), 0);
                    } else if (s.length() > 0 && s.charAt(0) == '\n') {
                        userReason.setText("");
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(userReason.getWindowToken(), 0);
                    } else
                        ; // normal behavior
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        if (upload != null) {
            upload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    promptUserForImage();
                }
            });
        }
        if (continueBtn != null) {
            continueBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    save();
                    mCallbacks.onAnimatedNavigationAction(new SmokeDataIntroFragment(), "", R.animator.card_flip_left_in, R.animator.card_flip_left_out, R.animator.card_flip_right_in, R.animator.card_flip_right_out, true);
                }
            });

        }
    }

    @Override
    protected void setPicture(String pictureUri) {
        final ImageView pic = (ImageView) rootView.findViewById(R.id.img_reason_quitting);
        if (pic != null && pictureUri != null) {
            Glide.with(getActivity()).load(Uri.parse(pictureUri)).centerCrop().animate(android.R.anim.fade_in).listener(new RequestListener<Uri, GlideDrawable>() {
                @Override
                public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
                    Log.e(TAG, "Error loading picture.", e);
                    return false;
                }

                @Override
                public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                    if (pic != null) {
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int)getResources().getDimension(R.dimen.view_height_small), (int)getResources().getDimension(R.dimen.view_width_small));
                        lp.gravity = Gravity.CENTER_HORIZONTAL;
                        lp.setMargins(0, 0, 0, (int)getResources().getDimension(R.dimen.spacing_normal));
                        pic.setLayoutParams(lp);
                        if (Build.VERSION.SDK_INT >= 21) {
                            pic.setBackground(getResources().getDrawable(R.drawable.white_frame, getActivity().getTheme()));
                        } else {
                            pic.setBackground(getResources().getDrawable(R.drawable.white_frame));
                        }
                    }
                    return false;
                }
            }).into(pic);
        }
    }


    @Override
    protected void save() {
        mReasonDesc = String.valueOf(((EditText) rootView.findViewById(R.id.reason_for_quitting_desc)).getText());
        Tracker tracker = mTrackerHost.getTracker();
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory(getString(R.string.category_reasons))
                .setAction(getString(R.string.action_reasons_to_quit_updated))
                .build());
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory(getString(R.string.category_reports))
                .setAction(getString(R.string.action_events_triggered))
                .setLabel(getString(R.string.category_reasons) + "|" + getString(R.string.action_reasons_to_quit_updated))
                        .build());
        new Thread(new Runnable() {
            @Override
            public void run() {
                ReasonForQuitting reasonForQuitting = new ReasonForQuitting();
                if (mPictureUri != null)
                    reasonForQuitting.setImagePath(mPictureUri.toString());
                if (mReasonDesc != null)
                    reasonForQuitting.setMessage(mReasonDesc);
                DbManager.getInstance().createReasonForQuitting(reasonForQuitting);
            }
        }).start();
    }

    protected void update(final int reasonForQuittingId) {
        mReasonDesc = String.valueOf(((EditText) rootView.findViewById(R.id.reason_for_quitting_desc)).getText());
        Tracker tracker = mTrackerHost.getTracker();
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory(getString(R.string.category_reasons))
                .setAction(getString(R.string.action_reasons_to_quit_updated))
                .build());
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory(getString(R.string.category_reports))
                .setAction(getString(R.string.action_events_triggered))
                .setLabel(getString(R.string.category_reasons) + "|" + getString(R.string.action_reasons_to_quit_updated))
                .build());
        new Thread(new Runnable() {
            @Override
            public void run() {
                ReasonForQuitting reasonForQuitting = DbManager.getInstance().getReasonById(reasonForQuittingId);
                if (mPictureUri != null)
                    reasonForQuitting.setImagePath(mPictureUri.toString());
                if (mReasonDesc != null)
                    reasonForQuitting.setMessage(mReasonDesc);
                DbManager.getInstance().updateReasonForQuitting(reasonForQuitting);
            }
        }).start();
    }


}
