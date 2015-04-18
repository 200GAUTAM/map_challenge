package com.suresh.mapchallenge.utils;

/**
 * Created by suresh on 18/4/15.
 */
public interface Constants {

    public static final String PLACES_API_KEY = "AIzaSyARCSobh0ubndXEYN_Op7oaaR6PIMDNVyQ";

    /*
     * Default values
     */
    public static final float MAP_ZOOM_LEVEL = 14f;
    public static final String DEFAULT_SEARCH_RADIUS = "500";
    public static final long API_NEARBY_SEARCH_REQUEST_DELAY = 2000;

    /*
     * API URLs
     */
    public static final String API_NEARBY_SEARCH = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";
}
