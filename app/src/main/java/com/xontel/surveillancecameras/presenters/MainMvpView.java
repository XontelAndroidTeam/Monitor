package com.xontel.surveillancecameras.presenters;


import com.xontel.surveillancecameras.base.MvpView;
import com.xontel.surveillancecameras.data.db.model.IpCam;

import java.util.List;

public interface MainMvpView extends MvpView {

    void onInsertingCamera();

    void onUpdatingCamera();

    void onDeletingCamera();

    void onGettingCamera(IpCam response);

    void onGettingAllCameras(List<IpCam> response);
}
