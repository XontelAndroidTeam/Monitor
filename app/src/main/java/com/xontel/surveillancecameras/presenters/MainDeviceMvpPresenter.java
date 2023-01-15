package com.xontel.surveillancecameras.presenters;

import com.xontel.surveillancecameras.base.MvpPresenter;
import com.xontel.surveillancecameras.data.db.model.CamDevice;

public interface MainDeviceMvpPresenter<V extends MainDeviceMvpView> extends MvpPresenter<V> {
    void createDevice(CamDevice device);
    void updateDevice(CamDevice device);
    void deleteDevice(CamDevice device);
    void getAllDevices();
    void getDeviceById(int id);
}
