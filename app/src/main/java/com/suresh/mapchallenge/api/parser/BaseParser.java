package com.suresh.mapchallenge.api.parser;

import com.android.volley.Response;

/**
 * Created by suresh on 18/4/15.
 */
public abstract class BaseParser<T, V> implements Response.Listener<T> {

    private ResultListener<V> resultListener;

    protected BaseParser(ResultListener<V> resultListener) {
        this.resultListener = resultListener;
    }

    @Override
    public void onResponse(T response) {
        V result = parseResult(response);
        if (resultListener != null) resultListener.consumeResult(result);
    }

    public abstract V parseResult(T jsonResult);

    public interface ResultListener<V> {
        public void consumeResult(V result);
    }

}
