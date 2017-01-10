package com.mmgct.quitguide2.fragments.dialogs;

/**
 * Created by 35527 on 11/3/2015.
 */

import android.app.Dialog;
import android.app.DialogFragment;
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
import com.mmgct.quitguide2.utils.Constants;


/**
 * Created by 35527 on 8/21/2015.
 */

public class OkDialog extends DialogFragment {

    private View rootView;
    private DialogDismissListener mDialogDismissListener;

    public static final String DIALOG_TEXT = "dialog_text";
    public static final String DISMISS_TEXT_KEY = "dismiss_text";


    public static OkDialog newInstance(String dialogText) {
        OkDialog d = new OkDialog();
        // d.setStyle(DialogFragment.STYLE_NO_FRAME, 0);
        Bundle args = new Bundle();
        args.putString(DIALOG_TEXT, dialogText);
        d.setArguments(args);
        return d;
    }

    public static OkDialog newInstance(String dialogText, String dismissText) {
        OkDialog d = new OkDialog();
        // d.setStyle(DialogFragment.STYLE_NO_FRAME, 0);
        Bundle args = new Bundle();
        args.putString(DIALOG_TEXT, dialogText);
        args.putString(DISMISS_TEXT_KEY, dismissText);
        d.setArguments(args);
        return d;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog diag = super.onCreateDialog(savedInstanceState);
        diag.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return diag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_user_input_error, null);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        Bundle args = getArguments();

        if (args.containsKey(DIALOG_TEXT)) {
            ((TextView)rootView.findViewById(R.id.dialog_text)).setText(args.getString(DIALOG_TEXT));
        }

        if (args.containsKey(DISMISS_TEXT_KEY)) {
            ((TextView)rootView.findViewById(R.id.btn_dismiss)).setText(args.getString(DISMISS_TEXT_KEY, "OK"));
        }

        rootView.findViewById(R.id.btn_dismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDialogDismissListener != null) {
                    Bundle args = new Bundle();
                    args.putBoolean(Constants.OK_BUTTON_PRESSED_KEY, true);
                    mDialogDismissListener.onDialogDismissed(args);
                }
                dismiss();
            }
        });

        return rootView;
    }

    public DialogDismissListener getDialogDismissListener() {
        return mDialogDismissListener;
    }

    public void setDialogDismissListener(DialogDismissListener mDialogDismissListener) {
        this.mDialogDismissListener = mDialogDismissListener;
    }
}