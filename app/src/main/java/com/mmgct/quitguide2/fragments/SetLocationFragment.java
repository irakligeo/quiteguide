package com.mmgct.quitguide2.fragments;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mmgct.quitguide2.MainActivity;
import com.mmgct.quitguide2.R;
import com.mmgct.quitguide2.fragments.dialogs.OkDialog;
import com.mmgct.quitguide2.fragments.dialogs.iOSStyledDecisionDialog;
import com.mmgct.quitguide2.fragments.listeners.DialogDismissListener;
import com.mmgct.quitguide2.fragments.listeners.OnAnimationActionListener;
import com.mmgct.quitguide2.managers.DbManager;
import com.mmgct.quitguide2.models.GeoTag;
import com.mmgct.quitguide2.models.Notification;
import com.mmgct.quitguide2.models.RecurringNotification;
import com.mmgct.quitguide2.models.Tip;
import com.mmgct.quitguide2.service.AddressOpsIntentService;
import com.mmgct.quitguide2.service.AddressResultReceiver;
import com.mmgct.quitguide2.utils.Common;
import com.mmgct.quitguide2.utils.Constants;
import com.mmgct.quitguide2.utils.UiUtils;
import com.mmgct.quitguide2.views.adapters.AddressItem;
import com.mmgct.quitguide2.views.notifications.OpenToScreenNavigation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by 35527 on 1/26/2016.
 */
public class SetLocationFragment extends BaseFragment implements OnMapReadyCallback, ConnectionCallbacks, OnConnectionFailedListener, OnMapLongClickListener, GoogleMap.OnMapClickListener, Handler.Callback, DialogDismissListener, GoogleMap.OnInfoWindowClickListener {

    public static final String TAG = SetLocationFragment.class.getSimpleName();
    private boolean mSlideDownAnimation;
    private boolean mProcessingUserClick;

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    public static final String MAP_TAG = "map_fragment";

    private View rootView;
    private OnAnimationActionListener mOnAnimationActionListener;
    private UserChooseTipFragment.SetLocationEventListener mLocationEventListener;
    private Location mCurrentLocation;
    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mGoogleMap;
    private EditText mEdtAddress;
    private AddressResultReceiver mResultReceiver;
    private ListView mLvAddresses;
    private TextView mTvInstructions;
    private LinearLayout mLytAddr;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private Address mSelectedAddress;
    private RecurringNotification mRecurringNotification;
    private ArrayAdapter<AddressItem> mAddressListAdapter;
    private MarkerOptions mCurrentMarkerOptions; // For the marker representing the users current location
    private List<MarkerWrapper> mSavedLocations; // For storing all of the markers the user has saved previously
    private Marker mSelectedMarker; // Last marker added with longMapPress()
    private FragmentManager mFragmentManager;
    private String mStringAddress; /*To hold the String representation of address, to store in new GeoTag*/
    private MapFragment mMapFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mResultReceiver = new AddressResultReceiver(new Handler(), this, getActivity());
        mSavedLocations = new ArrayList<>();
        UiUtils.showProgressDialog(getActivity());
    }

    private void setupForSequence() {
        if (getArguments() != null) {
            String sequenceType = getArguments().getString(UserChooseTipFragment.SCREEN_SEQUENCE_EXTRA_KEY, "");
            // Craving or Slip screen sequence
            if (UserChooseTipFragment.SEQUENCE_TYPE_CRAVING.equals(sequenceType)
                    || UserChooseTipFragment.SEQUENCE_TYPE_SLIP.equals(sequenceType)) {
                try {
                    ((MainActivity) getActivity()).setAlternativeNav(new MainActivity.AlternativeNav() {
                        @Override
                        public void alternativeNavigationAction() {
                            mSlideDownAnimation = true;
                            disableStackedFragmentAnimations();
                        }
                    });
                } catch (ClassCastException e) {
                    throw new ClassCastException("Hosting activity must be MainActivity.");
                }
            }
            // Settings sequence
            else if (UserChooseTipFragment.SEQUENCE_TYPE_SETTINGS.equals(sequenceType)) {
                // Remove close button
                rootView.findViewById(R.id.btn_close).setVisibility(View.GONE);
            }
        }
    }


    private synchronized void buildGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setOnMapLongClickListener(this);
        mGoogleMap.setOnMapClickListener(this);
        mGoogleMap.setOnInfoWindowClickListener(this);
        LatLng currentCoordinates = new LatLng(42.1928518, 44.4866121);
        CameraUpdate point = CameraUpdateFactory.newLatLngZoom(currentCoordinates, 5f);
        mGoogleMap.animateCamera(point, 1000, null);
        buildGoogleApiClient();
        createLocationRequest();
        buildLocationSettingsRequest();
        mGoogleApiClient.connect();
    }


    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {
            UiUtils.showProgressDialog(getActivity());
            mGoogleApiClient.connect();
        }
        if (mOnAnimationActionListener != null) {
            mOnAnimationActionListener.startTransitionAnimation(false);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            if (mGoogleMap != null) {
                mGoogleMap.clear();
            }
            mGoogleApiClient.disconnect();
        }
        // Check if map fragment still exists in fragment manager if so remove it
        if (mFragmentManager.findFragmentByTag(MAP_TAG) != null){
            mFragmentManager.beginTransaction().remove(mFragmentManager.findFragmentByTag(MAP_TAG)).commitAllowingStateLoss();
            mFragmentManager.executePendingTransactions();
        }
        if(mOnAnimationActionListener != null) {
            mOnAnimationActionListener.startTransitionAnimation(true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // If coming from settings
        if (getArguments() != null
                && UserChooseTipFragment.SEQUENCE_TYPE_SETTINGS.equals(getArguments().getString(UserChooseTipFragment.SCREEN_SEQUENCE_EXTRA_KEY))){
            try {
                ((ManageNotificationsFragment)getTargetFragment()).setTabToDisplay(ManageNotificationsFragment.MAP);
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (rootView != null) { // If popBackStack enter
            if (mLytAddr != null){
                mLytAddr.setVisibility(View.GONE);
            }
            if (mTvInstructions != null) {
                mTvInstructions.setVisibility(View.VISIBLE);
            }
            setupForSequence();
            return rootView;
        }
        // Need to use getFragmentManager for lollipop and below
        /*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Log.d(TAG, "using getFragmentManager");
            mFragmentManager = getFragmentManager();
        } else {
            Log.d(TAG, "using getChildFragmentManager");
            mFragmentManager = getChildFragmentManager();
        }*/

        rootView = inflater.inflate(R.layout.fragment_set_location, container, false);
        mLvAddresses = (ListView) rootView.findViewById(R.id.lv_matching_addresses);
        mEdtAddress = (EditText) rootView.findViewById(R.id.edt_address_location);
        mTvInstructions = (TextView) rootView.findViewById(R.id.tv_map_instructions);
        mLytAddr = (LinearLayout) rootView.findViewById(R.id.lyt_addr_list);
        setupForSequence();
        bindListeners();


        // Get reference to GoogleMap asynchronously use child frag manager since this the map is a nested fragment
        // MapFragment mapFragment = (MapFragment) mFragmentManager.findFragmentById(R.id.map_fragment);
        /*if (getTargetFragment() == null) {
            mFragmentManager = getChildFragmentManager();
        }
        else {
            mFragmentManager = getTargetFragment().getChildFragmentManager();
        }*/

        mFragmentManager = getChildFragmentManager();
        mMapFragment = MapFragment.newInstance();
        mFragmentManager.beginTransaction().replace(R.id.container_set_location, mMapFragment, MAP_TAG).commit();
        mFragmentManager.executePendingTransactions();
        mMapFragment.getMapAsync(this);


        //MapView mapView = (MapView) rootView.findViewById(R.id.map_view);
        /*if (mapView != null) {
            mapView.onCreate(null);
            mapView.getMapAsync(this);
        }*/
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void bindListeners() {
        // EditText listeners
        mEdtAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (mTvInstructions == null || mLytAddr == null) {
                    return;
                }

                if (hasFocus) {
                    mTvInstructions.setVisibility(View.GONE);
                    mLytAddr.setVisibility(View.VISIBLE);
                } else {
                    mTvInstructions.setVisibility(View.VISIBLE);
                    mLytAddr.setVisibility(View.GONE);
                }
            }
        });

        mEdtAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // Clear focus here from edittext
                    v.clearFocus();
                    // Hide soft keyboard
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                return false;
            }
        });

        rootView.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHeaderCallbacks.showHeader();
                mHeaderCallbacks.enableDrawers();
                mSlideDownAnimation = true;
                disableStackedFragmentAnimations();
                // Hide soft keyboard
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                mCallbacks.clearBackStack(new HomeFragment());
            }
        });

        mLvAddresses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    mTrackerHost.send(getString(R.string.category_notification_tip), getString(R.string.action_add_location_by_address));
                    mTrackerHost.send(getString(R.string.category_notification_tip) +"|"+getString(R.string.action_add_location_by_address));

                    mSelectedAddress = ((AddressItem) parent.getAdapter().getItem(position)).getAddress();
                    if (mSelectedAddress != null) {
                        showDialog();
                    }
                } catch (ClassCastException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void disableStackedFragmentAnimations() {
        if (mOnAnimationActionListener != null) {
            mOnAnimationActionListener.disableAnimation();
        }
        Common.mDisableWhatsNewAnim = true;
    }


    @Override
    public void onConnected(Bundle bundle) {

        Log.i(TAG, "Connected to GoogleApiClient");
        checkLocationSettings();
        // Now add a text watcher for the ListView
        addAddressChangeListener();

        // ----------- Code for using .getLastLocation() instead of LocationListener ---------------

        /*mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mCurrentLocation == null) {
            Log.e(TAG, "Couldn't get last known location");
            return;
        }
        LatLng currentCoordinates = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        CameraUpdate point = CameraUpdateFactory.newLatLngZoom(currentCoordinates, 15f);
        mGoogleMap.animateCamera(point, 1000, null);
        mGoogleMap.addMarker(new MarkerOptions().position(currentCoordinates));
        if (mCurrentLocation != null) {
            // Determine whether a Geocoder is available.
            if (!Geocoder.isPresent()) {
                Toast.makeText(getActivity(), R.string.no_geocoder_available,
                        Toast.LENGTH_LONG).show();
                return;
            }
            startIntentService();
        }
        UiUtils.dismissProgressDialog();*/

        // -----------------------------------------------------------------------------------------
    }

    // Gets 1 address based on current lat lng coordinates
    private void startIntentService() {
        Intent intent = new Intent(getActivity(), AddressOpsIntentService.class);
        intent.putExtra(AddressOpsIntentService.AddressConstants.LOCATION_DATA_EXTRA, mCurrentLocation);
        intent.putExtra(AddressOpsIntentService.AddressConstants.RECEIVER, mResultReceiver);
        getActivity().startService(intent);
    }

    // Gets a list of addresses based on user input
    private void startIntentService(String partialAddress, int resultsToFetch) {
        Intent intent = new Intent(getActivity(), AddressOpsIntentService.class);
        intent.putExtra(AddressOpsIntentService.AddressConstants.LOCATION_DATA_EXTRA, mCurrentLocation);
        intent.putExtra(AddressOpsIntentService.AddressConstants.RECEIVER, mResultReceiver);
        intent.putExtra(AddressOpsIntentService.AddressConstants.ADDRESS_LOOKUP_EXTRA, partialAddress);
        intent.putExtra(AddressOpsIntentService.AddressConstants.NUM_TO_FETCH_EXTRA, resultsToFetch);
        getActivity().startService(intent);
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
        Toast.makeText(getActivity(), getString(R.string.error_google_api_failed), Toast.LENGTH_LONG).show();
        UiUtils.dismissProgressDialog();
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (!mProcessingUserClick) {
            mProcessingUserClick = true;
            Common.longPressHapticVibrate(getActivity());
            mSelectedMarker = addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_black)));
            mSelectedAddress = new Address(Locale.getDefault());
            mSelectedAddress.setLongitude(latLng.longitude);
            mSelectedAddress.setLatitude(latLng.latitude);
            // Offload this, also don't use AddressOpsIntentService for reverse geocoding
            // that class should be used primarily for loading the list view or initially setting EditText
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
                    String errorMessage = "";
                    if (mGoogleApiClient.isConnected() && Geocoder.isPresent()) {
                        boolean hasError = false;
                        try {
                            // Determine whether a Geocoder is available.
                            if (!Geocoder.isPresent()) {
                                Toast.makeText(getActivity(), R.string.no_geocoder_available,
                                        Toast.LENGTH_LONG).show();
                                return;
                            }
                            List<Address> addresses = geocoder.getFromLocation(mSelectedAddress.getLatitude(), mSelectedAddress.getLongitude(), 1);
                            if (addresses != null && addresses.size() >= 1) {
                                mSelectedAddress = addresses.get(0);
                            }
                        } catch (IOException e) {
                            errorMessage = getString(R.string.address_service_not_available);
                            hasError = true;
                            showErrorOnUiThread(getString(R.string.error_service_not_available));
                            Log.e(TAG, errorMessage, e);
                        } catch (IllegalArgumentException e) {
                            errorMessage = getString(R.string.address_invalid_lat_long);
                            Log.e(TAG, errorMessage + ". " +
                                "Latitude = " + mSelectedAddress.getLatitude() +
                                ", Longitude = " +
                                mSelectedAddress.getLongitude(), e);
                            hasError = true;
                            showErrorOnUiThread(getString(R.string.error_service_not_available));
                        }
                        if (!hasError) {
                            getActivity().runOnUiThread(new Runnable() { // When it's done show diag on rendering thread
                                @Override
                                public void run() {
                                    showDialog();
                                }
                            });
                        } else {
                            getActivity().runOnUiThread(new Runnable() { // When it's done show diag on rendering thread
                                @Override
                                public void run() {
                                    if (mSelectedMarker != null && mGoogleMap != null) {
                                        mSelectedMarker.remove();
                                    }
                                }
                            });
                        }
                    }
                    mProcessingUserClick = false;
                }

                private void showErrorOnUiThread(final String errorMsg) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).start();
        }
    }

    private void showDialog() {
        mStringAddress = concatAddressLines(mSelectedAddress);
        iOSStyledDecisionDialog dialog = iOSStyledDecisionDialog.newInstance(getString(R.string.diag_loc_location_noted), mStringAddress.toString() + "\n\n" + getString(R.string.diag_loc_schedule), getString(R.string.yes), getString(R.string.no));
        dialog.setCancelable(false);
        dialog.setTargetFragment(this, 0);
        dialog.show(getFragmentManager(), Constants.LOCATION_NOTED_DIALOG_TAG);
        mProcessingUserClick = false;
    }


    private String concatAddressLines(Address addr) {
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

    // Take focus off edit text make instructions visible hide ListView container
    @Override
    public void onMapClick(LatLng latLng) {
        if (mEdtAddress != null) {
            // Hide soft keyboard
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mEdtAddress.getWindowToken(), 0);
            mEdtAddress.clearFocus();
        }
    }

    // Initially sets the address of the EditText to the users last know location
    public void setAddressText(Address address) {
        if (mEdtAddress != null && address != null) {
            mEdtAddress.setText(extractStringAddress(address, ", "));
        }
    }

    private void addAddressChangeListener() {
        if (mEdtAddress != null) {
            mEdtAddress.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    startIntentService(s.toString(), 3);
                }
            });
        }
    }

    private String extractStringAddress(Address a, String delimiter) {
        boolean first = true;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < a.getMaxAddressLineIndex(); i++) {
            if (first) {
                sb.append(a.getAddressLine(i));
                first = false;
            } else {
                sb.append(delimiter);
                sb.append(a.getAddressLine(i));
            }
        }
        return sb.toString();
    }

    @Override
    public boolean handleMessage(Message msg) {
        if (msg != null && msg.getData() != null) {
            Bundle args = msg.getData();
            String type = args.getString(AddressOpsIntentService.AddressConstants.TYPE_EXTRA);
            ArrayList<Address> addresses = args.getParcelableArrayList(AddressOpsIntentService.AddressConstants.RESULT_DATA_KEY);

            // Null check
            if (addresses == null || addresses.size() < 1) {
                return false;
            }

            if (type.equalsIgnoreCase(AddressOpsIntentService.AddressConstants.TYPE_REVERSE_GEOCODE)) { // Long click show dialog
                setAddressText(addresses.get(0));
            } else if (type.equalsIgnoreCase(AddressOpsIntentService.AddressConstants.TYPE_GEOCODE)) {
                refreshListView(addresses);
            }
        }
        return false;
    }

    private void refreshListView(List<Address> addressList) {
        if (!isAdded() || getActivity() == null) {
            return;
        }
        List<AddressItem> addressItems = new ArrayList<>();
        for (Address a : addressList) {
            AddressItem item = new AddressItem();
            item.setAddressString(extractStringAddress(a, "\n"));
            item.setAddress(a);
            addressItems.add(item);
        }

        mAddressListAdapter = new ArrayAdapter<>(getActivity(), R.layout.lv_item_addr, addressItems);


        if (mLvAddresses != null) {
            mLvAddresses.setAdapter(mAddressListAdapter);
        }
    }


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void initCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "App lacks permissions, attempting authorization request...");
            ActivityCompat.requestPermissions(getActivity(), new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, getResources().getInteger(R.integer.request_code_location_permision));
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mCurrentLocation = location;
                if (mCurrentLocation == null) {
                    Log.e(TAG, "Error location should not be null");
                    return;
                }
                LatLng currentCoordinates = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
                CameraUpdate point = CameraUpdateFactory.newLatLngZoom(currentCoordinates, 150f);
                mGoogleMap.animateCamera(point, 1000, null);
                mCurrentMarkerOptions = new MarkerOptions().position(currentCoordinates);
                addMarker(mCurrentMarkerOptions);
                new AsyncCaller().execute(); // Paint all previously geotagged loactions
                // Determine whether a Geocoder is available.
                if (!Geocoder.isPresent()) {
                    Toast.makeText(getActivity(), R.string.no_geocoder_available,
                            Toast.LENGTH_LONG).show();
                    return;
                }
                // startIntentService(); Uncomment to have the user's current address loaded into the EditText
                UiUtils.dismissProgressDialog();
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            }
        });
    }

    private Marker addMarker(MarkerOptions position) {
        return mGoogleMap.addMarker(position);
    }

    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }


    /**
     * Check if the device's location settings are adequate for the app's needs using the
     * {@link com.google.android.gms.location.SettingsApi#checkLocationSettings(GoogleApiClient,
     * LocationSettingsRequest)} method, with the results provided through a {@code PendingResult}.
     */
    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );
        final SetLocationFragment callback = this;
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        initCurrentLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to" +
                                " upgrade location settings ");
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            ((MainActivity) getActivity()).setLocationFragmentCallback(callback);
                            status.startResolutionForResult(getActivity(), MainActivity.REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog " +
                                "not created.");
                        OkDialog diag = OkDialog.newInstance("\n"+getString(R.string.diag_settings_change_unavailable)+"\n");
                        diag.show(getActivity().getFragmentManager(), "diag");
                        UiUtils.dismissProgressDialog();
                        break;
                }
            }
        });
    }



    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
        if (mSlideDownAnimation) {
            return AnimatorInflater.loadAnimator(getActivity(), R.animator.oa_slide_down);
        } else if (Common.mDisableWhatsNewAnim) {
            return AnimatorInflater.loadAnimator(getActivity(), R.animator.oa_instant_disappear);
        } else {
            return super.onCreateAnimator(transit, enter, nextAnim);
        }
    }

    @Override
    public void onDialogDismissed(Bundle args) {
        if (args.containsKey(iOSStyledDecisionDialog.BUTTON_LEFT_CALLBACK_KEY)) {
            createRecurringNotification();
            fireNavSequence(false);
        } else if (mGoogleMap != null && mSelectedMarker != null) {
            mSelectedMarker.remove();
        }
    }

    private void fireNavSequence(boolean buildFromExisting) {
        if (getArguments() != null) {
            Bundle chooseTipArgs = new Bundle();
            chooseTipArgs.putBoolean(UserChooseTipFragment.BUILD_FROM_EXISTING_KEY, buildFromExisting);
            String navSequenceType = getArguments().getString(UserChooseTipFragment.SCREEN_SEQUENCE_EXTRA_KEY, "");

            // If this screen is coming from Craving or Slip screen
            if (navSequenceType.equals(UserChooseTipFragment.SEQUENCE_TYPE_SLIP)
                    || navSequenceType.equals(UserChooseTipFragment.SEQUENCE_TYPE_CRAVING)) {
                if (mLocationEventListener != null) {
                    mLocationEventListener.locationSelected(mRecurringNotification, buildFromExisting);
                    getActivity().getFragmentManager().popBackStack();
                }
            }

            // If UserChooseTipFragment is not a part of the backstack currently... WhatsNew Sequence
            else if (UserChooseTipFragment.SEQUENCE_TYPE_WHATS_NEW.equals(navSequenceType)) {
                chooseTipArgs.putBoolean(UserChooseTipFragment.GEOFENCE_KEY, true);
                chooseTipArgs.putString(UserChooseTipFragment.SCREEN_SEQUENCE_EXTRA_KEY, UserChooseTipFragment.SEQUENCE_TYPE_WHATS_NEW);
                UserChooseTipFragment geoTipFrag = UserChooseTipFragment.newInstanceWithRecurringNotification(mRecurringNotification);
                geoTipFrag.setArguments(chooseTipArgs);

                mCallbacks.onAnimatedNavigationAction(geoTipFrag,
                        UserChooseTipFragment.TAG,
                        R.animator.card_flip_right_in,
                        R.animator.card_flip_right_out,
                        R.animator.card_flip_left_in,
                        R.animator.card_flip_left_out,
                        true);
            }

            else if (UserChooseTipFragment.SEQUENCE_TYPE_SETTINGS.equals(navSequenceType)) {
                chooseTipArgs.putBoolean(UserChooseTipFragment.GEOFENCE_KEY, true);
                chooseTipArgs.putString(UserChooseTipFragment.SCREEN_SEQUENCE_EXTRA_KEY, UserChooseTipFragment.SEQUENCE_TYPE_SETTINGS);
                UserChooseTipFragment geoTipFrag = UserChooseTipFragment.newInstanceWithRecurringNotification(mRecurringNotification);
                geoTipFrag.setArguments(chooseTipArgs);

                mCallbacks.onAnimatedNavigationAction(geoTipFrag,
                        UserChooseTipFragment.TAG,
                        R.animator.oa_slide_left_in,
                        R.animator.oa_slide_left_out,
                        R.animator.oa_slide_right_in,
                        R.animator.oa_slide_right_out,
                        true);
            }
        }
    }

    /**
     * Helper makes the RecurringNotification, GeoTag, and Notification for {@link com.mmgct.quitguide2.views.notifications.service.NotificationService}
     */
    private void createRecurringNotification() {
        if (mSelectedAddress != null) {
            mRecurringNotification = new RecurringNotification();
            mRecurringNotification.setActive(true); // Active state
            // Geotag
            GeoTag geoTag = new GeoTag(mSelectedAddress.getLatitude(), mSelectedAddress.getLongitude(), mStringAddress, System.currentTimeMillis());
            mRecurringNotification.setGeoTag(geoTag);
            // Notification
            Notification notification = Notification.newNotIngestedInstance();  // We use this Notification object's id as
            notification.setOpenToScreen(OpenToScreenNavigation.TIP);           // request code for geofence PendingIntent
            mRecurringNotification.setNotification(notification);
            notification.setRecurringNotification(mRecurringNotification);
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        int index = mSavedLocations.indexOf(new MarkerWrapper(marker, -1)); // Doesn't matter what id is checks base on marker = marker
        if (index >= 0 && index < mSavedLocations.size()) {
            MarkerWrapper markerWrapper = mSavedLocations.get(index);
            mRecurringNotification = DbManager.getInstance().getRecurringNotificationById(markerWrapper.reccuringNotificationId);
            fireNavSequence(true);
        }
    }

    private class AsyncCaller extends AsyncTask<Void, Void, List<RecurringNotification>> {



        @Override
        protected List<RecurringNotification> doInBackground(Void... params) {
            List<RecurringNotification> geoFences = DbManager.getInstance().getRecurringGeofenceNotifications(); // Get all RecurringNotifications with geofences to place on map
            return geoFences;
        }

        @Override
        protected void onPostExecute(List<RecurringNotification> geoFences) {
            if (isAdded()) {
                super.onPostExecute(geoFences);
                if (mGoogleMap != null && geoFences != null) {
                    for (RecurringNotification rn : geoFences) { // Convert RecurringNotifications to map markers
                        if (rn.getGeoTag() != null) {
                            GeoTag gt = rn.getGeoTag();
                            Tip tip = rn.getTip();
                            String address = gt.getAddress();
                            int indexOfFirstNL = address.indexOf('\n');
                            if (indexOfFirstNL != -1) {
                                address = address.substring(0, indexOfFirstNL);
                            }
                            MarkerOptions mo = new MarkerOptions()
                                    .position(new LatLng(gt.getLat(), gt.getLon()))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_black))
                                    .title(address)
                                    .snippet(tip != null ? tip.getContent() : getString(R.string.edit_my_message));

                            mSavedLocations.add(new MarkerWrapper(mGoogleMap.addMarker(mo), rn.getId()));
                        }
                    }
                    Log.i(TAG, "Marked all user locations");
                }
            }
        }
    }

    public OnAnimationActionListener getOnAnimationActionListener() {
        return mOnAnimationActionListener;
    }

    public void setOnAnimationActionListener(OnAnimationActionListener mOnAnimationActionListener) {
        this.mOnAnimationActionListener = mOnAnimationActionListener;
    }

    public UserChooseTipFragment.SetLocationEventListener getLocationEventListener() {
        return mLocationEventListener;
    }

    public void setLocationEventListener(UserChooseTipFragment.SetLocationEventListener mSetLocationEventListener) {
        this.mLocationEventListener = mSetLocationEventListener;
    }

    /**
     * Wraps marker and recurring notification id
     */
    private class MarkerWrapper {

        private Marker mMarker;
        private int reccuringNotificationId;

        public MarkerWrapper(Marker mMarker, int reccuringNotificationId) {
            this.mMarker = mMarker;
            this.reccuringNotificationId = reccuringNotificationId;
        }

        public Marker getMarker() {
            return mMarker;
        }

        public void setMarker(Marker mMarker) {
            this.mMarker = mMarker;
        }

        public int getReccuringNotificationId() {
            return reccuringNotificationId;
        }

        public void setReccuringNotificationId(int reccuringNotificationId) {
            this.reccuringNotificationId = reccuringNotificationId;
        }

        // They are equal if marker = marker
        @Override
        public boolean equals(Object o) {
            boolean equal = false;
            if (o == this) {
                equal = true;
            }
            else if (o instanceof MarkerWrapper) {
                MarkerWrapper wrapperToCheck = (MarkerWrapper) o;
                if (wrapperToCheck.getMarker().equals(this.getMarker())) {
                    equal = true;
                }
            }
            return equal;
        }
    }
}
