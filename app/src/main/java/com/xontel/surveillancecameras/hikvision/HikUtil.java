package com.xontel.surveillancecameras.hikvision;

import android.annotation.SuppressLint;
import android.util.Log;

import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.INT_PTR;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;
import com.hikvision.netsdk.NET_DVR_DIGITAL_CHANNEL_STATE;
import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.data.db.model.IpCam;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleEmitter;
import io.reactivex.rxjava3.core.SingleOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;
import rx.SingleSubscriber;


public class HikUtil {
    public static final String TAG = HikUtil.class.getSimpleName() ;

    public static int loginNormalDevice(CamDevice camDevice) {
        INT_PTR error = new INT_PTR();

        // get instance
        NET_DVR_DEVICEINFO_V30 netDeviceInfoV30 = new NET_DVR_DEVICEINFO_V30();

        // call NET_DVR_Login_v30 to login on, port 8000 as default
        int iLogID = HCNetSDK.getInstance().NET_DVR_Login_V30(
               camDevice.getDomain(),
                HIKPlayer.DEFAULT_HIKVISION_PORT_NUMBER,
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
        camDevice.setChannels(netDeviceInfoV30.byChanNum + netDeviceInfoV30.byIPChanNum);

        Log.i(TAG, "NET_DVR_Login is Successful!");
        return iLogID;
    }


    public static int getChannelsState(CamDevice camDevice){
        NET_DVR_DIGITAL_CHANNEL_STATE net_dvr_digital_channel_state = new NET_DVR_DIGITAL_CHANNEL_STATE();

        if(!HCNetSDK.getInstance().NET_DVR_GetDVRConfig(camDevice.getLogId(),
                HCNetSDK.NET_DVR_GET_DIGITAL_CHANNEL_STATE,
                0, net_dvr_digital_channel_state)){
            Log.e(TAG, "failed to get channels state");
            camDevice.getCams()
                    .add(new IpCam(1,
                            camDevice.getId(),
                            camDevice.getDeviceType(),
                            camDevice.getId()));
            return -1;
        }else{
            Log.v(TAG, "Suc to get channels state");
            byte[] analogChannels = net_dvr_digital_channel_state.byAnalogChanState;
            byte[] digitalChannels = net_dvr_digital_channel_state.byDigitalChanState;
            for(int i = 0 ; i < analogChannels.length ; i++){
                if(analogChannels[i] == 1){
                    camDevice.getCams().add(new IpCam(i, camDevice.getId(), camDevice.getDeviceType(), camDevice.getLogId()
                    ));
                }
                Log.v(TAG, "channel : "+analogChannels[i]);
            }

            for(int i = 0 ; i < digitalChannels.length ; i++){
                if(digitalChannels[i] == 1){
                    camDevice.getCams().add(new IpCam(i+33, camDevice.getId(), camDevice.getDeviceType(), camDevice.getLogId()
                            ));
                }
                Log.v(TAG, "channel : "+digitalChannels[i]);
            }
            return 0;
        }
    }

    public interface HikInterface{
        void onLogInSuccess(int logId);
        void onLogInFailed();

    }

    @SuppressLint("CheckResult")
    public static void extractCamsFromDevice(CamDevice camDevice){

    }

}
