package com.xontel.surveillancecameras.hikvision;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO;
import com.hikvision.netsdk.RealPlayCallBack;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.utils.StorageHelper;

import org.MediaPlayer.PlayM4.Player;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;


public class HIKSinglePlayer implements RealPlayCallBack {
    public static final String TAG = HIKSinglePlayer.class.getSimpleName();
    public static final int DEFAULT_HIKVISION_PORT_NUMBER = 8000;
    public static final int DEFAULT_Dahua_PORT_NUMBER = 37777;
    public static final int STREAM_Mode = Player.STREAM_REALTIME;
    private Context context;
    private SimpleDateFormat sDateFormat;
    private final HCNetSDK netSDKInstance = HCNetSDK.getInstance();
    private final Player playerInstance = Player.getInstance();
    private SurfaceView mSurfaceView;
    private boolean stopPlayback = false;
    private int m_iPort = -1;
    private int playId = -1;
    private int realPlayId = -1; // return by NET_DVR_RealPlay_V30
    private int playbackId = -1; // return by NET_DVR_PlayBackByTime
    private int channel = 0;
    private int logId;
    private int iRealHandle;
    private int iDataType;
    private byte[] pDataBuffer;
    private int iDataSize;
    private int streamType;
    private boolean isShow = false;
    private boolean isConfigure = false;

    //
    public HIKSinglePlayer(int channel, int logId, int streamType, Context context) {
        this.channel = channel;
        this.logId = logId;
        this.streamType = streamType;
        this.context = context;
        m_iPort = playerInstance.getPort();
        openStream();

    }

    public void initView(SurfaceView surfaceView) {
        mSurfaceView = surfaceView;
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
             //   Log.v("TATZ", "[Hik] surfaceCreated"+ "port : "+m_iPort);
                if (-1 == m_iPort) {return;}
                    Surface surface = holder.getSurface();
                    if (surface.isValid()) {

                        configurePlayer(holder);
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }


            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
              //  Log.v("TATZ", "[Hik] surfaceDestroyed "+"port : "+m_iPort);
                if (isShow) {isShow = false;}
                unConfigurePlay();
                if (-1 == m_iPort) {
                    return;
                }
                if (holder.getSurface().isValid()) {
                    if (!Player.getInstance().setVideoWindow(m_iPort, 0, null)) {
                        Log.e(TAG, "Player failed to set or destroy display area!");
                    }
                }
            }
        });
    }

    private void openStream() {


        NET_DVR_PREVIEWINFO previewInfo = new NET_DVR_PREVIEWINFO();
        previewInfo.lChannel = channel;
        previewInfo.dwStreamType = 1; // mainstream
        previewInfo.bBlocked = 1;

        realPlayId = netSDKInstance.NET_DVR_RealPlay_V40(logId, previewInfo, this);

        if (realPlayId < 0) {
            Log.e(TAG, "NET_DVR_RealPlay is failed!Err: " + netSDKInstance.NET_DVR_GetLastError());
            return;
        }
    }

    private void configurePlayer(SurfaceHolder holder) {
             //   if (!Player.getInstance().setCurrentFrameNum(m_iPort, 60)) {
              //      Log.e(TAG, "Player failed to set frame rate!");
              //  }
                if (!Player.getInstance().setVideoWindow(m_iPort, 0, holder)) {
                    Log.e(TAG, "Player failed to set or destroy display area!");
                }


                if (!Player.getInstance().play(m_iPort, mSurfaceView.getHolder())) {
                    Log.e(TAG, "Failed to play！ " + m_iPort);
                    return;
                }

                if (!Player.getInstance().playSound(m_iPort)) {
                    Log.e(TAG, "Playing audio exclusively failed! failure code :" + Player.getInstance().getLastError(m_iPort));
                    return;
                }
               // Log.i("TATZ", "[configurePlayer] channel: "+channel);
                isConfigure = true ;
                isShow = true;

                Log.v(TAG, "framerate" + Player.getInstance().getCurrentFrameNum(m_iPort) + "");

    }

    private void unConfigurePlay() {
        isConfigure = false;
        playerInstance.stopSound();
        // player stop play
        if (!playerInstance.stop(m_iPort)) {
            Log.e(TAG, "stop is failed!");
            return;
        }

//        if (!playerInstance.closeStream(m_iPort)) {
//            Log.e(TAG, "closeStream is failed!");
//            return;
//        }
    }

    public void processRealData( byte[] pDataBuffer, int iDataSize) {
          //  Log.i("TATZ", "[processRealData] "+"port : "+m_iPort);
            if (!Player.getInstance().inputData(m_iPort, pDataBuffer, iDataSize)) {
                Log.e(TAG, "inputData failed with: " +
                        playerInstance.getLastError(m_iPort));
            }
    }

    private void stopStream() {
        if (realPlayId < 0) {
            Log.e(TAG, "realPlayId < 0");
            return;
        }
        // net sdk stop preview
        if (!netSDKInstance.NET_DVR_StopRealPlay(realPlayId)) {
            Log.e(TAG, "StopRealPlay is failed!Err:" + netSDKInstance.NET_DVR_GetLastError());
            return;
        }

        if (!playerInstance.freePort(m_iPort)) {
            Log.e(TAG, "freePort is failed!" + m_iPort);
            return;
        }
        m_iPort = -1 ;
        realPlayId = -1;
    }

    private void stopSinglePlayer() {
        playerInstance.stopSound();
        // player stop play
        if (!playerInstance.stop(m_iPort)) {
            Log.e(TAG, "stop is failed!");
            return;
        }

        if (!playerInstance.closeStream(m_iPort)) {
            Log.e(TAG, "closeStream is failed!");
            return;
        }
        if (!playerInstance.freePort(m_iPort)) {
            Log.e(TAG, "freePort is failed!" + m_iPort);
            return;
        }
        m_iPort = -1;
    }



    @Override
    public void fRealDataCallBack(int iRealHandle, int iDataType, byte[] pDataBuffer, int iDataSize) {
        if (HCNetSDK.NET_DVR_SYSHEAD == iDataType){
          //  Log.i("TATZ", "[NET_DVR_SYSHEAD] iDataType: "+iDataType+ "port : "+m_iPort);
            if (!Player.getInstance().setStreamOpenMode(m_iPort, STREAM_Mode))  //set stream mode
            {
                Log.e(TAG, "Failed to set streaming mode！");
                return;
            }
            if (!Player.getInstance().openStream(m_iPort, pDataBuffer, iDataSize, 2 * 1024 * 1024)) //open stream
            {
                Log.e(TAG, "Failed to open stream！");
                return;
            }
        }
        else{
          //  Log.i("TATZ", "[isShow] isShow: "+isShow + "port : "+m_iPort);
            if (isShow ) {
                processRealData( pDataBuffer, iDataSize);
            }
        }
    }

    public void captureVideo() {

    }

    public void captureFrame() {
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
            Log.e("TATZ", "error: " + err.getMessage());
        }
    }


}
