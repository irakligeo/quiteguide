package com.mmgct.quitguide2.views.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.RelativeLayout;

/**
 * Created by 35527 on 11/10/2015.
 */
public class SlideRelativeLayout extends RelativeLayout {

    public SlideRelativeLayout(Context context) {
        super(context);
    }

    public SlideRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public int getXFraction() {
        int width = getResources().getDisplayMetrics().widthPixels;
        return (width == 0) ? 0 : (int) (getX()/width);
    }

    public void setXFraction (float xFraction) {
        int width = getResources().getDisplayMetrics().widthPixels;
        setX((int)(xFraction*width));
    }

    public int getYFraction() {
        int height = getResources().getDisplayMetrics().heightPixels;
        return (height == 0) ? 0 : (int)(getY()/height);
    }

    public void setYFraction(float yFraction) {
        int height = getResources().getDisplayMetrics().heightPixels;
         setY((int)(yFraction*height));
    }
}
