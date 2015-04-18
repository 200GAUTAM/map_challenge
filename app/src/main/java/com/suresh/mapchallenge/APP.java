package com.suresh.mapchallenge;

import android.app.Application;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by suresh on 18/4/15.
 */
public class APP extends Application {

    private static APP instance;
    private RequestQueue requestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        requestQueue = Volley.newRequestQueue(this);
    }

    public static APP getInstance() {
        return instance;
    }

    public void addRequestToQueue(Request request) {
        requestQueue.add(request);
    }
}
