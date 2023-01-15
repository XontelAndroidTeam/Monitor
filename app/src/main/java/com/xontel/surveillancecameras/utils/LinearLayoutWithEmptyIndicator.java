package com.xontel.surveillancecameras.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class LinearLayoutWithEmptyIndicator extends LinearLayout {
    private View emptyView;

    private RecyclerView.AdapterDataObserver emptyObserver ;

    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }

    public void setEmptyObserver(RecyclerView.AdapterDataObserver emptyObserver) {
        this.emptyObserver = emptyObserver;
    }

    public LinearLayoutWithEmptyIndicator(Context context) {
        super(context);
        init();
    }

    public LinearLayoutWithEmptyIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LinearLayoutWithEmptyIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public LinearLayoutWithEmptyIndicator(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
    }
}
