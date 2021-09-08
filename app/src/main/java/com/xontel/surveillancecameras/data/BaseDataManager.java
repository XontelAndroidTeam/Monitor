package com.xontel.surveillancecameras.data;

import android.content.Context;

import com.xontel.surveillancecameras.data.db.AppDatabase;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.data.db.model.User;
import com.xontel.surveillancecameras.data.network.RestApiHelper;
import com.xontel.surveillancecameras.data.network.pojo.FeedItem;
import com.xontel.surveillancecameras.data.network.pojo.LoginRequest;
import com.xontel.surveillancecameras.data.network.pojo.TimeZoneResponse;
import com.xontel.surveillancecameras.data.network.pojo.UserProfile;
import com.xontel.surveillancecameras.data.network.pojo.WrapperResponse;
import com.xontel.surveillancecameras.data.prefs.PreferencesHelper;
import com.xontel.surveillancecameras.data.utils.LoggedInMode;
import com.xontel.surveillancecameras.di.ApplicationContext;

import java.util.List;

import javax.inject.Inject;


import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;


public class BaseDataManager implements DataManager {
    private static final String TAG = "BaseDataManager";


    private final Context mContext;
    private final AppDatabase mDatabase;
    private final PreferencesHelper mPreferencesHelper;
    private final RestApiHelper mApiHelper;

    @Inject
    public BaseDataManager(@ApplicationContext Context context,
                           AppDatabase database,
                           PreferencesHelper preferencesHelper,
                           RestApiHelper apiHelper) {
        mContext = context;
        mDatabase = database;
        mPreferencesHelper = preferencesHelper;
        mApiHelper = apiHelper;
    }

    @Override
    public void updateApiHeader(Long userId, String accessToken) {

    }

    @Override
    public void setUserLoggedOut() {
        logoutUser();
    }

    @Override
    public void updateUserInfo(String accessToken, Long userId, LoggedInMode loggedInMode, String userName, String email, String profilePicPath) {
        mPreferencesHelper.setAccessToken(accessToken);
        mPreferencesHelper.setUserId(userId);
        mPreferencesHelper.setUserLoggedIn(loggedInMode);
        mPreferencesHelper.setUserName(userName);
        mPreferencesHelper.setUserEmail(email);
        mPreferencesHelper.setUserProfilePicUrl(profilePicPath);
    }


    @Override
    public Flowable<List<IpCam>> getAll() {
        return mDatabase.camDao().getAll();
    }

    @Override
    public Single<Long> insertIpCam(IpCam mIpCam) {
        return mDatabase.camDao().insertIpCam(mIpCam);
    }

    @Override
    public Single<List<Long>> insertAllIpCam(IpCam... mIpCamsList) {
        return mDatabase.camDao().insertAllIpCam(mIpCamsList);
    }

    @Override
    public Single<Integer> deleteIpCam(IpCam mIpCam) {
        return mDatabase.camDao().deleteIpCam(mIpCam);
    }

    @Override
    public Single<Integer> updateIpCam(IpCam mIpCam) {
        return mDatabase.camDao().updateIpCam(mIpCam);
    }

    @Override
    public Single<IpCam> getIpCamById(int id) {
        return mDatabase.camDao().getIpCamById(id);
    }

    @Override
    public Single<List<IpCam>> loadAllByIds(int[] cameraIds) {
        return mDatabase.camDao().loadAllByIds(cameraIds);
    }

    @Override
    public Single<IpCam> findByName(String name) {
        return mDatabase.camDao().findByName(name);
    }

    @Override
    public Single<WrapperResponse<UserProfile>> doLoginApiCall(LoginRequest request) {
        return mApiHelper.doLoginApiCall(request);
    }

    @Override
    public Single<WrapperResponse<List<FeedItem>>> getFeedList() {
        return mApiHelper.getFeedList();
    }

    @Override
    public Single<TimeZoneResponse> getTimeZone() {
        return mApiHelper.getTimeZone();
    }

    @Override
    public int getUserLoggedInMode() {
        return mPreferencesHelper.getUserLoggedInMode();
    }

    @Override
    public void setUserLoggedIn(LoggedInMode mode) {
        mPreferencesHelper.setUserLoggedIn(mode);
    }

    @Override
    public Long getUserId() {
        return mPreferencesHelper.getUserId();
    }

    @Override
    public void setUserId(Long userId) {
        mPreferencesHelper.setUserId(userId);
    }

    @Override
    public String getUserName() {
        return mPreferencesHelper.getUserName();
    }

    @Override
    public void setUserName(String userName) {
        mPreferencesHelper.setUserName(userName);
    }

    @Override
    public String getUserEmail() {
        return mPreferencesHelper.getUserEmail();
    }

    @Override
    public void setUserEmail(String email) {
        mPreferencesHelper.setUserEmail(email);
    }

    @Override
    public String getUserProfilePicUrl() {
        return mPreferencesHelper.getUserProfilePicUrl();
    }

    @Override
    public void setUserProfilePicUrl(String profilePicUrl) {
        mPreferencesHelper.setUserProfilePicUrl(profilePicUrl);
    }

    @Override
    public String getAccessToken() {
        return mPreferencesHelper.getAccessToken();
    }

    @Override
    public void setAccessToken(String accessToken) {
        mPreferencesHelper.getAccessToken();
    }

    @Override
    public String getUserMobile() {
        return mPreferencesHelper.getUserMobile();
    }

    @Override
    public void setUserMobile(String mobileNumber) {
        mPreferencesHelper.setUserMobile(mobileNumber);
    }

    @Override
    public boolean isCoachMarkView() {
        return mPreferencesHelper.isCoachMarkView();
    }

    @Override
    public void setCoachMarkView(boolean isShowCoachMark) {
        mPreferencesHelper.setCoachMarkView(isShowCoachMark);
    }

    @Override
    public boolean isFirstTime() {
        return mPreferencesHelper.isFirstTime();
    }

    @Override
    public void setFirstTime(boolean firstTime) {
        mPreferencesHelper.setCoachMarkView(firstTime);
    }

    @Override
    public void logoutUser() {
        mPreferencesHelper.logoutUser();
    }
}
