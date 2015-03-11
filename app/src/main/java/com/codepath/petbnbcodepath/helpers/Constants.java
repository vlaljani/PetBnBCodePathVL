package com.codepath.petbnbcodepath.helpers;

import java.util.Currency;
import java.util.Locale;

/**
 * Created by vibhalaljani on 3/7/15.
 */
public class Constants {
    public static final String APPLICATION_ID = "yyCmYRd4b99Jihn7YhGIZYNlgb3NMWxeiIOL0CQw";
    public static final String CLIENT_KEY = "ZFCnvTC39XpM2J0DpwXgfEUmoaItS4UzOZ1OEcmv";
    public static final String petVacayUserTable = "PetVacayUser";
    public static final String petVacayOwnerTable = "PetVacayOwner";
    public static final String petVacayListingTable = "PetVacayListing";
    public static final String petVacayReviewTable = "PetVacayReview";
    public static final int whereWithinMiles = 20;
    public static final int nearbyQueryLimit = 10;
    public static final int zoom = 10;

    public static final String listingCostKey = "cost";
    public static final String listingLatlngKey = "latlng";
    public static final String sitterIdKey = "sitterId";
    public static final String firstNameKey = "first_name";
    public static final String lastNameKey = "last_name";
    public static final String coverPictureKey = "cover_picture";
    public static final String listingIdKey = "listingId";
    public static final String reviewerIdKey = "reviewerId";

    public static final String summaryKey = "listingSummary";
    public static final String numReviewsKey = "numReviews";
    public static final String descriptionKey = "description";

    public static final String latitude = "latitude";
    public static final String longitude = "longitude";

    public static final String GOOGLE_API_KEY = "AIzaSyDWL29OaAFz5WyJ_e-bU3UmJ8QruGpFQxQ";

    public static final String currencySymbol = Currency.getInstance(Locale.US).getSymbol(Locale.US);

}
