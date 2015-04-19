package com.suresh.mapchallenge;

import android.animation.Animator;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ListView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.suresh.mapchallenge.api.PlacesApiHelper;
import com.suresh.mapchallenge.api.model.Place;
import com.suresh.mapchallenge.api.parser.BaseParser;
import com.suresh.mapchallenge.utils.CategoryAdapter;
import com.suresh.mapchallenge.utils.Constants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class MainActivity extends ActionBarActivity implements Constants, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnInfoWindowClickListener, View.OnLayoutChangeListener,
        View.OnClickListener, CategoryAdapter.OnCategoryChangedListener {

    private GoogleApiClient googleApiClient;
    private GoogleMap map;
    private Location latestLocation;
    private boolean paddingSet = false;
    private int mapTopPadding = -1; //Amount of padding to be applied to the top of the map (to prevent the category dropdown overlapping the map controls)

    private HashMap<Marker, Place> mpMap = new HashMap<Marker, Place>(); //Storing markers and their corresponding places
    private HashSet<Place> placeSet; //Maintaining hashset of places to prevent duplicates
    private CategoryAdapter adapter;

    /*
     * savedInstanceState bundle keys
     */
    private static final String KEY_PLACES = "place_set";
    private static final String KEY_CATEGORY_SELECTION = "selected_categories";
    private static final String KEY_MAP_INITIALISED = "map_initialised";

    //View handles
    private View categoryDropdownToggle, categoryDropdownSection, touchInterceptor, loadingSection;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle(R.string.map_screen_actionbar_title);
        initMap();
        initGooglePlayServices();
        initViews();
        initOrRestoreVariables(savedInstanceState);
    }

    private void initMap() {
        SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFrag);
        mapFrag.getMapAsync(this);
    }

    private void initGooglePlayServices() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void initViews() {
        categoryDropdownToggle = findViewById(R.id.categoryDropdownToggle);
        categoryDropdownToggle.setOnClickListener(this);
        categoryDropdownToggle.addOnLayoutChangeListener(this); //Used to calculate the amount of padding for the map controls
        categoryDropdownSection = findViewById(R.id.categoryDropdownSection);
        touchInterceptor = findViewById(R.id.touchInterceptor);
        loadingSection = findViewById(R.id.loadingSection);
        listView = (ListView) findViewById(R.id.categoryList);
        listView.setDividerHeight(0);
    }

    private void initOrRestoreVariables(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            placeSet = new HashSet<Place>();
            paddingSet = false;
            adapter = new CategoryAdapter(this);
        } else {
            placeSet = (HashSet<Place>) savedInstanceState.getSerializable(KEY_PLACES);
            paddingSet = false;

            boolean[] checked = savedInstanceState.getBooleanArray(KEY_CATEGORY_SELECTION);
            adapter = new CategoryAdapter(this, checked);
        }

        listView.setAdapter(adapter);
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        v.removeOnLayoutChangeListener(this);

        int margin = (int) getResources().getDimension(R.dimen.category_dropdown_margin);
        mapTopPadding = (bottom - top) + margin;
        trySettingMapPadding();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_action_search_again:
                restartSearch();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }

    /**
     * Triggers showing/hiding of the category dropdown list
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.categoryDropdownToggle:
            case R.id.touchInterceptor:
                showHideDropdown();
                break;
        }
    }

    private void showHideDropdown() {
        int vis;
        View.OnClickListener listener;
        float alphaVal;

        if (categoryDropdownSection.isShown()) {
            vis = View.GONE;
            listener = null;
            alphaVal = 0;
        } else {
            vis = View.VISIBLE;
            listener = this;
            alphaVal = 1;
        }

        touchInterceptor.setVisibility(vis);
        touchInterceptor.setOnClickListener(listener);

        categoryDropdownSection.animate()
                .setDuration(FADE_ANIM_DURATION)
                .setInterpolator(new DecelerateInterpolator())
                .alpha(alphaVal)
                .setListener(new FadeAnimationListener(categoryDropdownSection, vis))
                .start();
    }

    private static class FadeAnimationListener implements Animator.AnimatorListener {

        private View view;
        private int visibilityAfterAnim;

        private FadeAnimationListener(View view, int visibility) {
            this.view = view;
            visibilityAfterAnim = visibility;
        }

        @Override
        public void onAnimationStart(Animator animation) {
            if (visibilityAfterAnim == View.VISIBLE) view.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (visibilityAfterAnim == View.GONE) view.setVisibility(View.GONE);
        }

        @Override public void onAnimationCancel(Animator animation) { }
        @Override public void onAnimationRepeat(Animator animation) { }
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    private void setCameraToCurrentUserLocation() {
        LatLng latLng = new LatLng(latestLocation.getLatitude(), latestLocation.getLongitude());
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, MAP_ZOOM_LEVEL);
        map.animateCamera(update, 1, null);
    }

    private void trySettingMapPadding() {
        if (paddingSet) return; //Map's padding has already been set

        if (map == null || mapTopPadding == -1) return; //We don't have all info yet

        paddingSet = true;

        map.setPadding(0, mapTopPadding, 0, 0);
    }

    private void getNearbyPlaces(Location location) {
        loadingSection.setOnClickListener(this);
        loadingSection.animate()
                .setDuration(400)
                .setInterpolator(new DecelerateInterpolator())
                .alpha(1)
                .setListener(new FadeAnimationListener(loadingSection, View.VISIBLE))
                .start();

        PlacesApiHelper.getPlacesNearby(location, new NearbySearchResult());
    }

    private void plotPlaces(Collection<Place> places, boolean avoidDuplicates) {
        for (Place p : places) {
            //Skip existing places
            if (avoidDuplicates && placeSet.contains(p)) continue;

            MarkerOptions marker = new MarkerOptions();

            LatLng latLng = new LatLng(p.lat, p.lng);
            marker.position(latLng);

            marker.title(p.name);
            marker.snippet(p.address);
            marker.visible(adapter.isCategoryChosen(p.category));
            marker.icon(BitmapDescriptorFactory.defaultMarker(p.category.hue));

            Marker m = map.addMarker(marker);
            mpMap.put(m, p);
            if (avoidDuplicates) placeSet.add(p);
        }
    }

    private void restartSearch() {
        latestLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (latestLocation == null) {
            //TODO: Handle error
            return;
        }

        //Clear existing data
        mpMap.clear();
        placeSet.clear();
        map.clear();

        //Trigger search API call again
        getNearbyPlaces(latestLocation);
    }

    /**
     * Controls the showing and hiding of markers based on the options chosen by the user
     * @param category
     * @param chosen
     */
    @Override
    public void onCategoryOptionChanged(Place.Category category, boolean chosen) {
        for (Map.Entry<Marker, Place> e : mpMap.entrySet()) {
            if (e.getValue().category == category) {
                e.getKey().setVisible(chosen);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(KEY_PLACES, placeSet);
        outState.putBooleanArray(KEY_CATEGORY_SELECTION, adapter.getChecked());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        initOrRestoreVariables(savedInstanceState);
    }

    /*
     * Google Maps callbacks
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMyLocationEnabled(true);
        map.setOnInfoWindowClickListener(this);

        trySettingMapPadding();

        //Restore markers if available
        if (placeSet.size() > 0) plotPlaces(placeSet, false);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Place selected = mpMap.get(marker);

        Intent i = new Intent(this, DetailActivity.class);
        i.putExtra(DetailFragment.KEY_PLACE, selected);
        startActivity(i);
    }

    /*
     * Google Play Services callbacks (Location API stuff)
     */

    /**
     * Starting point of the app. We can start getting location data once this callback is triggered.
     * @param bundle
     */
    @Override
    public void onConnected(Bundle bundle) {
        latestLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (latestLocation != null) {
            setCameraToCurrentUserLocation();

            //Trigger API call if there is no existing data
            if (placeSet.isEmpty()) getNearbyPlaces(latestLocation);
        } else {
            //TODO: Display error message
            Log.v("test", "Location is null!");
        }
    }

    @Override public void onConnectionSuspended(int i) { Log.v("test", "Connection suspended: " + i); }

    @Override public void onConnectionFailed(ConnectionResult connectionResult) { Log.v("test", "Connection Failed: " + connectionResult.toString()); }

    private class NearbySearchResult implements BaseParser.ResultListener<ArrayList<Place>> {
        @Override
        public void consumeResult(ArrayList<Place> result, boolean moreResults) {
            if (result != null) {
                Log.v("test", result.toString());
                plotPlaces(result, true);
            }

            if (!moreResults) {
                loadingSection.setOnClickListener(null);
                loadingSection.animate()
                        .setDuration(400)
                        .setInterpolator(new DecelerateInterpolator())
                        .alpha(0)
                        .setListener(new FadeAnimationListener(loadingSection, View.GONE))
                        .start();
            }
        }
    }
}
