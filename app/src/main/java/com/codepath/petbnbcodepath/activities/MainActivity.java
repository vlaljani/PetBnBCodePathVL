package com.codepath.petbnbcodepath.activities;

import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.codepath.petbnbcodepath.R;
import com.codepath.petbnbcodepath.fragments.LandingPageFragment;
import com.codepath.petbnbcodepath.helpers.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.parse.ParseGeoPoint;


public class MainActivity extends ActionBarActivity implements
                                                    GoogleApiClient.ConnectionCallbacks,
                                                    LocationListener,
                                                    GoogleApiClient.OnConnectionFailedListener,
                                                    LandingPageFragment.OnLandingPageListener {
    private static final String TAG = "MAINACTIVITY";

    private FrameLayout frag_landing_page;
    private LandingPageFragment landingPageFragment;

    private GoogleApiClient mGoogleApiClient;

    private long UPDATE_INTERVAL = 5 * 60000;  /* 60 secs */
    //private long FASTEST_INTERVAL = 5000; /* 5 secs */
    private long FASTEST_INTERVAL = 5 * 60000;

    private ParseGeoPoint currLatLng;

    /*
	 * Define a request code to send to Google Play services This code is
	 * returned in Activity.onActivityResult
	 */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupViews();
    }

    private void setupViews() {

        frag_landing_page = (FrameLayout) findViewById(R.id.frag_landing_page);

        // Create the location client to start receiving updates
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();

    }



    protected void onStart() {
        super.onStart();
        // Connect the client.
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        // Disconnecting the client invalidates it.
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    public void onConnected(Bundle dataBundle) {
        Location mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(
                                                                                  mGoogleApiClient);
        Toast.makeText(this, Double.toString(mCurrentLocation.getLatitude()) + "," +
                Double.toString(mCurrentLocation.getLongitude()), Toast.LENGTH_LONG).show();
        currLatLng = new ParseGeoPoint(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        Toast.makeText(this, "new! " + Double.toString(mCurrentLocation.getLatitude()) + "," +
                Double.toString(mCurrentLocation.getLongitude()), Toast.LENGTH_LONG).show();

        landingPageFragment = LandingPageFragment.newInstance(
                                   mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frag_landing_page, landingPageFragment);
        ft.commit();

        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (i == CAUSE_SERVICE_DISCONNECTED) {
            Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
        } else if (i == CAUSE_NETWORK_LOST) {
            Toast.makeText(this, "Network lost. Please re-connect.", Toast.LENGTH_SHORT).show();
        }
    }

    protected void startLocationUpdates() {
        LocationRequest mLocationRequest = new LocationRequest();
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
        currLatLng = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

        landingPageFragment = LandingPageFragment.newInstance(
                location.getLatitude(), location.getLongitude());
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frag_landing_page, landingPageFragment);
        ft.commit();
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

    public void onLoginSignup(View view) {
        Intent i = new Intent(MainActivity.this, LoginSignupActivity.class);
        startActivity(i);
    }

    public void onEtQuerySubmit(String query) {
        Toast.makeText(MainActivity.this, query, Toast.LENGTH_SHORT).show();
    }

    public void onlvLandingPageItemClick(double latitude, double longitude) {
        Intent i = new Intent(MainActivity.this, MapActivity.class);
        i.putExtra(Constants.latitude, currLatLng.getLatitude());
        i.putExtra(Constants.longitude, currLatLng.getLongitude());
        startActivity(i);
    }
}
