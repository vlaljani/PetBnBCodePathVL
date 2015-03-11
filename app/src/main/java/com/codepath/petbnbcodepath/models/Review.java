package com.codepath.petbnbcodepath.models;

import android.util.Log;

import com.codepath.petbnbcodepath.helpers.Constants;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by vibhalaljani on 3/7/15.
 */
public class Review {
    private String reviewer_first_name;
    private String reviewer_city;
    private String reviewer_state;
    private String review_description;

    public Review() {
        review_description = null;
        reviewer_city = null;
        reviewer_state = null;
        reviewer_first_name = null;
    }

    public String getReviewer_first_name() {
        return reviewer_first_name;
    }

    public void setReviewer_first_name(String reviewer_first_name) {
        this.reviewer_first_name = reviewer_first_name;
    }

    public String getReviewer_city() {
        return reviewer_city;
    }

    public void setReviewer_city(String reviewer_city) {
        this.reviewer_city = reviewer_city;
    }

    public String getReviewer_state() {
        return reviewer_state;
    }

    public void setReviewer_state(String reviewer_state) {
        this.reviewer_state = reviewer_state;
    }

    public String getReview_description() {
        return review_description;
    }

    public void setReview_description(String review_description) {
        this.review_description = review_description;
    }
}
