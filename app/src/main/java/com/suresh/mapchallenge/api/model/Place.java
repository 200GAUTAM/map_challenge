package com.suresh.mapchallenge.api.model;

import java.util.ArrayList;

/**
 * Created by suresh on 18/4/15.
 */
public class Place {

    public String id, name, photoId;
    public ArrayList<String> types;
    public double lat, lng;

    @Override
    public String toString() {
        String output = "ID: " + id + ", Name: " + name + ", types: " + types
                + ", Location: (" + lat + "," + lng + ")";
        if (photoId != null) output += ", Photo ID: " + photoId;
        return output;
    }
}
