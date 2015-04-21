package com.suresh.mapchallenge.utils;

import android.animation.Animator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by suresh on 21/4/15.
 */
public class Utils {

    public static void animateTransition(View view, long duration, boolean shouldDisplay) {
        float alphaVal = (shouldDisplay) ? 1 : 0;
        int visibility = (shouldDisplay) ? View.VISIBLE : View.GONE;

        view.animate()
                .setDuration(duration)
                .setInterpolator(new DecelerateInterpolator())
                .alpha(alphaVal)
                .setListener(new FadeAnimationListener(view, visibility))
                .start();
    }

    private static class FadeAnimationListener implements Animator.AnimatorListener {

        private View view;
        private int visibilityAfterAnim;

        private FadeAnimationListener(View view, int visibility) {
            this.view = view;
            visibilityAfterAnim = visibility;
        }

        @Override
        public void onAnimationStart(Animator animation) {
            if (visibilityAfterAnim == View.VISIBLE) view.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            if (visibilityAfterAnim == View.GONE) view.setVisibility(View.GONE);
        }

        @Override public void onAnimationCancel(Animator animation) { }
        @Override public void onAnimationRepeat(Animator animation) { }
    }
}
