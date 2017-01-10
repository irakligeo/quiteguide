package com.mmgct.quitguide2;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mmgct.quitguide2.fragments.HomeFragment;
import com.mmgct.quitguide2.fragments.QuitDateIntroFragment;
import com.mmgct.quitguide2.fragments.ReasonQuittingIntroFragment;
import com.mmgct.quitguide2.fragments.listeners.ExternalImageActivityCallbacks;
import com.mmgct.quitguide2.fragments.listeners.GATrackerHostCallback;
import com.mmgct.quitguide2.fragments.listeners.HeaderCallbacks;
import com.mmgct.quitguide2.fragments.listeners.NavCallbacks;
import com.mmgct.quitguide2.fragments.listeners.PictureFragmentCallbacks;
import com.mmgct.quitguide2.managers.DbManager;
import com.mmgct.quitguide2.utils.Common;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by 35527 on 10/27/2015.
 */
public class IntroActivity extends Activity implements NavCallbacks, ExternalImageActivityCallbacks, HeaderCallbacks, GATrackerHostCallback {

    private View rootView;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = getLayoutInflater().inflate(R.layout.activity_intro, null, false);

        onNavigationAction(new QuitDateIntroFragment(), HomeFragment.TAG, false);

        setContentView(rootView);
    }

    @Override
    public void onNavigationAction(Fragment fragment, String tag, boolean addToStack) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.main_fragment_container, fragment, tag);
        if (addToStack) {
            ft.addToBackStack("");
        }
        ft.commit();
    }

    @Override
    public void onAnimatedNavigationAction(Fragment fragment, String tag, int enterId, int exitId, int popEnterId, int popExitId, boolean addToStack) {
        FragmentTransaction ft = getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.animator.card_flip_right_in, R.animator.card_flip_right_out,
                        R.animator.card_flip_left_in, R.animator.card_flip_left_out)
                .replace(R.id.main_fragment_container, fragment);
                if (addToStack) {
                    ft.addToBackStack(null);
                }
        ft.commit();
    }

    @Override
    public void onNoPopAnimatedNavigationAction(Fragment fragment, String tag, int enterId, int exitId, boolean addToStack) {
        // unimpl
    }

    @Override
    public void onAddAnimationNavigationAction(Fragment fragment, String tag, int enterId, int exitId, int popEnterId, int popExitId, boolean addToStack) {

    }

    @Override
    public void onAddNavigationAction(Fragment fragment, String tag, boolean addToStack) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.main_fragment_container, fragment, tag);
        if (addToStack) {
            ft.addToBackStack("");
        }
        ft.commit();
    }

    @Override
    public void onFragmentFinished(String tag, Bundle bundle) {

    }

    @Override
    public void popBackStack() {
        getFragmentManager().popBackStack();
    }

    @Override
    public void clearBackStack(Fragment loadAfterClear) {
        getFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    /* ---------------------------------- Picture methods ----------------------------------------*/

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)  {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {

            }
        }
    }

    public Intent getPictureIntent(PictureFragmentCallbacks pictureFragment) {
        // Camera exists? Then proceed...
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (Common.shouldAskPermission() && ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE"};
            int permsRequestCode = 200;
            requestPermissions(perms, permsRequestCode);
            return null;
        }

        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            // Create the File where the photo should go.
            // If you don't do this, you may get a crash in some devices.
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast toast = Toast.makeText(this, "ფოტოს შენახვისას პრობლემაა...", Toast.LENGTH_SHORT);
                toast.show();
                return null;
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri fileUri = Uri.fromFile(photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        fileUri);
                pictureFragment.setImageUri(fileUri);
            }
        }
        return takePictureIntent;
    }

    public Intent getGalleryIntent() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        return i;
    }

    protected File createImageFile() throws IOException {

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    // Don't need these for this activity
    @Override
    public void showBack() {

    }

    @Override
    public void showBackBlack() {

    }

    @Override
    public void showMain() {

    }

    @Override
    public void slideHeaderUp() {

    }

    @Override
    public void slideHeaderDown() {

    }

    @Override
    public void hideHeader() {

    }

    @Override
    public void showHeader() {

    }

    @Override
    public void disableDrawers() {

    }

    @Override
    public void enableDrawers() {

    }

    @Override
    public void showSave() {

    }

    @Override
    public void setOnSaveListener(OnSaveListener saveListener) {

    }

    @Override
    public void showBackWithPlus() {

    }

    @Override
    public void hideAllHeaderViews() {

    }

    @Override
    public void showBackNotification() {

    }

    @Override
    public ViewGroup getHeaderLayout(int layoutId) {
        return null;
    }


    @Override
    public Tracker getTracker() {
        if (mTracker == null) {
            mTracker = ((Application)getApplication()).getTracker(Application.TrackerName.APP_TRACKER);
        }
        return mTracker;
    }
    @Override
    public void send(String category, String action) {
        getTracker().send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .build());
    }

    @Override
    public void send(String category, String action, String label) {
        getTracker().send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .build());
    }

    /**
     * Sends bar delimited label.
     * @param label
     */
    @Override
    public void send(String label) {
        getTracker().send(new HitBuilders.EventBuilder()
                .setCategory(getString(R.string.category_reports))
                .setAction(getString(R.string.action_events_triggered))
                .setLabel(label)
                .build());

    }
}
