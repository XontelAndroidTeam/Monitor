package com.xontel.surveillancecameras.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

public class StorageBroadcastReceiver extends BroadcastReceiver {
    SharedPreferences mSharedPreferences ;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        SDCardObservable.getInstance().updateValue(action);
    }
}
