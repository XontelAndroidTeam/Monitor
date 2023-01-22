package com.xontel.surveillancecameras.data;

import android.content.Context;

import com.xontel.surveillancecameras.data.db.AppDatabase;
import com.xontel.surveillancecameras.data.db.model.CamDevice;
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
import com.xontel.surveillancecameras.hikvision.HIKDevice;

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

    }

    @Override
    public void updateUserInfo(String accessToken, Long userId, LoggedInMode loggedInMode, String userName, String email, String profilePicPath) {

    }


    @Override
    public Flowable<List<HIKDevice>> getDevicesAll() {
        return mDatabase.mDevicesDao().getDevicesAll();
    }

    @Override
    public Single<Long> insertCamDevice(HIKDevice mCamDevice) {
        return mDatabase.mDevicesDao().insertCamDevice(mCamDevice);
    }

    @Override
    public Single<List<Long>> insertAllCamDevice(HIKDevice... mCamDevicesList) {
        return mDatabase.mDevicesDao().insertAllCamDevice(mCamDevicesList);
    }

    @Override
    public Integer deleteCamDevice(HIKDevice mCamDevice) {
       return   mDatabase.mDevicesDao().deleteCamDevice(mCamDevice);
    }

    @Override
    public void updateCamDevice(HIKDevice mCamDevice) {
         mDatabase.mDevicesDao().updateCamDevice(mCamDevice);
    }

    @Override
    public Single<HIKDevice> getCamDeviceById(int id) {
        return mDatabase.mDevicesDao().getCamDeviceById(id);
    }

    @Override
    public Single<List<HIKDevice>> loadAllDevicesByIds(int[] cameraIds) {
        return mDatabase.mDevicesDao().loadAllDevicesByIds(cameraIds);
    }

    @Override
    public Single<HIKDevice> findDeviceByName(String name) {
        return mDatabase.mDevicesDao().findDeviceByName(name);
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
    public void setFirstTime(boolean firstTime) {
        mPreferencesHelper.setFirstTime(firstTime);
    }

    @Override
    public void setGridCount(int gridCount) {
        mPreferencesHelper.setGridCount(gridCount);
    }

    @Override
    public int getGridCount() {
        return mPreferencesHelper.getGridCount();
    }

    @Override
    public void setAutoSlideShow(boolean autoSlideShow) {
        mPreferencesHelper.setAutoSlideShow(autoSlideShow);
    }

    @Override
    public boolean getAutoSlideShow() {
        return mPreferencesHelper.getAutoSlideShow();
    }

    @Override
    public void setSlideShowInterval(int slideShowInterval) {
        mPreferencesHelper.setSlideShowInterval(slideShowInterval);
    }

    @Override
    public int getSlideShowInterval() {
        return mPreferencesHelper.getSlideShowInterval();
    }

    @Override
    public void setStorageMedia(int storageMedia) {
        mPreferencesHelper.setStorageMedia(storageMedia);
    }

    @Override
    public int getStorageMedia() {
        return mPreferencesHelper.getStorageMedia();
    }


}
