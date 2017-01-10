package com.mmgct.quitguide2.fragments.listeners;

import android.content.Intent;

/**
 * Created by 35527 on 10/30/2015.
 */
public interface ExternalImageActivityCallbacks {
    public static final int REQUEST_CODE_TAKE_PICTURE = 0;
    public static final int REQUEST_CODE_GALLERY = 1;

    public Intent getPictureIntent(PictureFragmentCallbacks pictureFragment);
    public Intent getGalleryIntent();
}
