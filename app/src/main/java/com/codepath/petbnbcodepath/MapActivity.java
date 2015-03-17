package com.codepath.petbnbcodepath;

import android.content.Intent;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.codepath.petbnbcodepath.fragments.ListingSummaryFragment;
import com.codepath.petbnbcodepath.helpers.Constants;
import com.codepath.petbnbcodepath.models.Listing;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;


public class MapActivity extends ActionBarActivity
                         implements GoogleMap.OnMarkerClickListener, ListingSummaryFragment.OnBtnDetsListener {

    private static final String TAG = "MAPACTIVITY";

    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private ArrayList<Listing> nearbyListings;
    private MyListingSummaryAdapter listingSummaryAdapter;
    private ViewPager vpPager;
    private ArrayList<Marker> markers;

    private IconGenerator iconFactoryTeal;
    private IconGenerator iconFactoryRed;


    public void onBtnDetsClick(String coverPictureUrl, String firstName, String lastName,
                               int numReviews, int cost ) {
        Intent i = new Intent(MapActivity.this, BookingDetailsActivity.class);
        i.putExtra(Constants.coverPictureKey, coverPictureUrl);
        i.putExtra(Constants.firstNameKey, firstName);
        i.putExtra(Constants.lastNameKey, lastName);
        i.putExtra(Constants.numReviewsKey, numReviews);
        i.putExtra(Constants.listingCostKey, cost);
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        vpPager = (ViewPager) findViewById(R.id.vpPager);

        // Set the view pager adapter for the pager
        nearbyListings = new ArrayList<>();
        markers = new ArrayList<>();
        final double latitude = getIntent().getDoubleExtra("latitude", 0);
        final double longitude = getIntent().getDoubleExtra("longitude", 0);

        mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap map) {
                    loadMap(map);
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude)
                            ,Constants.zoom));
                    getNearbyListings(latitude, longitude);
                }
            });
        } else {
            Toast.makeText(this, "Error - Map Fragment was null!!", Toast.LENGTH_SHORT).show();
        }

        iconFactoryTeal = new IconGenerator(MapActivity.this);
        iconFactoryTeal.setColor(getResources().getColor(R.color.theme_teal));
        iconFactoryTeal.setContentPadding(0, 0, 0, 0);
        iconFactoryTeal.setTextAppearance(MapActivity.this, R.style.CodeFont);

        iconFactoryRed = new IconGenerator(MapActivity.this);
        iconFactoryRed.setColor(getResources().getColor(R.color.red));
        iconFactoryRed.setContentPadding(0, 0, 0, 0);
        iconFactoryRed.setTextAppearance(MapActivity.this, R.style.CodeFont);

    }

    public void onNearbyListingsLoaded() {
        for (int i = 0; i < nearbyListings.size(); i++) {

            // get the things you need to make the markers
            double currListLat = nearbyListings.get(i).getLatLng().getLatitude();
            double currListLong = nearbyListings.get(i).getLatLng().getLongitude();
            int currListCost = nearbyListings.get(i).getCost();

            // Add marker to map
            MarkerOptions currMarker;
            if (i == 0) {
                currMarker = new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromBitmap(iconFactoryRed.
                                makeIcon(Constants.currencySymbol
                                        + String.valueOf(currListCost))))
                        .position(new LatLng(currListLat, currListLong))
                        .anchor(iconFactoryRed.getAnchorU(), iconFactoryRed.getAnchorV());
            } else {
                currMarker = new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromBitmap(iconFactoryTeal.
                                makeIcon(Constants.currencySymbol
                                        + String.valueOf(currListCost))))
                        .position(new LatLng(currListLat, currListLong))
                        .anchor(iconFactoryTeal.getAnchorU(), iconFactoryTeal.getAnchorV());
            }
            markers.add(map.addMarker(currMarker));


        }

        // Also set fragment pager adapter for the mini fragment below
        listingSummaryAdapter = new MyListingSummaryAdapter(getSupportFragmentManager(),
                nearbyListings.size(),
                nearbyListings);
        vpPager.setAdapter(listingSummaryAdapter);
        vpPager.setOnPageChangeListener(new OnPageChangeListener() {

            // This method will be invoked when a new page becomes selected.
            @Override
            public void onPageSelected(int position) {
                int currListCost = nearbyListings.get(position).getCost();
                double currListLat = nearbyListings.get(position).getLatLng().getLatitude();
                double currListLong = nearbyListings.get(position).getLatLng().getLongitude();
                MarkerOptions newMarker = new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromBitmap(iconFactoryRed.
                                makeIcon(Constants.currencySymbol
                                        + String.valueOf(currListCost))))
                        .position(new LatLng(currListLat, currListLong))
                        .anchor(iconFactoryRed.getAnchorU(), iconFactoryRed.getAnchorV());

                markers.get(position).remove();
                markers.set(position, map.addMarker(newMarker));

                if (position < nearbyListings.size() - 1) {
                    currListCost = nearbyListings.get(position + 1).getCost();
                    currListLat = nearbyListings.get(position + 1).getLatLng().getLatitude();
                    currListLong = nearbyListings.get(position + 1).getLatLng().getLongitude();
                    newMarker = new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromBitmap(iconFactoryTeal.
                                    makeIcon(Constants.currencySymbol
                                            + String.valueOf(currListCost))))
                            .position(new LatLng(currListLat, currListLong))
                            .anchor(iconFactoryTeal.getAnchorU(), iconFactoryTeal.getAnchorV());
                    markers.get(position + 1).remove();
                    markers.set(position + 1, map.addMarker(newMarker));
                }

                if (position > 0) {
                    currListCost = nearbyListings.get(position - 1).getCost();
                    currListLat = nearbyListings.get(position - 1).getLatLng().getLatitude();
                    currListLong = nearbyListings.get(position - 1).getLatLng().getLongitude();
                    newMarker = new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromBitmap(iconFactoryTeal.
                                    makeIcon(Constants.currencySymbol
                                            + String.valueOf(currListCost))))
                            .position(new LatLng(currListLat, currListLong))
                            .anchor(iconFactoryTeal.getAnchorU(), iconFactoryTeal.getAnchorV());
                    markers.get(position - 1).remove();
                    markers.set(position - 1, map.addMarker(newMarker));
                }
            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // Code goes here
            }

            // Called when the scroll state changes:
            // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {
                // Code goes here
            }
        });
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        for (int i = 0; i < markers.size(); i++) {
            if (marker.equals(markers.get(i))) {
                vpPager.setCurrentItem(i);
                return true;
            }
        }
        return false;
    }

    private void getNearbyListings(double latitude, double longitude) {
        String sitterIdKey = "sitterId";
        ParseGeoPoint currPoint = new ParseGeoPoint(latitude, longitude);
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.petVacayListingTable);
        query.whereWithinMiles(Constants.listingLatlngKey, currPoint, Constants.whereWithinMiles);
        query.include(sitterIdKey);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> listingList, ParseException e) {
                if (e == null) {
                    Log.d(TAG, "Retrieved " + listingList.size() + " listings");
                    nearbyListings.addAll(Listing.fromParseObjectList(listingList));
                    onNearbyListingsLoaded();
                } else {
                    Log.e("TAG", "Error: " + e.getMessage());

                    Toast.makeText(MapActivity.this,
                            getResources().getString(R.string.generic_error),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }



    protected void loadMap(GoogleMap googleMap) {
        map = googleMap;
        if (map != null) {
            // Map is ready
            Toast.makeText(this, "Map Fragment was loaded properly!", Toast.LENGTH_SHORT).show();

            /*map.setMyLocationEnabled(true);
            map.setOnMapLongClickListener(this);

            // Now that map has loaded, let's get our location!
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();

            connectClient();*/
        } else {
            Toast.makeText(this, "Error - Map was null!!", Toast.LENGTH_SHORT).show();
        }
        map.setOnMarkerClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
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

    public void onSendPush(View view) {
        ParsePush push = new ParsePush();
        push.setChannel("");
        push.setMessage("You have a new pet!");
        push.sendInBackground();
    }

    public static class MyListingSummaryAdapter extends FragmentPagerAdapter {
        private int NUM_ITEMS;
        private ArrayList<Listing> nearbyListings;


        public MyListingSummaryAdapter(FragmentManager fragmentManager, int num_items,
                                       ArrayList<Listing> nearbyListings) {
            super(fragmentManager);
            NUM_ITEMS = num_items;
            this.nearbyListings = nearbyListings;
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            Log.i(TAG, "frag adapter : " + position);
            return ListingSummaryFragment.newInstance(this.nearbyListings.get(position));

        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

    }

}
