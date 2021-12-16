package com.xontel.surveillancecameras.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.xontel.surveillancecameras.R;

public class StorageBroadcastReceiver extends BroadcastReceiver {
    SharedPreferences mSharedPreferences ;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action){
            case Intent.ACTION_MEDIA_MOUNTED:
                Toast.makeText(context, R.string.media_storage_ready, Toast.LENGTH_LONG).show();
                break;
            case Intent.ACTION_MEDIA_EJECT:
                Toast.makeText(context, R.string.media_ejected, Toast.LENGTH_LONG).show();
        }
        SDCardObservable.getInstance().updateValue(action);
    }
}
