package com.codepath.petbnbcodepath.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.calendarlistview.library.DayPickerView;
import com.andexert.calendarlistview.library.SimpleMonthAdapter;
import com.codepath.petbnbcodepath.R;
import com.codepath.petbnbcodepath.adapters.ExpandableListAdapter;
import com.codepath.petbnbcodepath.helpers.Constants;
import com.codepath.petbnbcodepath.fragments.DatePickerDialog;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class BookingDetailsActivity extends ActionBarActivity implements
                                                      DatePickerDialog.OnDatePickerDialogListener {

    private static final String TAG = "BOOKINGDETAILACTIVITY";

    private ImageView ivCoverPicture;
    private TextView tvSummary;
    private TextView tvNumReviews;
    private TextView tvCost;
    private TextView tvSayHello;
    private TextView tvSayHelloSub;
    private Button btnSelDropDates;
    private Button btnSelPickDates;
    private ExpandableListView lvPrice;

    private String coverPictureUrl;
    private String firstName;
    private String lastName;
    private String objectId;
    private int numReviews;
    private int cost;

    private int total_nights = 0;
    private int service_charge;

    private String fontHtmlBeg;
    private String fontHtmlEnd = "</font>";

    private String selDateFontHtmlBeg;
    private String selDateFontHtmlEnd = "</font>";

    private ExpandableListAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;

    private DatePickerDialog datePickerDialog;

    private Date booking_dropOffDate;
    private Date booking_pickUpDate;

    private Button btnBook;

    public void onDatesSelected(Date dropOffDate, Date pickUpDate) {
        LocalDate dropOffDateJoda = new LocalDate(dropOffDate);
        LocalDate pickUpDateJoda = new LocalDate(pickUpDate);
        total_nights = Days.daysBetween(dropOffDateJoda, pickUpDateJoda).getDays();

        Toast.makeText(this, "total nights " + total_nights, Toast.LENGTH_SHORT).show();

        String btnDropOffText = fontHtmlBeg + getResources().getString(R.string.drop_off)
                + fontHtmlEnd + "<br/>" + selDateFontHtmlBeg +
                dropOffDate.getDate() + "/"
                + String.valueOf(dropOffDate.getMonth() + 1) + "/"
                + dropOffDate.getYear()
                + selDateFontHtmlEnd;
        String btnPickUpText = fontHtmlBeg + getResources().getString(R.string.pick_up)
                + fontHtmlEnd + "<br/>" + selDateFontHtmlBeg +
                pickUpDate.getDate() + "/"
                + String.valueOf(dropOffDate.getMonth() + 1) + "/"
                + pickUpDate.getYear() + selDateFontHtmlEnd;
        btnSelDropDates.setText(Html.fromHtml(btnDropOffText));
        btnSelPickDates.setText(Html.fromHtml(btnPickUpText));
        prepareListData();

        booking_dropOffDate = dropOffDate;
        booking_pickUpDate = pickUpDate;
        btnBook.setEnabled(true);
        btnBook.setAlpha(Constants.btnEnabledAlpha);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_details);

        coverPictureUrl = getIntent().getStringExtra(Constants.coverPictureKey);
        firstName = getIntent().getStringExtra(Constants.firstNameKey);
        lastName = getIntent().getStringExtra(Constants.lastNameKey);
        numReviews = getIntent().getIntExtra(Constants.numReviewsKey, 0);
        cost = getIntent().getIntExtra(Constants.listingCostKey, 0);
        objectId = getIntent().getStringExtra(Constants.objectIdKey);

        datePickerDialog = DatePickerDialog.newInstance();

        fontHtmlBeg = "<font color=\"" + getResources().getColor(R.color.theme_teal) + "\">";
        selDateFontHtmlBeg = "<font color=\"" + getResources().getColor(R.color.dark_gray)
                + "\">";



        setupViews();
    }

    private void setupViews() {


        ivCoverPicture = (ImageView) findViewById(R.id.ivCoverPicture);
        tvSummary = (TextView) findViewById(R.id.tvSummary);
        tvNumReviews = (TextView) findViewById(R.id.tvNumReviews);
        tvCost = (TextView) findViewById(R.id.tvCost);
        tvSayHello = (TextView) findViewById(R.id.tvSayHello);
        tvSayHelloSub = (TextView) findViewById(R.id.tvSayHelloSub);
        btnSelDropDates = (Button) findViewById(R.id.btnSelDropDates);
        btnSelPickDates = (Button) findViewById(R.id.btnSelPickDates);
        btnBook = (Button) findViewById(R.id.btnBook);
        lvPrice = (ExpandableListView) findViewById(R.id.lvPrice);

        btnBook.setEnabled(false);
        btnBook.setAlpha(Constants.btnDisabledAlpha);

        Picasso.with(this).load(coverPictureUrl).into(ivCoverPicture);
        tvSummary.setText(firstName + " " + lastName);
        if (numReviews == 1) {
            tvNumReviews.setText(String.valueOf(numReviews) + " " +
                                                        getResources().getString(R.string.review));
        } else {
            tvNumReviews.setText(String.valueOf(numReviews) + " " +
                                                       getResources().getString(R.string.reviews));
        }
        tvCost.setText(Constants.currencySymbol + String.valueOf(cost));
        tvSayHello.setText(getResources().getString(R.string.say_hello_1) + " " + firstName
                           + getResources().getString(R.string.say_hello_2));
        tvSayHelloSub.setText(getResources().getString(R.string.say_hello_det_1) + " " + firstName
                           + " " + getResources().getString(R.string.say_hello_det_2));

        String btnDropOffText = fontHtmlBeg + getResources().getString(R.string.drop_off)
                                + fontHtmlEnd + "<br/>" + selDateFontHtmlBeg +
                                getResources().getString(R.string.sel_date) + selDateFontHtmlEnd;

        String btnPickUpText = fontHtmlBeg + getResources().getString(R.string.pick_up)
                + fontHtmlEnd + "<br/>" + selDateFontHtmlBeg +
                getResources().getString(R.string.sel_date) + selDateFontHtmlEnd;

        btnSelPickDates.setText(Html.fromHtml(btnPickUpText));
        btnSelDropDates.setText(Html.fromHtml(btnDropOffText));

        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        lvPrice.setAdapter(listAdapter);
        prepareListData();


        setupViewListeners();

    }

    private void setupViewListeners() {
        btnSelDropDates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show(getSupportFragmentManager(), TAG);
            }
        });

        btnSelPickDates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show(getSupportFragmentManager(), TAG);
            }
        });

        btnBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser currentUser = ParseUser.getCurrentUser();
                if (currentUser != null) {

                    final ParseObject booking = new ParseObject(Constants.petVacayBookingHistoryTable);
                    booking.put("cost_per_night", cost);
                    booking.put("startDate", booking_dropOffDate);
                    booking.put("endDate", booking_pickUpDate);
                    booking.put("pending", true);
                    booking.put(Constants.ownerIdKey, currentUser);

                    ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.petVacayListingTable);
                    query.getInBackground(objectId, new GetCallback<ParseObject>() {
                        public void done(ParseObject object, ParseException e) {
                            if (e == null) {
                                booking.put(Constants.listingIdKey, object);
                                booking.saveInBackground();
                            } else {
                                Log.e("TAG", "Error: " + e.getMessage());

                                Toast.makeText(BookingDetailsActivity.this,
                                        getResources().getString(R.string.generic_error),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Intent i = new Intent(BookingDetailsActivity.this, LoginSignupActivity.class);
                    startActivity(i);
                }
            }
        });

    }


    private void prepareListData() {

        service_charge = (int) Math.round(Constants.service_charge_percentage * cost * total_nights);
        int total_cost = (cost * total_nights) + service_charge;



        // Adding child data
        listDataHeader.clear();
        listDataChild.clear();
        listDataHeader.add(String.valueOf(total_cost));


        // Adding child data
        List<String> cost_division = new ArrayList<String>();
        cost_division.add(Constants.currencySymbol + String.valueOf(cost) + " " +
                          getResources().getString(R.string.times) + " " + total_nights + " " +
                          getResources().getString(R.string.nights) + ";" +
                          String.valueOf(cost * total_nights));
        cost_division.add(getResources().getString(R.string.service_fee) + ";" +
                          String.valueOf(service_charge));

        listDataChild.put(listDataHeader.get(0), cost_division); // Header, Child data

        listAdapter.notifyDataSetChanged();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_booking_details, menu);
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
}

