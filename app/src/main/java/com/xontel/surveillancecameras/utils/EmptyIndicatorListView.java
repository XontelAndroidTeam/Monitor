package com.xontel.surveillancecameras.utils;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

public class EmptyIndicatorListView extends ListView {
    private View emptyView ;


    @Override
    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);

        if(adapter != null) {
            adapter.registerDataSetObserver(emptyObserver);
        }

        emptyObserver.onChanged();
    }

    private DataSetObserver emptyObserver = new DataSetObserver() {


        @Override
        public void onChanged() {
            ListAdapter adapter =  getAdapter();
            if(adapter != null && emptyView != null) {
                if(adapter.getCount() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                    setVisibility(View.GONE);
                }
                else {
                    emptyView.setVisibility(View.GONE);
                    setVisibility(View.VISIBLE);
                }
            }

        }
    };
    public EmptyIndicatorListView(Context context) {
        super(context);
    }

    public EmptyIndicatorListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EmptyIndicatorListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public EmptyIndicatorListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
