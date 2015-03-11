package com.codepath.petbnbcodepath.models;

import android.util.Log;

import com.codepath.petbnbcodepath.helpers.Constants;
import com.codepath.petbnbcodepath.net.GoogleMapReverseGeoCodingClient;
import com.parse.CountCallback;
import com.parse.GetCallback;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vibhalaljani on 3/8/15.
 *
 * This is a model class for our listing schema. It is used for the landing page and the
 * swiping fragments below the map.
 */
public class Listing {

    private static final String TAG = "LISTING";

    // Stores the actual latitude, longitude because that's what we pass around
    private ParseGeoPoint latLng;

    // Stores the result of the reverse geocoding - currently just the city and state
    private String cityState;
    private String first_name;
    private String last_name;
    private String coverPictureUrl;

    private int cost;
    private int numReviews;

    private Review firstReview;

    private GoogleMapReverseGeoCodingClient client;

    public Review getFirstReview() {
        return firstReview;
    }

    public String getCityState() {
        return cityState;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getCoverPictureUrl() {
        return coverPictureUrl;
    }

    public int getNumReviews() {
        return numReviews;
    }

    public int getCost() {
        return cost;
    }

    public ParseGeoPoint getLatLng() {
        return latLng;
    }

    public static ArrayList<Listing> fromParseObjectList (List<ParseObject> listingList) {
        ArrayList<Listing> nearbyListings = new ArrayList<>();
        for (int i = 0; i < listingList.size(); i++) {
            nearbyListings.add(Listing.fromParseObject(listingList.get(i)));
        }
        return nearbyListings;
    }

    public static Listing fromParseObject(ParseObject listing) {
        final Listing currListing = new Listing();

        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.petVacayReviewTable);
        query.whereEqualTo(Constants.listingIdKey, listing);
        query.countInBackground(new CountCallback() {
            public void done(int count, ParseException e) {
                if (e == null) {
                    currListing.numReviews = count;
                } else {
                    Log.i(TAG, "Error: " + e.getMessage());
                }
            }
        });

        // This query gets the first review for this particular listener
        ParseQuery<ParseObject> query1 = ParseQuery.getQuery(Constants.petVacayReviewTable);
        query1.whereEqualTo(Constants.listingIdKey, listing);
        query1.include(Constants.reviewerIdKey);
        query1.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (parseObject == null) {
                    Log.d(TAG, "Error: " + e.toString());
                } else {
                    currListing.firstReview = new Review();
                    currListing.firstReview.setReview_description(parseObject.
                                                               getString(Constants.descriptionKey));
                    currListing.firstReview.setReviewer_first_name(parseObject.
                         getParseObject(Constants.reviewerIdKey).getString(Constants.firstNameKey));
                }

            }
        });

        currListing.cost = listing.getInt(Constants.listingCostKey);
        currListing.latLng = listing.getParseGeoPoint(Constants.listingLatlngKey);
        currListing.client = new GoogleMapReverseGeoCodingClient();
        currListing.client.getCity(currListing.latLng);

        // listener pattern because the reverse geocoding call that returns the city is an
        // asynchronous call. So we set the city only when a value is returned
        currListing.client.setCityListener(new GoogleMapReverseGeoCodingClient.GeoCodingListener() {
            @Override
            public void onCityLoaded(String city) {
                currListing.cityState = city;

            }
        });

        ParseObject sitter = listing.getParseObject(Constants.sitterIdKey);
        currListing.first_name = sitter.getString(Constants.firstNameKey);
        currListing.last_name = sitter.getString(Constants.lastNameKey);
        currListing.coverPictureUrl = sitter.getParseFile(Constants.coverPictureKey).getUrl();
        return currListing;
    }
}
