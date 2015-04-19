package com.suresh.mapchallenge.api.model;

import java.io.Serializable;
import java.util.HashSet;

/**
 * Created by suresh on 18/4/15.
 */
public class Place implements Serializable {

    public String id, name, photoId, address;
    public HashSet<String> types;
    public double lat, lng;
    public Category category;

    public enum Category {
        BAR(0, new String[]{"bar", "liquor_store"}),
        BAKERY_CAFE(281, new String[]{"bakery", "cafe"}),
        RESTAURANT(196, new String[]{"food", "restaurant"}),
        DELIVERY_TAKEAWAY(44, new String[]{"meal_delivery", "meal_takeaway"});

        public float hue; //Hue value to set the marker colour
        public String[] types; //Map API types that belong to a particular category

        Category(float hue, String[] types) {
            this.hue = hue;
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
