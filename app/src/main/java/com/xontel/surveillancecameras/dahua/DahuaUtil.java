package com.xontel.surveillancecameras.dahua;

import android.util.Log;

import com.company.NetSDK.INetSDK;
import com.company.NetSDK.NET_DEVICEINFO_Ex;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.hikvision.CamDevice;

import io.reactivex.rxjava3.core.Single;


public class DahuaUtil {
    public static final String TAG = DahuaUtil.class.getSimpleName();


    public static Single<CamDevice> loginNormalDevice(CamDevice camDevice) {
        return Single.create(
                emitter -> {
                    NET_DEVICEINFO_Ex net_deviceinfo_ex = new NET_DEVICEINFO_Ex();
                    long iLogID = INetSDK.LoginEx2(
                            camDevice.getDomain(),
                            DahuaPlayer.DEFAULT_Dahua_PORT_NUMBER,
                            camDevice.userName,
                            camDevice.password,
                            20,
                            null,
                            net_deviceinfo_ex,
                            0);
                    if (iLogID == 0) {
                        String errorMessage = "NET_DVR_Login is failed!Err: ";
                        Log.e(TAG, errorMessage);
                    } else {
                        Log.i(TAG, "DAHUA_Login is Successful! "+iLogID);
                    }
                    camDevice.setLogId((int) iLogID);
                    for (int i = 0; i < net_deviceinfo_ex.nChanNum; i++) {
                        Log.v(TAG, "channel : " + net_deviceinfo_ex.nChanNum);
                            camDevice.getCams().add(new IpCam(i+1, (int) camDevice.getId(), camDevice.getDeviceType(), camDevice.getLogId()
                            ));
                    }


                    camDevice.setChannels(camDevice.getCams().size());
                    emitter.onSuccess(camDevice);
                }
        );
    }
}
