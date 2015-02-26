package com.yahoo.americancurry.petpeeve.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.SeekBar;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.yahoo.americancurry.petpeeve.R;
import com.yahoo.americancurry.petpeeve.fragments.ComposeFragment;
import com.yahoo.americancurry.petpeeve.model.Pin;
import com.yahoo.americancurry.petpeeve.model.PinLocal;
import com.yahoo.americancurry.petpeeve.utils.GoogleMapsUtil;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MapPinActivity extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerDragListener, SeekBar.OnSeekBarChangeListener, GoogleMap.OnMarkerClickListener {

    public static final int DEFAULT_RADIUS = 75;
    private int currentRadius = DEFAULT_RADIUS;
    public static final int RADIUS_COLOR = 0x6FA1B7EC;
    public static final int DEFAULT_ZOOM = 17;
    /*
     * Define a request code to send to Google Play services This code is
     * returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LatLng latLngForAddress;
    private long UPDATE_INTERVAL = 60000;  /* 60 secs */
    private long FASTEST_INTERVAL = 60000; /* 5 secs */
    private Circle mapCircle;
    private SeekBar seekBarRadius;
    private Marker currentMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_pin);
        setUpActionBar();

        mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    loadMap(map);
                    map.setOnMapLongClickListener(MapPinActivity.this);
                    map.setOnMarkerDragListener(MapPinActivity.this);
                    map.setOnMarkerClickListener(MapPinActivity.this);
                }
            });
        } else {
            Toast.makeText(this, "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
        }

        seekBarRadius = (SeekBar) findViewById(R.id.seekBarRadius);
        seekBarRadius.setOnSeekBarChangeListener(this);

    }

    protected void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            // Map is ready
            Log.d("DEBUG", "Map Fragment was loaded properly!");
            map.setMyLocationEnabled(true);
            map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            map.getUiSettings().setMapToolbarEnabled(false);

            // Now that map has loaded, let's get our location!
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();

            connectClient();
        } else {
            Toast.makeText(this, "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
    }

    protected void connectClient() {
        // Connect the client.
        if (isGooglePlayServicesAvailable() && mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    /*
     * Called when the Activity becomes visible.
    */
    @Override
    protected void onStart() {
        super.onStart();
        connectClient();
    }

    /*
     * Called when the Activity is no longer visible.
	 */
    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    /*
     * Handle results returned to the FragmentActivity by Google Play services
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Decide what to do based on the original request code
        switch (requestCode) {

            case CONNECTION_FAILURE_RESOLUTION_REQUEST:
            /*
             * If the result code is Activity.RESULT_OK, try to connect again
			 */
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        mGoogleApiClient.connect();
                        break;
                }

        }
    }

    private boolean isGooglePlayServicesAvailable() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates", "Google Play services is available.");
            return true;
        } else {
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment = new ErrorDialogFragment();
                errorFragment.setDialog(errorDialog);
                errorFragment.show(getSupportFragmentManager(), "Location Updates");
            }

            return false;
        }
    }

    /*
     * Called by Location Services when the request to connect the client
     * finishes successfully. At this point, you can request the current
     * location or start periodic updates
     */
    @Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location != null) {
            Log.d("DEBUG", "GPS location was found!");
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM);
            map.animateCamera(cameraUpdate);
            startLocationUpdates();
        } else {
            Toast.makeText(this, "Current location was null, enable GPS on emulator!", Toast.LENGTH_SHORT).show();
        }
    }

    protected void startLocationUpdates() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
    }

    public void onLocationChanged(Location location) {
        // Report to the UI that the location was updated
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Log.d("DEBUG", msg);
        //Generate a notification here if a qualifying pin is present for this user

        List<PinLocal> pinListForLocation = findQualifyingPins(location);

        //Originate notifications for all pins in the list

        for (PinLocal pin : pinListForLocation) {

            createNotification(new Random().nextInt(10000), R.drawable.ic_launcher, "Pin for you", pin.getText());
        }


    }

    private PendingIntent createPendingIntentForNotificationAction() {

        Intent intent = new Intent(this, DetailedPinActivity.class);

        int requestID = (int) System.currentTimeMillis();
        int flags = PendingIntent.FLAG_CANCEL_CURRENT;
        PendingIntent pIntent = PendingIntent.getActivity(this, requestID, intent, flags);
        return pIntent;


    }

    private List<PinLocal> findQualifyingPins(Location location) {


        List<PinLocal> validPinList = new ArrayList<PinLocal>(5);
        List<PinLocal> pinList = null;
        try{
       pinList = new Select()
                .from(PinLocal.class)
                .limit(25).execute();
     }
        catch(SQLiteException e){
            return validPinList;
        }
        for (PinLocal pin : pinList) {

            Double pinLocalLatitude = pin.getLocationCentreLatitude();
            Double pinLocalLongitude = pin.getLocationCentreLongitude();
            Location pinLocation = new Location("");
            if(pinLocalLongitude!=null)
            pinLocation.setLongitude(pinLocalLongitude);
            if(pinLocalLatitude !=null)
            pinLocation.setLatitude(pinLocalLatitude);
            float distance = pinLocation.distanceTo(location);
            if (distance < pin.getLocationRadius()) {

                validPinList.add(pin);
            }
        }
        return validPinList;
    }

    /*
     * Called by Location Services if the connection to the location client
     * drops because of an error.
     */
    @Override
    public void onConnectionSuspended(int i) {
        if (i == CAUSE_SERVICE_DISCONNECTED) {
            Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
        } else if (i == CAUSE_NETWORK_LOST) {
            Toast.makeText(this, "Network lost. Please re-connect.", Toast.LENGTH_SHORT).show();
        }
    }

    /*
     * Called by Location Services if the attempt to Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects. If the error
		 * has a resolution, try sending an Intent to start a Google Play
		 * services activity that can resolve error.
		 */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
				 * PendingIntent
				 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry. Location services not available to you", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        mapCircle.setRadius(progress);
        currentRadius = progress;

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (currentMarker != null) {

            Pin pin = new Pin();
            pin.setLocationCentreLatitude(currentMarker.getPosition().latitude);
            pin.setLocationCentreLongitude(currentMarker.getPosition().longitude);
            pin.setLocationRadius(currentRadius);
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            ComposeFragment composeFragment = ComposeFragment.newInstance("Compose Message");
            Bundle dataBundle = new Bundle();
            Bundle bundle = new Bundle();
            bundle.putSerializable("pinInfo", pin);
            composeFragment.setArguments(bundle);
            composeFragment.show(fm, "fragment_compose");
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_map_pin, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {

                map.clear();

                Log.d("DEBUG", "Searching for " + query);

                GoogleMapsUtil.getLocationForAddress(query, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                        try {
                            JSONArray results = response.getJSONArray("results");

                            if (results.length() >= 1) {

                                //Navigate to the first result for simplicity
                                JSONObject firstResult = results.getJSONObject(0).getJSONObject("geometry");

                                if (firstResult != null) {
                                    JSONObject firstLocation = firstResult.getJSONObject("location");
                                    if (firstLocation != null) {

                                        latLngForAddress = new LatLng(firstLocation.getDouble("lat"), firstLocation.getDouble("lng"));
                                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngForAddress, DEFAULT_ZOOM));
                                        Marker marker = map.addMarker(new MarkerOptions()
                                                .position(latLngForAddress).title(query));
                                        marker.showInfoWindow();
                                    }
                                }

                            }

                        } catch (JSONException e) {
                            Toast.makeText(MapPinActivity.this, "Unable to search for location " + query, Toast.LENGTH_SHORT).show();
                        }


                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                    }
                });


                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onMapLongClick(final LatLng point) {

        //clear all markers on the map
        map.clear();

        Marker marker = map.addMarker(new MarkerOptions()
                .position(point).draggable(true));
        dropPinEffect(marker);
        removeMapCircle();
        mapCircle = map.addCircle(new CircleOptions()
                .center(point)
                .radius(DEFAULT_RADIUS).strokeColor(Color.TRANSPARENT).fillColor(RADIUS_COLOR));
        seekBarRadius.setVisibility(View.VISIBLE);


    }

    private void removeMapCircle() {
        if (mapCircle != null) {
            mapCircle.remove();
        }
    }

    private void dropPinEffect(final Marker marker) {

        currentMarker = marker;

        // Handler allows us to repeat a code block after a specified delay
        final android.os.Handler handler = new android.os.Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 1000;

        // Use the bounce interpolator
        final android.view.animation.Interpolator interpolator =
                new BounceInterpolator();

        // Animate marker with a bounce updating its position every 15ms
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                // Calculate t for bounce based on elapsed time
                float t = Math.max(
                        1 - interpolator.getInterpolation((float) elapsed
                                / duration), 0);
                // Set the anchor
                marker.setAnchor(0.5f, 1.0f + 14 * t);

                if (t > 0.0) {
                    // Post this event again 15ms from now.
                    handler.postDelayed(this, 15);
                } else { // done elapsing, show window
                    marker.showInfoWindow();
                }
            }
        });
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

        removeMapCircle();

    }

    @Override
    public void onMarkerDrag(Marker marker) {

        //do nothing

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

        removeMapCircle();
        mapCircle = map.addCircle(new CircleOptions()
                .center(marker.getPosition())
                .radius(DEFAULT_RADIUS).strokeColor(Color.TRANSPARENT).fillColor(RADIUS_COLOR));


    }

    //  createNotification(56, R.drawable.ic_launcher, "New Message",
//      "There is a new message from Bob!");
    private void createNotification(int nId, int iconRes, String title, String body) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(iconRes)
                .setContentTitle(title)
                .setContentText(body).setContentIntent(createPendingIntentForNotificationAction());

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // mId allows you to update the notification later on.
        mNotificationManager.notify(nId, mBuilder.build());

    }

    // Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends DialogFragment {

        // Global field to contain the error dialog
        private Dialog mDialog;

        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }


    }

    private void setUpActionBar() {

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#F07241")));
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.custom_menu_bar, null);
        mActionBar.setCustomView(mCustomView);
        mActionBar.setDisplayShowCustomEnabled(true);
    }
}
