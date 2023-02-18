package com.xontel.surveillancecameras.utils;

import android.text.Editable;
import android.util.Log;

public enum CamDeviceType {

    HIKVISION(0),
    DAHUA(1) ;

    private final int value;
    private CamDeviceType(int value) {
        this.value = value;
    }

    public static int getTypeFromString(String text) {
        Log.v("CamDeviceType", "drop down choice : "+text + " "+HIKVISION);
        if(HIKVISION.toString().equalsIgnoreCase(text)){
            return HIKVISION.getValue();
        }else if(DAHUA.toString().equalsIgnoreCase(text)){
            return DAHUA.getValue();
        }
        return -1;
    }


    public int getValue() {
        return value;
    }
}
