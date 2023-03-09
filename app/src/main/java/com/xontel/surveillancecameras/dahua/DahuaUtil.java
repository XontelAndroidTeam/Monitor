package com.xontel.surveillancecameras.dahua;

import static com.company.NetSDK.FinalVar.NET_LOGIN_ERROR_MAXCONNECT;
import static com.company.NetSDK.FinalVar.NET_LOGIN_ERROR_NETWORK;
import static com.company.NetSDK.FinalVar.NET_LOGIN_ERROR_PASSWORD;
import static com.company.NetSDK.FinalVar.NET_LOGIN_ERROR_USER;

import android.content.Context;
import android.util.Log;

import com.company.NetSDK.FinalVar;
import com.company.NetSDK.INetSDK;
import com.company.NetSDK.NET_DEVICEINFO_Ex;
import com.company.NetSDK.SDKDEV_CHANNEL_CFG;
import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.data.db.model.CamDevice;
import com.xontel.surveillancecameras.utils.CamDeviceType;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Single;


public class DahuaUtil {
    public static final String TAG = DahuaUtil.class.getSimpleName();


    public static Single<Long> loginNormalDevice(Context context, CamDevice camDevice) {
        return Single.create(
                emitter -> {
                    NET_DEVICEINFO_Ex net_deviceinfo_ex = new NET_DEVICEINFO_Ex();
                    long logId = INetSDK.LoginEx2(
                            camDevice.getDomain(),
                            DahuaPlayer.DEFAULT_Dahua_PORT_NUMBER,
                            camDevice.userName,
                            camDevice.password,
                            20,
                            null,
                            net_deviceinfo_ex,
                            0);

                    if (logId == 0) {
                        int errorValue = INetSDK.GetLastError();
                        String errorMessage;
                        switch (errorValue) {
                            case NET_LOGIN_ERROR_PASSWORD:
                            case NET_LOGIN_ERROR_USER:
                                errorMessage = context.getString(R.string.wrong_user_password);
                                break;
                            case NET_LOGIN_ERROR_NETWORK:
                                errorMessage = context.getString(R.string.network_error);
                                break;
                            case NET_LOGIN_ERROR_MAXCONNECT:
                                errorMessage = context.getString(R.string.max_users);
                                break;
                            default:
                                errorMessage = context.getString(R.string.error_occurred);

                        }

                        Log.e(TAG, "DAHUA_Login failed : " + errorMessage);
                        emitter.onError(new Throwable(errorMessage));
                    } else {
                        Log.i(TAG, "DAHUA_Login is Successful! " + logId);
                        emitter.onSuccess(logId);
                    }
                }
        );
    }


    public static Single<List<IpCam>> getChannels(CamDevice camDevice) {
        return Single.create(
                emitter -> {
                    List<IpCam> cams = new ArrayList<>();
                    Log.v(TAG, "analog ================ ");
                    for (int i = 0; i < camDevice.getChannels(); i++) {
                        Log.v(TAG, "channel : " + i);
                        cams.add(new IpCam(i + 1, (int) camDevice.getId(), camDevice.getName(), true, (int) camDevice.getLogId(), CamDeviceType.DAHUA.getValue()
                        ));
                    }

                }
        );
    }


    public static Single<String> extractChannelName(IpCam ipCam){
        return Single.create(emitter -> {
            String name = "";
            SDKDEV_CHANNEL_CFG[] sdk_dev_channel_cfg = new SDKDEV_CHANNEL_CFG[1];
            if (!INetSDK.GetDevConfig(ipCam.getLoginId(),
                    FinalVar.SDK_DEV_CHANNELCFG,
                    ipCam.getChannel(), sdk_dev_channel_cfg, null,  5000)) {
                String errorMessage = "failed to get channel name : " + INetSDK.GetLastError();
                Log.e(TAG, errorMessage);
                emitter.onError(new Throwable(errorMessage));
            } else {
                name = new String(sdk_dev_channel_cfg[0].szChannelName, StandardCharsets.UTF_8).replaceAll("\0", "");
                Log.v(TAG, "channel name is : " + name);
                emitter.onSuccess(name);
            }
        });

    }
}
