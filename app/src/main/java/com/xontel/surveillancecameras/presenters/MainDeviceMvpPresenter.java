package com.xontel.surveillancecameras.presenters;

import com.xontel.surveillancecameras.base.MvpPresenter;
import com.xontel.surveillancecameras.data.db.model.CamDevice;
import com.xontel.surveillancecameras.hikvision.HIKDevice;

public interface MainDeviceMvpPresenter<V extends MainDeviceMvpView> extends MvpPresenter<V> {
    void createDevice(HIKDevice device);
    void updateDevice(HIKDevice device);
    void deleteDevice(HIKDevice device);
    void getAllDevices();
    void getDeviceById(int id);
}
