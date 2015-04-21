package com.suresh.mapchallenge.utils;

/**
 * Created by suresh on 18/4/15.
 */
public interface Constants {

    public static final String PLACES_API_KEY = "AIzaSyBFIKu0oTHsmV4sfL1uKvXsWAuTj4MCRLg";

    /*
     * Default values
     */
    public static final float START_ZOOM_LEVEL = 15f;
    public static final float SEARCH_MIN_ZOOM = 13.6f;
    public static final float SEARCH_MAX_ZOOM = 16.6f;
    public static final String DEFAULT_SEARCH_RADIUS = "500";
    public static final String MAX_IMAGE_WIDTH = "1000"; //Used in the places photos request
    public static final long API_NEARBY_SEARCH_REQUEST_DELAY = 2000;

    //Maximum number of markers to display on the map. Used by the LruCache to evict the oldest markers.
    public static final int MAX_MARKER_COUNT = 150;

    /*
     * API URLs
     */
    public static final String API_NEARBY_SEARCH = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";
    public static final String API_IMAGE_BASE_URL = "https://maps.googleapis.com/maps/api/place/photo";
    public static final String API_PLACE_DETAIL = "https://maps.googleapis.com/maps/api/place/details/json";
}
