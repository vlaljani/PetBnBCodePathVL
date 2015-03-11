package com.codepath.petbnbcodepath.adapters;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.petbnbcodepath.R;
import com.codepath.petbnbcodepath.models.Listing;
import com.codepath.petbnbcodepath.views.EllipsizingTextView;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by vibhalaljani on 3/7/15.
 *
 * This is the adapter for displaying the listview items on the landing page.
 */

public class LandingPageAdapter extends ArrayAdapter {
    private static final String TAG = "USERADAPTER";

    private static class ViewHolder {
        ImageView ivCoverPicture;
        TextView tvDescription;
        TextView tvState;
        TextView tvCity;
        TextView tvName;
    }

    public LandingPageAdapter(Context context, List<Listing> sitters) {
        super(context, android.R.layout.simple_list_item_1, sitters);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Listing currSitter = (Listing) getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_landing_page,
                    parent, false);
            viewHolder.ivCoverPicture = (ImageView) convertView.findViewById(R.id.ivCoverPicture);
            viewHolder.tvDescription = (EllipsizingTextView) convertView.findViewById(R.id.tvDescription);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            viewHolder.tvState = (TextView) convertView.findViewById(R.id.tvState);
            viewHolder.tvCity = (TextView) convertView.findViewById(R.id.tvCity);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // resetting all the views
        viewHolder.ivCoverPicture.setImageResource(0);
        viewHolder.tvDescription.setText("");
        viewHolder.tvName.setText("");
        viewHolder.tvCity.setText("");
        viewHolder.tvState.setText("");

        // Getting the screen width so we can resize the image appropriately
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int targetWidth = size.x;


        // Resizing with 0 height, allows the height to be variable, while the width is
        // fixed as the screen width - so the aspect ratio is maintained.
        Picasso.with(getContext())
               .load(currSitter
               .getCoverPictureUrl())
               .resize(targetWidth, 0)
               .placeholder(getContext().getResources().getDrawable(R.drawable.placeholder))
               .into(viewHolder.ivCoverPicture);

        // If there is no first review then we have no snippets to print and will just tell
        // the user that the sitter is a verified sitter
        if (currSitter.getFirstReview() != null) {
                viewHolder.tvDescription.setText("\"" +
                        currSitter.getFirstReview().getReview_description() + "\"");
                viewHolder.tvName.setText("- " + currSitter.getFirstReview()
                                                           .getReviewer_first_name());
                // Since the city is obtained by an asynchronous reverse geocoding call, it might
                // be null when the UI thread is doing this - in which case we don't want to
                // display it rather than displaying null
                if (currSitter.getCityState() != null) {
                    viewHolder.tvCity.setText(", " + currSitter.getCityState());
                }
        } else {
                viewHolder.tvDescription.setText(currSitter.getFirst_name() + " " +
                getContext().getResources().getString(R.string.sitter_std_string));
        }


        return convertView;
    }
}
