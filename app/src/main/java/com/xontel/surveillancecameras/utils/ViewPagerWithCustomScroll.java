package com.xontel.surveillancecameras.utils;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.lang.reflect.Field;

public class ViewPagerWithCustomScroll extends ViewPager {

    private Boolean disable = false;
    public ViewPagerWithCustomScroll(Context context) {
        super(context);
        init();
    }

    private void init() {
        try {
            Field mScroller;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            Field interpolator = ViewPager.class.getDeclaredField("sInterpolator");
            interpolator.setAccessible(true);
            FixedSpeedScroller scroller = new FixedSpeedScroller(getContext(), (Interpolator) interpolator.get(null));
            // scroller.setFixedDuration(5000);
            mScroller.set(this, scroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ViewPagerWithCustomScroll(Context context, AttributeSet attrs){
        super(context,attrs);
        init();
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return !disable && super.onInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return !disable && super.onTouchEvent(event);
    }

    public void disableScroll(Boolean disable){
        //When disable = true not work the scroll and when disble = false work the scroll
        this.disable = disable;
    }
}
