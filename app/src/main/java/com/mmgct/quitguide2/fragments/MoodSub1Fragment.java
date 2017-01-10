package com.mmgct.quitguide2.fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.fragments.dialogs.UserInputDialog;
import com.mmgct.quitguide2.fragments.listeners.DialogDismissListener;
import com.mmgct.quitguide2.fragments.listeners.ExternalImageActivityCallbacks;
import com.mmgct.quitguide2.fragments.listeners.PictureFragmentCallbacks;
import com.mmgct.quitguide2.managers.DbManager;
import com.mmgct.quitguide2.models.Note;
import com.mmgct.quitguide2.models.PictureNote;
import com.mmgct.quitguide2.utils.Common;
import com.mmgct.quitguide2.utils.Constants;
import com.mmgct.quitguide2.utils.ImgUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by 35527 on 11/25/2015.
 */
public class MoodSub1Fragment extends BaseFlipInFragment implements DialogDismissListener, PictureFragmentCallbacks {

    private static final String TAG = MoodSub1Fragment.class.getSimpleName();
    private View rootView;
    private ExternalImageActivityCallbacks mPicCallbacks;
    private Uri mPicUri;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mPicCallbacks = (ExternalImageActivityCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement PictureFragmentCallbacks");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_mood_sub_1, container, false);
        bindListeners();
        return rootView;
    }

    private void bindListeners() {
        final Fragment target = this;

        rootView.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTrackerHost.send(getString(R.string.category_mood), getString(R.string.action_positive_mood), getString(R.string.label_closed));
                mTrackerHost.send(getString(R.string.category_mood)+"|"+getString(R.string.action_positive_mood)+"|"+getString(R.string.label_closed));
                close();
            }
        });

        rootView.findViewById(R.id.btn_mood1_write_note).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment diag = UserInputDialog.newInstance(getResources().getString(R.string.header_write_note),
                        getResources().getString(R.string.desc_write_note));
                diag.setTargetFragment(target, 0);
                diag.show(getActivity().getFragmentManager(), "");
            }
        });

        ImageButton journal = (ImageButton) rootView.findViewById(R.id.btn_mood1_journal);
        journal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                JournalFragment jf = new JournalFragment();
                jf.setAnimationType(Constants.ANIM_FLIP_LEFT_IN);
                jf.setTargetFragment(target, 0);
                Bundle args = new Bundle();
                args.putBoolean(JournalFragment.SHOW_CLOSE_BTN, true);
                jf.setArguments(args);
                flipOut();
                mTrackerHost.send(getString(R.string.category_mood), getString(R.string.action_positive_mood), getString(R.string.label_journal));
                mTrackerHost.send(getString(R.string.category_mood)+"|"+getString(R.string.action_positive_mood)+"|"+getString(R.string.label_journal));
                mCallbacks.onAddNavigationAction(jf, "", true);
            }
        });


        final PictureFragmentCallbacks callbacks = this;

        rootView.findViewById(R.id.btn_mood1_take_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public synchronized void onClick(View v) {
                Common.shortDisable(v, 250);
                mTrackerHost.send(getString(R.string.category_mood), getString(R.string.action_positive_mood), getString(R.string.label_take_photo));
                mTrackerHost.send(getString(R.string.category_mood) + "|" + getString(R.string.action_positive_mood) + "|" + getString(R.string.label_take_photo));
                if (mPicCallbacks.getPictureIntent(callbacks) != null) {
                    startActivityForResult(mPicCallbacks.getPictureIntent(callbacks), ExternalImageActivityCallbacks.REQUEST_CODE_TAKE_PICTURE);
                }
            }
        });
    }

    private void close() {
        mPlayBackAnimation = false;
        mCallbacks.popBackStack();
        mCallbacks.popBackStack();
    }

    @Override
    public void onDialogDismissed(Bundle args) {
        if (args.containsKey(UserInputDialog.DIALOG_USER_INPUT_KEY)) {
            String s = args.getString(UserInputDialog.DIALOG_USER_INPUT_KEY);
            DbManager.getInstance().createNote(new Note(System.currentTimeMillis(), s));
            mTrackerHost.send(getString(R.string.category_mood), getString(R.string.action_positive_mood), getString(R.string.label_write_note));
            mTrackerHost.send(getString(R.string.category_mood)+"|"+getString(R.string.action_positive_mood)+"|"+getString(R.string.label_write_note));
        }
        close();
    }

    @Override
    public void setImageUri(Uri uri) {
        mPicUri = uri;
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        if (requestCode == ExternalImageActivityCallbacks.REQUEST_CODE_TAKE_PICTURE) {
                            PictureNote pic = new PictureNote();
                            pic.setPicturePath(mPicUri.getPath());
                            DbManager.getInstance().createPictureNote(pic);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error retrieving image bitmap.", e);
                        return;
                    }
                }
            }
        }).start();
    }

    public void setGrayedOut(boolean grayedOut) {
        if (rootView == null)
            return;
        Paint grayscalePaint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        grayscalePaint.setColorFilter(new ColorMatrixColorFilter(cm));
        if (grayedOut) {
            rootView.setLayerType(View.LAYER_TYPE_HARDWARE, grayscalePaint);
        } else {
            rootView.setLayerType(View.LAYER_TYPE_NONE, null);
        }
    }
}
