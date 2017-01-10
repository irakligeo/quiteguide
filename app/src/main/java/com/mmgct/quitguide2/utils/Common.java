package com.mmgct.quitguide2.utils;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.ColorFilter;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.fragments.dialogs.iOSStyledDecisionDialog;
import com.mmgct.quitguide2.fragments.listeners.OnGeocodeOperationCompleted;
import com.mmgct.quitguide2.managers.DbManager;
import com.mmgct.quitguide2.models.GeoTag;
import com.mmgct.quitguide2.models.Notification;
import com.mmgct.quitguide2.models.NotificationHistory;
import com.mmgct.quitguide2.models.RecurringNotification;
import com.mmgct.quitguide2.views.notifications.OpenToScreenNavigation;
import com.mmgct.quitguide2.views.notifications.service.NotificationService;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;

/**
 * Created by 35527 on 10/28/2015.
 */
public class Common {

    private static final String TAG = Common.class.getSimpleName();
    public static boolean mDisableWhatsNewAnim = false;
    public static boolean mGlobalDisableAnim = false;

    private static final int BUFFER_SIZE = 256;

    // Uses JodaTime format
    public static String formatTimestamp(String pattern, long timestamp) {
        String date = null;
        DateTime dateTime = new DateTime(timestamp);
        DateTimeFormatter dtf = DateTimeFormat.forPattern(pattern);
        date = dtf.print(dateTime);
        return date;
    }

    public static String assetFileToString(String filename, Context ctx) {
        StringBuilder sb = new StringBuilder();

        try {
            InputStream is = ctx.getResources().getAssets().open(filename);
            byte[] buffer = new byte[BUFFER_SIZE];
            int i = 0;
            try {
                while ((i = is.read(buffer, 0, buffer.length)) != -1) {
                        sb.append(new String(buffer, 0, i, "UTF-8"));
                }
            } catch (IOException e) { e.printStackTrace();}
            finally {
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     *
     * @param aTime - timestamp
     * @return Days between aTime and now.
     */
    public static int daysFromNow(long aTime) {
        return Days.daysBetween(new DateTime(System.currentTimeMillis()).withTimeAtStartOfDay(),
                new DateTime(aTime).withTimeAtStartOfDay()).getDays();
    }

    public static long millisAtStartOfDay(long timestamp) {
        return (new DateTime(timestamp).withTimeAtStartOfDay().getMillis());
    }

    /**
     * This method temporarily disables view for the length specified
     * @param viewToTemporarilyDisable
     * @param duration
     */
    public static void shortDisable(final View viewToTemporarilyDisable, int duration) {
        Handler handler = new Handler();
        viewToTemporarilyDisable.setEnabled(false);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                viewToTemporarilyDisable.setEnabled(true);
            }
        }, duration);
    }

    public static Point screenResolution(Context context) {
        WindowManager windowManager =
                (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point screenResolution = new Point();

        if (Build.VERSION.SDK_INT < 14)
            throw new RuntimeException("Unsupported Android version.");
        display.getRealSize(screenResolution);

        return screenResolution;
    }

    public static void createNewNotificationHistory(Notification n, DateTime scheduleDate) {
        NotificationHistory nh = new NotificationHistory();
        nh.setScheduledDeliveryDate(scheduleDate.getMillis());
        nh.setNotification(n);
        DbManager.getInstance().createNotificationHistory(nh);
        n.setNotificationHistory(nh);
        DbManager.getInstance().updateNotification(n);
    }


    public static void updateNotificationHistory(NotificationHistory nh, DateTime scheduleDate) {
        nh.setActualDeliveryDate(0);
        nh.setScheduledDeliveryDate(scheduleDate.getMillis());
        DbManager.getInstance().updateNotificationHistory(nh);
    }

    /**
     * This method returns next year for dates that have already passed or this year for dates
     * that have yet to pass
     * @param month - schedule month
     * @param dayOfMonth - schedule dayOfMonth
     * @return year - this year or next year
     */
    public static int getScheduleYear(int month, int dayOfMonth) {
        int year = -1;
        DateTime now = new DateTime(System.currentTimeMillis()).withTimeAtStartOfDay();
        DateTime scheduleDate = new DateTime(now.getYear(), month, dayOfMonth, 0, 0);
        if (scheduleDate.isAfter(now) || scheduleDate.equals(now)) {
            year = now.getYear();
        } else {
            year = now.getYear()+1;
        }
        return year;
    }

    /**
     * Sets a 300 ms filter on an ImageView to give the appearance of being clicked.
     * @param v - A view to put the filter on.
     * @param color - The color of the filter.
     */
    public static void addClickFilter(final ImageView v, int color) {
        Handler handler = new Handler();
        v.setColorFilter(color);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                v.setColorFilter(null);
            }
        }, 300);
    }
    /**
     * Sets a 300 ms filter on an Button to give the appearance of being clicked.
     * @param v - A view to put the filter on.
     * @param color - The color of the filter.
     */
    public static void addClickFilter(final Button v, int color) {
        Handler handler = new Handler();
        v.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                v.getBackground().setColorFilter(null);
            }
        }, 300);
    }

    public static void longPressHapticVibrate(Context ctx) {
        Vibrator vibe = (Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(50);
    }

    public static void flipOut(Context c, View v) {
        if (v != null) {
            Animator flipOut = AnimatorInflater.loadAnimator(c, R.animator.card_flip_left_out);
            flipOut.setTarget(v);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(flipOut);
            animatorSet.start();
        }
    }

    public static void flipIn(Context c, View v) {
        if (v != null) {
            Animator flipOut = AnimatorInflater.loadAnimator(c, R.animator.card_flip_right_in);
            flipOut.setTarget(v);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(flipOut);
            animatorSet.start();
        }
    }

    /**
     * Helper returns reverse geocode for coordinates
     */
    public static void reverseGeocode(final Activity ctx, final double lat, final double lng, final OnGeocodeOperationCompleted onGeocodeOperationCompleted){


        new Thread(new Runnable() {
            @Override
            public void run() {
                Address address = new Address(Locale.getDefault());
                //address.setLatitude(lat);
                // address.setLongitude(lng);
                Geocoder geocoder = new Geocoder(ctx, Locale.getDefault());
                String errorMessage = "";
                    boolean hasError = false;
                    try {
                        // Determine whether a Geocoder is available.
                        if (!Geocoder.isPresent()) {
                            Toast.makeText(ctx, R.string.no_geocoder_available,
                                    Toast.LENGTH_LONG).show();
                            return;
                        }
                        List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
                        if (addresses != null && addresses.size() >= 1) {
                            address = addresses.get(0);
                        }
                    } catch (IOException e) {
                        errorMessage = ctx.getString(R.string.address_service_not_available);
                        hasError = true;
                        showErrorOnUiThread(ctx.getString(R.string.error_service_not_available));
                        Log.e(TAG, errorMessage, e);
                    } catch (IllegalArgumentException e) {
                        errorMessage = ctx.getString(R.string.address_invalid_lat_long);
                        Log.e(TAG, errorMessage + ". " +
                                "Latitude = " + lat +
                                ", Longitude = " +
                                lng, e);
                        hasError = true;
                        showErrorOnUiThread(ctx.getString(R.string.error_service_not_available));
                    }
                    final Address tmp = address;
                    if (!hasError) {
                        ctx.runOnUiThread(new Runnable() { // When it's done notify caller go back on ui thread
                            @Override
                            public void run() {
                                onGeocodeOperationCompleted.reverseGeocodeCompleted(tmp);
                            }
                        });
                    }
                }


            private void showErrorOnUiThread(final String errorMsg) {
                ctx.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    /**
     * Helper makes the RecurringNotification, GeoTag, and Notification for {@link NotificationService}
     */
    public static RecurringNotification createRecurringNotification(double lat, double lng, String address) {

            RecurringNotification recurringNotification = new RecurringNotification();
            recurringNotification.setActive(true); // Active state
            // Geotag
            GeoTag geoTag = new GeoTag(lat, lng, address, System.currentTimeMillis());
            // DbManager.getInstance().createGeoTag(geoTag);
            recurringNotification.setGeoTag(geoTag);
            // Notification
            Notification notification = Notification.newNotIngestedInstance();  // We use this Notification object's id as
            notification.setOpenToScreen(OpenToScreenNavigation.TIP);           // request code for geofence PendingIntent
            recurringNotification.setNotification(notification);
            notification.setRecurringNotification(recurringNotification);

        return recurringNotification;
        }

 /*   public static boolean areNotificationsEnabled(Context ctx) {
        boolean isEnabled = true;
        ContentResolver contentResolver = ctx.getContentResolver();
        String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
        String packageName = ctx.getPackageName();
        if (enabledNotificationListeners == null || !enabledNotificationListeners.contains(packageName)) {
            // User has not granted the app the Notification access permission
            isEnabled = false;
        }
        return isEnabled;
    }*/


        private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
        private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

        public static boolean isNotificationEnabled(Context context) {

            try {

                AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);

                ApplicationInfo appInfo = context.getApplicationInfo();

                String pkg = context.getApplicationContext().getPackageName();

                int uid = appInfo.uid;

                Class appOpsClass = null; /* Context.APP_OPS_MANAGER */

                appOpsClass = Class.forName(AppOpsManager.class.getName());

                Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE, String.class);

                Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
                int value = (int)opPostNotificationValue.get(Integer.class);

                return ((int)checkOpNoThrowMethod.invoke(mAppOps,value, uid, pkg) == AppOpsManager.MODE_ALLOWED);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch(NoClassDefFoundError e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }

    public static String concatAddressLines(Address addr) {
        if (addr == null) {
            return "";
        }

        StringBuilder address = new StringBuilder();
        boolean first = true;
        for (int i = 0; i < addr.getMaxAddressLineIndex(); i++){
            if (first) {
                address.append(addr.getAddressLine(i));
                first = false;
            }
            else {
                address.append("\n");
                address.append(addr.getAddressLine(i));
            }
        }
        return address.toString();
    }

    public static String formatMilitaryToStandardTime(String timeOfDay) {
        String standardTime;
        try {
            int hours = Integer.parseInt(timeOfDay.substring(0, timeOfDay.indexOf(":")));
            String halfday = hours >= 12 ? "PM" : "AM";
            hours = hours % 12;
            if (hours == 0){
                hours = 12;
            }
            String minutes = timeOfDay.substring(timeOfDay.indexOf(":")+1);
            if (minutes.length() == 1) {
                minutes = "0"+minutes;
            }
            standardTime = hours+":"+minutes + " " + halfday;
        } catch (NumberFormatException e) {
            standardTime = "";
        } catch (IndexOutOfBoundsException e) {
            standardTime = "";
        } catch (Exception e) {
            standardTime = "";
        }

        return standardTime;
    }


    public static boolean shouldAskPermission(){
        return(Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP_MR1);
    }
}
