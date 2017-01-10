package com.mmgct.quitguide2.fragments.listeners;

import android.os.Bundle;

/**
 * Created by 35527 on 11/9/2015.
 */
public interface DialogDismissListener {

    /**
     * To be used by dialogs returning data to the hosting activity or fragment
     * @param args - arguments passed from dialog
     */
    void onDialogDismissed(Bundle args);
}
