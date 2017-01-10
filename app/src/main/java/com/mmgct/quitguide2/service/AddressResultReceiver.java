package com.mmgct.quitguide2.service;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.mmgct.quitguide2.models.PictureNote;
import com.mmgct.quitguide2.utils.UiUtils;

/**
 * Created by 35527 on 2/5/2016.
 */
@SuppressLint("ParcelCreator") // We don't need to implement parcelable here
public class AddressResultReceiver extends ResultReceiver {

    public final static String TAG = AddressResultReceiver.class.getSimpleName();

    private Handler.Callback mCallback;
    private Context mContext;

    /**
     * Create a new ResultReceive to receive results.  Your
     * {@link #onReceiveResult} method will be called from the thread running
     * <var>handler</var> if given, or from an arbitrary thread if null.
     *
     * @param handler
     */
    public AddressResultReceiver(Handler handler, Handler.Callback callback, Context ctx) {
        super(handler);
        mCallback = callback;
        mContext = ctx;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mCallback != null && resultCode != AddressOpsIntentService.AddressConstants.FAILURE_RESULT) {
            Message msg = new Message();
            if (resultCode == AddressOpsIntentService.AddressConstants.SUCCESS_REVERSE_GEO_CODE) {
                resultData.putString(AddressOpsIntentService.AddressConstants.TYPE_EXTRA, AddressOpsIntentService.AddressConstants.TYPE_REVERSE_GEOCODE);
                msg.setData(resultData);
            } else {
                resultData.putString(AddressOpsIntentService.AddressConstants.TYPE_EXTRA, AddressOpsIntentService.AddressConstants.TYPE_GEOCODE);
                msg.setData(resultData);
            }
            mCallback.handleMessage(msg);
        } else {
            if (mContext != null)
               Log.e(TAG, resultData.getString(AddressOpsIntentService.AddressConstants.RESULT_ERROR_KEY));
        }
    }
}