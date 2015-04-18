package com.suresh.mapchallenge.api;

import android.location.Location;
import android.net.Uri;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.suresh.mapchallenge.R;
import com.suresh.mapchallenge.api.model.Place;
import com.suresh.mapchallenge.api.parser.BaseParser;
import com.suresh.mapchallenge.api.parser.NearbySearchParser;
import com.suresh.mapchallenge.utils.Constants;

import java.util.ArrayList;

/**
 * Created by suresh on 18/4/15.
 */
public class PlacesApiHelper implements Constants {

    private static String[] placeTypes = {"bakery", "bar", "cafe", "food", "liquor_store", "meal_delivery", "meal_takeaway", "restaurant"};

    public static void getPlacesNearby(Location location,
                                       BaseParser.ResultListener<ArrayList<Place>> resultListener) {
        //Prepare the API request URL
        Uri.Builder builder = Uri.parse(API_NEARBY_SEARCH).buildUpon();
        addApiKeyParam(builder);
        addTypesParam(builder);
        addLocationParam(builder, location);
        builder.appendQueryParameter("radius", DEFAULT_SEARCH_RADIUS);

        NearbySearchParser parser = new NearbySearchParser(resultListener);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, builder.build().toString(),
                null, parser, parser);
    }

    private static void addLocationParam(Uri.Builder builder, Location location) {
        builder.appendQueryParameter("location", location.getLatitude() + "," + location.getLongitude());
    }

    private static void addApiKeyParam(Uri.Builder builder) {
        builder.appendQueryParameter("key", PLACES_API_KEY);
    }

    private static void addTypesParam(Uri.Builder builder) {
        StringBuilder typesParamStr = new StringBuilder();

        for (int i = 0; i < placeTypes.length; i++) {
            typesParamStr.append(placeTypes[i]);
            if (i < placeTypes.length - 1) typesParamStr.append("|");
        }

        builder.appendQueryParameter("types", typesParamStr.toString());
    }

}
