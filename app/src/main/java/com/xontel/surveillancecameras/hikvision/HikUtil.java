package com.xontel.surveillancecameras.hikvision;

import android.util.Log;

import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.INT_PTR;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;
import com.xontel.surveillancecameras.data.db.model.IpCam;


public class HikUtil {
    public static final String TAG = HikUtil.class.getSimpleName() ;

    public static int loginNormalDevice(CamDevice camDevice) {
        INT_PTR error = new INT_PTR();

        // get instance
        NET_DVR_DEVICEINFO_V30 netDeviceInfoV30 = new NET_DVR_DEVICEINFO_V30();

        // call NET_DVR_Login_v30 to login on, port 8000 as default
        int iLogID = HCNetSDK.getInstance().NET_DVR_Login_V30(
               camDevice.getIpAddress(),
                HIKSinglePlayer.DEFAULT_HIKVISION_PORT_NUMBER,
                camDevice.getUserName(),
                camDevice.getPassWord(),
                netDeviceInfoV30);
        
        if (iLogID < 0) {
            error.iValue = HCNetSDK.getInstance().NET_DVR_GetLastError();
            Log.e(TAG, "NET_DVR_Login is failed!Err: "
                    + HCNetSDK.getInstance().NET_DVR_GetErrorMsg(error));
            return -1;
        }
        camDevice.setLogId(iLogID);
        if (netDeviceInfoV30.byChanNum > 0) {
            camDevice.setChannels(netDeviceInfoV30.byChanNum);
        } else if (netDeviceInfoV30.byIPChanNum > 0) {
            camDevice.setChannels(netDeviceInfoV30.byIPChanNum);
        }
        Log.i(TAG, "NET_DVR_Login is Successful!");
        return iLogID;
    }

    public interface HikInterface{
        void onLogInSuccess(int logId);
        void onLogInFailed();

    }

    public static void extractCamsFromDevice(CamDevice camDevice){
        int logId = loginNormalDevice(camDevice);
        if(logId < 0){
            return;
        }
        int channels = camDevice.getChannels();
        for(int i = 1 ; i < channels ; i++){
            camDevice.getCams().add(new IpCam(i, camDevice.getId(), camDevice.getDeviceType(),camDevice.getLogId(),camDevice.getName(),camDevice.getIpAddress() == null ? camDevice.getUrl() : camDevice.getIpAddress()));
        }
    }

}
