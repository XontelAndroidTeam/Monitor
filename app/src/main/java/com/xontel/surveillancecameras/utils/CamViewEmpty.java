package com.xontel.surveillancecameras.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.xontel.surveillancecameras.R;

public class CamViewEmpty extends FrameLayout {
    private Context context;
    public CamViewEmpty(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public CamViewEmpty(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public CamViewEmpty(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    public CamViewEmpty(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        init();
    }

    private void init() {
        inflate(context, R.layout.item_add_cam, this);
    }

}
