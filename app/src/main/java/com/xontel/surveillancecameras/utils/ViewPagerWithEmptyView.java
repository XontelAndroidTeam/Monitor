package com.xontel.surveillancecameras.utils;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class ViewPagerWithEmptyView extends ViewPager {
    private View emptyView;

    private DataSetObserver emptyObserver = new DataSetObserver() {


        @Override
        public void onChanged() {
            PagerAdapter adapter =  getAdapter();
            if(adapter != null && emptyView != null) {
                if(adapter.getCount() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                    ViewPagerWithEmptyView.this.setVisibility(View.GONE);
                }
                else {
                    emptyView.setVisibility(View.GONE);
                    ViewPagerWithEmptyView.this.setVisibility(View.VISIBLE);
                }
            }

        }
    };

    public ViewPagerWithEmptyView(@NonNull Context context) {
        super(context);
    }

    public ViewPagerWithEmptyView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setAdapter(@Nullable PagerAdapter adapter) {
        super.setAdapter(adapter);

        if(adapter != null) {
            adapter.registerDataSetObserver(emptyObserver);
        }

        emptyObserver.onChanged();
    }

    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }
}
