package com.xontel.surveillancecameras.hikvision;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO;
import com.hikvision.netsdk.RealPlayCallBack;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.utils.StorageHelper;

import org.MediaPlayer.PlayM4.Player;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;


public class HIKSinglePlayer implements RealPlayCallBack{
    public static final String TAG = HIKSinglePlayer.class.getSimpleName();
    public static final int DEFAULT_HIKVISION_PORT_NUMBER = 8000;
    public static final int DEFAULT_Dahua_PORT_NUMBER = 37777;
    private Player.MPInteger stWidth;
    private Player.MPInteger stHeight;
    private Player.MPInteger stSize;
    private Context context;
    private SimpleDateFormat sDateFormat;
    public static final int HIK_MAIN_STREAM_CODE = 0;      //主码流
    public static final int HIK_SUB_STREAM_CODE = 1;
    private final HCNetSDK netSDKInstance = HCNetSDK.getInstance();
    private final Player playerInstance = Player.getInstance();
    private SurfaceView mSurfaceView;
    private boolean stopPlayback = false;
    private  int m_iPort = -1;
    private int playId = -1 ;
    private int realPlayId = -1; // return by NET_DVR_RealPlay_V30
    private int playbackId = -1; // return by NET_DVR_PlayBackByTime
    private  int channel = 0;
    private int logId;
    private int streamType ;
    private boolean isShow = true;
//
    public HIKSinglePlayer(int channel, int logId, int streamType,Context context) {
        this.channel = channel;
        this.logId = logId;
        this.streamType = streamType ;
        this.context = context;
    }

    public  void initView(SurfaceView surfaceView) {
        mSurfaceView = surfaceView;
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                if (-1 == m_iPort) {
                   m_iPort = playerInstance.getPort();
                Surface surface = holder.getSurface();
                if (surface.isValid()) {
                    startSinglePreview();
                    if(!Player.getInstance().setCurrentFrameNum(m_iPort, 60)){
                        Log.e(TAG, "Player failed to set frame rate!");
                    }
                    if (!Player.getInstance().setVideoWindow(m_iPort, 0, holder)) {
                        Log.e(TAG, "Player failed to set or destroy display area!");
                    }
                }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                holder.setFormat(PixelFormat.TRANSLUCENT);
                Log.e(TAG, "surfaceChanged");
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.e(TAG, "surfaceDestroyed");
                stopSinglePreview();
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

    private void startSinglePreview() {
        if (playbackId >= 0) {
            Log.i(TAG, "Please stop palyback first");
            return;
        }
        NET_DVR_PREVIEWINFO previewInfo = new NET_DVR_PREVIEWINFO();
        previewInfo.lChannel = channel;
        previewInfo.dwStreamType = 1; // mainstream
        previewInfo.bBlocked = 1;

        realPlayId = netSDKInstance.NET_DVR_RealPlay_V40(logId, previewInfo, this);

        if (realPlayId < 0) {
            Log.e(TAG, "NET_DVR_RealPlay is failed!Err: " + netSDKInstance.NET_DVR_GetLastError());
            return;
        }
        isShow = false;
    }

    public  void playOrStopStream() {
        if (logId < 0) {
            Log.e(TAG, "Please log in to the device first");
            return;
        }
        if (playId < 0) {   //播放

            RealPlayCallBack fRealDataCallBack = getRealPlayerCbf()/* realplayCallback*/;
            if (fRealDataCallBack == null) {
                Log.e(TAG, "fRealDataCallBack object is failed!");
                return;
            }
            Log.i(TAG, "m_iStartChan:" + channel);

            NET_DVR_PREVIEWINFO previewInfo = new NET_DVR_PREVIEWINFO();
            previewInfo.lChannel = channel;
            previewInfo.dwStreamType = streamType;                                                             //子码流
            previewInfo.bBlocked = 1;
            // HCNetSDK start preview
            playId = HCNetSDK.getInstance().NET_DVR_RealPlay_V40(logId, previewInfo, fRealDataCallBack);
            Log.e(TAG, "Live preview logId!-----------------" + playId);
            if (playId < 0) {
                Log.e(TAG, "Live preview failed!-----------------Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
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
                Log.e(TAG, "-------------------Shutdown failed!");
                return;
            }
            if (!Player.getInstance().freePort(m_iPort)) {
                Log.e(TAG, "-------------------Failed to release playback port!" + m_iPort);
                return;
            }
            m_iPort = -1;
//            logId = -1;
            playId = -1;
//            mPlayButton.setText("play");
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
                    Log.e(TAG, "Failed to play！");
                    return;
                }

                if (!Player.getInstance().playSound(m_iPort)) {
                    Log.e(TAG, "Playing audio exclusively failed! failure code :" + Player.getInstance().getLastError(m_iPort));
                    return;
                }
                Log.v(TAG, "framerate" + Player.getInstance().getCurrentFrameNum(m_iPort)+"");
            }
        } else {
            if (!Player.getInstance().inputData(m_iPort, pDataBuffer,
                    iDataSize)) {
                // Log.e(TAG, "inputData failed with: " +
                // playerInstance.getLastError(playPort));
                for (int i = 0; i < 4000 && playId >= 0
                        && !stopPlayback; i++) {
                    if (Player.getInstance().inputData(m_iPort,
                            pDataBuffer, iDataSize)) {
                        break;

                    }

                    if (i % 100 == 0) {
                        Log.e(TAG, "inputData failed with: "
                                + Player.getInstance()
                                .getLastError(m_iPort) + ", i:" + i);
                    }
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

    private void stopSinglePreview() {
        if (realPlayId < 0) {
            Log.e(TAG, "realPlayId < 0");
            return;
        }
        // net sdk stop preview
        if (!netSDKInstance.NET_DVR_StopRealPlay(realPlayId)) {
            Log.e(TAG, "StopRealPlay is failed!Err:" + netSDKInstance.NET_DVR_GetLastError());
            return;
        }

        realPlayId = -1;
        stopSinglePlayer();
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

    public void cleanUp() {
        // release player resource
        stopPlayback = true;
        playerInstance.closeStream(m_iPort);
        playerInstance.freePort(m_iPort);
        m_iPort = -1;

        // release net SDK resource
      // netSDKInstance.NET_DVR_Cleanup();
    }


    private boolean openPlayer(byte[] buffer, int bufferSize) {

//		do {
//			playPort = Player.getInstance().getPort();
//
//		} while ( -1 == playPort );

        if ( ! Player.getInstance().setStreamOpenMode(m_iPort, Player.STREAM_FILE ) ) {
            System.out.println( "The player set stream mode failed!" );
            return false;
        }

        if ( ! Player.getInstance().openStream( m_iPort, buffer, bufferSize, 2 * 1024 * 1024/*PLAYING_BUFFER_SIZE*/ ) ) {
            Player.getInstance().freePort( m_iPort );
            m_iPort = -1;

            return false;
        }

//		Player.getInstance().setStreamOpenMode( playPort, 0 );
        System.out.println( "We are using " + mSurfaceView.getHolder() + " as a Displayer." );

        if ( ! Player.getInstance().play( m_iPort, mSurfaceView.getHolder() ) ) {
            Player.getInstance().closeStream( m_iPort );
            Player.getInstance().freePort( m_iPort );

            m_iPort = -1;

            return false;
        }

        return true;
    }

    public void catchErrorIfNecessary() {
        int code = HCNetSDK.getInstance().NET_DVR_GetLastError();
        if ( 0 != code ) System.out.println( "Error: " + code );
    }

    @Override
    public void fRealDataCallBack(int handle, int dataType, byte[] buffer, int bufferSize) {
        System.out.println( String.format( "fRealDataCallBack{ handle : %s, dataType : %s, bufferSize : %s }",
                handle, dataType, bufferSize ) );

        int i = 0;

        switch ( dataType ) {
            case HCNetSDK.NET_DVR_SYSHEAD:
                if ( -1 == (m_iPort = Player.getInstance().getPort() ) ) {
                    System.out.println( "Can't get play port!" );

                    return;
                }

                if ( 0 < bufferSize ) {
                    if ( openPlayer( buffer, bufferSize ) ) {
                        System.out.println( "Open player successfully." );
                    } else {
                        System.out.println( "Open player failed." );
                    }
                }

                break;

            case HCNetSDK.NET_DVR_STREAMDATA:
            case HCNetSDK.NET_DVR_STD_VIDEODATA:
            case HCNetSDK.NET_DVR_STD_AUDIODATA:

                if ( 0 < bufferSize && -1 != m_iPort ) {

                    try {
                        for (i = 0; i < 400; i++) {
                            if ( Player.getInstance().inputData( m_iPort, buffer,
                                    bufferSize ) ) {
                                System.out.println( "Played successfully." );
                                break;
                            }

                            System.out.println( "Playing failed." );
                            Thread.sleep( 10 );
                        }
                    } catch (Exception e) {

                    }

                    if ( i == 400 ) {
                        System.out.println( "inputData failed" );
                    }

//					if ( Player.getInstance().inputData( playPort, buffer, bufferSize ) ) {
//						System.out.println( "Played successfully." );
//					} else {
//						System.out.println( "Playing failed." );
//					}
                }

//				if ( -1 != playPort ) {
//					// closing the player
//				}
//
//				if ( openPlayer( buffer, bufferSize ) ) {
//
//				}
        }

        //if ( -1 == playPort ) return;

        //Player.getInstance().inputData( playPort, buffer, bufferSize );
    }

    public void captureVideo(){

    }

    public void captureFrame() {
        try {
            Player.MPInteger stWidth = new Player.MPInteger();
            Player.MPInteger stHeight = new Player.MPInteger();
            if (!Player.getInstance().getPictureSize(m_iPort, stWidth, stHeight)) {
                return ;
            }
            int nSize = 5 * stWidth.value * stHeight.value;
            byte[] picBuf = new byte[nSize];
            Player.MPInteger stSize = new Player.MPInteger();
            if (!Player.getInstance().getBMP(m_iPort, picBuf, nSize, stSize)) {
                return ;
            }
            if (sDateFormat == null) {
                sDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh_mm_ss_Sss");
            }
            String date = sDateFormat.format(new java.util.Date());
            File dir = new File(StorageHelper.getMediaDirectory(context,Environment.DIRECTORY_PICTURES).getAbsolutePath());
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
