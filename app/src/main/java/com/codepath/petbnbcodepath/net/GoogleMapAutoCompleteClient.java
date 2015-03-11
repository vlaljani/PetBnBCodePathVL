package com.codepath.petbnbcodepath.net;

import android.util.Log;

import com.codepath.petbnbcodepath.helpers.Constants;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by vibhalaljani on 3/7/15.
 *
 * Class to provide the auto-complete for the search bar in the header of the list view.
 *
 * It uses the Google APIs
 */
public class GoogleMapAutoCompleteClient {

    private static final String TAG = "GOOGLEMAPAUTOCOMPLETE";

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";

    private static AsyncHttpClient client = new AsyncHttpClient();

    private ArrayList<String> resultList = null;

    public ArrayList<String> autocomplete(String input) {

        StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
        sb.append("?key=" + Constants.GOOGLE_API_KEY);
        sb.append("&components=country:us");
        sb.append("&sensor=false");
        sb.append("&input=" + input);

        client.get(sb.toString(), null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    // Create a JSON object hierarchy from the results
                    JSONArray predsJsonArray = response.getJSONArray("predictions");

                    // Extract the Place descriptions from the results
                    resultList = new ArrayList(predsJsonArray.length());
                    for (int i = 0; i < predsJsonArray.length(); i++) {
                        resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Cannot process JSON results", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable t, JSONObject e) {
                Log.e(TAG, "Error: " + e.toString());
            }
        });
        return resultList;
    }
}
