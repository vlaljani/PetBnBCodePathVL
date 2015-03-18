package com.codepath.petbnbcodepath.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.codepath.petbnbcodepath.R;
import com.codepath.petbnbcodepath.adapters.LandingPageAdapter;
import com.codepath.petbnbcodepath.adapters.PlacesAutoCompleteAdapter;
import com.codepath.petbnbcodepath.helpers.Constants;
import com.codepath.petbnbcodepath.models.Listing;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vibhalaljani on 3/17/15.
 */
public class LandingPageFragment extends Fragment {
    private static final String TAG = "LANDINGPAGEFRAGMENT";

    private ListView lvLandingPage;
    private ArrayList<Listing> sitterArrayList;
    private ArrayAdapter<Listing> sitterArrayAdapter;
    private AutoCompleteTextView etSearch;

    private ParseGeoPoint currLatLng;

    private OnLandingPageListener listener;

    public static LandingPageFragment newInstance(Double latitude, Double longitude) {
        LandingPageFragment landingPageFragment = new LandingPageFragment();
        Bundle args = new Bundle();

        args.putDouble(Constants.latitude, latitude);
        args.putDouble(Constants.longitude, longitude);
        landingPageFragment.setArguments(args);

        return landingPageFragment;
    }

    public interface OnLandingPageListener {
        public void onEtQuerySubmit(String query);
        public void onlvLandingPageItemClick(double latitude, double longitude);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnLandingPageListener) {
            listener = (OnLandingPageListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement MyListFragment.OnItemSelectedListener");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_landing_page, container, false);

        setupViews(view, inflater);

        return view;
    }


    // Creation lifecycle event
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void setupViews(View view, LayoutInflater inflater) {

        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int targetWidth = size.x;

        View landingPageView = inflater.inflate(R.layout.landing_page_header, null);
        ImageView ivBg = (ImageView) landingPageView.findViewById(R.id.ivBg);
        etSearch = (AutoCompleteTextView) landingPageView.findViewById(R.id.etSearch);
        etSearch.setAdapter(new PlacesAutoCompleteAdapter(getActivity(), R.layout.list_item));

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

        lvLandingPage = (ListView) view.findViewById(R.id.lvLandingPage);
        lvLandingPage.addHeaderView(landingPageView, null, false);
        sitterArrayList = new ArrayList<>();
        sitterArrayAdapter = new LandingPageAdapter(getActivity(), sitterArrayList);
        lvLandingPage.setAdapter(sitterArrayAdapter);

        currLatLng = new ParseGeoPoint(getArguments().getDouble(Constants.latitude),
                                       getArguments().getDouble(Constants.longitude));

        setupViewListeners();

        getPetVacayListingData();
    }

    public void getPetVacayListingData() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.petVacayListingTable);
        query.whereWithinMiles(Constants.listingLatlngKey, currLatLng, Constants.whereWithinMiles);
        query.include(Constants.sitterIdKey);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> listingList, ParseException e) {
                if (e == null) {
                    sitterArrayList.addAll(Listing.fromParseObjectList(listingList));
                    sitterArrayAdapter.notifyDataSetChanged();
                } else {
                    Log.e(TAG, "Error: " + e.getMessage());

                    Toast.makeText(getActivity(),
                            getResources().getString(R.string.generic_error),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void setupViewListeners() {
        etSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String query = (String) parent.getItemAtPosition(position);
                listener.onEtQuerySubmit(query);
            }
        });

        lvLandingPage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Listing currListing = (Listing) parent.getItemAtPosition(position);
                if (currListing.getLatLng() != null) {
                    listener.onlvLandingPageItemClick(currListing.getLatLng().getLatitude(),
                                                      currListing.getLatLng().getLongitude());
                } else {
                    listener.onlvLandingPageItemClick(currLatLng.getLatitude(),
                                                      currLatLng.getLongitude());
                }

            }
        });
    }


}
