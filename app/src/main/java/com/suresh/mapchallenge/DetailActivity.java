package com.suresh.mapchallenge;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.suresh.mapchallenge.api.model.Place;

/**
 * Created by suresh on 18/4/15.
 */
public class DetailActivity extends ActionBarActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Place place = (Place) getIntent().getSerializableExtra(DetailFragment.KEY_PLACE);
        DetailFragment frag = DetailFragment.newInstance(place);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, frag).commit();
    }
}
