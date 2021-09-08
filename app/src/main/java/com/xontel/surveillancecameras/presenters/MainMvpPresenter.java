package com.xontel.surveillancecameras.presenters;


import com.xontel.surveillancecameras.base.MvpPresenter;
import com.xontel.surveillancecameras.data.db.model.IpCam;

public interface MainMvpPresenter <V extends MainMvpView> extends MvpPresenter<V> {
    void createCamera(IpCam ipCam);
    void updateCamera(IpCam ipCam);
    void deleteCamera(IpCam ipCam);
    void getAllCameras();
    void getCameraById(int id);
}
