package com.suresh.mapchallenge.api.parser;

import com.suresh.mapchallenge.api.model.PlaceDetail;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by suresh on 21/4/15.
 */
public class PlaceDetailParser extends BaseParser<JSONObject, PlaceDetail> {

    public PlaceDetailParser(ResultListener<PlaceDetail> resultListener) {
        super(resultListener);
    }

    @Override
    public PlaceDetail parseResult(JSONObject json) throws Exception {
        if (json.getString("status").equalsIgnoreCase("OK")) {
            JSONObject info = json.getJSONObject("result");
            PlaceDetail detail = new PlaceDetail();

            //Phone number
            if (info.has("international_phone_number")) {
                detail.phoneNumber = info.getString("international_phone_number");
            }

            //Parse opening hours if available
            if (info.has("opening_hours")) {
                JSONObject opInfo = info.getJSONObject("opening_hours");
                PlaceDetail.OpeningHours opHrs = new PlaceDetail.OpeningHours();
                detail.openingHours = opHrs;

                opHrs.openNow = opInfo.getBoolean("open_now");

                JSONArray dayTimingsText = opInfo.getJSONArray("weekday_text");
                String[] dayTimings = new String[dayTimingsText.length()];
                opHrs.dayTimings = dayTimings;
                for (int i = 0; i < dayTimings.length; i++) {
                    dayTimings[i] = dayTimingsText.getString(i);
                }
            }

            //Parse reviews if any
            if (info.has("reviews")) {
                JSONArray reviewArray = info.getJSONArray("reviews");
                PlaceDetail.Review[] reviews = new PlaceDetail.Review[reviewArray.length()];
                detail.reviews = reviews;

                for (int i = 0; i < reviews.length; i++) {
                    JSONObject reviewObj = reviewArray.getJSONObject(i);
                    PlaceDetail.Review review = new PlaceDetail.Review();
                    reviews[i] = review;

                    review.authorName = reviewObj.getString("author_name");
                    review.rating = reviewObj.getInt("rating");
                    review.reviewText = reviewObj.getString("text");
                }
            }

            return detail;
        }

        return null;
    }
}
