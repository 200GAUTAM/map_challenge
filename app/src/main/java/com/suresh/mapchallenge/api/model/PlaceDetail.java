package com.suresh.mapchallenge.api.model;

import java.io.Serializable;

/**
 * Created by suresh on 21/4/15.
 */
public class PlaceDetail implements Serializable {

    public String phoneNumber;
    public OpeningHours openingHours;
    public float avgRating;
    public Review[] reviews;

    public static class OpeningHours implements Serializable {
        public boolean isOpenNow;
        public String[] dayTimings;
    }

    public static class Review implements Serializable {
        public String authorName, reviewText;
        public int rating;
    }

    public void calculateAvgRating() {
        if (reviews != null) {
            int sum = 0;
            for (Review r : reviews) {
                sum += r.rating;
            }

            avgRating = (float) sum / (float) reviews.length;
        }
    }
}
