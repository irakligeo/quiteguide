package com.mmgct.quitguide2.views.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by 35527 on 12/4/2015.
 */
public class SlideToDeleteLayout extends LinearLayout {

    private static final String TAG = "SlideToDeleteLayout";
    private GestureDetector mGestures;
    private Matrix mTranslate;
    private Bitmap rootView;
    private float mConstantDx;

    public SlideToDeleteLayout(Context context) {
        super(context);
        GestureListener listener = new GestureListener(this);
        mGestures = new GestureDetector(context, listener, null, true);
        mTranslate = new Matrix();
        rootView = getDrawingCache();
    }

    public SlideToDeleteLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        GestureListener listener = new GestureListener(this);
        mGestures = new GestureDetector(context, listener, null, true);
        mTranslate = new Matrix();
        rootView = getDrawingCache();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean retVal = false;
        retVal = mGestures.onTouchEvent(event);
        return retVal;
    }

    /*@Override
    protected void onDraw(Canvas canvas) {
        Log.v(TAG, "onDraw");
        canvas.drawBitmap(getDrawingCache(), mTranslate, null);
    }*/

    public void onMove(final float dx, float dy) {
       // mTranslate.postTranslate(dx, dy);
       // invalidate();
        Log.d(TAG, "dx= " + dx);
        final DisplayMetrics dm = getResources().getDisplayMetrics();
        if (dx >= 0) {
            post(new Runnable() {
                @Override
                public void run() {
                    setX(Math.min(dx/dm.density, dm.density * 50));
                }
            });
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    setX(Math.max(dx/dm.density, dm.density * 50));
                }
            });
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        SlideToDeleteLayout mView;
        float mXLineLength;

        public GestureListener(SlideToDeleteLayout view) {
            mView = view;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true; // Must return true for any gesture motion you wish to detect
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            mView.onMove(-distanceX, -distanceY);
            return true;
        }
    }
}
