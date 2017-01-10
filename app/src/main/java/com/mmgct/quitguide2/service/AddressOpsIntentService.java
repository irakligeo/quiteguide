package com.mmgct.quitguide2.service;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.mmgct.quitguide2.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by 35527 on 2/5/2016.
 */
public class AddressOpsIntentService extends IntentService {

    private static final String TAG = AddressOpsIntentService.class.getSimpleName();

    protected ResultReceiver mReceiver;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public AddressOpsIntentService(String name) {
        super(name);
    }

    /**
     * Default constructor
     */

    public AddressOpsIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        ArrayList<Address> addresses = null;
        String errorMessage = "";

        Location location = intent.getParcelableExtra(AddressConstants.LOCATION_DATA_EXTRA);
        mReceiver = intent.getParcelableExtra(AddressConstants.RECEIVER);
        int numAddrToFetch = intent.getIntExtra(AddressConstants.NUM_TO_FETCH_EXTRA, 1);

        if (intent.hasExtra(AddressConstants.ADDRESS_LOOKUP_EXTRA)) {
            String address = intent.getStringExtra(AddressConstants.ADDRESS_LOOKUP_EXTRA);
            if (address == null || address.trim().equals("")) {
                return;
            }
            try {
                addresses = new ArrayList<>(geocoder.getFromLocationName(address, numAddrToFetch));
            } catch (IOException e) {
                errorMessage = getString(R.string.address_service_not_available);
                Log.e(TAG, errorMessage, e);
            } catch (IllegalArgumentException e) {
                errorMessage = getString(R.string.address_invalid_lat_long);
                Log.e(TAG, errorMessage + ". " +
                        "Latitude = " + location.getLatitude() +
                        ", Longitude = " +
                        location.getLongitude(), e);
            }
            deliverResults(addresses, errorMessage, AddressConstants.SUCCESS_GEO_CODE);
        } else {
            try {
                addresses = new ArrayList<>(geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), numAddrToFetch));
            } catch (IOException e) {
                errorMessage = getString(R.string.address_service_not_available);
                Log.e(TAG, errorMessage, e);
            } catch (IllegalArgumentException e) {
                errorMessage = getString(R.string.address_invalid_lat_long);
                Log.e(TAG, errorMessage + ". " +
                        "Latitude = " + location.getLatitude() +
                        ", Longitude = " +
                        location.getLongitude(), e);
            }
            deliverResults(addresses, errorMessage, AddressConstants.SUCCESS_REVERSE_GEO_CODE);
        }
    }

    private void deliverResults(ArrayList<Address> addresses, String errorMessage, int statusCode) {
        // No cases found
        if (addresses == null || addresses.size() == 0) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.address_none_found);
                Log.e(TAG, errorMessage);
            }
            deliverResultsToReceiver(AddressConstants.FAILURE_RESULT, errorMessage);
        } else {
            deliverResultsToReceiver(statusCode, addresses);
        }
    }


    // Error
    private void deliverResultsToReceiver(int resultCode, String message) {
        Bundle args = new Bundle();
        args.putString(AddressConstants.RESULT_ERROR_KEY, message);
        mReceiver.send(resultCode, args);
    }
    // Success
    private void deliverResultsToReceiver(int resultCode, ArrayList<Address> addresses) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(AddressConstants.RESULT_DATA_KEY, addresses);
        mReceiver.send(resultCode, args);
    }

    public final class AddressConstants {
        public static final int SUCCESS_GEO_CODE = 0;
        public static final int SUCCESS_REVERSE_GEO_CODE = 1;
        public static final int FAILURE_RESULT = 2;
        public static final String PACKAGE_NAME =
                "com.mmgct.quitguide2.service";
        public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
        public static final String RESULT_DATA_KEY = PACKAGE_NAME +
                ".RESULT_DATA_KEY";
        public static final String RESULT_ERROR_KEY = PACKAGE_NAME +".RESULT_ERROR_KEY";
        public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME +
                ".LOCATION_DATA_EXTRA";
        public static final String ADDRESS_LOOKUP_EXTRA = PACKAGE_NAME + ".ADDRESS_LOOKUP_EXTRA";
        public static final String NUM_TO_FETCH_EXTRA = PACKAGE_NAME + ".NUM_TO_FETCH_EXTRA";
        public static final String TYPE_REVERSE_GEOCODE = PACKAGE_NAME + ".REVERSE_GEOCODE";
        public static final String TYPE_GEOCODE = PACKAGE_NAME + ".GEOCODE";
        public static final String TYPE_EXTRA = PACKAGE_NAME + ".TYPE";

    }
}
