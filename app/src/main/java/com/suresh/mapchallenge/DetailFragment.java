package com.suresh.mapchallenge;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.suresh.mapchallenge.api.PlacesApiHelper;
import com.suresh.mapchallenge.api.model.Place;

/**
 * Created by suresh on 18/4/15.
 */
public class DetailFragment extends Fragment {

    public static final String KEY_PLACE = "place";

    private Place place;

    //View handles
    private Toolbar toolbar;
    private NetworkImageView imgBanner;
    private TextView tvTitle, tvAddress;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        imgBanner = (NetworkImageView) view.findViewById(R.id.imgBanner);
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvAddress = (TextView) view.findViewById(R.id.tvAddress);

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
}
