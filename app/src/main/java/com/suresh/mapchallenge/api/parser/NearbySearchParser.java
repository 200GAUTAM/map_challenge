package com.suresh.mapchallenge.api.parser;

import android.util.Log;

import com.suresh.mapchallenge.api.model.Place;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by suresh on 18/4/15.
 */
public class NearbySearchParser extends BaseParser<JSONObject, ArrayList<Place>> {

    public NearbySearchParser(ResultListener<ArrayList<Place>> resultListener) {
        super(resultListener);
    }

    @Override
    public ArrayList<Place> parseResult(JSONObject json) throws Exception {
        ArrayList<Place> places = new ArrayList<Place>();

        if (json.getString("status").equalsIgnoreCase("OK")) {
            JSONArray placesArr = json.getJSONArray("results");

            for (int i = 0; i < placesArr.length(); i++) {
                JSONObject placeObj = placesArr.getJSONObject(i);
                Place place = new Place();
                places.add(place);

                //Parse basic info
                place.id = placeObj.getString("id");
                place.name = placeObj.getString("name");
                place.address = placeObj.getString("vicinity");
                JSONObject locationObj = placeObj.getJSONObject("geometry").getJSONObject("location");
                place.lat = locationObj.getDouble("lat");
                place.lng = locationObj.getDouble("lng");

                //Parse types
                ArrayList<String> types = new ArrayList<String>();
                place.types = types;
                JSONArray typeArr = placeObj.getJSONArray("types");
                for (int j = 0; j < typeArr.length(); j++) {
                    types.add(typeArr.getString(j));
                }

                //Parse photo if available
                if (placeObj.has("photos")) {
                    JSONObject photoObj = placeObj.getJSONArray("photos").getJSONObject(0); //Only one photo returned in this endpoint
                    place.photoId = photoObj.getString("photo_reference");
                }
            }
        } else {
            Log.w("NearbySearchParser", json.toString());
        }

        return places;
    }
}
