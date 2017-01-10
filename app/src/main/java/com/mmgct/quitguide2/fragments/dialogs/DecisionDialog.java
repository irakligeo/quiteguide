package com.mmgct.quitguide2.fragments.dialogs;

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

/**
 * Created by 35527 on 8/21/2015.
 */

public class DecisionDialog extends DialogFragment {

    private View rootView;
    public static final String DIALOG_TEXT = "dialog_text";
    public static final String DECISION_KEY = "choice";



    public static DecisionDialog newInstance(String dialogText) {
        DecisionDialog d = new DecisionDialog();
        // d.setStyle(DialogFragment.STYLE_NO_FRAME, 0);
        Bundle args = new Bundle();
        args.putString(DIALOG_TEXT, dialogText);
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
        rootView = inflater.inflate(R.layout.dialog_decision, null);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        Bundle args = getArguments();

        if (args.containsKey(DIALOG_TEXT)) {
            ((TextView)rootView.findViewById(R.id.dialog_text)).setText(args.getString(DIALOG_TEXT));
        }

        rootView.findViewById(R.id.yes_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getTargetFragment() != null) {
                    Bundle b = new Bundle();
                    b.putString(DECISION_KEY, "დიახ");
                    try {
                        ((DialogDismissListener) getTargetFragment()).onDialogDismissed(b);
                    } catch (ClassCastException e) {
                        throw new ClassCastException("Error host must implement DialogDismissListener");
                    }
                    dismiss();
                }
            }
        });

        rootView.findViewById(R.id.no_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString(DECISION_KEY, "არა");
                try {
                    ((DialogDismissListener) getTargetFragment()).onDialogDismissed(b);
                } catch (ClassCastException e) {
                    throw new ClassCastException("Error host must implement DialogDismissListener");
                }
                dismiss();
            }
        });

        return rootView;
    }
}