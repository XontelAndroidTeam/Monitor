package com.xontel.surveillancecameras.presenters;

import android.hardware.camera2.CameraDevice;

import com.xontel.surveillancecameras.base.MvpView;
import com.xontel.surveillancecameras.data.db.model.CamDevice;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.hikvision.HIKDevice;

import java.util.List;

public interface MainDeviceMvpView extends MvpView {

    void onInsertingDevice();

    void onUpdatingDevice();

    void onDeletingDevice();

    void onGettingDevice(HIKDevice response);

    void onGettingAllDevices(List<HIKDevice> response);
}
