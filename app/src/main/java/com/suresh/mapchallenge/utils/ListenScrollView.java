package com.suresh.mapchallenge.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class ListenScrollView extends ScrollView {

    private OnScrollChangedListener listener;

    public ListenScrollView(Context context) {
        super(context);
    }

    public ListenScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListenScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setListener(OnScrollChangedListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        if (listener != null) listener.onScrollChanged(l, t, oldl, oldt);
    }

    public interface OnScrollChangedListener {
        public void onScrollChanged(int l, int t, int oldl, int oldt);
    }
}
