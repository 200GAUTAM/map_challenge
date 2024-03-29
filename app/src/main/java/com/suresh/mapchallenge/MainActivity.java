package com.suresh.mapchallenge;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.suresh.mapchallenge.api.PlacesApiHelper;
import com.suresh.mapchallenge.api.model.Place;
import com.suresh.mapchallenge.api.parser.BaseParser;
import com.suresh.mapchallenge.utils.CategoryAdapter;
import com.suresh.mapchallenge.utils.Constants;
import com.suresh.mapchallenge.utils.MarkerCache;
import com.suresh.mapchallenge.utils.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

public class MainActivity extends ActionBarActivity implements Constants, OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnInfoWindowClickListener, View.OnLayoutChangeListener,
        View.OnClickListener, CategoryAdapter.OnCategoryChangedListener,
        View.OnTouchListener, GoogleMap.OnCameraChangeListener,
        GoogleMap.OnMyLocationButtonClickListener, MarkerCache.MarkerEvictedListener,
        GoogleMap.OnMarkerClickListener {

    private GoogleApiClient googleApiClient;
    private GoogleMap map;
    private LatLng searchLocation;
    private boolean paddingSet = false, centreMarked = false;
    private boolean ignoreCameraChange = false; //Flag to decide if we should ignore the camera movement (happens when a user clicks on a marker. Don't want to trigger an API call then)
    private int mapTopPadding = -1; //Amount of padding to be applied to the top of the map (to prevent the category dropdown overlapping the map controls)
    private ErrorType currentError; //Track the current error being displayed

    private MarkerCache markerCache = new MarkerCache(MAX_MARKER_COUNT, this); //Storing markers and their corresponding places
    private HashSet<Place> placeSet; //Maintaining hashset of places to prevent duplicates
    private CategoryAdapter adapter;

    /*
     * savedInstanceState bundle keys
     */
    private static final String KEY_PLACES = "place_set";
    private static final String KEY_CATEGORY_SELECTION = "selected_categories";
    private static final String KEY_SEARCH_LOCATION_MARKER = "search_location_marker";

    //View handles
    private View categoryDropdownToggle, categoryDropdownSection, touchInterceptor,
            errorSection, loadingSection, zoomError;
    private TextView tvZoomWarning, tvErrorText;
    private ImageView imgErrorIcon;
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
        errorSection = findViewById(R.id.errorSection);
        loadingSection = findViewById(R.id.loadingSection);
        zoomError = findViewById(R.id.zoomError);
        tvZoomWarning = (TextView) findViewById(R.id.tvZoomWarning);
        tvErrorText = (TextView) findViewById(R.id.tvErrorText);
        imgErrorIcon = (ImageView) findViewById(R.id.imgErrorIcon);
        errorSection.setOnTouchListener(this);
        listView = (ListView) findViewById(R.id.categoryList);
        listView.setDividerHeight(0);
    }

    private void initOrRestoreVariables(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            placeSet = new HashSet<Place>();
            paddingSet = false;
            adapter = new CategoryAdapter(this);
        } else {
            searchLocation = savedInstanceState.getParcelable(KEY_SEARCH_LOCATION_MARKER);
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
            case R.id.menu_action_refresh:
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

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.errorSection:
                return true; //Intercepting touch events to prevent the user from interacting with the views below
            default:
                return false;
        }
    }

    private void showHideDropdown() {
        if (!categoryDropdownSection.isShown()) {
            touchInterceptor.setVisibility(View.VISIBLE);
            touchInterceptor.setOnClickListener(this);
        } else {
            touchInterceptor.setVisibility(View.GONE);
            touchInterceptor.setOnClickListener(null);
        }

        Utils.animateTransition(categoryDropdownSection, 200, !categoryDropdownSection.isShown());
    }



    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();

        //Show network error if required
        toggleErrorSection(ErrorType.NETWORK, !isNetworkConnected());
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    private void setCameraToCurrentUserLocation() {
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(searchLocation, START_ZOOM_LEVEL);
        map.moveCamera(update);
    }

    private void trySettingMapPadding() {
        if (paddingSet) return; //Map's padding has already been set

        if (map == null || mapTopPadding == -1) return; //We don't have all info yet

        paddingSet = true;

        map.setPadding(0, mapTopPadding, 0, 0);
    }

    private void getNearbyPlaces() {
        //Cancel existing/pending search requests
        APP.getInstance().cancelRequests(PlacesApiHelper.TAG_NEARBY_REQUESTS);

        toggleLoadingSection(true);
        PlacesApiHelper.getPlacesNearby(searchLocation, new NearbySearchResult());
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
            markerCache.put(m, p);
            if (avoidDuplicates) placeSet.add(p);
        }
    }

    private void restartSearch() {
        //Clear existing data
        markerCache.setListener(null);
        markerCache.evictAll();
        markerCache.setListener(this);
        placeSet.clear();
        map.clear();

        //Trigger search API call again
        getNearbyPlaces();
    }

    @Override
    public void onMarkerEvictedFromCache(Marker m, Place p) {
        m.remove(); //Remove the marker from the map
        placeSet.remove(p); //Remove the corresponding place from the set
    }

    /**
     * Controls the showing and hiding of markers based on the options chosen by the user
     * @param category
     * @param chosen
     */
    @Override
    public void onCategoryOptionChanged(Place.Category category, boolean chosen) {
        for (Map.Entry<Marker, Place> e : markerCache.snapshot().entrySet()) {
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
        outState.putParcelable(KEY_SEARCH_LOCATION_MARKER, searchLocation);
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
        map.setOnCameraChangeListener(this);
        map.setOnMyLocationButtonClickListener(this);
        map.setOnInfoWindowClickListener(this);
        map.setOnMarkerClickListener(this);

        trySettingMapPadding();

        //Restore markers if available
        if (placeSet.size() > 0) plotPlaces(placeSet, false);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Place selected = markerCache.get(marker);

        Intent i = new Intent(this, DetailActivity.class);
        i.putExtra(DetailFragment.KEY_PLACE, selected);
        startActivity(i);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        ignoreCameraChange = true;
        return false;
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if (ignoreCameraChange) { //Ignoring camera change if the camera change was triggered by clicking on a marker. Can limit number of requests then.
            ignoreCameraChange = false;
            return;
        }

        if (cameraPosition.zoom >= SEARCH_MIN_ZOOM && cameraPosition.zoom <= SEARCH_MAX_ZOOM) { //Within range
            if (zoomError.isShown()) toggleZoomError(false); //Hide the zoom warning if displayed

            searchLocation = cameraPosition.target;
            getNearbyPlaces();
        } else { //Too far out. Display warning
            int warningTextRes = (cameraPosition.zoom < SEARCH_MIN_ZOOM)
                    ? R.string.zoom_in_warning : R.string.zoom_out_warning;
            toggleZoomError(true, warningTextRes);
        }


        if (!centreMarked) {
            centreMarked = true;
            Point mapCentre = map.getProjection().toScreenLocation(cameraPosition.target);
            View centreMarker = findViewById(R.id.imgCentre);
            centreMarker.setX(mapCentre.x - centreMarker.getWidth() / 2);
            centreMarker.setY(mapCentre.y - centreMarker.getHeight());
            centreMarker.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Location currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (currentLocation != null) {
            searchLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            restartSearch();
        }
        return false;
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
        Location currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (currentLocation != null) {
            //Hide the error section if visible (Happens if the user returns to the app after changing GPS settings)
            toggleErrorSection(ErrorType.GPS, false);

            if (searchLocation != null) return; //Abort if we already have data (happens when screen rotates, user returns to screen/app etc.)

            //Initialise screen
            searchLocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            setCameraToCurrentUserLocation(); //Initialise the map to the user's current location

        } else { //Location/GPS not enabled on device. Display error
            toggleErrorSection(ErrorType.GPS, true);
        }
    }

    @Override public void onConnectionSuspended(int i) { Log.v("test", "Connection suspended: " + i); }

    @Override public void onConnectionFailed(ConnectionResult connectionResult) { Log.v("test", "Connection Failed: " + connectionResult.toString()); }

    private void toggleErrorSection(ErrorType type, boolean shouldDisplay) {
        if (shouldDisplay) {
            if (currentError == null) { //Proceed only if there's no error being displayed at that moment
                currentError = type;
                int errorIcon, errorText;
                if (type == ErrorType.GPS) { //GPS error
                    errorIcon = R.drawable.ic_gps_off;
                    errorText = R.string.gps_error;
                } else { //Network error
                    errorIcon = R.drawable.ic_wifi_off;
                    errorText = R.string.network_error;
                }

                imgErrorIcon.setImageDrawable(getResources().getDrawable(errorIcon));
                tvErrorText.setText(errorText);
            } else {
                return;
            }
        } else {
            if (currentError == type) {
                currentError = null; //Clear the current error
            } else {
                return; //Don't hide the section if this is not the error being displayed right now
            }
        }

        int vis = (shouldDisplay) ? View.VISIBLE : View.GONE;
        errorSection.setVisibility(vis);
    }

    private void toggleZoomError(boolean shouldDisplay) {
        toggleZoomError(shouldDisplay, -1);
    }

    private void toggleZoomError(boolean shouldDisplay, int warningTextRes) {
        if (warningTextRes != -1) tvZoomWarning.setText(warningTextRes);
        Utils.animateTransition(zoomError, 300, shouldDisplay);
    }

    private void toggleLoadingSection(boolean shouldDisplay) {
        Utils.animateTransition(loadingSection, 600, shouldDisplay);
    }



    private class NearbySearchResult implements BaseParser.ResultListener<ArrayList<Place>> {
        @Override
        public void consumeResult(ArrayList<Place> result, boolean moreResults) {
            if (result != null) {
                plotPlaces(result, true);
            }

            if (!moreResults) {
                toggleLoadingSection(false);
            }
        }
    }
}
