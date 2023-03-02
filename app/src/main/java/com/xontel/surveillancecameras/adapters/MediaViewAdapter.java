package com.xontel.surveillancecameras.adapters;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

public class MediaViewAdapter extends PagerAdapter {


    public MediaViewAdapter(@NonNull FragmentActivity activity, int pagesCount) {
        super(activity, pagesCount);
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return false;
    }
}
