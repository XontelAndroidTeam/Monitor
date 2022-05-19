package com.xontel.surveillancecameras.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.hikvision.netsdk.ExceptionCallBack;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO;
import com.hikvision.netsdk.RealPlayCallBack;

import org.MediaPlayer.PlayM4.Player;

import java.io.File;
import java.io.FileOutputStream;
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
    private static  HIKDevice hikDevice ;
    private static final String TAG = "HikUtil";
    private static final int HIK_MAIN_STREAM_CODE = 0;      //主码流
    private static final int HIK_SUB_STREAM_CODE = 1;      //子码流
    private static NET_DVR_DEVICEINFO_V30 m_oNetDvrDeviceInfoV30 = null;

    private  int m_iPlaybackID = -1;
    private  int playId = -1;


    public  onPicCapturedListener mPicCapturedListener;
    private SimpleDateFormat sDateFormat;
    private  Player.MPInteger stWidth;
    private  Player.MPInteger stHeight;
    private  Player.MPInteger stSize;

    /**
     * Define the interface to monitor the success of the picture screenshot
     */
    public interface onPicCapturedListener {
        void onPicCaptured(Bitmap bitmap, String bitmapFileAbsolutePath);

        void onPicDataSaved(byte[] picData);
    }

    public HikUtil() {
    }

    /**
     * Initialize HCNet SDK
     *
     * @return
     */
    public static boolean initSDK() {

        if (!HCNetSDK.getInstance().NET_DVR_Init()) {
            Log.e(TAG, "HCNetSDK ---------initialization failed!");
            return false;
        }
        //Print the log to the local, do not need to print temporarily
//        HCNetSDK.getInstance().NET_DVR_SetLogToFile(3, "/mnt/sdcard/sdklog/", true);
        return true;
    }



    public  static void loginDevice(HIKDevice hikDevice, HikInterface hikInterface) {
        new Thread(new Runnable(){
            @Override
            public void run() {
                boolean logged = login(hikDevice);
                if(logged) {
                    hikInterface.onLogInSuccess(hikDevice.getLogId());
                }else{
                    hikInterface.onLogInFailed();
                }
            }
        }).start();
    }




    private static boolean login(HIKDevice hikDevice){
        try {
            if (hikDevice.getLogId() < 0) {
                // 登录设备
                hikDevice.setLogId(getLogId(hikDevice));
                if (hikDevice.getLogId() < 0) {
                    Log.e(TAG, "Device login failed！");
                    return false;
                }
                // 获取异常回调和异常设置的回调
                ExceptionCallBack oexceptionCbf = getExceptiongCbf();
                if (oexceptionCbf == null) {
                    Log.e(TAG, "exception callback object failed！");
                    return false;
                }

                if (!HCNetSDK.getInstance().NET_DVR_SetExceptionCallBack(oexceptionCbf)) {

                    Log.e(TAG, "Failed to register receive exception, reconnect message callback function !");
                    return false;
                }

//                loginButton.setText("注销");
                Log.i(TAG, "login successful ！");
                return true;
            } else {
                // 是否登出
                if (!HCNetSDK.getInstance().NET_DVR_Logout_V30(hikDevice.getLogId())) {
                    Log.e(TAG, " User logout failed!");
                    return false;
                }
//                loginButton.setText("登录");
                hikDevice.setLogId(-1);
                return true;
            }

        } catch (Exception err) {
            Log.e(TAG, "mistake: " + err.toString());
            return false;
        }
    }

    private static int getLogId(HIKDevice hikDevice) {
        //实例化设备信息对象
        m_oNetDvrDeviceInfoV30 = new NET_DVR_DEVICEINFO_V30();
        if (null == m_oNetDvrDeviceInfoV30) {
            Log.e(TAG, "Instantiate device information(NET_DVR_DEVICEINFO_V30)fail!");
            return -1;
        }
        // call NET_DVR_Login_v30 to login on, port 8000 as default
        int iLogID = HCNetSDK.getInstance().NET_DVR_Login_V30(
               "192.168.1.123",
                8000,
                "admin",
                "X0nPAssw0rd_000",
                m_oNetDvrDeviceInfoV30
        );

        if (iLogID < 0) {
            Log.e(TAG, "Network device login failed!-------------Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return -1;
        }
        if (m_oNetDvrDeviceInfoV30.byChanNum > 0) {
            hikDevice.setChannels(m_oNetDvrDeviceInfoV30.byStartChan);
        } else if (m_oNetDvrDeviceInfoV30.byIPChanNum > 0) {
            hikDevice.setChannels(m_oNetDvrDeviceInfoV30.byStartDChan);
        }
        Log.i(TAG, "Network device login successfully!");

        return iLogID;
    }

    private static ExceptionCallBack getExceptiongCbf() {
        ExceptionCallBack oExceptionCbf = new ExceptionCallBack() {
            public void fExceptionCallBack(int iType, int iUserID, int iHandle) {
                System.out.println("recv exception------------------------------, type:" + iType);
            }
        };
        return oExceptionCbf;
    }
//
//    /**
//     * Capture a frame of picture, return bitmap object successfully, return null if failed
//     * After testing:
//     * It takes <10ms to get screenshot data
//     * It takes ≈25ms to save the screenshot data to disk
//     * It takes about 45ms from taking screenshots - saving to disk - decoding file to bitmap
//     */
//    public Bitmap captureFrame(onPicCapturedListener picCapturedListener) {
//        try {
//            long time1 = System.currentTimeMillis();
//            mPicCapturedListener = picCapturedListener;
//            Player.MPInteger stWidth = new Player.MPInteger();
//            Player.MPInteger stHeight = new Player.MPInteger();
//            if (!Player.getInstance().getPictureSize(m_iPort, stWidth, stHeight)) {
//                Log.e(TAG, "Failed to get image size ---> error code:" + Player.getInstance().getLastError(m_iPort));
//                return null;
//            }
//            int nSize = 5 * stWidth.value * stHeight.value;
//            byte[] picBuf = new byte[nSize];
//            Player.MPInteger stSize = new Player.MPInteger();
//            if (!Player.getInstance().getBMP(m_iPort, picBuf, nSize, stSize)) {
//                Log.e(TAG, "Failed to get bitmap ----> error code:" + Player.getInstance().getLastError(m_iPort));
//                return null;
//            }
//            long time2 = System.currentTimeMillis();
//            if (sDateFormat == null) {
//                sDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh_mm_ss_Sss");
//            }
//            String date = sDateFormat.format(new java.util.Date());
//            File dir = new File(Environment.getExternalStorageDirectory() + "/capture");
//            if (!dir.exists()) {
//                dir.mkdir();
//            }
//            File file = new File(dir, date + ".jpg");
//            FileOutputStream fos = new FileOutputStream(file);
//            fos.write(picBuf, 0, stSize.value);
//            fos.close();
//            long time3 = System.currentTimeMillis();
//            Bitmap bitmap = BitmapFactory.decodeFile(dir.getAbsolutePath() + "/" + date + ".jpg");
//            long time4 = System.currentTimeMillis();
//            //图片保存成功了，通知给外面
//            mPicCapturedListener.onPicCaptured(bitmap, file.getAbsolutePath());
//            return bitmap;
//        } catch (Exception err) {
//            Log.e(TAG, "error: " + err.toString());
//        } finally {
//
//            return null;
//        }
//    }

//    /**
//     *  Capture a frame of picture, return bitmap object successfully, return null if failed
//     *  Image data is stored in memory
//     */
//    public  byte[] captureFrameOnMemroy(onPicCapturedListener picCapturedListener, Handler handler) {
//        try {
//            long start = System.currentTimeMillis();
//            mPicCapturedListener = picCapturedListener;
//            if (stWidth == null) {
//                stWidth = new Player.MPInteger();
//            }
//            if (stHeight == null) {
//                stHeight = new Player.MPInteger();
//            }
//            if (!Player.getInstance().getPictureSize(m_iPort, stWidth, stHeight)) {
//                Log.e(TAG, "Failed to get image size---> error code:" + Player.getInstance().getLastError(m_iPort));
//                return null;
//            }
//            int nSize = 5 * stWidth.value * stHeight.value;
//            byte[] picBuf = new byte[nSize];
//            if (stSize == null) {
//
//                stSize = new Player.MPInteger();
//            }
//            if (!Player.getInstance().getBMP(m_iPort, picBuf, nSize, stSize)) {
////                mPicCapturedListener.onPicDataSavedError();
//                Log.e(TAG, "Failed to get bitmap----> error code:" + Player.getInstance().getLastError(m_iPort));
//                return null;
//            }
//            //图片保存数据获取成功了，通知给外面。或者用handler发送出去
//           /* mPicCapturedListener.onPicDataSaved(picBuf);
//            Message message = handler.obtainMessage();
//            message.obj = picBuf;
//            message.what = Constant.VIDEO_FRAME_PIC_DATA_SAVED;
//            handler.sendMessage(message);
//            long end = System.currentTimeMillis();*/
//            return picBuf;
//        } catch (Exception err) {
//            Log.e(TAG, "error: " + err.toString());
//        }
//        return null;
//    }

    public interface HikInterface{
        void onLogInSuccess(int logId);
        void onLogInFailed();

    }
}
