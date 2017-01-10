package com.mmgct.quitguide2.fragments.dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.fragments.listeners.DialogDismissListener;

/**
 * This dialog conforms to the look and feel of an iOS dialog
 * UI components comprise header, content, and two selection buttons
 * Created by 35527 on 1/4/2016.
 */
public class iOSStyledDecisionDialog extends DialogFragment implements View.OnClickListener {

    public static final String HEADER_KEY = "header";
    public static final String CONTENT_KEY = "content";
    public static final String BUTTON_LEFT_KEY = "left_button";
    public static final String BUTTON_RIGHT_KEY = "right_button";
    public static final String BUTTON_LEFT_CALLBACK_KEY = "left_callback";
    public static final String BUTTON_RIGHT_CALLBACK_KEY = "right_callback";

    private View rootView;
    private DialogDismissListener mOnDismissListener;

    /**
     * Use this method to get a new instance of the dialog
     * @param header
     * @param content
     * @param leftButtonLabel
     * @param rightButtonLabel
     * @return
     */
    public static iOSStyledDecisionDialog newInstance(String header, String content, String leftButtonLabel, String rightButtonLabel) {
        iOSStyledDecisionDialog dialog = new iOSStyledDecisionDialog();
        Bundle args = new Bundle();
        // dialog.setStyle(DialogFragment.STYLE_NO_FRAME, 0);
        args.putString(HEADER_KEY, header);
        args.putString(CONTENT_KEY, content);
        args.putString(BUTTON_LEFT_KEY, leftButtonLabel);
        args.putString(BUTTON_RIGHT_KEY, rightButtonLabel);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog diag = new Dialog(getActivity());
        diag.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return diag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_decision_ios, null, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setupUI();
        return rootView;
    }

    private void setupUI() {
        Bundle args = getArguments();
        if (args == null) {
            return;
        }
        ((TextView)rootView.findViewById(R.id.header_dialog_decision)).setText(args.getString(HEADER_KEY, ""));
        ((TextView)rootView.findViewById(R.id.content_dialog_decision)).setText(args.getString(CONTENT_KEY, ""));
        ((TextView)rootView.findViewById(R.id.btn_right_dialog_decision)).setText(args.getString(BUTTON_RIGHT_KEY, ""));
        ((TextView)rootView.findViewById(R.id.btn_left_dialog_decision)).setText(args.getString(BUTTON_LEFT_KEY, ""));

        rootView.findViewById(R.id.btn_left_dialog_decision).setOnClickListener(this);
        rootView.findViewById(R.id.btn_right_dialog_decision).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Bundle args = new Bundle();
        switch (v.getId()) {
            case(R.id.btn_left_dialog_decision):
                args.putBoolean(BUTTON_LEFT_CALLBACK_KEY, true);
                if (mOnDismissListener != null) {
                    mOnDismissListener.onDialogDismissed(args);
                }
                else {
                    try {
                        ((DialogDismissListener) getTargetFragment()).onDialogDismissed(args);
                    } catch (ClassCastException e) {
                        throw new ClassCastException("Host fragment must implement DialogDismissListener");
                    }
                }
                    break;
            case(R.id.btn_right_dialog_decision):
                args.putBoolean(BUTTON_RIGHT_CALLBACK_KEY, true);
                if (mOnDismissListener != null) {
                    mOnDismissListener.onDialogDismissed(args);
                }
                else {
                    try {
                        ((DialogDismissListener) getTargetFragment()).onDialogDismissed(args);
                    } catch (ClassCastException e) {
                        throw new ClassCastException("Host fragment must implement DialogDismissListener");
                    }
                }
                break;
        }
        dismiss();
    }

    public DialogDismissListener getOnDismissListener() {
        return mOnDismissListener;
    }

    public void setOnDismissListener(DialogDismissListener mOnDismissListener) {
        this.mOnDismissListener = mOnDismissListener;
    }
}
