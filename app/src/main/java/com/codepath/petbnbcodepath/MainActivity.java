package com.codepath.petbnbcodepath;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.codepath.petbnbcodepath.adapters.LandingPageAdapter;
import com.codepath.petbnbcodepath.adapters.PlacesAutoCompleteAdapter;
import com.codepath.petbnbcodepath.helpers.Constants;
import com.codepath.petbnbcodepath.models.Listing;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.parse.FindCallback;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseException;


import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements
                                                    GoogleApiClient.ConnectionCallbacks,
                                                    LocationListener,
                                                    GoogleApiClient.OnConnectionFailedListener{
    private static final String TAG = "MAINACTIVITY";

    private ListView lvLandingPage;
    private ArrayList<Listing> sitterArrayList;
    private ArrayAdapter<Listing> sitterArrayAdapter;
    private AutoCompleteTextView etSearch;

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
        getPetVacayListingData();
    }

    private void setupViews() {

        // Create the location client to start receiving updates
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int targetWidth = size.x;

        View landingPageView = LayoutInflater.from(this).inflate(R.layout.landing_page_header, null);
        ImageView ivBg = (ImageView) landingPageView.findViewById(R.id.ivBg);
        etSearch = (AutoCompleteTextView) landingPageView.findViewById(R.id.etSearch);
        etSearch.setAdapter(new PlacesAutoCompleteAdapter(this, R.layout.list_item));

        Drawable headerDrawable = getResources().getDrawable(R.drawable.landingpagebg);
        int currWidth = headerDrawable.getIntrinsicWidth();
        int currHeight = headerDrawable.getIntrinsicHeight();
        int origAspectRatio = currWidth / currHeight;
        int targetHeight = targetWidth / origAspectRatio;

        // Load a bitmap from the drawable folder
        Bitmap bMap = BitmapFactory.decodeResource(getResources(), R.drawable.landingpagebg);
        // Resize the bitmap to 150x100 (width x height)
        Bitmap bMapScaled = Bitmap.createScaledBitmap(bMap, targetWidth, targetHeight, true);
        // Loads the resized Bitmap into an ImageView
        ivBg.setImageBitmap(bMapScaled);

        lvLandingPage = (ListView) findViewById(R.id.lvLandingPage);
        lvLandingPage.addHeaderView(landingPageView, null, false);
        sitterArrayList = new ArrayList<>();
        sitterArrayAdapter = new LandingPageAdapter(this, sitterArrayList);
        lvLandingPage.setAdapter(sitterArrayAdapter);

        setupViewListeners();
    }

    public void setupViewListeners() {
        etSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String query = (String) parent.getItemAtPosition(position);
                Toast.makeText(MainActivity.this, query, Toast.LENGTH_SHORT).show();
            }
        });

        lvLandingPage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Listing currListing = (Listing) parent.getItemAtPosition(position);
                if (currListing.getLatLng() != null) {
                    Toast.makeText(MainActivity.this,
                            String.valueOf(currListing.getLatLng().getLatitude()) + ", " +
                            String.valueOf(currListing.getLatLng().getLongitude()),
                            Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(MainActivity.this, MapActivity.class);
                    i.putExtra(Constants.latitude, currListing.getLatLng().getLatitude());
                    i.putExtra(Constants.longitude, currListing.getLatLng().getLongitude());
                    startActivity(i);
                } else {
                    Intent i = new Intent(MainActivity.this, MapActivity.class);
                    i.putExtra(Constants.latitude, currLatLng.getLatitude());
                    i.putExtra(Constants.longitude, currLatLng.getLongitude());
                    startActivity(i);
                    Toast.makeText(MainActivity.this,
                            "current gps coordinates",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
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
        Toast.makeText(this, Double.toString(mCurrentLocation.getLatitude()) + "," +
                Double.toString(mCurrentLocation.getLongitude()), Toast.LENGTH_LONG).show();

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

    public void getPetVacayListingData() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.petVacayListingTable);
        //Log.i(TAG, "" + currLatLng.getLatitude() + "," + currLatLng.getLongitude());
        //query.whereWithinMiles(Constants.listingLatlngKey, currLatLng, Constants.whereWithinMiles);
        query.include(Constants.sitterIdKey);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> listingList, ParseException e) {
                if (e == null) {
                    sitterArrayList.addAll(Listing.fromParseObjectList(listingList));
                    sitterArrayAdapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "Error: " + e.getMessage());

                    Toast.makeText(MainActivity.this,
                            getResources().getString(R.string.generic_error),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
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
}
