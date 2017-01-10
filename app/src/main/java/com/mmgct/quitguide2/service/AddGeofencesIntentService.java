package com.mmgct.quitguide2.service;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.mmgct.quitguide2.BuildConfig;
import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.managers.DbManager;
import com.mmgct.quitguide2.models.GeoTag;
import com.mmgct.quitguide2.models.RecurringNotification;
import com.mmgct.quitguide2.utils.Constants;
import com.mmgct.quitguide2.views.notifications.service.NotificationService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 35527 on 2/11/2016.
 */
public class AddGeofencesIntentService extends IntentService implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    public static final String TAG = AddGeofencesIntentService.class.getSimpleName();
    public static final String RECURRING_NOTIFICATION_ID_EXTRA_KEY = "agis_rec_notification"; // For passing a single recurring notification
    public static final String FUNCTION_EXTRA_KEY = "agis_function"; // These are for telling this IntentService which operation to perform
    public static final String FUNCTION_TYPE_SET_SINGLE = "agis_add_single"; // Set single geofence
    public static final String FUNCTION_TYPE_SET_ALL = "agis_all"; // Set all geofences
    public static final String FUNCTION_REMOVE_SINGLE = "agis_remove_single"; // Remove single geofence
    public static final String FUNCTION_REMOVE_ALL = "agis_remove_all"; // Remove all geofences


    private GoogleApiClient mGoogleApiClient;
    private List<RecurringNotification> mGeofenceNotifications;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public AddGeofencesIntentService(String name) {
        super(name);
    }

    /**
     * Required default constructor
     */

    public AddGeofencesIntentService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        buildGoogleApiClient();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Get all of the RecurringNotifications with non null GeoTags and set GeoFences for them
     *
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v(TAG, "Starting AddGeofencesIntentService");
        if (DbManager.getInstance() == null) { // Because this is sometimes loaded right after boot
            DbManager.init(getApplicationContext());
        }
        buildGoogleApiClient();
        if (intent.getExtras().containsKey(FUNCTION_EXTRA_KEY)) {
            switch(intent.getExtras().getString(FUNCTION_EXTRA_KEY)) {
                case(FUNCTION_TYPE_SET_ALL):
                    Log.v(TAG, "Set all geofence notifications selected");
                    mGeofenceNotifications = DbManager.getInstance().getRecurringGeofenceNotifications();
                    // Debug
                    if (mGeofenceNotifications != null)
                        for (int i = 0; mGeofenceNotifications.size() > i; i++) {
                            Log.d(TAG, mGeofenceNotifications.get(i).toString());
                        }
                    break;
                case(FUNCTION_TYPE_SET_SINGLE):
                    Log.v(TAG, "Set single geofence notification selected");
                    mGeofenceNotifications = new ArrayList<>();
                    int id = intent.getIntExtra(RECURRING_NOTIFICATION_ID_EXTRA_KEY, -1);
                    if (id != -1) {
                        mGeofenceNotifications.add(DbManager.getInstance().getRecurringNotificationById(id));
                    }
                    break;
                // TODO Add functionality for removing single and removing all geofences
            }
            if (mGeofenceNotifications != null && mGeofenceNotifications.size() > 0) {
                connect();
            }
        }
    }

    private void connect() {
        if (mGoogleApiClient == null) {
            buildGoogleApiClient();
        }
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }


    protected void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    // Geofencing

    private GeofencingRequest getGeofencingRequest(RecurringNotification recurringNotification) throws GeofenceException {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        GeoTag geotag = recurringNotification.getGeoTag();

        if (geotag == null) {
            throw new GeofenceException(getString(R.string.error_loc_get_geo_req));
        }

        // get user selected lat lng
        double lat, lng;
        lat = geotag.getLat();
        lng = geotag.getLon();

        // Add geofence
        builder.addGeofence(new Geofence.Builder()
                .setRequestId(String.valueOf(lat + lng))
                .setCircularRegion(lat, lng, Constants.GEOFENCE_RADIUS_IN_METERS)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build());
        // For debug build, will trigger geofence event if in geofenced area when geofence is set
        if (BuildConfig.DEBUG) {
            builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        }

        // Return a GeofencingRequest.
        return builder.build();
    }



    /**
     * Adds geofences, which sets alerts to be notified when the device enters one of the
     * specified geofences. Handles the success or failure results returned by addGeofences().
     */

    public void addGeofence(RecurringNotification recurringNotification) {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    // The GeofenceRequest object.
                    getGeofencingRequest(recurringNotification),
                    // A pending intent that that is reused when calling removeGeofences(). This
                    // pending intent is used to generate an intent when a matched geofence
                    // transition is observed.
                    getGeofencePendingIntent(recurringNotification));
            Log.i(TAG, "Geofence added.");
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        } catch (GeofenceException geofenceException) {
            Toast.makeText(this, "We're sorry an error has occurred.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, geofenceException.getMessage());
        }
    }

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private PendingIntent getGeofencePendingIntent(RecurringNotification recurringNotification) throws GeofenceException {
        Intent intent = new Intent(this, NotificationService.class);
        if (recurringNotification == null && recurringNotification.getNotification() == null) {
            Log.e(TAG, getString(R.string.error_loc_get_geo_pend_intent));
            throw new GeofenceException(getString(R.string.error_loc_get_geo_pend_intent));
        }
        int notificationId =  recurringNotification.getNotification().getId();
        // Add notification id as extra
        intent.putExtra(Constants.NOTIFICATION_ID_KEY, notificationId);
        // requestCode should be the Notification's unique id so we can enable/disable this later
        return PendingIntent.getService(this,
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void logSecurityException(SecurityException securityException) {
        Log.e(TAG, "Invalid location permission. " +
                "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
    }

    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost for some reason.
        Log.i(TAG, "Connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
        Toast.makeText(this, getString(R.string.error_google_api_failed), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (mGeofenceNotifications != null) {
            for (int i = 0; i < mGeofenceNotifications.size(); i++) {
                RecurringNotification recurringNotification = mGeofenceNotifications.get(i);
                if (recurringNotification.isActive()) {
                    addGeofence(recurringNotification);
                    Log.i(TAG, "Set geofence recurring notification: "+recurringNotification.getId());
                    mGeofenceNotifications.remove(i);
                }
            }
        }

        Log.i(TAG, "Connected to GoogleApiClient");
    }

    private class GeofenceException extends Exception {
        public GeofenceException() {
            super();
        }
        public GeofenceException(String msg) {
            super(msg);
        }

        public GeofenceException(String detailMessage, Throwable throwable) {
            super(detailMessage, throwable);
        }

        public GeofenceException(Throwable throwable) {
            super(throwable);
        }

        @Override
        public String getMessage() {
            return super.getMessage();
        }
    }

}
