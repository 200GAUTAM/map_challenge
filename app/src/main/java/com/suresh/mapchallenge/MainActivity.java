package com.suresh.mapchallenge;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.suresh.mapchallenge.api.PlacesApiHelper;
import com.suresh.mapchallenge.api.model.Place;
import com.suresh.mapchallenge.api.parser.BaseParser;
import com.suresh.mapchallenge.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends ActionBarActivity implements Constants, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnInfoWindowClickListener {

    private GoogleApiClient googleApiClient;
    private GoogleMap map;
    private Location latestLocation;
    private boolean mapLocationInitialised = false;

    private HashMap<Place, Marker> markers = new HashMap<Place, Marker>(); //Storing places and their corresponding markers

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle(R.string.map_screen_actionbar_title);
        initMap();
        initGooglePlayServices();
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

    private void tryInitialisingMapLocation() {
        if (mapLocationInitialised) return; //Map location has already been initialised

        if (map == null || latestLocation == null) return; //We don't have all info yet

        mapLocationInitialised = true;

        LatLng latLng = new LatLng(latestLocation.getLatitude(), latestLocation.getLongitude());
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, MAP_ZOOM_LEVEL);
        map.animateCamera(update, 1, null);
    }

    private void getNearbyPlaces(Location location) {
        PlacesApiHelper.getPlacesNearby(location, new NearbySearchResult());
    }

    private void plotPlaces(ArrayList<Place> places) {
        for (Place p : places) {
            //Skip existing places
            if (markers.containsKey(p)) continue;

            MarkerOptions marker = new MarkerOptions();

            LatLng latLng = new LatLng(p.lat, p.lng);
            marker.position(latLng);

            marker.title(p.name);
            marker.snippet(p.address);

            Marker m = map.addMarker(marker);
            markers.put(p, m);
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

        tryInitialisingMapLocation();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        //TODO: Launch detail activity
    }

    /*
     * Google Play Services callbacks (Location API stuff)
     */

    @Override
    public void onConnected(Bundle bundle) {
        latestLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (latestLocation != null) {
            Log.v("test", "Location = " + latestLocation.getLatitude() + "," + latestLocation.getLongitude());
            tryInitialisingMapLocation();
            getNearbyPlaces(latestLocation);
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
