package com.xontel.surveillancecameras.presenters;

import android.hardware.camera2.CameraDevice;

import com.xontel.surveillancecameras.base.MvpView;
import com.xontel.surveillancecameras.data.db.model.CamDevice;
import com.xontel.surveillancecameras.data.db.model.IpCam;

import java.util.List;

public interface MainDeviceMvpView extends MvpView {

    void onInsertingDevice();

    void onUpdatingDevice();

    void onDeletingDevice();

    void onGettingDevice(CamDevice response);

    void onGettingAllDevices(List<CamDevice> response);
}
