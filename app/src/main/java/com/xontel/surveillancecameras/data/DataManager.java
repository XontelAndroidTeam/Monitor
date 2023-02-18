package com.xontel.surveillancecameras.data;

import android.content.Context;

import com.xontel.surveillancecameras.data.db.dao.CamDao;
import com.xontel.surveillancecameras.data.db.dao.DevicesDao;
import com.xontel.surveillancecameras.data.network.RestApiHelper;
import com.xontel.surveillancecameras.data.prefs.PreferencesHelper;
import com.xontel.surveillancecameras.data.utils.LoggedInMode;
import com.xontel.surveillancecameras.hikvision.CamDevice;
import com.xontel.surveillancecameras.utils.MediaData;

import java.util.List;

import io.reactivex.rxjava3.core.Single;

public interface DataManager extends CamDao, DevicesDao, PreferencesHelper, RestApiHelper {
    void updateApiHeader(Long userId, String accessToken);

    void setUserLoggedOut();

    void updateUserInfo(
            String accessToken,
            Long userId,
            LoggedInMode loggedInMode,
            String userName,
            String email,
            String profilePicPath);

    Single<List<MediaData>> getStoredMedia(Context context, String mediaType);

    Single<Integer> loginHikDevice(CamDevice camDevice);

    Single<Integer> getChannelsInfo(CamDevice camDevice);

    Single<Integer> getCFgInfo(CamDevice camDevice);
}
