package com.mmgct.quitguide2.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.fragments.listeners.ExternalImageActivityCallbacks;
import com.mmgct.quitguide2.fragments.listeners.PictureFragmentCallbacks;
import com.mmgct.quitguide2.managers.DbManager;
import com.mmgct.quitguide2.models.ReasonForQuitting;
import com.mmgct.quitguide2.utils.ImgUtils;

import java.io.File;

/**
 * Created by 35527 on 10/28/2015.
 */
public abstract class ReasonQuittingBaseFragment extends HeaderBaseFragment implements PictureFragmentCallbacks {

    private static final String TAG = "ReasonQuittingBase";
    protected Uri mPictureUri;
    protected ExternalImageActivityCallbacks mPicCallbacks;

    /**
     * Subclasses should implement this method to set an image to the ImageView.
     */
    protected abstract void setPicture(String picturePath);

    /**
     * Subclasses should implement this method to save user data
     */
    protected abstract void save();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mPicCallbacks = (ExternalImageActivityCallbacks) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement ExternalImageActivityCallbacks");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mPicCallbacks = null;
    }

    protected void promptUserForImage() {

        final ReasonQuittingBaseFragment thisFragment = this;

        // Tell the host activity to swap out this frag for the camera frag
        AlertDialog dialog = new AlertDialog.Builder(getView().getContext())
                .setPositiveButton("გადაიღე სურათი", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        if(mPicCallbacks.getPictureIntent(thisFragment) != null) {
                            startActivityForResult(mPicCallbacks.getPictureIntent(thisFragment), ExternalImageActivityCallbacks.REQUEST_CODE_TAKE_PICTURE);
                        }
                    }
                })
                .setNegativeButton("ამოირჩიე", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        startActivityForResult(mPicCallbacks.getGalleryIntent(), ExternalImageActivityCallbacks.REQUEST_CODE_GALLERY);
                    }
                }).setIcon(null).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // If image capture was successful set it to ImageView
        if (requestCode == ExternalImageActivityCallbacks.REQUEST_CODE_TAKE_PICTURE
                || requestCode == ExternalImageActivityCallbacks.REQUEST_CODE_GALLERY) {
            if (requestCode == ExternalImageActivityCallbacks.REQUEST_CODE_GALLERY && data != null) {
                mPictureUri = data.getData();
            }
            if (resultCode == Activity.RESULT_OK && mPictureUri != null) {
                setPicture(mPictureUri.toString());
            } else if (resultCode == Activity.RESULT_CANCELED && mPictureUri != null) {
                File file = new File(mPictureUri.getPath());
                if (file.exists()) {
                    file.delete();
                }
            }
        }
    }

    @Override
    public void setImageUri(Uri uri) {
        mPictureUri = uri;
    }
}