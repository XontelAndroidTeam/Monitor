package com.xontel.surveillancecameras.hikvision;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.INT_PTR;
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO;
import com.hikvision.netsdk.RealPlayCallBack;
import com.xontel.surveillancecameras.utils.CamPlayer;
import com.xontel.surveillancecameras.utils.StorageHelper;

import org.MediaPlayer.PlayM4.Player;
import org.MediaPlayer.PlayM4.PlayerCallBack;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;


public class HIKPlayer extends CamPlayer implements RealPlayCallBack {
    public static final String TAG = HIKPlayer.class.getSimpleName();


    public HIKPlayer(int channel, int logId, Context context) {
        super(channel, logId, context);
    }

    @Override
    public void startLiveView() {
        openStream();
    }

    @Override
    public void stopLiveView() {
        unConfigurePlay();
        stopStream();
    }


    // OUR START IS HERE
    @Override
    public void openStream() {

        NET_DVR_PREVIEWINFO previewInfo = new NET_DVR_PREVIEWINFO();
        previewInfo.lChannel = channel;
        previewInfo.dwStreamType = 1; // mainstream
        previewInfo.bBlocked = 1;

        new Thread(() -> {
            realPlayId = HCNetSDK.getInstance().NET_DVR_RealPlay_V40((int) logId, previewInfo, HIKPlayer.this);
            if (realPlayId < 0L) {
                showError("NET_DVR_RealPlay is failed!Err: " + HCNetSDK.getInstance().NET_DVR_GetLastError() + " channel : " + channel);
                return;
            }
        }).start();


    }


    // THIS IS WHILE RUNNING
    @Override
    public void configurePlayer(int iDataType, byte[] pDataBuffer, int iDataSize) {
        if (m_iPort >= 0) {
            return;
        }


        m_iPort = Player.getInstance().getPort();
        if (m_iPort == -1) {
            showError("getPort is failed with: "
                    + Player.getInstance().getLastError(m_iPort));
            return;
        }

        Log.i(TAG, "getPort succ with: " + m_iPort);
        if (iDataSize > 0) {
            if (!Player.getInstance().setStreamOpenMode(m_iPort,
                    Player.STREAM_REALTIME)) // set stream mode
            {
                showError("setStreamOpenMode failed");
                return;
            }


            if (!Player.getInstance().openStream(m_iPort, pDataBuffer,
                    iDataSize, 2 * 1024 * 1024)) // open stream
            {
                showError("openStream failed");
                return;
            }


            if (!Player.getInstance().play(m_iPort, mSurfaceView.getHolder())) {
                showError("play failed");
                return;
            }
            if (!Player.getInstance().playSound(m_iPort)) {
                showError("playSound failed with error code:"
                        + Player.getInstance().getLastError(m_iPort));
                return;
            }
            if (!Player.getInstance().setImageCorrection(m_iPort, 0)) {
                showError("failed to setImageCorrection :"
                        + Player.getInstance().getLastError(m_iPort));
                return;
            }
            Player.getInstance().setDisplayCB(m_iPort, new PlayerCallBack.PlayerDisplayCB() {
                @Override
                public void onDisplay(int i, byte[] bytes, int i1, int i2, int i3, int i4, int i5, int i6) {
                    isLoading.postValue(false);
                }
            });
//            Player.getInstance().set(m_iPort, new PlayerCallBack.PlayerDisplayCB() {
//                @Override
//                public void onDisplay(int i, byte[] bytes, int i1, int i2, int i3, int i4, int i5, int i6) {
//                    isLoading.postValue(false);
//                }
//            });

            if (!Player.getInstance().setCurrentFrameNum(m_iPort,
                    10)) // set stream mode
            {
                INT_PTR int_ptr = new INT_PTR();
                int_ptr.iValue = HCNetSDK.getInstance().NET_DVR_GetLastError();
                Log.v(TAG, "setCurrentFrameNum failed" + HCNetSDK.getInstance().NET_DVR_GetErrorMsg(int_ptr));
//                return;
            }
            isConfigured = true;
            Log.v(TAG, "configured");
        }

    }

    @Override
    public void play(byte[] pDataBuffer, int iDataSize) {
        if (!Player.getInstance().inputData(m_iPort, pDataBuffer, iDataSize)) {
            Log.e(TAG, "inputData failed with: " + Player.getInstance().getLastError(m_iPort));
        }
    }


    // THE END
    @Override
    public void unConfigurePlay() {
        isConfigured = false;
        if (!Player.getInstance().stopSound()) {
//           showError( "stop sound failed!");
            return;
        }
        // player stop play
        if (!Player.getInstance().stop(m_iPort)) {
            showError("stop is failed!");
            return;
        }

    }

    @Override
    public void stopStream() {
        isConfigured = false;
        if (realPlayId < 0L) {
            return;
        }

        // net sdk stop preview
        if (!HCNetSDK.getInstance().NET_DVR_StopRealPlay((int) realPlayId)) {
            showError("StopRealPlay is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return;
        }

//        if (!Player.getInstance().closeStream(m_iPort)) {
//            showError( "closeStream is failed!");
//            return;
//        }
//        if (!Player.getInstance().freePort(m_iPort)) {
//            showError( "freePort is failed!" + m_iPort);
//            return;
//        }


        m_iPort = -1;
        realPlayId = -1;
        isLoading.postValue(true);
        isError.postValue(false);
    }


    @Override
    public String getTAG() {
        return TAG;
    }


    @Override
    public void fRealDataCallBack(int iRealhandle, int iDataType, byte[] pDataBuffer, int iDataSize) {

        if (HCNetSDK.NET_DVR_SYSHEAD == iDataType) {
            configurePlayer(iDataType, pDataBuffer, iDataSize);
        } else {
            if (isConfigured) {
                play(pDataBuffer, iDataSize);
            }
        }
    }



    @Override
    public void captureVideo() {
        if (!isRecording) {
            String date = sDateFormat.format(new java.util.Date());
            File dir = new File(StorageHelper.getMediaDirectory(context, Environment.DIRECTORY_MOVIES).getAbsolutePath() + "/monitor");
            File file = new File(dir, date + ".mp4");
            if (!HCNetSDK.getInstance().NET_DVR_SaveRealData((int) realPlayId, file.getAbsolutePath())) {
                Log.e(TAG, "NET_DVR_SaveRealData on channel " + channel + " failed! error: "
                        + HCNetSDK.getInstance().NET_DVR_GetLastError());
                return;
            } else {
                Log.v(TAG, "Record started successfully on channel " + channel);
            }
            isRecording = true;
        } else {
            if (!HCNetSDK.getInstance().NET_DVR_StopSaveRealData((int) realPlayId)) {
                Log.e(TAG, "NET_DVR_StopSaveRealData failed! error: "
                        + HCNetSDK.getInstance()
                        .NET_DVR_GetLastError());
            } else {
                System.out.println("NET_DVR_StopSaveRealData succ!");
            }
            isRecording = false;
        }
    }


    @Override
    public void takeSnapshot() {
        try {
            Player.MPInteger stWidth = new Player.MPInteger();
            Player.MPInteger stHeight = new Player.MPInteger();
            if (!Player.getInstance().getPictureSize(m_iPort, stWidth, stHeight)) {
                return;
            }
            int nSize = 5 * stWidth.value * stHeight.value;
            byte[] picBuf = new byte[nSize];
            Player.MPInteger stSize = new Player.MPInteger();
            if (!Player.getInstance().getBMP(m_iPort, picBuf, nSize, stSize)) {
                return;
            }
            if (sDateFormat == null) {
                sDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh_mm_ss_Sss");
            }
            String date = sDateFormat.format(new java.util.Date());
            File dir = new File(StorageHelper.getMediaDirectory(context, Environment.DIRECTORY_PICTURES).getAbsolutePath());
            File file = new File(dir, date + ".jpg");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(picBuf, 0, stSize.value);
            fos.close();
            //  Bitmap bitmap = BitmapFactory.decodeFile(dir.getAbsolutePath() + "/" + date + ".jpg");
        } catch (Exception err) {
            showError("error: " + err.getMessage());

        }
    }


}
