package com.xontel.surveillancecameras.hikvision;

import static com.hikvision.netsdk.SDKError.NET_DVR_MAX_NUM;
import static com.hikvision.netsdk.SDKError.NET_DVR_MAX_USERNUM;
import static com.hikvision.netsdk.SDKError.NET_DVR_NETWORK_FAIL_CONNECT;
import static com.hikvision.netsdk.SDKError.NET_DVR_NETWORK_RECV_ERROR;
import static com.hikvision.netsdk.SDKError.NET_DVR_NETWORK_RECV_TIMEOUT;
import static com.hikvision.netsdk.SDKError.NET_DVR_NETWORK_SEND_ERROR;
import static com.hikvision.netsdk.SDKError.NET_DVR_PASSWORD_ERROR;

import android.content.Context;
import android.util.Log;

import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.INT_PTR;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;
import com.hikvision.netsdk.NET_DVR_DIGITAL_CHANNEL_STATE;
import com.hikvision.netsdk.NET_DVR_PICCFG_V30;
import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.data.db.model.CamDevice;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.utils.CamDeviceType;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Single;


public class HikUtil {
    public static final int CONNECTED = 1;
    public static final int DIGITAL_CHANNELS_START = 33;
    public static final String TAG = HikUtil.class.getSimpleName();

    public static Single<Long> loginNormalDevice(Context context, CamDevice camDevice) {
        return Single.create(
                emitter -> {
                    int logId;
                    INT_PTR error = new INT_PTR();
                    // get instance
                    NET_DVR_DEVICEINFO_V30 netDeviceInfoV30 = new NET_DVR_DEVICEINFO_V30();

                    // call NET_DVR_Login_v30 to login on, port 8000 as default
                    logId = HCNetSDK.getInstance().NET_DVR_Login_V30(
                            camDevice.getDomain(),
                            HIKPlayer.DEFAULT_HIKVISION_PORT_NUMBER,
                            camDevice.getUserName(),
                            camDevice.getPassWord(),
                            netDeviceInfoV30);

                    if (logId < 0) {
                        error.iValue = HCNetSDK.getInstance().NET_DVR_GetLastError();
                        String errorMessage = "NET_DVR_Login is failed!Err: "
                                + HCNetSDK.getInstance().NET_DVR_GetErrorMsg(error);
                        Log.e(TAG, errorMessage);
                        switch (error.iValue) {
                            case NET_DVR_PASSWORD_ERROR:
                                emitter.onError(new Throwable(context.getString(R.string.wrong_user_password)));
                            case NET_DVR_NETWORK_FAIL_CONNECT:
                            case NET_DVR_NETWORK_SEND_ERROR:
                            case NET_DVR_NETWORK_RECV_ERROR:
                            case NET_DVR_NETWORK_RECV_TIMEOUT:
                                emitter.onError(new Throwable(context.getString(R.string.network_error)));
                            case NET_DVR_MAX_NUM:
                            case NET_DVR_MAX_USERNUM:
                                emitter.onError(new Throwable(context.getString(R.string.max_users)));
                        }
                    } else {
                        Log.i(TAG, "NET_DVR_Login is Successful!");
                        emitter.onSuccess((long) logId);
                    }

                }
        );

    }

    public static Single<List<IpCam>> getChannels(CamDevice camDevice) {
        return Single.create(
                emitter -> {
                    List<IpCam> cams = new ArrayList<>();
                    INT_PTR int_ptr = new INT_PTR();
                    NET_DVR_DIGITAL_CHANNEL_STATE net_dvr_digital_channel_state = new NET_DVR_DIGITAL_CHANNEL_STATE();

                    if (!HCNetSDK.getInstance().NET_DVR_GetDVRConfig((int) camDevice.getLogId(),
                            HCNetSDK.NET_DVR_GET_DIGITAL_CHANNEL_STATE,
                            0, net_dvr_digital_channel_state)) {
                        int_ptr.iValue = HCNetSDK.getInstance().NET_DVR_GetLastError();
                        String errorMessage = "failed to get channels state " + HCNetSDK.getInstance().NET_DVR_GetErrorMsg(int_ptr);
                        Log.e(TAG, errorMessage);
                        emitter.onError(new Throwable(errorMessage));
                    } else {
                        Log.v(TAG, "Suc to get channels state " + camDevice.getDomain());
                        byte[] analogChannels = net_dvr_digital_channel_state.byAnalogChanState;
                        byte[] digitalChannels = net_dvr_digital_channel_state.byDigitalChanState;

                        Log.v(TAG, "analog ================ ");
                        for (int i = 0; i < analogChannels.length; i++) {
                            Log.v(TAG, "channel : " + analogChannels[i]);
                            if (analogChannels[i] == CONNECTED) {
                                cams.add(new IpCam(i + 1, (int) camDevice.getId(), camDevice.getName(),  CamDeviceType.HIKVISION.getValue(), (int) camDevice.getLogId(), true));
                            }
                        }

                        Log.v(TAG, "digital ================ ");
                        for (int i = 0; i < digitalChannels.length; i++) {
                            Log.v(TAG, "channel : " + digitalChannels[i]);
                            if (digitalChannels[i] == CONNECTED) {
                                cams.add(new IpCam(i + DIGITAL_CHANNELS_START, (int) camDevice.getId(), camDevice.getName(),  CamDeviceType.HIKVISION.getValue(),(int) camDevice.getLogId(), false
                                ));
                            }
                        }

                    }

                    emitter.onSuccess(cams);
                });
    }


    public static Single<String> extractChannelName(IpCam ipCam) {
        return Single.create(emitter -> {
            INT_PTR int_ptr = new INT_PTR();
            String name = "";
            NET_DVR_PICCFG_V30 net_dvr_piccfg_v30 = new NET_DVR_PICCFG_V30();
            if (!HCNetSDK.getInstance().NET_DVR_GetDVRConfig((int) ipCam.getLogId(),
                    HCNetSDK.NET_DVR_GET_PICCFG_V30,
                    ipCam.getChannel(), net_dvr_piccfg_v30)) {
                int_ptr.iValue = HCNetSDK.getInstance().NET_DVR_GetLastError();
                String errorMessage = "failed to get channel name : " + HCNetSDK.getInstance().NET_DVR_GetErrorMsg(int_ptr);
                Log.e(TAG, errorMessage);
            } else {
                name = new String(net_dvr_piccfg_v30.sChanName, StandardCharsets.UTF_8).replaceAll("\0", "");
                Log.v(TAG, "channel name is : " + name);

            }
            emitter.onSuccess(name);
        });

    }

}
