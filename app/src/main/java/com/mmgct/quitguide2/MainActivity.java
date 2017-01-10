package com.mmgct.quitguide2;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.emilsjolander.components.StickyScrollViewItems.StickyScrollView;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.location.LocationServices;
import com.mmgct.quitguide2.fragments.HistoryFragment;
import com.mmgct.quitguide2.fragments.HomeFragment;
import com.mmgct.quitguide2.fragments.JournalFragment;
import com.mmgct.quitguide2.fragments.ManageNotificationsFragment;
import com.mmgct.quitguide2.fragments.SetLocationFragment;
import com.mmgct.quitguide2.fragments.SettingsFragment;
import com.mmgct.quitguide2.fragments.TimeNotificationListFragment;
import com.mmgct.quitguide2.fragments.TipFromNoticeFragment;
import com.mmgct.quitguide2.fragments.WebviewFragment;
import com.mmgct.quitguide2.fragments.WhatsNewFragment;
import com.mmgct.quitguide2.fragments.listeners.DrawerCallbacks;
import com.mmgct.quitguide2.fragments.listeners.ExternalImageActivityCallbacks;
import com.mmgct.quitguide2.fragments.listeners.GATrackerHostCallback;
import com.mmgct.quitguide2.fragments.listeners.HeaderCallbacks;
import com.mmgct.quitguide2.fragments.listeners.NavCallbacks;
import com.mmgct.quitguide2.fragments.listeners.PictureFragmentCallbacks;
import com.mmgct.quitguide2.fragments.QuitPlanFragment;
import com.mmgct.quitguide2.managers.DbManager;
import com.mmgct.quitguide2.models.Profile;
import com.mmgct.quitguide2.models.State;
import com.mmgct.quitguide2.utils.Common;
import com.mmgct.quitguide2.utils.Constants;
import com.mmgct.quitguide2.utils.UiUtils;
import com.mmgct.quitguide2.views.adapters.NavMenuAdapter;
import com.mmgct.quitguide2.views.adapters.NavMenuItem;
import com.mmgct.quitguide2.views.adapters.TrophyGridAdapter;
import com.mmgct.quitguide2.views.notifications.OpenToScreenNavigation;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends GoogleApiActivity implements NavCallbacks, ExternalImageActivityCallbacks, DrawerCallbacks, HeaderCallbacks, OpenToScreenNavigation, GATrackerHostCallback{

    private static final String TAG = "MainActivity";
    public static int REQUEST_CHECK_SETTINGS = 100;
    private View rootView;
    private DrawerLayout mDrawerLayout;
    private ListView mLeftDrawer;
    private FrameLayout mRightDrawer;
    private List<NavMenuItem> mMenuItems;
    private StickyScrollView mStickyScrollView;
    private GridView mTrophyGrid;
    private OnSaveListener mOnSaveListener;
    private Tracker mTracker;
    private SetLocationFragment mLocationFragmentCallback;
    private AlternativeNav mAlternativeNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = getLayoutInflater().inflate(R.layout.activity_main, null, false);
        State state = DbManager.getInstance().getState();
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey(Constants.NOTIFICATION_OPEN_TO_SCREEN_KEY)) {
            openToScreen(getIntent().getExtras().getString(Constants.NOTIFICATION_OPEN_TO_SCREEN_KEY));
        } else if (state.isShowWhatsNewScreen() || BuildConfig.DEBUG) {
       // } else if (BuildConfig.DEBUG) {
            rootView.findViewById(R.id.header).setVisibility(View.GONE);
            onAnimatedNavigationAction(new WhatsNewFragment(), WhatsNewFragment.TAG, R.animator.oa_slide_up, R.animator.oa_slide_down, R.animator.card_flip_left_in, R.animator.oa_slide_down, false);
        } else {
            onNavigationAction(new HomeFragment(), HomeFragment.TAG, false);
        }
        // Build GoogleApiClient
        List<Api<?>> apis = new ArrayList<>();
        apis.add(LocationServices.API);
        super.buildGoogleApiClient(apis);

        setContentView(rootView);
    }


    @Override
    protected void onStart() {
        super.onStart();
        buildDrawers();
        bindListeners();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent entered!");
        super.onNewIntent(intent);
        if (intent.getExtras() != null && intent.getExtras().containsKey(Constants.NOTIFICATION_OPEN_TO_SCREEN_KEY)) {
            startActivity(intent);
        }

    }

    /**
     * Configures the two drawer pullouts
     */
    private void buildDrawers() {
        if (mDrawerLayout == null || mLeftDrawer == null || mRightDrawer == null) {
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            mLeftDrawer = (ListView) findViewById(R.id.left_drawer);
            mRightDrawer = (FrameLayout) findViewById(R.id.right_drawer);
            // Left drawer
            if (mMenuItems == null) {
                mMenuItems = createMenuList();
            }

            mLeftDrawer.setAdapter(new NavMenuAdapter(this, 0, mMenuItems));
            mLeftDrawer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String itemId = (String) view.getTag(R.id.menu_item_id);
                    if (itemId.equals(getResources().getString(R.string.menu_drawer_lbl_home))) {
                        send(getResources().getString(R.string.category_menu), getResources().getString(R.string.action_home_selected));
                        send(getResources().getString(R.string.category_menu) + "|" + getResources().getString(R.string.action_home_selected));
                        onNavigationAction(new HomeFragment(), "", false);
                    } else if (itemId.equals(getResources().getString(R.string.menu_drawer_lbl_quitplan))) {
                        send(getResources().getString(R.string.category_menu), getResources().getString(R.string.action_quit_plan));
                        send(getResources().getString(R.string.category_menu) + "|" + getResources().getString(R.string.action_quit_plan));
                        onNavigationAction(new QuitPlanFragment(), "", false);
                    } else if (itemId.equals(getResources().getString(R.string.menu_drawer_lbl_quitguide))) {
                        Bundle args = new Bundle();
                        args.putString(WebviewFragment.URL_KEY, "quitguide.html");
                        WebviewFragment webViewFragment = new WebviewFragment();
                        webViewFragment.setArguments(args);
                        send(getResources().getString(R.string.category_menu), getResources().getString(R.string.action_how_to_quit));
                        send((getResources().getString(R.string.category_menu) + "|" + getResources().getString(R.string.action_how_to_quit)));
                        onNavigationAction(webViewFragment, "", false);
                    } else if (itemId.equals(getResources().getString(R.string.menu_drawer_lbl_myjournals))) {
                        send(getResources().getString(R.string.category_menu), getResources().getString(R.string.action_journal));
                        send(getResources().getString(R.string.category_menu) + "|" + getResources().getString(R.string.action_journal));
                        onNavigationAction(new JournalFragment(), "", false);
                    } else if (itemId.equals(getResources().getString(R.string.menu_drawer_lbl_settings))) {
                        send(getResources().getString(R.string.category_menu), getResources().getString(R.string.action_settings));
                        send(getResources().getString(R.string.category_menu) + "|" + getResources().getString(R.string.action_settings));
                        onNavigationAction(new SettingsFragment(), "", false);
                    } else if (itemId.equals(getResources().getString(R.string.menu_drawer_lbl_myhistory))) {
                        send(getResources().getString(R.string.category_menu), getResources().getString(R.string.action_history));
                        send(getResources().getString(R.string.category_menu) + "|" + getResources().getString(R.string.action_history));
                        onNavigationAction(new HistoryFragment(), "", false);
                    } else if (itemId.equals(getString(R.string.menu_drawer_lbl_location))) {
                        // TODO ask about adding analytics here?
                        /*Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                        mainActivity.setFlags(mainActivity.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                        mainActivity.putExtra(ManageNotificationsFragment.SEQUENCE_EXTRA_KEY, true);
                        startActivity(mainActivity);*/
                        Fragment frag = new ManageNotificationsFragment();
                        Bundle args = new Bundle();
                        args.putString(ManageNotificationsFragment.SEQUENCE_EXTRA_KEY, ManageNotificationsFragment.SEQUENCE_FROM_DRAWER);
                        frag.setArguments(args);
                        onNavigationAction(frag, "", false);
                    } else if (itemId.equals(getString(R.string.menu_drawer_lbl_times_of_day))){
                        // TODO ask about adding analytics here?
                        onNavigationAction(new TimeNotificationListFragment(), "", false);
                    }
                    mDrawerLayout.closeDrawer(mLeftDrawer);
                }
            });
            // Right drawer uses StickyScrollView lib. https://github.com/emilsjolander/StickyScrollViewItems/tree/master/sample/src/com/emilsjolander/components/StickyScrollViewItems/samples
            mStickyScrollView = new StickyScrollView(this);
            // ScrollView inner container
            LinearLayout scrollViewContainer = new LinearLayout(this);
            scrollViewContainer.setOrientation(LinearLayout.VERTICAL);
            // Get header
            ImageView stickyHeader = (ImageView) getLayoutInflater().inflate(R.layout.view_header_sticky_right_drawer, null, false);
            stickyHeader.setTag("sticky");
            scrollViewContainer.addView(stickyHeader);
            // Get main drawer layout
            LinearLayout stickyContent = (LinearLayout) getLayoutInflater().inflate(R.layout.drawer_right_main, null, false);
            updateRightDrawerContent(stickyContent);
            // Add content to container
            scrollViewContainer.addView(stickyContent);
            // Set GridView adapter
            mTrophyGrid = (GridView) scrollViewContainer.findViewById(R.id.right_drawer_gridview);
            // This grid view should be not be scrollable
            mTrophyGrid.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_MOVE) {
                        return true;
                    }
                    return false;
                }
            });
            mTrophyGrid.setAdapter(new TrophyGridAdapter(this, DbManager.getInstance().getAwards()));
            // Add SSV inner container
            mStickyScrollView.addView(scrollViewContainer);
            // Add SSV to drawer container
            mRightDrawer.addView(mStickyScrollView);
        }


    }

    private int getSmokeFreeDays(long quitdate) {
        return Days.daysBetween(new DateTime(System.currentTimeMillis()).withTimeAtStartOfDay(),
                new DateTime(quitdate).withTimeAtStartOfDay()).getDays();
    }

    private void bindListeners() {

        final View.OnClickListener popBackstackListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popBackStack();
            }
        };

        findViewById(R.id.btn_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(mLeftDrawer);
            }
        });
        findViewById(R.id.btn_loc_tod_menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(mLeftDrawer);
            }
        });
        findViewById(R.id.btn_stats).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(mRightDrawer);
            }
        });
        // All these views onClick should popBackstack

        findViewById(R.id.header_back_nav).setOnClickListener(popBackstackListener);
        findViewById(R.id.header_black_back_nav).setOnClickListener(popBackstackListener);
        findViewById(R.id.notification_back_plus).setOnClickListener(popBackstackListener);
        findViewById(R.id.notification_back_plus_text).setOnClickListener(popBackstackListener);
        findViewById(R.id.notification_back).setOnClickListener(popBackstackListener);
        findViewById(R.id.notification_back_text).setOnClickListener(popBackstackListener);

        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (drawerView == mRightDrawer && mStickyScrollView != null) {
                    mRightDrawer.requestFocus();
                    if (mStickyScrollView.getScrollY() != 0) {
                        mStickyScrollView.scrollTo(0, 0);
                    }
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                mRightDrawer.requestFocus();
                if (mStickyScrollView.getScrollY() != 0) {
                    mStickyScrollView.scrollTo(0, 0);
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        findViewById(R.id.header_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnSaveListener != null) {
                    mOnSaveListener.onSaved();
                }
            }
        });
    }

    /**
     * Adds all menu options to a list. Note add any {@link NavMenuItem} at position 0 that will be
     * overwritten by {@link NavMenuItem} for the header.
     * @return menuItems = list of left drawer menu items
     */
    private List<NavMenuItem> createMenuList() {
        List<NavMenuItem> menuItems = new ArrayList<>();

        menuItems.add(new NavMenuItem());
        menuItems.add(new NavMenuItem(R.drawable.menu_drawer_ic_home, getResources().getString(R.string.menu_drawer_lbl_home)));
        menuItems.add(new NavMenuItem(R.drawable.menu_drawer_ic_quitplan, getResources().getString(R.string.menu_drawer_lbl_quitplan)));
        menuItems.add(new NavMenuItem(R.drawable.menu_drawer_ic_location, getString(R.string.menu_drawer_lbl_location)));
        menuItems.add(new NavMenuItem(R.drawable.menu_drawer_ic_time, getString(R.string.menu_drawer_lbl_times_of_day)));
        menuItems.add(new NavMenuItem(R.drawable.menu_drawer_ic_myhistory, getResources().getString(R.string.menu_drawer_lbl_myhistory)));
//        menuItems.add(new NavMenuItem(R.drawable.menu_drawer_ic_journal, getResources().getString(R.string.menu_drawer_lbl_journal)));
        menuItems.add(new NavMenuItem(R.drawable.menu_drawer_ic_quitguide, getResources().getString(R.string.menu_drawer_lbl_quitguide)));
        menuItems.add(new NavMenuItem(R.drawable.menu_drawer_ic_settings, getResources().getString(R.string.menu_drawer_lbl_settings)));

        return menuItems;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                        enterId, exitId,
                        popEnterId, popExitId)
                .replace(R.id.main_fragment_container, fragment, tag);
        if (addToStack) {
            ft.addToBackStack(null);
        }
        ft.commit();
    }

    @Override
    public void onNoPopAnimatedNavigationAction(Fragment fragment, String tag, int enterId, int exitId, boolean addToStack) {
        FragmentTransaction ft = getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        enterId, exitId)
                .replace(R.id.main_fragment_container, fragment);
        if (addToStack) {
            ft.addToBackStack(null);
        }
        ft.commit();
    }

    @Override
    public void onAddAnimationNavigationAction(Fragment fragment, String tag, int enterId, int exitId, int popEnterId, int popExitId, boolean addToStack) {
        FragmentTransaction ft = getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        enterId, exitId,
                        popEnterId, popExitId)
                .add(R.id.main_fragment_container, fragment);
        if (addToStack) {
            ft.addToBackStack(tag);
        }
        ft.commit();
    }

    @Override
    public void onAddNavigationAction(Fragment fragment, String tag, boolean addToStack) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.main_fragment_container, fragment, tag);
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
        onNavigationAction(loadAfterClear, HomeFragment.TAG, false);
    }



    /* ---------------------------------- Picture methods --------------------------------------- */
    @SuppressWarnings("NewApi")
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
        Log.v(TAG, "Image file, " + image.getCanonicalPath() + " created.");
        return image;
    }

    /* ------------------------------------------------------------------------------------------ */


    @Override
    public void updateStats() {
        updateRightDrawerContent((ViewGroup) rootView);
    }

    @Override
    public void openDrawer(int side) {
        // Coming from a notification intent, drawers may not be initialized yet
        if (mDrawerLayout == null) {
            buildDrawers();
        }
        if (side == DrawerCallbacks.LEFT) {
            mDrawerLayout.openDrawer(mLeftDrawer);
        } else {
            mDrawerLayout.openDrawer(mRightDrawer);
        }
    }

    /**
     * Updates right drawer's views
     * @param container - container hosting right drawer views
     */
    public void updateRightDrawerContent(ViewGroup container) {
        // Update gridview
        if (mTrophyGrid != null && mTrophyGrid.getAdapter() != null) {
            ((TrophyGridAdapter) mTrophyGrid.getAdapter()).notifyDataSetChanged();
            mTrophyGrid.invalidateViews();
        }

        // Wire drawer content
        TextView quitDate = (TextView) container.findViewById(R.id.drawer_quit_date);
        TextView daysSmokeFree = (TextView) container.findViewById(R.id.drawer_days_smoke_free);
        TextView cigsAvoided = (TextView) container.findViewById(R.id.drawer_cigarettes_avoided);
        TextView moneySaved = (TextView) container.findViewById(R.id.drawer_money_saved);
        TextView minutesSaved = (TextView) container.findViewById(R.id.drawer_minutes_saved);
        ImageButton share = (ImageButton) container.findViewById(R.id.drawer_share);

        if (daysSmokeFree == null
                || cigsAvoided == null
                || moneySaved == null
                || minutesSaved == null
                || share == null)
            return;

        Profile profile = DbManager.getInstance().getProfile();
        // Quitdate
        String formattedQuitDate = "";
        quitDate.setText(formattedQuitDate = Common.formatTimestamp("MM/dd/yyyy", profile.getQuitDate()));
        // Smokefree days
        int smokeFreeDays = getSmokeFreeDays(profile.getQuitDate());
        final int sfd = smokeFreeDays; // used for share button
        double savings = 0;
        if (smokeFreeDays < 0) {
            smokeFreeDays *= -1;
            daysSmokeFree.setText(smokeFreeDays == 1 ? smokeFreeDays + " დღე მოწევის გარეშე"
                    : smokeFreeDays + " დღე მოწევის გარეშე");

            // Cigs avoided
            int notSmoked = (int) Math.ceil((double) profile.getNumDailyCigs() * smokeFreeDays / 7 * profile.getSmokeFreq());
            cigsAvoided.setText(notSmoked + " სიგარეტი არ მომიწევია");
            // Money saved
            savings = (double) profile.getNumDailyCigs() / 20 * smokeFreeDays / 7 * profile.getSmokeFreq() * profile.getPricePerPack();
            moneySaved.setText(String.format(" შენ დაზოგე $%.2f!", savings));
            // Minutes saved
            int timeSaved = notSmoked * 10;
            minutesSaved.setText(timeSaved + " წუთი!");
        } else {
            daysSmokeFree.setText(0 + " დღე მოწევის გარეშე");
            cigsAvoided.setText("0 ღერი სიგარეტი არ მომიწევია");
            moneySaved.setText("შენ დაზოგე $0.00");
            minutesSaved.setText(0 + " წუთი!");
        }
        // Share button
        final String qd = formattedQuitDate;
        final double svd = savings;
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // GA
                send(getString(R.string.category_status), getString(R.string.action_share_stats_selected));
                send(getString(R.string.category_status) + "|" + getString(R.string.action_share_stats_selected));
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                // Check to see if date is in the future or the past
                StringBuilder sb = new StringBuilder();
                if (sfd < 0) {
                    sb.append("I quit on ");
                    sb.append(qd);
                    if (svd > 0) {
                        sb.append(" and saved ");
                        sb.append(String.format("$%.2f", svd));
                    }
                    sb.append("!");
                } else if (sfd == 0) {
                    sb.append("მე დღეს თავს ვანებებ მოწევას!");
                } else {
                    sb.append("მე მოწევას თავს დავანებებ ");
                    sb.append(qd);
                    sb.append("!");
                }
                sendIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });
    }

    @Override
    public void showBackBlack() {
        View back = findViewById(R.id.header_black_back_nav);
        View main = findViewById(R.id.header_main);

        if (back == null || main == null)
            return;

        hideAllHeaderViews();
        back.setVisibility(View.VISIBLE);
    }

    @Override
    public void showBack() {
        View back = findViewById(R.id.header_back_nav);
        View main = findViewById(R.id.header_main);

        if (back == null || main == null)
            return;

        back.setVisibility(View.VISIBLE);
        main.setVisibility(View.GONE);
    }

    @Override
    public void showMain() {

        hideAllHeaderViews();
        View main = findViewById(R.id.header_main);
        main.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideAllHeaderViews(){
        ViewGroup header = (ViewGroup)rootView.findViewById(R.id.header);
        for (int i = 0; i < header.getChildCount(); i++) {
            View v = header.getChildAt(i);
            v.setVisibility(View.GONE);
        }
    }

    @Override
    public void hideHeader() {
        final View header = rootView.findViewById(R.id.header);
        if (header == null) {
            return;
        }
        // Set delay for the animation
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                header.setVisibility(View.GONE);
            }
        }, 225);
    }

    @Override
    public void showHeader() {
        // Set delay for the animation
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.header).setVisibility(View.VISIBLE);
            }
        }, 50);
    }

    public void slideHeaderDown() {
        final View header = findViewById(R.id.header);
        if (header != null) {
            Animator animator = AnimatorInflater.loadAnimator(this, R.animator.slide_down_long_deccelerate);
            animator.setTarget(header);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    // Unimpl
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (header != null) {
                        header.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    // Unimpl
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    //Unimpl
                }
            });
            animator.start();
        }
    }

    public void slideHeaderUp() {
        final View header = findViewById(R.id.header);
        if (header != null) {
            header.setVisibility(View.VISIBLE);
            Animator animator = AnimatorInflater.loadAnimator(this, R.animator.slide_up_long_accelerate);
            animator.setTarget(header);
            animator.start();
        }
    }

    public void slideHeaderOff(int delay) {
        final View header = findViewById(R.id.header);
        header.animate()
                .setStartDelay(delay)
                .translationY(-header.getHeight())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        header.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).start();
    }

    @Override
    public void disableDrawers() {
        if (mDrawerLayout == null) {
            mDrawerLayout = (DrawerLayout) rootView.findViewById(R.id.drawer_layout);
        }
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    public void enableDrawers() {
        if (mDrawerLayout == null) {
            mDrawerLayout = (DrawerLayout) rootView.findViewById(R.id.drawer_layout);
        }
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @Override
    public void showSave() {
        View back = findViewById(R.id.header_black_back_nav);
        View save = findViewById(R.id.header_save);
        View main = findViewById(R.id.header_main);

        if (back == null || main == null || save == null)
            return;

        back.setVisibility(View.VISIBLE);
        save.setVisibility(View.VISIBLE);
        main.setVisibility(View.GONE);

    }

    @Override
    public void setOnSaveListener(OnSaveListener saveListener) {
        mOnSaveListener = saveListener;
    }

    @Override
    public void showBackWithPlus() {
        hideAllHeaderViews();
        rootView.findViewById(R.id.header_notifications_with_back_plus).setVisibility(View.VISIBLE);
    }

    @Override
    public void showBackNotification(){
        hideAllHeaderViews();
        rootView.findViewById(R.id.header_notifications_back).setVisibility(View.VISIBLE);
    }

    @Override
    public ViewGroup getHeaderLayout(int layoutId) {
        return (ViewGroup) rootView.findViewById(layoutId);
    }


    @Override
    public void openToScreen(String screenKey) {
        if (screenKey == null) {
            return;
        }
        Fragment fragment = null;
        switch (screenKey) {
            case (HOME):
                send(getString(R.string.category_notification_fired_and_accepted), "Home");
                send(getString(R.string.category_notification_fired_and_accepted) +"|"+ "Home");
                fragment = new HomeFragment();
                break;
            case (MY_QUIT_PLAN):
                send(getString(R.string.category_notification_fired_and_accepted), "MyQuitPlan");
                send(getString(R.string.category_notification_fired_and_accepted) +"|"+ "MyQuitPlan");
                fragment = new QuitPlanFragment();
                break;
            case (HOW_TO_QUIT):
                send(getString(R.string.category_notification_fired_and_accepted), "HowToQuit");
                send(getString(R.string.category_notification_fired_and_accepted) +"|"+ "HowToQuit");
                Bundle howToQuitArgs = new Bundle();
                howToQuitArgs.putString(WebviewFragment.URL_KEY, "quitguide.html");
                fragment = new WebviewFragment();
                fragment.setArguments(howToQuitArgs);
                break;
            case (MANAGE_MY_MOOD):
                send(getString(R.string.category_notification_fired_and_accepted), "ManageMyMood");
                send(getString(R.string.category_notification_fired_and_accepted) +"|"+ "ManageMyMood");
                fragment = new HomeFragment();
                Bundle moodArgs = new Bundle();
                moodArgs.putBoolean(MANAGE_MY_MOOD, true);
                fragment.setArguments(moodArgs);
                break;
            case (JOURNAL):
                send(getString(R.string.category_notification_fired_and_accepted), "Journal");
                send(getString(R.string.category_notification_fired_and_accepted) +"|"+ "Journal");
                fragment = new JournalFragment();
                break;
            case (TRACK_MY_CRAVINGS):
                send(getString(R.string.category_notification_fired_and_accepted), "TrackMyCravings");
                send(getString(R.string.category_notification_fired_and_accepted) +"|"+ "TrackMyCravings");
                fragment = new HomeFragment();
                Bundle cravingArgs = new Bundle();
                cravingArgs.putBoolean(TRACK_MY_CRAVINGS, true);
                fragment.setArguments(cravingArgs);
                break;
            case (STATISTICS):
                send(getString(R.string.category_notification_fired_and_accepted), "Statistics");
                send(getString(R.string.category_notification_fired_and_accepted) +"|"+ "Statistics");
                fragment = new HomeFragment();
                Bundle statsArgs = new Bundle();
                statsArgs.putBoolean(STATISTICS, true);
                fragment.setArguments(statsArgs);
                break;
            case (TIP):
                send(getString(R.string.category_notification_fired_and_accepted), "Tips");
                send(getString(R.string.category_notification_fired_and_accepted) +"|"+ "Tips");
                // hide header
                rootView.findViewById(R.id.header).setVisibility(View.GONE);
                // look for notification id in extras, default to random if not there
                Bundle args = new Bundle();
                int id = getIntent().getIntExtra(Constants.NOTIFICATION_ID_KEY, -1);
                if (id != -1) {
                    args.putInt(Constants.NOTIFICATION_ID_KEY, id);
                } else {
                    args.putBoolean(TipFromNoticeFragment.RANDOM_FALLBACK, true);
                }
                fragment = new TipFromNoticeFragment();
                fragment.setArguments(args);
                break;
        }
        onNavigationAction(fragment, "", false);
    }

    @Override
    public Tracker getTracker() {
        if (mTracker == null) {
            mTracker = ((Application) getApplication()).getTracker(Application.TrackerName.APP_TRACKER);
        }
        return mTracker;
    }

    @Override
    public void send(String category, String action) {
        getTracker().set("&cd4", Common.formatTimestamp("yyyy-MM-dd'T'HH:mm:ss'Z'", System.currentTimeMillis()));
        getTracker().send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .build());
    }

    @Override
    public void send(String category, String action, String label) {
        getTracker().set("&cd4", Common.formatTimestamp("yyyy-MM-dd'T'HH:mm:ss'Z'", System.currentTimeMillis()));
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
        getTracker().set("&cd4", Common.formatTimestamp("yyyy-MM-dd'T'HH:mm:ss'Z'", System.currentTimeMillis()));
        getTracker().send(new HitBuilders.EventBuilder()
                .setCategory(getString(R.string.category_reports))
                .setAction(getString(R.string.action_events_triggered))
                .setLabel(label)
                .build());

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {

            }
        }
        if (requestCode == getResources().getInteger(R.integer.request_code_location_permision)) {
            boolean permissionGranted = true;
            for (int i : grantResults) {
                if (i != PackageManager.PERMISSION_GRANTED) {
                    permissionGranted = false;
                }
            }
            if (permissionGranted) {
                Log.i(TAG, "Location permissions granted");
                try {
                    SetLocationFragment setLocationFragment = (SetLocationFragment) getFragmentManager().findFragmentByTag(SetLocationFragment.TAG);
                    if (setLocationFragment != null) {
                        setLocationFragment.initCurrentLocation();
                    }
                } catch (ClassCastException e) {
                    Log.e(TAG, "Mistagged fragment exists");
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(TAG, "onActivityResult called with request code= " + requestCode + " and result code= " + resultCode);
        Log.d(TAG, "Entered on activity result");
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                Log.i(TAG, "Location services enables, getting current location...");
                initLocationTracking();
            } else {
                Log.i(TAG, "User chose not to make required location settings changes.");
                UiUtils.dismissProgressDialog();
            }
        }
    }

    public void setLocationFragmentCallback(SetLocationFragment mLocationFragmentCallback) {
        this.mLocationFragmentCallback = mLocationFragmentCallback;
    }

    public void initLocationTracking() {
        if (mLocationFragmentCallback != null) {
            mLocationFragmentCallback.initCurrentLocation();
        }
    }

    public void openApplicationSettings()  {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "Activity not found for package: " + getPackageName());
        }
    }

    public void showProgressDialog() {
        UiUtils.showProgressDialog(this);
    }

    @Override
    public void onBackPressed() {
        // Special fragment transaction and animation cases for back button presses
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry bsEntry = getFragmentManager().getBackStackEntryAt(getFragmentManager().getBackStackEntryCount() - 1);
            if (bsEntry != null && Constants.CLEAR_BACKSTACK_ON_BACK.equals(bsEntry.getName())) {
                Common.mDisableWhatsNewAnim = true;
                if (mAlternativeNav != null) {
                    mAlternativeNav.alternativeNavigationAction();
                    mAlternativeNav = null; // consumes callback
                }
                clearBackStack(new HomeFragment());
                return;
            }
        }
        super.onBackPressed();
        Common.mDisableWhatsNewAnim = false;
    }


    public interface AlternativeNav {
        void alternativeNavigationAction();
    }

    public AlternativeNav getAlternativeNav() {
        return mAlternativeNav;
    }

    public void setAlternativeNav(AlternativeNav mAlternativeNav) {
        this.mAlternativeNav = mAlternativeNav;
    }

    /*-------------------------------Location Services--------------------------------------------*/


    /**
     * @return Address - address object representing the user's last known location
     */
    // TODO these should be processing asynchronously
    public Location getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, getResources().getInteger(R.integer.request_code_location_permision));
        }
        Location loc = LocationServices.FusedLocationApi.getLastLocation(getGoogleApiClient());
        return loc;
    }

    public Address getAddress(Location loc) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addresses == null || addresses.size() <= 0 ? null : addresses.get(0);
    }

    public Address getLastKnownAddress() {
        return getAddress(getLastKnownLocation());
    }
}
