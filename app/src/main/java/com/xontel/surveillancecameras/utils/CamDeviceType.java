package com.xontel.surveillancecameras.utils;

import android.content.Context;
import android.text.Editable;
import android.util.Log;

import com.xontel.surveillancecameras.R;

public enum CamDeviceType {

    HIKVISION(0),
    DAHUA(1) ;

    private final int value;
    private CamDeviceType(int value) {
        this.value = value;
    }

    public static int getTypeFromString(Context context, String text) {
        String[] types = context.getResources().getStringArray(R.array.device_type);
        Log.v("CamDeviceType", "drop down choice : "+text + " "+HIKVISION);
        if(types[0].equalsIgnoreCase(text)){
            return HIKVISION.getValue();
        }else if(types[1].equalsIgnoreCase(text)){
            return DAHUA.getValue();
        }
        return -1;
    }


    public int getValue() {
        return value;
    }
}
