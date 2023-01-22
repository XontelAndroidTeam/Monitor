package com.xontel.surveillancecameras.root;

import android.app.Application;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import com.company.NetSDK.CB_fDisConnect;
import com.company.NetSDK.CB_fHaveReConnect;
import com.company.NetSDK.CB_fSubDisConnect;
import com.company.NetSDK.INetSDK;
import com.company.NetSDK.NET_PARAM;
import com.hikvision.netsdk.HCNetSDK;
import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.di.component.ApplicationComponent;
import com.xontel.surveillancecameras.di.component.DaggerApplicationComponent;
import com.xontel.surveillancecameras.di.module.ApplicationModule;

public class MyApp extends Application {
    private ApplicationComponent mApplicationComponent;
    public static final String HIK_LOG_FILE_PATH = "/sdcard/hik_log.txt";
    public static final String DAH_LOG_FILE_PATH = "/sdcard/dah_log.txt";

    class DeviceDisConnect implements CB_fDisConnect {
        @Override
        public void invoke(long lLoginID, String pchDVRIP, int nDVRPort) {

            return;
        }
    }

    public class DeviceReConnect implements CB_fHaveReConnect {
        @Override
        public void invoke(long lLoginID, String pchDVRIP, int nDVRPort) {

        }
    }

    public class DeviceSubDisConnect implements CB_fSubDisConnect {
        @Override
        public void invoke(int emInterfaceType, boolean bOnline,
                           long lOperateHandle, long lLoginID) {

        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this)).build();
        mApplicationComponent.inject(this);
        initSdks();
    }

    public ApplicationComponent getComponent() {
        return mApplicationComponent;
    }

    // Needed to replace the component with a test specific one
    public void setComponent(ApplicationComponent applicationComponent) {
        mApplicationComponent = applicationComponent;
    }

    private void initSdks() {
        initHikSDK();
        initDahuaSDK();
    }

    private void initDahuaSDK() {
        // init net sdk
        DeviceDisConnect disConnect = new DeviceDisConnect();
        boolean zRet = INetSDK.Init(disConnect);

        INetSDK.SetConnectTime(3000, 3);
        NET_PARAM stNetParam = new NET_PARAM();
        stNetParam.nWaittime = 6 * 1000; // ??????????
        stNetParam.nSearchRecordTime = 20 * 1000; // ?????????????
        INetSDK.SetNetworkParam(stNetParam);

        DeviceReConnect reConnect = new DeviceReConnect();
        INetSDK.SetAutoReconnect(reConnect);

        DeviceSubDisConnect subDisConnect = new DeviceSubDisConnect();
        INetSDK.SetSubconnCallBack(subDisConnect);
//        Utils.createFile(DAH_LOG_FILE_PATH);
//        Utils.openLog(DAH_LOG_FILE_PATH);
    }

    private void initHikSDK() {
        if (!HCNetSDK.getInstance().NET_DVR_Init()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.init_sdk_init_error)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            try {
                                this.finalize();
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        }
                    });
            // Create the AlertDialog object and return it
            builder.create().show();
            return;

        }

        HCNetSDK.getInstance().NET_DVR_SetLogToFile(3, HIK_LOG_FILE_PATH, false);
    }
}
