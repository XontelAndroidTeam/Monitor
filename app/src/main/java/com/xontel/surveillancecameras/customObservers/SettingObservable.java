package com.xontel.surveillancecameras.customObservers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.xontel.surveillancecameras.BR;
import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.data.DataManager;
import com.xontel.surveillancecameras.enums.StorageMedia;
import com.xontel.surveillancecameras.utils.SharedPreferencesPropertyWrapper;

import javax.inject.Inject;

public class SettingObservable extends BaseObservable {
    private boolean autoSlideShow;
    private int slideShowInterval;
    private StorageMedia mStorageMedia;
    private DataManager mDataManager;
    private Context mContext;


    @Inject
    public SettingObservable(Context context, DataManager dataManager) {
        this.mDataManager = dataManager;
        this.mContext = context;
    }

    @Bindable
    public Boolean getAutoSlideShow() {
        return mDataManager.getAutoSlideShow();
    }

    @Bindable
    public String getSlideShowInterval() {
        return mContext.getResources().getString(R.string.seconds_formated, mDataManager.getSlideShowInterval());
    }

    @Bindable
    public String getStorageMedia() {
        return mDataManager.getStorageMedia()+"";
    }

    public void setAutoSlideShow(Boolean value) {
        // Avoids infinite loops.
        if (autoSlideShow != value) {
            autoSlideShow = value;

            // React to the change.
            saveAutoSlideShow(autoSlideShow);

            // Notify observers of a new value.
            notifyPropertyChanged(BR.autoSlideShow);
        }
    }


    public void setSlideShowInterval(String value) {
        int intervalInSeconds;
        try {
            intervalInSeconds = Integer.parseInt(value.split(" ")[0]);
        } catch (NumberFormatException numberFormatException) {
            numberFormatException.printStackTrace();
            intervalInSeconds = SharedPreferencesPropertyWrapper.DEFAULT_SLIDE_SHOW_INTERVAL;
        }
        // Avoids infinite loops.
        if (slideShowInterval != intervalInSeconds) {
            slideShowInterval = intervalInSeconds;

            // React to the change.
            saveSlideShowInterval(slideShowInterval);

            // Notify observers of a new value.
            notifyPropertyChanged(BR.slideShowInterval);
        }
    }


    public void setStorageMedia(String value) {
        // Avoids infinite loops.
//        if (mStorageMedia.getValue() == value.getValue()) {
//            mStorageMedia = value;
//
//             React to the change.
//            saveStorageMedia(mStorageMedia);
//
//             Notify observers of a new value.
//            notifyPropertyChanged(BR.storageMedia);
//        }
    }

    private void saveSlideShowInterval(int slideShowInterval) {
        Log.v("TAGGH", "slideShowInterval  saved");
        mDataManager.setSlideShowInterval(slideShowInterval);
    }

    private void saveAutoSlideShow(boolean autoSlideShow) {
        Log.v("TAGGH", "autoSlideShow  saved");
        mDataManager.setAutoSlideShow(autoSlideShow);
    }

    private void saveStorageMedia(StorageMedia storageMedia) {
        Log.v("TAGGH", "storageMedia  saved");
        mDataManager.setStorageMedia(storageMedia.getValue());
    }


}
