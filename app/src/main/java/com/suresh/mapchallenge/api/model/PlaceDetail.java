package com.suresh.mapchallenge.api.model;

/**
 * Created by suresh on 21/4/15.
 */
public class PlaceDetail {

    public String phoneNumber;
    public OpeningHours openingHours;
    public float avgRating;
    public Review[] reviews;

    public static class OpeningHours {
        public boolean isOpenNow;
        public String[] dayTimings;
    }

    public static class Review {
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
