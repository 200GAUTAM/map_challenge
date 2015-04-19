package com.suresh.mapchallenge.api;

import android.location.Location;
import android.net.Uri;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.suresh.mapchallenge.APP;
import com.suresh.mapchallenge.api.model.Place;
import com.suresh.mapchallenge.api.parser.BaseParser;
import com.suresh.mapchallenge.api.parser.NearbySearchParser;
import com.suresh.mapchallenge.utils.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Created by suresh on 18/4/15.
 */
public class PlacesApiHelper implements Constants {

    /**
     * Used to initiate a search for places around a particular location
     * @param location
     * @param resultListener
     */
    public static void getPlacesNearby(Location location,
                                       BaseParser.ResultListener<ArrayList<Place>> resultListener) {
        //Prepare the request URL
        Uri.Builder builder = Uri.parse(API_NEARBY_SEARCH).buildUpon();
        addTypesParam(builder);
        addLocationParam(builder, location);
        builder.appendQueryParameter("radius", DEFAULT_SEARCH_RADIUS);

        makePlacesNearbyRequest(builder, resultListener, 0);
    }

    /**
     * Used when getting subsequent pages of a particular search
     * @param nextPageToken
     * @param resultListener
     */
    public static void getPlacesNearby(String nextPageToken,
                                       BaseParser.ResultListener<ArrayList<Place>> resultListener) {
        //Prepare the request URL
        Uri.Builder builder = Uri.parse(API_NEARBY_SEARCH).buildUpon();
        builder.appendQueryParameter("pagetoken", nextPageToken);

        makePlacesNearbyRequest(builder, resultListener, API_NEARBY_SEARCH_REQUEST_DELAY);
    }

    private static void makePlacesNearbyRequest(Uri.Builder builder,
                                                BaseParser.ResultListener<ArrayList<Place>> resultListener,
                                                long requestDelay) {
        addApiKeyParam(builder);

        NearbySearchParser parser = new NearbySearchParser(resultListener);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, builder.build().toString(),
                null, parser, parser);

        if (requestDelay > 0) {
            APP.getInstance().addRequestWithDelay(request, requestDelay);
        } else {
            APP.getInstance().addRequest(request);
        }
    }

    private static void addLocationParam(Uri.Builder builder, Location location) {
        builder.appendQueryParameter("location", location.getLatitude() + "," + location.getLongitude());
    }

    private static void addApiKeyParam(Uri.Builder builder) {
        builder.appendQueryParameter("key", PLACES_API_KEY);
    }

    private static void addTypesParam(Uri.Builder builder) {
        StringBuilder typesParamStr = new StringBuilder();

        ArrayList<String> placeTypes = new ArrayList<String>();
        for (Place.Category c : Place.Category.values()) {
            placeTypes.addAll(Arrays.asList(c.types));
        }

        Iterator<String> iter = placeTypes.iterator();
        while (iter.hasNext()) {
            typesParamStr.append(iter.next());
            if (iter.hasNext()) typesParamStr.append("|");
        }

        builder.appendQueryParameter("types", typesParamStr.toString());
    }

    public static String getPhotoUrl(String photoReference) {
        Uri.Builder builder = Uri.parse(API_IMAGE_BASE_URL).buildUpon();
        addApiKeyParam(builder);
        builder.appendQueryParameter("photoreference", photoReference);
        builder.appendQueryParameter("maxwidth", MAX_IMAGE_WIDTH);
        return builder.build().toString();
    }

}
