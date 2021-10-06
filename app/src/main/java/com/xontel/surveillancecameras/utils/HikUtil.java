package com.xontel.surveillancecameras.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
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
 * Author:  Leslie
 * Use Haikang SDK to play video stream tools
 * 前提：1->/libs/下放入：AudioEngineSDK.jar,HCNetSDK.jar,PlayerSDK.jar
 * 2->/src/main/jniLibs/下放入：很多 .so 文件.
 * 3->Add network permissions
 * Currently only deals with Hikvision cameras
 * (Indoor gun type network camera-【型号：DS-2CD5026EFWD】-【Software version：V5.4.5_170222】)
 * But this example is not limited to this model。
 * Instructions[Due to preview 2 路，So many static methods，Static variables removed，The call flow has also changed]：
 * 1.HikUtil.initSDK();
 * 2.HikUtil hikUtil = new HikUtil();
 * 2.hikUtil.initView(surfaceView);
 * 3.hikUtil.setDeviceData("192.168.1.22",8000,"admin","eyecool2016");
 * 4.hikUtil.loginDevice(mHandler,LOGIN_SUCCESS_CODE);
 * 5.hikUtil.playOrStopStream();
 */

public class HikUtil {
    private static final String TAG = "HikUtil";
    private static final int HIK_MAIN_STREAM_CODE = 0;      //主码流
    private static final int HIK_SUB_STREAM_CODE = 1;      //子码流
    private NET_DVR_DEVICEINFO_V30 m_oNetDvrDeviceInfoV30 = null;
    private  int m_iStartChan = 3;
    private  int m_iPort = -1;
    private  int m_iPlaybackID = -1;
    private  int logId = -1;
    private  int playId = -1;
    private SurfaceView mSurfaceView;
    public String mIpAddress;
    private  int mPort;
    private String mUserName;
    private String mPassWord;
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
        //Print log to local, no need to print temporarily
//        HCNetSDK.getInstance().NET_DVR_SetLogToFile(3, "/mnt/sdcard/sdklog/", true);
        return true;
    }

    public  void initView(SurfaceView surfaceView) {
        mSurfaceView = surfaceView;
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
                Log.i(TAG, "surface is created" + m_iPort);
                if (-1 == m_iPort) {
                    return;
                }
                Surface surface = holder.getSurface();
                if (surface.isValid()) {
                    if (!Player.getInstance().setVideoWindow(m_iPort, 0, holder)) {
                        Log.e(TAG, "The player failed to set or destroy the display area!");
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.i(TAG, "Player setVideoWindow release!" + m_iPort);
                if (-1 == m_iPort) {
                    return;
                }
                if (holder.getSurface().isValid()) {
                    if (!Player.getInstance().setVideoWindow(m_iPort, 0, null)) {
                        Log.e(TAG, "The player failed to set or destroy the display area!");
                    }
                }
            }
        });
    }

    /**
     * Configure webcam parameters
     * @param ipAddress IP 地址
     * @param port 端口号，默认是 8000
     * @param userName 用户名
     * @param passWord 密码
     */
    public  void setDeviceData(String ipAddress, int port, String userName, String passWord) {
        mIpAddress = ipAddress;
        mPort = port;
        mUserName = userName;
        mPassWord = passWord;

    }

    public  void loginDevice(final Handler handler, final int resultCode) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean loginState = login(mIpAddress, mPort, mUserName, mPassWord);
                Message message = handler.obtainMessage();
                message.obj = loginState;
                message.what = resultCode;
                handler.sendMessage(message);
            }
        }).start();
    }

    /**
     * Play or stop the video stream
     */
    public  void playOrStopStream() {

        if (logId < 0) {
            Log.e(TAG, "Please log in to the device first");
            return;
        }
        if (playId < 0) {   //Play

            RealPlayCallBack fRealDataCallBack = getRealPlayerCbf();
            if (fRealDataCallBack == null) {
                Log.e(TAG, "fRealDataCallBack object is failed!");
                return;
            }
            Log.i(TAG, "m_iStartChan:" + m_iStartChan);

            NET_DVR_PREVIEWINFO previewInfo = new NET_DVR_PREVIEWINFO();
            previewInfo.lChannel = m_iStartChan;
            previewInfo.dwStreamType = HIK_SUB_STREAM_CODE;                                                             //子码流
            previewInfo.bBlocked = 1;
            // HCNetSDK start preview
            playId = HCNetSDK.getInstance().NET_DVR_RealPlay_V40(logId, previewInfo, fRealDataCallBack);
            if (playId < 0) {
                Log.e(TAG, "Real-time preview failed!-----------------Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
                return;
            }

            Log.i(TAG, "NetSdk Play successfully ！");
//            mPlayButton.setText("停止");
        } else {    //停止播放
            if (playId < 0) {
                Log.e(TAG, "m_iPlayID < 0");
                return;
            }

            //  net sdk stop preview
            if (!HCNetSDK.getInstance().NET_DVR_StopRealPlay(playId)) {
                Log.e(TAG, "Failed to stop preview!----------------mistake:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
                return;
            }

            playId = -1;
            Player.getInstance().stopSound();
            // player stop play
            if (!Player.getInstance().stop(m_iPort)) {
                Log.e(TAG, "-------------------Pause failed!");
                return;
            }

            if (!Player.getInstance().closeStream(m_iPort)) {
                Log.e(TAG, "-------------------Failed to shut down!");
                return;
            }
            if (!Player.getInstance().freePort(m_iPort)) {
                Log.e(TAG, "-------------------Failed to release the playback port!" + m_iPort);
                return;
            }
            m_iPort = -1;
            logId = -1;
            playId = -1;
//            mPlayButton.setText("Play");
        }

    }

    private RealPlayCallBack getRealPlayerCbf() {
        RealPlayCallBack cbf = new RealPlayCallBack() {
            public void fRealDataCallBack(int iRealHandle, int iDataType, byte[] pDataBuffer, int iDataSize) {
                // 播放通道1
                processRealData(1, iDataType, pDataBuffer, iDataSize, Player.STREAM_REALTIME);
            }
        };
        return cbf;
    }

    public  void processRealData(int iPlayViewNo, int iDataType, byte[] pDataBuffer, int iDataSize, int iStreamMode) {
        if (HCNetSDK.NET_DVR_SYSHEAD == iDataType) {
            if (m_iPort >= 0) {
                return;
            }
            m_iPort = Player.getInstance().getPort();
            if (m_iPort == -1) {
                Log.e(TAG, "Failed to get port！: " + Player.getInstance().getLastError(m_iPort));
                return;
            }
            Log.i(TAG, "Get the port successfully！: " + m_iPort);
            if (iDataSize > 0) {
                if (!Player.getInstance().setStreamOpenMode(m_iPort, iStreamMode))  //set stream mode
                {
                    Log.e(TAG, "Failed to set streaming mode！");
                    return;
                }
                if (!Player.getInstance().openStream(m_iPort, pDataBuffer, iDataSize, 2 * 1024 * 1024)) //open stream
                {
                    Log.e(TAG, "Failed to open stream！");
                    return;
                }
                if (!Player.getInstance().play(m_iPort, mSurfaceView.getHolder())) {
                    Log.e(TAG, "Playback failed！");
                    return;
                }
                if (!Player.getInstance().playSound(m_iPort)) {
                    Log.e(TAG, "Failed to play audio exclusively！Failure code :" + Player.getInstance().getLastError(m_iPort));
                    return;
                }
            }
        } else {
            if (!Player.getInstance().inputData(m_iPort, pDataBuffer, iDataSize)) {
//		    		Log.e(TAG, "inputData failed with: " + Player.getInstance().getLastError(m_iPort));
                for (int i = 0; i < 4000 && m_iPlaybackID >= 0; i++) {
                    if (!Player.getInstance().inputData(m_iPort, pDataBuffer, iDataSize))
                        Log.e(TAG, "Failed to input stream data: " + Player.getInstance().getLastError(m_iPort));
                    else
                        break;
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();

                    }
                }
            }

        }

    }


    private  boolean login(String ipAddress, int portNum, String userName, String passWord) {
        try {
            if (logId < 0) {
                // 登录设备
                logId = loginDevice(ipAddress, portNum, userName, passWord);
                if (logId < 0) {
                    Log.e(TAG, "Device login failed！");
                    return false;
                }
                // 获取异常回调和异常设置的回调
                ExceptionCallBack oexceptionCbf = getExceptiongCbf();
                if (oexceptionCbf == null) {
                    Log.e(TAG, "Exception callback object failed！");
                    return false;
                }

                if (!HCNetSDK.getInstance().NET_DVR_SetExceptionCallBack(oexceptionCbf)) {
                    Log.e(TAG, "Register receiving exception, reconnect message callback function failed !");
                    return false;
                }

//                loginButton.setText("Logout");
                Log.i(TAG, "login successful ！");
                return true;
            } else {
                // 是否登出
                if (!HCNetSDK.getInstance().NET_DVR_Logout_V30(logId)) {
                    Log.e(TAG, " User logout failed!");
                    return false;
                }
//                loginButton.setText("登录");
                logId = -1;
                return true;
            }
        } catch (Exception err) {
            Log.e(TAG, "mistake: " + err.toString());
            return false;
        }
    }

    private  int loginDevice(String ipAddress, int portNum, String userName, String passWord) {
        //实例化设备信息对象
        m_oNetDvrDeviceInfoV30 = new NET_DVR_DEVICEINFO_V30();
        if (null == m_oNetDvrDeviceInfoV30) {
            Log.e(TAG, "Instantiate device information(NET_DVR_DEVICEINFO_V30)fail!");
            return -1;
        }
        // call NET_DVR_Login_v30 to login on, port 8000 as default
        int iLogID = HCNetSDK.getInstance().NET_DVR_Login_V30(ipAddress, portNum, userName, passWord, m_oNetDvrDeviceInfoV30);
        if (iLogID < 0) {
            Log.e(TAG, "Network device login failed!-------------Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return -1;
        }
        if (m_oNetDvrDeviceInfoV30.byChanNum > 0) {
            m_iStartChan = m_oNetDvrDeviceInfoV30.byStartChan;
        } else if (m_oNetDvrDeviceInfoV30.byIPChanNum > 0) {
            m_iStartChan = m_oNetDvrDeviceInfoV30.byStartDChan;
        }
        Log.i(TAG, "Network device login successfully!");

        return iLogID;
    }

    private ExceptionCallBack getExceptiongCbf() {
        ExceptionCallBack oExceptionCbf = new ExceptionCallBack() {
            public void fExceptionCallBack(int iType, int iUserID, int iHandle) {
                System.out.println("recv exception------------------------------, type:" + iType);
            }
        };
        return oExceptionCbf;
    }

    /**
     * Capture a frame of picture, return the bitmap object successfully, return null on failure
     * After testing：
     * Time-consuming to obtain screenshot data <10ms
     * Time-consuming saving to disk after capturing screenshot data ≈25ms
     * From the number of screenshots taken-save to disk-decoded file to bitmap 耗时 ≈45ms
     */
    public Bitmap captureFrame(onPicCapturedListener picCapturedListener) {
        try {
            long time1 = System.currentTimeMillis();
            mPicCapturedListener = picCapturedListener;
            Player.MPInteger stWidth = new Player.MPInteger();
            Player.MPInteger stHeight = new Player.MPInteger();
            if (!Player.getInstance().getPictureSize(m_iPort, stWidth, stHeight)) {
                Log.e(TAG, "Failed to get image size---> error code:" + Player.getInstance().getLastError(m_iPort));
                return null;
            }
            int nSize = 5 * stWidth.value * stHeight.value;
            byte[] picBuf = new byte[nSize];
            Player.MPInteger stSize = new Player.MPInteger();
            if (!Player.getInstance().getBMP(m_iPort, picBuf, nSize, stSize)) {
                Log.e(TAG, "Failed to get bitmap----> error code:" + Player.getInstance().getLastError(m_iPort));
                return null;
            }
            long time2 = System.currentTimeMillis();
            if (sDateFormat == null) {
                sDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh_mm_ss_Sss");
            }
            String date = sDateFormat.format(new java.util.Date());
            File dir = new File(Environment.getExternalStorageDirectory() + "/capture");
            if (!dir.exists()) {
                dir.mkdir();
            }
            File file = new File(dir, date + ".jpg");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(picBuf, 0, stSize.value);
            fos.close();
            long time3 = System.currentTimeMillis();
            Bitmap bitmap = BitmapFactory.decodeFile(dir.getAbsolutePath() + "/" + date + ".jpg");
            long time4 = System.currentTimeMillis();
            //The picture is saved successfully, notify the outside
            mPicCapturedListener.onPicCaptured(bitmap, file.getAbsolutePath());
            return bitmap;
        } catch (Exception err) {
            Log.e(TAG, "error: " + err.toString());
        } finally {

            return null;
        }
    }

    /**
     * Capture a frame of picture, return the bitmap object successfully, return null if it fails
     * Picture data is stored in memory
     */
    public  byte[] captureFrameOnMemroy(onPicCapturedListener picCapturedListener, Handler handler) {
        try {
            long start = System.currentTimeMillis();
            mPicCapturedListener = picCapturedListener;
            if (stWidth == null) {
                stWidth = new Player.MPInteger();
            }
            if (stHeight == null) {
                stHeight = new Player.MPInteger();
            }
            if (!Player.getInstance().getPictureSize(m_iPort, stWidth, stHeight)) {
                Log.e(TAG, "Failed to get image size---> error code:" + Player.getInstance().getLastError(m_iPort));
                return null;
            }
            int nSize = 5 * stWidth.value * stHeight.value;
            byte[] picBuf = new byte[nSize];
            if (stSize == null) {

                stSize = new Player.MPInteger();
            }
            if (!Player.getInstance().getBMP(m_iPort, picBuf, nSize, stSize)) {
//                mPicCapturedListener.onPicDataSavedError();
                Log.e(TAG, "Failed to get bitmap----> error code:" + Player.getInstance().getLastError(m_iPort));
                return null;
            }
            //图片保存数据获取成功了，通知给外面。或者用handler发送出去
           /* mPicCapturedListener.onPicDataSaved(picBuf);
            Message message = handler.obtainMessage();
            message.obj = picBuf;
            message.what = Constant.VIDEO_FRAME_PIC_DATA_SAVED;
            handler.sendMessage(message);
            long end = System.currentTimeMillis();*/
            return picBuf;
        } catch (Exception err) {
            Log.e(TAG, "error: " + err.toString());
        }
        return null;
    }
}
