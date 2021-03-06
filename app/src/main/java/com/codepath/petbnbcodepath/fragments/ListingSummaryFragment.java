package com.codepath.petbnbcodepath.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.petbnbcodepath.R;
import com.codepath.petbnbcodepath.helpers.Constants;
import com.codepath.petbnbcodepath.models.Listing;
import com.squareup.picasso.Picasso;

import java.util.Currency;


/**
 * Created by vibhalaljani on 3/8/15.
 *
 * This is the fragment below the map that shows the listing summary. This is then used in a
 * view pager to get swipeable fragments.
 */
public class ListingSummaryFragment extends Fragment {
    private static final String TAG = "LISTINGSUMMARYFRAGMENT";

    private ImageView ivCoverPicture;
    private TextView tvSummary;
    private TextView tvNumReviews;
    private TextView tvCost;

    private Button btnDets;

    private OnBtnDetsListener listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof OnBtnDetsListener) {
            listener = (OnBtnDetsListener) activity;
        } else {
            throw new ClassCastException(activity.toString()
                    + " must implement MyListFragment.OnItemSelectedListener");
        }
    }

    public interface OnBtnDetsListener {
        public void onBtnDetsClick(String coverPictureUrl, String firstName, String lastName,
                                   String objectId, int numReviews, int cost);
    }


    // Inflation logic
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_listing, container, false);

        setupViews(view);

        return view;
    }

    private void setupViews(View view) {
        ivCoverPicture = (ImageView) view.findViewById(R.id.ivCoverPicture);
        tvSummary = (TextView) view.findViewById(R.id.tvSummary);
        tvNumReviews = (TextView) view.findViewById(R.id.tvNumReviews);
        tvCost = (TextView) view.findViewById(R.id.tvCost);

        btnDets = (Button) view.findViewById(R.id.btnDets);

        Picasso.with(getActivity()).load(getArguments().getString(Constants.coverPictureKey))
                .into(ivCoverPicture);
        tvSummary.setText(getArguments().getString(Constants.summaryKey));
        int numReviews = getArguments().getInt(Constants.numReviewsKey);
        if (numReviews == 1) {
            tvNumReviews.setText(String.valueOf(getArguments().getInt(Constants.numReviewsKey)) + " " +
                    getResources().getString(R.string.review));
        } else {
            tvNumReviews.setText(String.valueOf(getArguments().getInt(Constants.numReviewsKey)) + " " +
                    getResources().getString(R.string.reviews));
        }
        tvCost.setText(Constants.currencySymbol +
                                  String.valueOf(getArguments().getInt(Constants.listingCostKey)));

        setupViewListeners();
    }

    private void setupViewListeners() {
        btnDets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onBtnDetsClick(getArguments().getString(Constants.coverPictureKey),
                        getArguments().getString(Constants.summaryKey).split(" ")[0],
                        getArguments().getString(Constants.summaryKey).split(" ")[1],
                        getArguments().getString(Constants.objectIdKey),
                        getArguments().getInt(Constants.numReviewsKey),
                        getArguments().getInt(Constants.listingCostKey));
            }
        });

    }

    public static ListingSummaryFragment newInstance(Listing listing) {
        ListingSummaryFragment listingSummaryFragment = new ListingSummaryFragment();

        Bundle args = new Bundle();
        args.putString(Constants.coverPictureKey, listing.getCoverPictureUrl());
        args.putString(Constants.summaryKey, listing.getFirst_name() + " " + listing.getLast_name());
        args.putString(Constants.objectIdKey, listing.getObjectId());
        args.putInt(Constants.numReviewsKey, listing.getNumReviews());
        args.putInt(Constants.listingCostKey, listing.getCost());
        listingSummaryFragment.setArguments(args);

        return listingSummaryFragment;
    }


}
