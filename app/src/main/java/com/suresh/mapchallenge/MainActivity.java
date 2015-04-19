package com.suresh.mapchallenge;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;

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
import com.suresh.mapchallenge.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class MainActivity extends ActionBarActivity implements Constants, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnInfoWindowClickListener, View.OnLayoutChangeListener {

    private GoogleApiClient googleApiClient;
    private GoogleMap map;
    private Location latestLocation;
    private boolean mapInitialised = false;
    private int mapTopPadding; //Amount of padding to be applied to the top of the map (to prevent the category dropdown overlapping the map controls)

    private HashMap<Marker, Place> mpMap = new HashMap<Marker, Place>(); //Storing markers and their corresponding places
    private HashSet<Place> placeSet = new HashSet<Place>(); //Maintaining hashset of places to prevent duplicates

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle(R.string.map_screen_actionbar_title);
        initMap();
        initGooglePlayServices();

        findViewById(R.id.categoryDropdown).addOnLayoutChangeListener(this);
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        v.removeOnLayoutChangeListener(this);

        int margin = (int) getResources().getDimension(R.dimen.category_dropdown_margin);
        mapTopPadding = (bottom - top) + margin;
        map.setPadding(0, mapTopPadding, 0, 0);
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

    private void tryInitialisingMap() {
        if (mapInitialised) return; //Map location has already been initialised

        if (map == null || latestLocation == null) return; //We don't have all info yet

        mapInitialised = true;

        LatLng latLng = new LatLng(latestLocation.getLatitude(), latestLocation.getLongitude());
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, MAP_ZOOM_LEVEL);
        map.animateCamera(update, 1, null);

        //Trigger API call to get nearby places
        getNearbyPlaces(latestLocation);
    }

    private void getNearbyPlaces(Location location) {
        PlacesApiHelper.getPlacesNearby(location, new NearbySearchResult());
    }

    private void plotPlaces(ArrayList<Place> places) {
        for (Place p : places) {
            //Skip existing places
            if (placeSet.contains(p)) continue;

            MarkerOptions marker = new MarkerOptions();

            LatLng latLng = new LatLng(p.lat, p.lng);
            marker.position(latLng);

            marker.title(p.name);
            marker.snippet(p.address);
            marker.icon(BitmapDescriptorFactory.defaultMarker(p.category.hue));

            Marker m = map.addMarker(marker);
            mpMap.put(m, p);
            placeSet.add(p);
        }
    }

    /*
     * Google Maps callbacks
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMyLocationEnabled(true);
        map.setOnInfoWindowClickListener(this);

        tryInitialisingMap();
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

    @Override
    public void onConnected(Bundle bundle) {
        latestLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (latestLocation != null) {
            Log.v("test", "Location = " + latestLocation.getLatitude() + "," + latestLocation.getLongitude());
            tryInitialisingMap();
        } else {
            //TODO: Display error message
            Log.v("test", "Location is null!");
        }
    }

    @Override public void onConnectionSuspended(int i) { Log.v("test", "Connection suspended: " + i); }

    @Override public void onConnectionFailed(ConnectionResult connectionResult) { Log.v("test", "Connection Failed: " + connectionResult.toString()); }

    private class NearbySearchResult implements BaseParser.ResultListener<ArrayList<Place>> {
        @Override
        public void consumeResult(ArrayList<Place> result) {
            if (result != null) {
                Log.v("test", result.toString());
                plotPlaces(result);
            }
        }
    }
}
