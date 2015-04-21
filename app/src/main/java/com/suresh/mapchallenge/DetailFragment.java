package com.suresh.mapchallenge;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.suresh.mapchallenge.api.PlacesApiHelper;
import com.suresh.mapchallenge.api.model.Place;
import com.suresh.mapchallenge.api.model.PlaceDetail;
import com.suresh.mapchallenge.api.parser.BaseParser;
import com.suresh.mapchallenge.utils.Utils;

import java.text.DecimalFormat;

/**
 * Created by suresh on 18/4/15.
 */
public class DetailFragment extends Fragment {

    public static final String KEY_PLACE = "place";

    private Place place;

    //View handles
    private Toolbar toolbar;
    private NetworkImageView imgBanner;
    private TextView tvTitle, tvAddress, tvOpenStatus, tvAvgRating;
    private View openingHrsSection, reviewsSection, progressBar;
    private LinearLayout llOpeningHrs, llReviews;

    public static DetailFragment newInstance(Place place) {
        Bundle args = new Bundle();
        args.putSerializable(KEY_PLACE, place);

        DetailFragment frag = new DetailFragment();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        place = (Place) getArguments().getSerializable(KEY_PLACE);

        PlacesApiHelper.getPlaceDetails(place.id, new PlaceDetailResult());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        imgBanner = (NetworkImageView) view.findViewById(R.id.imgBanner);
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvAddress = (TextView) view.findViewById(R.id.tvAddress);
        openingHrsSection = view.findViewById(R.id.openingHrsSection);
        reviewsSection = view.findViewById(R.id.reviewsSection);
        progressBar = view.findViewById(R.id.progressBar);

        tvOpenStatus = (TextView) view.findViewById(R.id.tvOpenStatus);
        llOpeningHrs = (LinearLayout) view.findViewById(R.id.llOpeningHrs);

        tvAvgRating = (TextView) view.findViewById(R.id.tvAvgRating);
        llReviews = (LinearLayout) view.findViewById(R.id.llReviews);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ActionBarActivity activity = (ActionBarActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (place.photoId != null) {
            imgBanner.setImageUrl(PlacesApiHelper.getPhotoUrl(place.photoId),
                    APP.getInstance().getBannerImageLoader());
        }
        tvTitle.setText(place.name);
        tvAddress.setText(place.address);
    }

    public void displayDetailedInformation(PlaceDetail info) {
        progressBar.setVisibility(View.GONE);
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        if (info.openingHours != null) bindOpeningHoursInfo(inflater, info.openingHours);

        if (info.reviews != null) bindReviewsInfo(inflater, info.avgRating, info.reviews);
    }

    public void bindOpeningHoursInfo(LayoutInflater inflater, PlaceDetail.OpeningHours openingHours) {
        //Binding open now information
        int openText, openTextColor;
        if (openingHours.isOpenNow) {
            openText = R.string.place_open;
            openTextColor = R.color.place_open_text;
        } else {
            openText = R.string.place_closed;
            openTextColor = R.color.place_closed_text;
        }
        tvOpenStatus.setText(openText);
        tvOpenStatus.setTextColor(getResources().getColor(openTextColor));

        //Binding daily timing information
        for (String timing : openingHours.dayTimings) {
            TextView timingRow = (TextView) inflater.inflate(R.layout.row_opening_time, llOpeningHrs, false);
            timingRow.setText(timing);
            llOpeningHrs.addView(timingRow);
        }

        //Displaying container
        Utils.animateTransition(openingHrsSection, 600, true);
    }

    public void bindReviewsInfo(LayoutInflater inflater, float avgRating, PlaceDetail.Review[] reviews) {
        tvAvgRating.setText(new DecimalFormat("#.#").format(avgRating));

        //Binding review information
        for (PlaceDetail.Review r : reviews) {
            View reviewRow = inflater.inflate(R.layout.row_review, llReviews, false);

            ((TextView)reviewRow.findViewById(R.id.tvAuthorName)).setText(r.authorName);
            ((RatingBar)reviewRow.findViewById(R.id.ratingBar)).setRating(r.rating);
            ((TextView)reviewRow.findViewById(R.id.tvReviewText)).setText(r.reviewText);

            llReviews.addView(reviewRow);
        }

        //Displaying container
        Utils.animateTransition(reviewsSection, 600, true);
    }

    private class PlaceDetailResult implements BaseParser.ResultListener<PlaceDetail> {

        @Override
        public void consumeResult(PlaceDetail result, boolean moreResults) {
            displayDetailedInformation(result);
        }
    }
}
