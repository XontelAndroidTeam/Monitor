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
    public static final int CONNECTED = 1;
    public static final int DIGITAL_CHANNELS_START = 33;
    public static final String TAG = HikUtil.class.getSimpleName() ;

    public static Single<CamDevice> loginNormalDevice(CamDevice camDevice) {
       return Single.create(
               emitter -> {
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
                       String errorMessage = "NET_DVR_Login is failed!Err: "
                               + HCNetSDK.getInstance().NET_DVR_GetErrorMsg(error);
                       Log.i(TAG, errorMessage);
                   }else {
                       Log.i(TAG, "NET_DVR_Login is Successful!");

                   }
                   camDevice.setLogId(iLogID);
                   emitter.onSuccess(camDevice);
               }
       ) ;

    }

    public static Single<CamDevice> getChannelsState(CamDevice camDevice){
        return Single.create(
                emitter -> {
                    INT_PTR int_ptr = new INT_PTR();
                    NET_DVR_DIGITAL_CHANNEL_STATE net_dvr_digital_channel_state = new NET_DVR_DIGITAL_CHANNEL_STATE();

                    if (!HCNetSDK.getInstance().NET_DVR_GetDVRConfig(camDevice.getLogId(),
                            HCNetSDK.NET_DVR_GET_DIGITAL_CHANNEL_STATE,
                            0, net_dvr_digital_channel_state)) {
                        int_ptr.iValue = HCNetSDK.getInstance().NET_DVR_GetLastError();
                        String errorMessage =  "failed to get channels state "+ HCNetSDK.getInstance().NET_DVR_GetErrorMsg(int_ptr);
                        Log.e(TAG, errorMessage);
//                        camDevice.getCams()
//                                .add(new IpCam(1,
//                                        (int) camDevice.getId(),
//                                        camDevice.getDeviceType(),
//                                        (int) camDevice.getId()));
                    } else {
                        Log.v(TAG, "Suc to get channels state "+ camDevice.getDomain());
                        byte[] analogChannels = net_dvr_digital_channel_state.byAnalogChanState;
                        byte[] digitalChannels = net_dvr_digital_channel_state.byDigitalChanState;
                        Log.v(TAG, "analog :  ");
                        for (int i = 0; i < analogChannels.length; i++) {
                            Log.v(TAG, "channel : " + analogChannels[i]);
                            if (analogChannels[i] == CONNECTED) {
                                camDevice.getCams().add(new IpCam(i+1, (int) camDevice.getId(), camDevice.getDeviceType(), camDevice.getLogId()
                                ));
                            }
                        }

                        Log.v(TAG, "digital :  ");
                        for (int i = 0; i < digitalChannels.length; i++) {
                            Log.v(TAG, "channel : " + digitalChannels[i]);
                            if (digitalChannels[i] == CONNECTED) {
                                camDevice.getCams().add(new IpCam(i + DIGITAL_CHANNELS_START, (int) camDevice.getId(), camDevice.getDeviceType(), camDevice.getLogId()
                                ));
                            }
                        }

                    }
                    camDevice.setChannels(camDevice.getCams().size());
                    emitter.onSuccess(camDevice);
                });
    }


    public interface HikInterface{
        void onLogInSuccess(int logId);
        void onLogInFailed();

    }

    @SuppressLint("CheckResult")
    public static void extractCamsFromDevice(CamDevice camDevice){

    }

}
