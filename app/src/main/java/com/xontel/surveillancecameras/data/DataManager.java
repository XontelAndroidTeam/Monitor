package com.xontel.surveillancecameras.data;

import com.xontel.surveillancecameras.data.db.dao.CamDao;
import com.xontel.surveillancecameras.data.network.RestApiHelper;
import com.xontel.surveillancecameras.data.prefs.PreferencesHelper;
import com.xontel.surveillancecameras.data.utils.LoggedInMode;

public interface DataManager extends CamDao, PreferencesHelper, RestApiHelper {
    void updateApiHeader(Long userId, String accessToken);

    void setUserLoggedOut();

    void updateUserInfo(
            String accessToken,
            Long userId,
            LoggedInMode loggedInMode,
            String userName,
            String email,
            String profilePicPath);
}
