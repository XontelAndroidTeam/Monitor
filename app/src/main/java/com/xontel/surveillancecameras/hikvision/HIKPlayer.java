package com.xontel.surveillancecameras.hikvision;

import static com.hikvision.netsdk.SDKError.NET_DVR_CHAN_NOTSUPPORT;

import android.content.Context;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.util.Log;

import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.INT_PTR;
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO;
import com.hikvision.netsdk.RealPlayCallBack;
import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.utils.CamPlayer;
import com.xontel.surveillancecameras.utils.StorageHelper;

import org.MediaPlayer.PlayM4.Player;
import org.MediaPlayer.PlayM4.PlayerCallBack;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


public class HIKPlayer extends CamPlayer implements PlayerCallBack.PlayerDisplayCB, CamPlayerView.SurfaceCallback {
    public static final String TAG = HIKPlayer.class.getSimpleName();
    public static final int DEFAULT_HIKVISION_PORT_NUMBER = 8000;

    private RealPlayCallBack mRealPlayCallBack;


    public HIKPlayer(Context context) {
        super(context);
    }


    // OUR START IS HERE

    public void openStream() {
        new Thread(() -> {
            NET_DVR_PREVIEWINFO previewInfo = new NET_DVR_PREVIEWINFO();
            previewInfo.lChannel = mIpCam.getChannel();
            previewInfo.dwStreamType = mIpCam.getStreamType(); // mainstream
            previewInfo.bBlocked = 1;

            mRealPlayCallBack = (iRealhandle, iDataType, pDataBuffer, iDataSize) -> {
                if (HCNetSDK.NET_DVR_SYSHEAD == iDataType) {
                    configurePlayer(iDataType, pDataBuffer, iDataSize);
                } else {
                    if (isConfigured) {
                        play(pDataBuffer, iDataSize);
                    }
                }
            };
            realPlayId = HCNetSDK.getInstance().NET_DVR_RealPlay_V40(mIpCam.getLoginId(), previewInfo, mRealPlayCallBack);
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


    @Override
    public void showError(String logMessage) {
        super.showError(logMessage);
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


            if (!(mCamPlayerView != null && mCamPlayerView.getSurfaceView() != null) || !Player.getInstance().play(m_iPort, mCamPlayerView.getSurfaceView().getHolder())) {
                Player.getInstance().closeStream(m_iPort);
                showError("play failed");
                return;
            }
            if (!Player.getInstance().playSound(m_iPort)) {
                Log.e(TAG, "playSound failed with error code:"
                        + Player.getInstance().getLastError(m_iPort));
            }

            if (!Player.getInstance().setImageCorrection(m_iPort, 0)) {
                Log.e(TAG, "failed to setImageCorrection :"
                        + Player.getInstance().getLastError(m_iPort));
                return;
            }
            lock = 0;
            Player.getInstance().setDisplayCB(m_iPort, this);
            isConfigured = true;
        }

    }

    @Override
    public void play(byte[] pDataBuffer, int iDataSize) {
        Player.getInstance().inputData(m_iPort, pDataBuffer, iDataSize);
//            Log.e(TAG, "inputData failed with: " + Player.getInstance().getLastError(m_iPort));

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
            Log.e(TAG, "stop is failed!");
        }


    }


    @Override
    public void stopStream() {
        if (realPlayId < 0L) {
            return;
        }
        // net sdk stop preview
        if (!HCNetSDK.getInstance().NET_DVR_StopRealPlay(realPlayId)) {
            Log.e(TAG, "StopRealPlay is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
        }
        stopRecordingVideo();
        if (!Player.getInstance().closeStream(m_iPort)) {
            Log.e(TAG, "closeStream is failed!");
        }
        if (!Player.getInstance().freePort(m_iPort)) {
            Log.e(TAG, "freePort is failed!" + m_iPort);
        }
        m_iPort = -1;
        realPlayId = -1;
    }

    @Override
    public String getTAG() {
        return TAG;
    }

    @Override
    public void freePort() {
        Player.getInstance().freePort(m_iPort);
    }


//    @Override
//    public void fRealDataCallBack(int iRealhandle, int iDataType, byte[] pDataBuffer, int iDataSize) {
//
//    }

    @Override
    public boolean recordVideo() {
        if (!isRecording) {
            String date = sDateFormat.format(new Date());
            File dir = new File(StorageHelper.getMediaDirectory(context, Environment.DIRECTORY_MOVIES).getAbsolutePath());
            File file = new File(dir, date + ".mp4");
            Log.v(TAG, file.getAbsolutePath());
            if (HCNetSDK.getInstance().NET_DVR_SaveRealData((int) realPlayId, file.getAbsolutePath())) {
                Log.v(TAG, "Record started successfully ");
                isRecording = true;
                return true;
            }
            Log.e(TAG, "NET_DVR_SaveRealData failed! error: "
                    + HCNetSDK.getInstance().NET_DVR_GetLastError());
        }

        return false;
    }

    @Override
    public boolean stopRecordingVideo() {
        if (isRecording) {
            if (HCNetSDK.getInstance().NET_DVR_StopSaveRealData((int) realPlayId)) {
                System.out.println("NET_DVR_StopSaveRealData succ!");
                return true;
            }
            Log.e(TAG, "NET_DVR_StopSaveRealData failed! error: "
                    + HCNetSDK.getInstance()
                    .NET_DVR_GetLastError());
            isRecording = false;
        }
        return false;
    }

    @Override
    public void takeSnapshot() {
        try {
            Player.MPInteger stWidth = new Player.MPInteger();
            Player.MPInteger stHeight = new Player.MPInteger();
            if (!Player.getInstance().getPictureSize(m_iPort, stWidth, stHeight)) {
//                showMessage(context.getString(R.string.cant_take_photo) + " " + "width : ");
                return;
            }
            int nSize = 5 * stWidth.value * stHeight.value;
            byte[] picBuf = new byte[nSize];
            Player.MPInteger stSize = new Player.MPInteger();
            if (!Player.getInstance().getBMP(m_iPort, picBuf, nSize, stSize)) {
//                showMessage(context.getString(R.string.cant_take_photo) + "22");
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
            MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()}, new String[]{"image/*"}, (s, uri) -> Log.i(TAG, "onScanCompleted_video: " + uri));
            showMessage(context.getString(R.string.snapshot_taken));
            //  Bitmap bitmap = BitmapFactory.decodeFile(dir.getAbsolutePath() + "/" + date + ".jpg");
        } catch (Exception err) {
            err.printStackTrace();
            showMessage(err.getMessage() );
        }
    }


    @Override
    public void onDisplay(int i, byte[] bytes, int i1, int i2, int i3, int i4, int i5, int i6) {
        if (lock == 0) {
            mRunnable = () -> {
                if (mCamPlayerView != null) {
                    mCamPlayerView.showLoading(false);
                    lock = 1;
                }

            };
            mHandler.post(mRunnable);
        }
    }
}
