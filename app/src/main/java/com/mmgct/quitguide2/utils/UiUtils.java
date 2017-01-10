package com.mmgct.quitguide2.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.mmgct.quitguide2.R;

/**
 * Created by 35527 on 2/5/2016.
 */
public class UiUtils {

    private static final String TAG = UiUtils.class.getSimpleName();
    private static ProgressDialog mProgressDialog;

    public static void showProgressDialog(Activity activity) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            return;
        }
        try {
            mProgressDialog = new ProgressDialog(activity, R.style.CustomAlertDialogTheme);
            // mProgressDialog.setCancelable(false);
            mProgressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
            if (activity != null && !activity.isFinishing()) {
                mProgressDialog.show();
            }
        } catch (WindowManager.BadTokenException e) { // Sometimes gets thrown even though Activity#isFinishing is checked...
            Log.e(TAG, "Cannot show progress dialog");
        }
    }

    public static void dismissProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public static void showToast(Context context, String string) {
        if (string == null || context == null) {
            return;
        } else {
            Toast.makeText(context, string, Toast.LENGTH_LONG).show();
        }
    }
}
