package com.mmgct.quitguide2.fragments.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.fragments.listeners.DialogDismissListener;

/**
 * Created by 35527 on 11/25/2015.
 */
public class UserInputDialog extends DialogFragment {

    private View rootView;
    public static final String DIALOG_HEADER_KEY = "header_key";
    public static final String DIALOG_DESC_KEY = "desc_key";
    public static final String DIALOG_USER_INPUT_KEY = "ui_key";

    /**
     *
     * @param headerText - The header text for the dialog
     * @param descText - The content text for the dialog
     * @return UserInputDialog
     */
    public static UserInputDialog newInstance(String headerText, String descText) {
        UserInputDialog userInputDialog = new UserInputDialog();
        Bundle args = new Bundle();
        args.putString(DIALOG_HEADER_KEY, headerText);
        args.putString(DIALOG_DESC_KEY, descText);
        userInputDialog.setArguments(args);
        return userInputDialog;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog diag = super.onCreateDialog(savedInstanceState);
        diag.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return diag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.dialog_user_input, container, false);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        Bundle args = getArguments();

        if (args == null) {
            throw new IllegalArgumentException("This dialog must have either header or description text, get an instance with UserInputDialog.newInstance(header, desc)");
        }

        String header = args.getString(DIALOG_HEADER_KEY);
        String desc = args.getString(DIALOG_DESC_KEY);

        TextView tvHeader = (TextView) rootView.findViewById(R.id.input_diag_header);
        TextView tvDesc = (TextView) rootView.findViewById(R.id.input_diag_desc);

        if (header == null) {
            tvHeader.setVisibility(View.GONE);
        } else {
            tvHeader.setText(header);
        }
        if (tvDesc == null) {
            tvDesc.setVisibility(View.GONE);
        } else {
            tvDesc.setText(desc);
        }

        bindListeners();

        // Show keyboard for EditText
        EditText edtNote = (EditText) rootView.findViewById(R.id.input_diag_edit);
        edtNote.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return rootView;
    }

    private void bindListeners() {
        rootView.findViewById(R.id.input_diag_dismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        EditText userInput = (EditText) rootView.findViewById(R.id.input_diag_edit);
        String s = userInput.getText().toString();
        Bundle args = new Bundle();
        if (s != null && !s.equals("")) {
            args.putString(DIALOG_USER_INPUT_KEY, s);
        }
        super.onDismiss(dialog);
        try {
            ((DialogDismissListener)getTargetFragment()).onDialogDismissed(args);
        } catch (ClassCastException e) {
            throw new ClassCastException("Target fragment must implement DialogDismissListener.");
        }
    }
}
