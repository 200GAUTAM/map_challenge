package com.suresh.mapchallenge.api.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by suresh on 18/4/15.
 */
public class Place implements Serializable {

    public String id, name, photoId, address;
    public ArrayList<String> types;
    public double lat, lng;
    public Category category;

    public enum Category {
        BAR(new String[]{"bar", "liquor_store"}),
        BAKERY_CAFE(new String[]{"bakery", "cafe"}),
        RESTAURANT(new String[]{"food", "restaurant", "meal_delivery", "meal_takeaway"});

        public String[] types; //Map API types that belong to a particular category

        Category(String[] types) {
            this.types = types;
        }
    }

    @Override
    public String toString() {
        String output = "ID: " + id + ", Name: " + name + ", Address: " + address
                + ", Category: " + category + ", types: " + types
                + ", Location: (" + lat + "," + lng + ")";
        if (photoId != null) output += ", Photo ID: " + photoId;
        return output;
    }
}
