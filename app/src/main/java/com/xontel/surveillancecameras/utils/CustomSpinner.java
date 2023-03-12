package com.xontel.surveillancecameras.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xontel.surveillancecameras.R;

public class CustomSpinner extends FrameLayout {
    private Spinner mSpinner;
    private Context mContext;
    public CustomSpinner(@NonNull Context context) {
        super(context);

        init(context);
    }

    public CustomSpinner(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomSpinner(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public CustomSpinner(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public Spinner getSpinner() {
        return mSpinner;
    }

    private void init(Context context) {
        this.mContext  = context;
        setBackground(context.getDrawable(R.drawable.custom_edit_text));
        setLayoutParams(new LayoutParams(100, 50));
        View view = inflate(mContext, R.layout.drop_down, this);
        mSpinner = view.findViewById(R.id.spinner);
    }
}
