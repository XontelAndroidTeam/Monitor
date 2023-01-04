package com.xontel.surveillancecameras.utils;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xontel.surveillancecameras.R;

public class NormalTextField extends TextField{
    public NormalTextField(@NonNull Context context) {
        super(context);
    }

    public NormalTextField(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public NormalTextField(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isPatternMatched() {
        return true;
    }

    @Override
    public int getErrorMessageStringId() {
        return R.string.empty_field;
    }
}
