package com.xontel.surveillancecameras.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xontel.surveillancecameras.R;

public class PlayerView extends FrameLayout {
    private TextView mSurfaceView;
    private int order = 0 ;
    public PlayerView(@NonNull Context context, int order) {
        super(context);
        this.order = order;
        init(context);
    }

    public PlayerView(@NonNull Context context, int order, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.order = order;
        init(context);
    }

    public PlayerView(@NonNull Context context, int order, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.order = order;
        init(context);
    }

    public PlayerView(@NonNull Context context, int order, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.order = order;
        init(context);
    }


    private void init(Context context){
       inflate(context, R.layout.item_player, this);
        mSurfaceView = findViewById(R.id.player);

    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mSurfaceView.setText(((ViewGroup)getParent()).indexOfChild(this)+"");
        Log.v("LOGGYY", "onAttachedToWindow ===== "+ ((ViewGroup)getParent()).indexOfChild(this));
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.v("LOGGYY", "onDetachedFromWindow ===== "+ ((ViewGroup)getParent()).indexOfChild(this));
    }
}
