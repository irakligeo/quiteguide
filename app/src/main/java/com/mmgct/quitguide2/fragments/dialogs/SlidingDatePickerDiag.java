package com.mmgct.quitguide2.fragments.dialogs;

/**
 * Created by 35527 on 10/28/2015.
 */

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mmgct.quitguide2.R;

import org.joda.time.DateTime;

import java.util.Locale;


/**
 * Created by 35527 on 9/23/2015.
 */
public class SlidingDatePickerDiag extends DialogFragment {

    private DatePickerDiagCallback mCallbacks;
    private View rootView;
    private DatePicker mDatePicker;
    private long mDate;

    private static final String TAG = "AddCardDiag";


    public SlidingDatePickerDiag() {
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
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        // Dialog fills screen and has no title
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));

        rootView = inflater.inflate(R.layout.dialog_slide_in, container, false);
        setButtons();
        addDatePicker();

        return rootView;
    }

    /**
     * Add DatePicker programmatically otherwise it will use the old button style. Very frustrating...
     */

    private void addDatePicker() {
        Locale locale = new Locale("ka");
        Locale.setDefault(locale);

        mDatePicker = (DatePicker) getActivity().getLayoutInflater().inflate(R.layout.view_datepicker, null, false);
        mDatePicker.setCalendarViewShown(false);
        mDatePicker.setSpinnersShown(true);

        DateTime now = new DateTime(System.currentTimeMillis()).withTimeAtStartOfDay();
        mDatePicker.setMaxDate(now.plusDays(14).getMillis());
        mDatePicker.init(now.getYear(), now.getMonthOfYear() - 1, now.getDayOfMonth(), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int day) {
                mDate = new DateTime().withDate(year, month + 1, day).getMillis();
            }
        });

        // Center parent
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rlp.addRule(RelativeLayout.CENTER_IN_PARENT);
        mDatePicker.setLayoutParams(rlp);
        ((ViewGroup) rootView.findViewById(R.id.main_container)).addView(mDatePicker);
    }

    private void setButtons() {
        // For buttons
        TextView done = (TextView) rootView.findViewById(R.id.btn_back_slide_in);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDate == 0) {
                    mDate = new DateTime(System.currentTimeMillis()).withTimeAtStartOfDay().getMillis();
                }
                mCallbacks.onDateSaved(mDate);
                dismiss();
            }
        });
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (DatePickerDiagCallback) getTargetFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException("Fragment must implement DatePickerDiagCallback!");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public interface DatePickerDiagCallback {
        void onDateSaved(long date);
    }

}
