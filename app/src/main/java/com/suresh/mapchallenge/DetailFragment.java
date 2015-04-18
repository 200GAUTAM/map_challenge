package com.suresh.mapchallenge;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.suresh.mapchallenge.api.model.Place;

/**
 * Created by suresh on 18/4/15.
 */
public class DetailFragment extends Fragment {

    private static final String KEY_PLACE = "place";

    private Place place;

    //View handles
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

        imgBanner = (NetworkImageView) view.findViewById(R.id.imgBanner);
        tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvAddress = (TextView) view.findViewById(R.id.tvAddress);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tvTitle.setText(place.name);
        tvAddress.setText(place.address);
    }
}
