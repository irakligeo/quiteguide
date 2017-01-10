package com.mmgct.quitguide2.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mmgct.quitguide2.MainActivity;
import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.fragments.dialogs.DecisionDialog;
import com.mmgct.quitguide2.fragments.listeners.DialogDismissListener;
import com.mmgct.quitguide2.fragments.listeners.HeaderCallbacks;
import com.mmgct.quitguide2.managers.DbManager;
import com.mmgct.quitguide2.models.ReasonForQuitting;

/**
 * Created by 35527 on 11/9/2015.
 */
public class ReasonQuittingFragment extends ReasonQuittingIntroFragment implements DialogDismissListener {

    public static final String TAG = ReasonQuittingFragment.class.getSimpleName();
    public static final String REASON_FOR_QUITTING_ID = "reason_for_quitting_id";
    private Bitmap mReasonForQuittingBitmap;
    private ReasonForQuitting mReasonForQuitting;
    private boolean isDeleted;
    private boolean isEditMode;


    public static ReasonQuittingFragment newEditInstance(int reasonForQuittingId) {
        ReasonQuittingFragment frag = new ReasonQuittingFragment();
        Bundle args = new Bundle();
        args.putInt(REASON_FOR_QUITTING_ID, reasonForQuittingId);
        frag.setArguments(args);
        return frag;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_reason_quitting, container, false);
        bindListeners();
        return rootView;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Set image and reason for quitting text if they exist
        int reasonForQuittingId;
        if (getArguments() != null && (reasonForQuittingId = getArguments().getInt(REASON_FOR_QUITTING_ID, -1)) != -1) {
            isEditMode = true;
            // Wire delete button
            ImageButton deleteButton = (ImageButton)rootView.findViewById(R.id.btn_delete_reason_for_quitting);
            deleteButton.setVisibility(View.VISIBLE);
            final Fragment thisFragment = this;
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DecisionDialog diag = DecisionDialog.newInstance(getString(R.string.text_dialog_delete));
                    diag.setTargetFragment(thisFragment, 0);
                    diag.show(getActivity().getFragmentManager(), "");
                }
            });

            mReasonForQuitting = DbManager.getInstance().getReasonById(reasonForQuittingId);

            if (mReasonForQuitting != null) {
                // User image
                if (mReasonForQuitting.getImagePath() != null) {
                    setPicture(mReasonForQuitting.getImagePath());
                }
                // User message
                String msg = mReasonForQuitting.getMessage();
                if (msg != null && !msg.trim().equals("")){
                    ((TextView)rootView.findViewById(R.id.reason_for_quitting_desc)).setText(msg);
                }
            }
        } else {
            // Close button
            ImageButton closeBtn = (ImageButton) rootView.findViewById(R.id.btn_close);
            if (closeBtn != null) {
                closeBtn.setVisibility(View.GONE);
            }
        }
        // Close button
        ImageButton closeBtn = (ImageButton) rootView.findViewById(R.id.btn_close);
        if (closeBtn != null) {
            closeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallbacks.popBackStack();
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isEditMode) {
            mHeaderCallbacks.disableDrawers();
            mHeaderCallbacks.hideHeader();
        }
        // If we are creating a new reason to quit from QuitPlanFragment
        if (mReasonForQuitting == null) {
            mHeaderCallbacks.showBack();
        } else {
            mHeaderCallbacks.showMain();
        }
    }

    @Override
    public void onStop() {
        if (isEditMode) {
            mHeaderCallbacks.enableDrawers();
            mHeaderCallbacks.showHeader();
            try {
                ((HomeFragment) getTargetFragment()).refresh();
            } catch (Exception e) {
                Log.e(TAG, "Error refreshing HomeFragment instance", e);
            }
        }
        String desc = String.valueOf(((EditText)rootView.findViewById(R.id.reason_for_quitting_desc)).getText());
        if (!isDeleted) {
            if (mPictureUri != null || !desc.equals("")
                    || (mReasonForQuitting != null && !mReasonForQuitting.getMessage().equals(desc))) {
                if (mReasonForQuitting == null) {
                    save();
                } else {
                    update(mReasonForQuitting.getId());
                }
            }
        } else {
            mTrackerHost.send(getString(R.string.category_reasons), getString(R.string.action_reasons_to_quit_updated));
            mTrackerHost.send(getString(R.string.category_reasons)+"|"+getString(R.string.action_reasons_to_quit_updated));
        }
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
     /*   // Make sure that header is showing
        if (isEditMode) {
            mHeaderCallbacks.slideHeaderUp();
        }*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReasonForQuittingBitmap != null) {
            mReasonForQuittingBitmap.recycle();
        }
    }


    private void delete() {
        if (mReasonForQuitting != null) {
            isDeleted = true;
            DbManager.getInstance().deleteReasonForQuitting(mReasonForQuitting);
        }
        mCallbacks.popBackStack();
    }

    @Override
    public void onDialogDismissed(Bundle args) {
        if (args.getString(DecisionDialog.DECISION_KEY, "null").equals("yes")){
            delete();
        }
    }
}
