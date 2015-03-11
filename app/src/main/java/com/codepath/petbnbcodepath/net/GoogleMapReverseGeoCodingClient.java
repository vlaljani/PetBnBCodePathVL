package com.codepath.petbnbcodepath.net;

import android.util.Log;

import com.codepath.petbnbcodepath.helpers.Constants;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.parse.ParseGeoPoint;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by vibhalaljani on 3/7/15.
 *
 * Class to provide reverse geocoding based on latitude and longitude. This only returns the
 * city and state because that's what I need for the landing page.
 *
 * It uses the Google API
 */
public class GoogleMapReverseGeoCodingClient {

    // Listener patter because the city is set asynchronously. So we want to return the data
    // only when the city is set.
    public interface GeoCodingListener {
        public void onCityLoaded(String city);
    }

    private static final String TAG = "GOOGLEMAPREVGEOCODING";

    private static final String GEOCODE_API_BASE = "https://maps.googleapis.com/maps/api/geocode";
    private static final String OUT_JSON = "/json";

    private static AsyncHttpClient client = new AsyncHttpClient();

    private String city;

    private GeoCodingListener listener;

    public void setCityListener(GeoCodingListener listener) {
        this.listener = listener;
    }

    public void getCity(ParseGeoPoint point) {
        String latlng = String.valueOf(point.getLatitude()) + ","
                                                           + String.valueOf(point.getLongitude());
        StringBuilder sb = new StringBuilder(GEOCODE_API_BASE + OUT_JSON);

        sb.append("?key=" + Constants.GOOGLE_API_KEY);
        sb.append("&latlng=" + latlng);
        sb.append("&result_type=sublocality%7Clocality");

        client.get(sb.toString(), null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                String resultsKey = "results";
                String formattedAddressKey = "formatted_address";
                if (response != null) {
                    if (response.optJSONArray(resultsKey) != null) {
                        JSONArray results = null;
                        try {
                            results = response.getJSONArray(resultsKey);
                            if (results.length() > 0) {
                                if (results.get(0) != null && ((JSONObject)results.get(0)).
                                        getString(formattedAddressKey) != null) {
                                    city = ((JSONObject)results.get(0)).
                                                                    getString(formattedAddressKey);
                                    int endIndex = city.lastIndexOf(",");
                                    city = city.substring(0, endIndex);
                                    listener.onCityLoaded(city);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable t, JSONObject e) {
                Log.e(TAG, "Throwable: " + t.toString() + " Error: " + e.toString());
            }
        });
    }
}
