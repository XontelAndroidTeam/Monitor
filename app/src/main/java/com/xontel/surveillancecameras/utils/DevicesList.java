package com.xontel.surveillancecameras.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class DevicesList extends RecyclerView {
    private View emptyView;


    private AdapterDataObserver emptyObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            Adapter<?> adapter =  getAdapter();
            if(adapter != null && emptyView != null) {
                if(adapter.getItemCount() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                    DevicesList.this.setVisibility(View.GONE);
                }
                else {
                    emptyView.setVisibility(View.GONE);
                    DevicesList.this.setVisibility(View.VISIBLE);
                }
            }

        }
    };
    public DevicesList(@NonNull Context context) {
        super(context);
    }

    public DevicesList(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DevicesList(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void setEmptyView(View emptyView) {
        this.emptyView = emptyView;
    }


    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);

        if(adapter != null) {
            adapter.registerAdapterDataObserver(emptyObserver);
        }

        emptyObserver.onChanged();
    }
}
