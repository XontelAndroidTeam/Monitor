package com.xontel.surveillancecameras.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.xontel.surveillancecameras.R;

public class StorageBroadcastReceiver extends BroadcastReceiver {

    public static MutableLiveData<Boolean> refreshRemovable = new MutableLiveData<>(false);

    public static MutableLiveData<Boolean> getObservable(){return refreshRemovable;}

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        switch (action){
            case Intent.ACTION_MEDIA_MOUNTED:
                Log.i("TATZ", "ACTION_MEDIA_MOUNTED: ");
                refreshRemovable.setValue(true);
                break;
            case Intent.ACTION_MEDIA_EJECT:
                Log.i("TATZ", "ACTION_MEDIA_EJECT: ");
                refreshRemovable.setValue(true);
        }
     //   SDCardObservable.getInstance().updateValue(action);
    }
}
