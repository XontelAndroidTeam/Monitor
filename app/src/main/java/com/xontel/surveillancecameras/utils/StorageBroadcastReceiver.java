package com.xontel.surveillancecameras.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.xontel.surveillancecameras.R;

public class StorageBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action){
            case Intent.ACTION_MEDIA_MOUNTED:
              Log.e(StorageBroadcastReceiver.class.getSimpleName(), context.getString(R.string.media_storage_ready));
                break;
            case Intent.ACTION_MEDIA_EJECT:
                Log.e(StorageBroadcastReceiver.class.getSimpleName(), context.getString(R.string.media_ejected));
        }
        SDCardObservable.getInstance().updateValue(action);
    }
}
