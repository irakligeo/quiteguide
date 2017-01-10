package com.mmgct.quitguide2.fragments.listeners;

import android.location.Address;

/**
 * Created by 35527 on 2/25/2016.
 * Listens for geocoding operations to finish
 */
public interface OnGeocodeOperationCompleted {

    void reverseGeocodeCompleted(Address address);
    void geocodeCompleted(Address address);
}
