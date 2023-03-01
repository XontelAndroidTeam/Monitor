package com.xontel.surveillancecameras.customObservers;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.xontel.surveillancecameras.BR;
import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.data.DataManager;
import com.xontel.surveillancecameras.enums.StorageMedia;
import com.xontel.surveillancecameras.utils.SharedPreferencesPropertyWrapper;

import javax.inject.Inject;

public class GridObservable extends BaseObservable {
    private int gridCount;
    private int iconId ;
    private DataManager mDataManager;
    private Context mContext;


    @Inject
    public GridObservable(Context context, DataManager dataManager) {
        this.mDataManager = dataManager;
        this.mContext = context;
        Log.v("testo", "fff2");
    }

    @Bindable
    public String getGridCount() {
        return String.valueOf(mDataManager.getGridCount());
    }

    public int getValue() {
        return mDataManager.getGridCount();
    }



    public void setGridCount(String stringValue) {
        int value = Integer.parseInt(stringValue);
        // Avoids infinite loops.
        if (gridCount != value) {
            gridCount = value;

            // React to the change.
            saveGridCount(gridCount);
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    // Notify observers of a new value.
                    notifyPropertyChanged(BR.gridCount);
                }
            });

        }
    }

    private void saveGridCount(int gridCount) {
        mDataManager.setGridCount(gridCount);
    }



}
