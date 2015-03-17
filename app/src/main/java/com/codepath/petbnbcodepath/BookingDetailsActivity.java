package com.codepath.petbnbcodepath;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.petbnbcodepath.adapters.ExpandableListAdapter;
import com.codepath.petbnbcodepath.helpers.Constants;
import com.codepath.petbnbcodepath.fragments.DatePickerDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public class BookingDetailsActivity extends ActionBarActivity {

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
    private int numReviews;
    private int cost;

    private int drop_off_year;
    private int drop_off_month;
    private int drop_off_date;
    private int pick_up_year;
    private int pick_up_month;
    private int pick_up_date;
    private int total_nights = 2;
    private int service_charge;

    private ExpandableListAdapter listAdapter;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;

    private DatePickerDialog datePickerDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_details);

        coverPictureUrl = getIntent().getStringExtra(Constants.coverPictureKey);
        firstName = getIntent().getStringExtra(Constants.firstNameKey);
        lastName = getIntent().getStringExtra(Constants.lastNameKey);
        numReviews = getIntent().getIntExtra(Constants.numReviewsKey, 0);
        cost = getIntent().getIntExtra(Constants.listingCostKey, 0);

        datePickerDialog = DatePickerDialog.newInstance();



        setupViews();
    }

    private void setupViews() {

        String fontHtmlBeg = "<font color=\"" + getResources().getColor(R.color.theme_teal) + "\">";
        String fontHtmlEnd = "</font>";

        String selDateFontHtmlBeg = "<font color=\"" + getResources().getColor(R.color.dark_gray)
                                                                                            + "\">";
        String selDateFontHtmlEnd = "</font>";
        ivCoverPicture = (ImageView) findViewById(R.id.ivCoverPicture);
        tvSummary = (TextView) findViewById(R.id.tvSummary);
        tvNumReviews = (TextView) findViewById(R.id.tvNumReviews);
        tvCost = (TextView) findViewById(R.id.tvCost);
        tvSayHello = (TextView) findViewById(R.id.tvSayHello);
        tvSayHelloSub = (TextView) findViewById(R.id.tvSayHelloSub);
        btnSelDropDates = (Button) findViewById(R.id.btnSelDropDates);
        btnSelPickDates = (Button) findViewById(R.id.btnSelPickDates);
        lvPrice = (ExpandableListView) findViewById(R.id.lvPrice);

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

    }


    private void prepareListData() {

        service_charge = (int) Math.round(Constants.service_charge_percentage * cost * total_nights);
        int total_cost = (cost * total_nights) + service_charge;



        // Adding child data
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
