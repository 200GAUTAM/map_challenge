package com.suresh.mapchallenge;

import android.app.Application;
import android.os.Handler;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by suresh on 18/4/15.
 */
public class APP extends Application {

    private static APP instance;
    private RequestQueue requestQueue;
    private Handler delayHandler; //Handler to post delayed requests

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        delayHandler = new Handler();
        requestQueue = Volley.newRequestQueue(this);
    }

    public static APP getInstance() {
        return instance;
    }

    public void addRequest(Request request) {
        requestQueue.add(request);
    }

    public void addRequestWithDelay(final Request request, long millis) {
        delayHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                requestQueue.add(request);
            }
        }, millis);
    }
}
