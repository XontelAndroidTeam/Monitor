package com.xontel.surveillancecameras.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xontel.surveillancecameras.R;

public class IpField extends TextField {
    public IpField(@NonNull Context context) {
        super(context);
    }

    public IpField(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public IpField(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean isPatternMatched() {
        Log.v(TAG, "text is : "+getText().toString());
        return Patterns.DOMAIN_NAME.matcher(getText().toString()).matches();
    }

    @Override
    public int getErrorMessageStringId() {
        return R.string.invalid_ip;
    }


}
