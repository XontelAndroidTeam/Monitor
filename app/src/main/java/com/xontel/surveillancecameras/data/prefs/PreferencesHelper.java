package com.xontel.surveillancecameras.data.prefs;

import com.xontel.surveillancecameras.data.utils.LoggedInMode;

public interface PreferencesHelper {
    void setFirstTime(boolean firstTime);

    void setGridCount(int gridCount);
    int getGridCount();

    void setAutoSlideShow(boolean autoSlideShow);
    boolean getAutoSlideShow();

    void setSlideShowInterval(int slideShowInterval);
    int getSlideShowInterval();

    void setStorageMedia(int storageMedia);
    int getStorageMedia();


}
