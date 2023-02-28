package com.xontel.surveillancecameras.hikvision;

import static com.hikvision.netsdk.SDKError.NET_DVR_CHAN_NOTSUPPORT;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.INT_PTR;
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO;
import com.hikvision.netsdk.RealPlayCallBack;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.utils.CamPlayer;
import com.xontel.surveillancecameras.utils.StorageHelper;

import org.MediaPlayer.PlayM4.Player;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class HIKPlayer extends CamPlayer implements RealPlayCallBack, CamPlayerView.SurfaceCallback {
    public static final String TAG = HIKPlayer.class.getSimpleName();
    public static final int DEFAULT_HIKVISION_PORT_NUMBER = 8000;



    private boolean isRecording;




    public HIKPlayer(Context context) {
        super(context);
    }


    // OUR START IS HERE

    public void openStream() {
        NET_DVR_PREVIEWINFO previewInfo = new NET_DVR_PREVIEWINFO();
        previewInfo.lChannel = mIpCam.getChannel();
        previewInfo.dwStreamType = mIpCam.getStreamType(); // mainstream
        previewInfo.bBlocked = 1;

        new Thread(() -> {
            realPlayId = HCNetSDK.getInstance().NET_DVR_RealPlay_V40(mIpCam.getLoginId(), previewInfo, HIKPlayer.this);
            if (realPlayId < 0L) {
                int error = HCNetSDK.getInstance().NET_DVR_GetLastError();
                Log.v(TAG, "Last error " + error);
                if (error == NET_DVR_CHAN_NOTSUPPORT) {
                    mIpCam.toggleStreamType();
                    openStream();
                } else {
                    INT_PTR int_ptr = new INT_PTR();
                    int_ptr.iValue = error;
                    showError("NET_DVR_RealPlay is failed!Err: " + HCNetSDK.getInstance().NET_DVR_GetErrorMsg(int_ptr) + " channel : " + mIpCam.getChannel());
                }
                return;
            }
        }).start();


    }



    // THIS IS WHILE RUNNING
    @Override
    public void configurePlayer(int iDataType, byte[] pDataBuffer, int iDataSize) {
        Log.v(TAG, "configuring channel :" + mIpCam.getChannel());
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


            if (!Player.getInstance().play(m_iPort, mCamPlayerView.getSurfaceView().getHolder())) {
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
            lock = 0;
            Player.getInstance().setDisplayCB(m_iPort, (i, bytes, i1, i2, i3, i4, i5, i6) -> {
                if (lock == 0) {
                    mRunnable = () -> {
                        if (mCamPlayerView != null) {
                            mCamPlayerView.showLoading(false);
                            lock = 1;
                        }

                    };
                    mHandler.post(mRunnable);
                }
            });
            isConfigured = true;
        }

    }

    @Override
    public void play(byte[] pDataBuffer, int iDataSize) {
        isStopped = false;
        if (!Player.getInstance().inputData(m_iPort, pDataBuffer, iDataSize)) {
//            Log.e(TAG, "inputData failed with: " + Player.getInstance().getLastError(m_iPort));
        }
    }


    // THE END
    @Override
    public void unConfigurePlay() {
        if (!isConfigured) {
            return;
        }
//        if (!Player.getInstance().setDisplayCB(m_iPort, null)) {
//            Log.e(TAG, "setDisplayCB is failed!" + m_iPort);
//        }

        mHandler.removeCallbacks(mRunnable);
        mRunnable = null;
        isConfigured = false;
        if (!Player.getInstance().stopSound()) {
            Log.e(TAG, "stop sound failed! ");
        }
//         player stop play
        if (!Player.getInstance().stop(m_iPort)) {
            showError("stop is failed!");
        }


    }



    @Override
    public void stopStream() {

        if (realPlayId < 0L) {
            return;
        }
        // net sdk stop preview
        if (!HCNetSDK.getInstance().NET_DVR_StopRealPlay(realPlayId)) {
            showError("StopRealPlay is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return;
        }

        if (!Player.getInstance().closeStream(m_iPort)) {
            showError("closeStream is failed!");
            return;
        }
        if (!Player.getInstance().freePort(m_iPort)) {
            showError("freePort is failed!" + m_iPort);
            return;
        }
        m_iPort = -1;
        realPlayId = -1;
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
            String date = sDateFormat.format(new Date());
            File dir = new File(StorageHelper.getMediaDirectory(context, Environment.DIRECTORY_MOVIES).getAbsolutePath() + "/monitor");
            File file = new File(dir, date + ".mp4");
            if (!HCNetSDK.getInstance().NET_DVR_SaveRealData((int) realPlayId, file.getAbsolutePath())) {
                Log.e(TAG, "NET_DVR_SaveRealData on channel " + mIpCam.getChannel() + " failed! error: "
                        + HCNetSDK.getInstance().NET_DVR_GetLastError());
                return;
            } else {
                Log.v(TAG, "Record started successfully on channel " + mIpCam.getChannel());
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
            String date = sDateFormat.format(new Date());
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
