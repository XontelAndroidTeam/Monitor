package com.xontel.surveillancecameras.dahua;

import android.util.Log;

import com.company.NetSDK.INetSDK;
import com.company.NetSDK.NET_DEVICEINFO_Ex;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.hikvision.CamDevice;
import com.xontel.surveillancecameras.hikvision.HIKSinglePlayer;


public class DahuaUtil {
    public static final String TAG = DahuaUtil.class.getSimpleName() ;

    public static int loginNormalDevice(CamDevice camDevice) {
        NET_DEVICEINFO_Ex net_deviceinfo_ex = new NET_DEVICEINFO_Ex();
        long iLogID = INetSDK.LoginEx2(camDevice.ipAddress,  HIKSinglePlayer.DEFAULT_Dahua_PORT_NUMBER, camDevice.userName, camDevice.password, 20, null, net_deviceinfo_ex, 0);
        if (iLogID == 0) {
            Log.e(TAG, "NET_DVR_Login is failed!Err: " + "error");
            return 0;
        }

        camDevice.setLogId((int)iLogID);
        if (net_deviceinfo_ex.nChanNum > 0) {
            camDevice.setChannels(net_deviceinfo_ex.nChanNum);
        }
        Log.i(TAG, "NET_DVR_Login is Successful!"+(int)iLogID);
        return (int)iLogID;
    }

    public static void extractCamsFromDevice(CamDevice camDevice){
        int logId =  loginNormalDevice(camDevice);
        if(logId == 0){
            return;
        }
        int channels = camDevice.getChannels();
        for(int i = 0 ; i < channels ; i++){
            camDevice.getCams().add(new IpCam(i, camDevice.getId(), camDevice.getDeviceType(),camDevice.getLogId(),camDevice.getName(),camDevice.getIpAddress() == null ? camDevice.getUrl() : camDevice.getIpAddress() ));
        }
    }
}
