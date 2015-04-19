package com.suresh.mapchallenge.api.model;

import java.io.Serializable;
import java.util.HashSet;

public class Place implements Serializable {

    public String id, name, photoId, address;
    public HashSet<String> types;
    public double lat, lng;
    public Category category;

    public enum Category {
        BAR("Bar", 0, new String[]{"bar", "liquor_store"}),
        BAKERY_CAFE("Cafe/Bakery", 281, new String[]{"bakery", "cafe"}),
        RESTAURANT("Restaurant", 196, new String[]{"food", "restaurant"}),
        DELIVERY_TAKEAWAY("Takeaway/Delivery", 44, new String[]{"meal_delivery", "meal_takeaway"});

        public String displayName; //Display name for the category
        public float hue; //Hue value to set the marker colour
        public String[] types; //Map API types that belong to a particular category

        Category(String displayName, float hue, String[] types) {
            this.displayName = displayName;
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

    @Override
    public boolean equals(Object o) {
        if (o instanceof Place) {
            Place other = (Place) o;
            return this.id.equals(other.id);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
