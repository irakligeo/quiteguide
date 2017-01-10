package com.mmgct.quitguide2.fragments.dialogs;

/**
 * Created by 35527 on 10/28/2015.
 */

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.dpizarro.uipicker.library.picker.PickerUI;
import com.dpizarro.uipicker.library.picker.PickerUISettings;
import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.fragments.listeners.DialogDismissListener;
import com.mmgct.quitguide2.managers.DbManager;
import com.mmgct.quitguide2.models.Content;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by 35527 on 9/23/2015.
 */
public class TriggerPickerDialog extends DialogFragment {

    private View rootView;
    private String mSelection;

    private static final String TAG = "TriggerPickerDialog";
    public static final String ARG_KEY = "selection";
    public static final String LOCATION_STRING = "This Location";
    public static final String TOD_STRING = "This Time of Day";


    public TriggerPickerDialog() {
    }


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // This is actually returning the dialog that this class wraps
        // Set animation to it here
        Dialog diag = new Dialog(getActivity(), R.style.DialogSlideAnim);
        return diag;
    }

    @Override
    public void onStart() {
        super.onStart();

        // safety check
        if (getDialog() == null) {
            return;
        }

        // All of these need to be run in onStart() for proper functionality

        Dialog diag = getDialog();

        DisplayMetrics dm = getResources().getDisplayMetrics();

        int dialogWidth = dm.widthPixels;
        int dialogHeight = dm.heightPixels / 2;

        Window window = diag.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();

        // Set width to full scree height to half screen and gravity to bottom
        wlp.height = dialogHeight;
        wlp.width = dialogWidth;
        wlp.gravity = Gravity.BOTTOM;

        window.setAttributes(wlp);

        // Can't dismiss by clicking out of dialog
        diag.setCanceledOnTouchOutside(false);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Bundle args = new Bundle();
        args.putString(ARG_KEY, mSelection);
        try {
            ((DialogDismissListener) getTargetFragment()).onDialogDismissed(args);
        } catch (ClassCastException e) {
            throw new ClassCastException("Fragment must implement DialogDismissListener");
        }
        }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        // Dialog fills screen and has no title
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));

        rootView = inflater.inflate(R.layout.dialog_slide_in, container, false);
        // Add picker UI
        PickerUI picker = new PickerUI(getActivity());

        List<Content> content = DbManager.getInstance().getAllTriggers();
        List<String> contentDesc = new ArrayList<String>();
        for (Content c : content) {
            contentDesc.add(c.getContentDescription());
        }

        // We want location and time selections to be at the top of the list,
        // we don't care about order on the rest of the choices
        if (contentDesc != null && contentDesc.size() > 1) {
            int index;
            // loc
            index = contentDesc.indexOf(LOCATION_STRING);
            String temp;
            if (index >= 0) {
                temp = contentDesc.get(0);
                contentDesc.set(0, contentDesc.get(index));
                contentDesc.set(index, temp);
            }
            // tod
            index = contentDesc.indexOf(TOD_STRING);
            if (index > 0) {
                temp = contentDesc.get(1);
                contentDesc.set(1, contentDesc.get(index));
                contentDesc.set(index, temp);
            }
        }

        PickerUISettings pickerUISettings = new PickerUISettings.Builder()
                .withItems(contentDesc)
                .withAutoDismiss(false)
                .withItemsClickables(false)
                .withUseBlur(false)
                .build();

        picker.setSettings(pickerUISettings);

        picker.setColorTextCenter(R.color.black);
        picker.setColorTextNoCenter(R.color.gray);
        picker.setBackgroundColorPanel(R.color.white);
        picker.setLinesColor(R.color.gray);

        picker.setOnClickItemPickerUIListener(
                new PickerUI.PickerUIItemClickListener() {
                    @Override
                    public void onItemClickPickerUI(int which, int position, String valueResult) {
                        mSelection = valueResult;
                    }
                });

        ((ViewGroup)rootView.findViewById(R.id.main_container)).addView(picker);

        picker.slide();

        rootView.findViewById(R.id.btn_back_slide_in).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return rootView;
    }
}
