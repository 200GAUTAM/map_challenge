package com.suresh.mapchallenge.utils;

import android.util.LruCache;

import com.google.android.gms.maps.model.Marker;
import com.suresh.mapchallenge.api.model.Place;

/**
 * LruCache extension that stores all the markers and their corresponding places. LruCache will clear out the oldest markers when the maximum marker count is reached.
 * This way, we can limit/control the total number of markers displayed on the map at any time.
 */
public class MarkerCache extends LruCache<Marker, Place> {

    private MarkerEvictedListener listener;

    public MarkerCache(int maxSize, MarkerEvictedListener listener) {
        super(maxSize);
        this.listener = listener;
    }

    @Override
    protected void entryRemoved(boolean evicted, Marker key, Place oldValue, Place newValue) {
        if (listener != null) listener.onMarkerEvictedFromCache(key, oldValue);
    }

    public void setListener(MarkerEvictedListener listener) {
        this.listener = listener;
    }

    public interface MarkerEvictedListener {
        public void onMarkerEvictedFromCache(Marker m, Place p);
    }
}
