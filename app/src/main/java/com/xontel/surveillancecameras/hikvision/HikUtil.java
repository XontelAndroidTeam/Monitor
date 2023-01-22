package com.xontel.surveillancecameras.hikvision;

import android.graphics.Bitmap;
import android.util.Log;

import com.hikvision.netsdk.ExceptionCallBack;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.INT_PTR;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;
import com.xontel.surveillancecameras.data.db.model.IpCam;

import org.MediaPlayer.PlayM4.Player;

import java.text.SimpleDateFormat;

/**
 * Created date: 2017/4/11
 * Author: Leslie
 * A tool class for playing video stream using Hikvision SDK.
 * Prerequisite: 1->/libs/ put: AudioEngineSDK.jar, HCNetSDK.jar, PlayerSDK.jar
 * 2->/src/main/jniLibs/ put: many .so files.
 * 3->Add network permission
 * Currently only processing Hikvision cameras (indoor box-type network cameras-[model: DS-2CD5026EFWD]-[software version: V5.4.5_170222])
 * But the example is not limited to this model.
 * Usage method [Because there are 2 ways to preview, so many static methods, static variables are removed, and the calling process has also changed]:
 * 1.HikUtil.initSDK();
 * 2. HikUtil hikUtil = new HikUtil();
 * 2.hikUtil.initView(surfaceView);
 * 3.hikUtil.setDeviceData("192.168.1.22",8000,"admin","eyecool2016");
 * 4.hikUtil.loginDevice(mHandler,LOGIN_SUCCESS_CODE);
 * 5.hikUtil.playOrStopStream();
 */

public class HikUtil {
    public static final String TAG = HikUtil.class.getSimpleName() ;

    /**
     * @return login ID
     * @fn loginNormalDevice
     * @brief login on device
     */
    public static int loginNormalDevice(HIKDevice hikDevice) {
        INT_PTR error = new INT_PTR();

        // get instance
        NET_DVR_DEVICEINFO_V30 netDeviceInfoV30 = new NET_DVR_DEVICEINFO_V30();

        // call NET_DVR_Login_v30 to login on, port 8000 as default
        int iLogID = HCNetSDK.getInstance().NET_DVR_Login_V30(
               hikDevice.getIpAddress(),
                HikPlayer.DEFAULT_HIKVISION_PORT_NUMBER,
                hikDevice.getUserName(),
                hikDevice.getPassWord(),
                netDeviceInfoV30);
        
        if (iLogID < 0) {
            error.iValue = HCNetSDK.getInstance().NET_DVR_GetLastError();
            Log.e(TAG, "NET_DVR_Login is failed!Err: "
                    + HCNetSDK.getInstance().NET_DVR_GetErrorMsg(error));
            return -1;
        }
        hikDevice.setLogId(iLogID);
        if (netDeviceInfoV30.byChanNum > 0) {
            hikDevice.setChannels(netDeviceInfoV30.byChanNum);
        } else if (netDeviceInfoV30.byIPChanNum > 0) {
            hikDevice.setChannels(netDeviceInfoV30.byIPChanNum);
        }
        Log.i(TAG, "NET_DVR_Login is Successful!");
        return iLogID;
    }

    public interface HikInterface{
        void onLogInSuccess(int logId);
        void onLogInFailed();

    }

    public static void extractCamsFromDevice(HIKDevice hikDevice){
        int logId = loginNormalDevice(hikDevice);
        if(logId < 0){
            return;
        }
        int channels = hikDevice.getChannels();
        for(int i = 0 ; i < channels ; i++){
            hikDevice.getCams().add(new IpCam(i, hikDevice.getId(), hikDevice.getDeviceType()));
        }
    }

}
