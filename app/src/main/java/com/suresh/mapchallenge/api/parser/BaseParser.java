package com.suresh.mapchallenge.api.parser;

import android.util.Log;

import com.android.volley.Response;

/**
 * Created by suresh on 18/4/15.
 */
public abstract class BaseParser<T, V> implements Response.Listener<T> {

    protected ResultListener<V> resultListener;

    public BaseParser(ResultListener<V> resultListener) {
        this.resultListener = resultListener;
    }

    @Override
    public void onResponse(T response) {
        V result = null;
        try {
            result = parseResult(response);
        } catch (Exception e) {
            Log.e("BaseParser", Log.getStackTraceString(e));
        }

        if (resultListener != null) resultListener.consumeResult(result);
    }

    public abstract V parseResult(T json) throws Exception;

    public interface ResultListener<V> {
        public void consumeResult(V result);
    }

}
