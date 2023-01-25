package com.xontel.surveillancecameras.dahua;

import android.graphics.PixelFormat;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.company.NetSDK.CB_fDisConnect;
import com.company.NetSDK.CB_fHaveReConnect;
import com.company.NetSDK.CB_fRealDataCallBackEx;
import com.company.NetSDK.CB_fSubDisConnect;
import com.company.NetSDK.EM_LOGIN_SPAC_CAP_TYPE;
import com.company.NetSDK.INetSDK;
import com.company.NetSDK.NET_DEVICEINFO_Ex;
import com.company.NetSDK.SDK_RealPlayType;
import com.company.PlaySDK.IPlaySDK;
import com.hikvision.netsdk.HCNetSDK;
import org.MediaPlayer.PlayM4.Player;
import java.io.File;
import java.text.SimpleDateFormat;

public class DahuaSinglePlayer {
    public static final String TAG = DahuaSinglePlayer.class.getSimpleName();
    private byte[] streamBuffer = new byte[102400];
    private final HCNetSDK netSDKInstance = HCNetSDK.getInstance();
    private final Player playerInstance = Player.getInstance();
    private final static int STREAM_BUF_SIZE = 1024 * 1024 * 2;
    private final static int RAW_AUDIO_VIDEO_MIX_DATA = 0;
    private NET_DEVICEINFO_Ex mNET_deviceinfo = null;
    private SurfaceView mSurfaceView;
    private CB_fRealDataCallBackEx _realDataCallBackEx ;
    private Boolean isRecording = false;
    private SimpleDateFormat sDateFormat;
    private long loginId = 0; // return by NET_DVR_Login_v30
    private long realPlayId = -1; // return by NET_DVR_RealPlay_V30
    private int playbackId  = -1; // return by NET_DVR_PlayBackByTime
    private int playPort = -1; // play port
    private int startChannel = 2; // start channel no
    private boolean stopPlayback = false;
    private boolean isShow = true;
    private int loginType = EM_LOGIN_SPAC_CAP_TYPE.EM_LOGIN_SPEC_CAP_MOBILE;
    private boolean _isOpenSound;
    private boolean _isRecording;
    private int _curVolume = 0;
    private boolean _isDelayPlay;
    private  int channel = 0;
    private int logId;
    private int streamType ;


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

    public DahuaSinglePlayer(int channel, int logId, int streamType) {
        this.channel = channel;
        this.logId = logId;
        this.streamType = streamType ;
    }

    public  void initView(SurfaceView surfaceView) {
        mSurfaceView = surfaceView;
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                mSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
                if (-1 == playPort) {
                    playPort = playerInstance.getPort();
                    Surface surface = holder.getSurface();
                    if (surface.isValid()) {
                        startPlay(logId,channel,streamType,mSurfaceView);
                     //   if(!Player.getInstance().setCurrentFrameNum(m_iPort, 60)){
                       //     Log.e(TAG, "Player failed to set frame rate!");
                      //  }
                      //  if (!Player.getInstance().setVideoWindow(m_iPort, 0, holder)) {
                      //      Log.e(TAG, "Player failed to set or destroy display area!");
                      //  }
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.i(TAG, "Player setVideoWindow release!" + playPort);
                if (-1 == playPort) {
                    return;
                }
                if (holder.getSurface().isValid()) {
                  //  if (!Player.getInstance().setVideoWindow(playPort, 0, null)) {
                     //   Log.e(TAG, "Player failed to set or destroy display area!");
                  //  }
                }
            }
        });
    }


    public void processRealData(int iDataType, byte[] pDataBuffer, int iDataSize, int iStreamMode) {
        // must decode data
//        if (HCNetSDK.NET_DVR_SYSHEAD == iDataType) {
        if (playPort >= 0) {
            return;
        }
        playPort = playerInstance.getPort();
        if (playPort == -1) {
            Log.e(TAG, "getPort is failed with: "
                    + playerInstance.getLastError(playPort));
            return;
        }
        Log.i(TAG, "getPort succ with: " + playPort);
        if (iDataSize > 0) {
            // set stream mode
            if (!playerInstance.setStreamOpenMode(playPort, iStreamMode)) {
                Log.e(TAG, "setStreamOpenMode failed");
                return;
            }
            // open stream
            if (!playerInstance.openStream(playPort, pDataBuffer, iDataSize, 2 * 1024 * 1024)) {
                Log.e(TAG, "openStream failed");
                return;
            }

            if (!playerInstance.play(playPort,
                    mSurfaceView.getHolder())) {
                Log.e(TAG, "play failed");
                return;
            }

            if (!playerInstance.playSound(playPort)) {
                Log.e(TAG, "playSound failed with error code:"
                        + playerInstance.getLastError(playPort));
                return;
            }
        }
    }



    public void startPlay(long loginId, int channel, int streamType, SurfaceView surfaceView) {
        realPlayId = INetSDK.RealPlayEx(loginId, channel, SDK_RealPlayType.SDK_RType_Realplay_1);
        if (realPlayId == 0L) {
            Log.e(TAG, "startPlay: RealPlayEx failed!");
            return;
        }
        if (!prePlay(surfaceView)) {
            Log.d(TAG, "prePlay returned false..");
            INetSDK.StopRealPlayEx(realPlayId);
            return;
        }
        if (realPlayId != 0L) {
            _realDataCallBackEx = new CB_fRealDataCallBackEx() {
                @Override
                public void invoke(long l, int dataType, byte[] buffer, int i1, int i2) {
                    if (RAW_AUDIO_VIDEO_MIX_DATA == dataType) {
                        processRealData(dataType, buffer, i1, 0);
                        IPlaySDK.PLAYInputData(playPort, buffer, buffer.length);
                    }
                }
            };
            INetSDK.SetRealDataCallBackEx(realPlayId, _realDataCallBackEx, 1);
        }
    }

    private boolean prePlay(SurfaceView surfaceView) {
        boolean isOpened = IPlaySDK.PLAYOpenStream(playPort, null, 0, STREAM_BUF_SIZE) != 0;

        if (!isOpened) {
            Log.d(TAG, "OpenStream Failed");
            return false;
        }
        boolean isPlaying = IPlaySDK.PLAYPlay(playPort, surfaceView) != 0;
        if (!isPlaying) {
            Log.d(TAG, "PLAYPlay Failed");
            IPlaySDK.PLAYCloseStream(playPort);
            return false;
        }
        if (_isOpenSound) {
            boolean isSuccess = IPlaySDK.PLAYPlaySoundShare(playPort) != 0;
            if (!isSuccess) {
                Log.d(TAG, "SoundShare Failed");
                IPlaySDK.PLAYStop(playPort);
                IPlaySDK.PLAYCloseStream(playPort);
                return false;
            }
            if (-1 == _curVolume) {
                _curVolume = IPlaySDK.PLAYGetVolume(playPort);
            } else {
                IPlaySDK.PLAYSetVolume(playPort, _curVolume);
            }
        }
        if (_isDelayPlay) {
            if (IPlaySDK.PLAYSetDelayTime(playPort, 500 /*ms*/, 1000 /*ms*/) == 0) {
                Log.d(TAG, "SetDelayTime Failed");
            }
        }
        return true;
    }



    public void startPlayHik(long loginHandle, int channel, int streamType, SurfaceView surfaceView) {
        realPlayId = INetSDK.RealPlayEx(loginId, 0, SDK_RealPlayType.SDK_RType_Realplay_1);
        if (realPlayId == 0L) {
            Log.e(TAG, "startPlay: RealPlayEx failed!");
            return;
        }
        if (!prePlayHik(surfaceView)) {
            Log.d(TAG, "prePlay returned false..");
            INetSDK.StopRealPlayEx(realPlayId);
            return;
        }
        if (realPlayId != 0L) {
            _realDataCallBackEx = new CB_fRealDataCallBackEx() {
                @Override
                public void invoke(long l, int dataType, byte[] buffer, int i1, int i2) {
//                    if (RAW_AUDIO_VIDEO_MIX_DATA == dataType) {
//                        processRealData(dataType, buffer, i1, 0);

                    System.arraycopy(
                            buffer,
                            0,
                            streamBuffer,
                            0,
                            buffer.length
                    );
                    Log.v(TAG, " iDataType : "+ dataType +
                            " pDataBuffer : "+streamBuffer.length +
                            "hashCode : "+streamBuffer.hashCode()+
                            " iDataSize : "+i1);
                    if(!playerInstance.inputData(playPort, streamBuffer, i1)){
                        Log.v(TAG, "failed play");
                    }
//                    }
                }
            };
            INetSDK.SetRealDataCallBackEx(realPlayId, _realDataCallBackEx, 1);
        }
    }
    private boolean prePlayHik(SurfaceView surfaceView) {
        if (!playerInstance.setStreamOpenMode(playPort,
                Player.STREAM_REALTIME)) // set stream mode
        {
            Log.e(TAG, "setStreamOpenMode failed");
            return false;
        }
        boolean isOpened = playerInstance.openStream(playPort, streamBuffer, 0, STREAM_BUF_SIZE) ;
        if (!isOpened) {
            Log.d(TAG, "OpenStream Failed");
            return false;
        }
        if (!playerInstance
                .setVideoWindow(playPort, 0, surfaceView.getHolder())) {
            Log.e(TAG, "Player setVideoWindow failed!");
        }
        boolean isPlaying = playerInstance.play(playPort, surfaceView.getHolder());
        if (!isPlaying) {
            Log.d(TAG, "PLAYPlay Failed");
            playerInstance.closeStream(playPort);
            return false;
        }

        return true;
    }


    public void stopPlay() {
        if (realPlayId == 0L) return;

        try {
            IPlaySDK.PLAYStop(playPort);
            if (_isOpenSound) {
                IPlaySDK.PLAYStopSoundShare(playPort);
            }
            IPlaySDK.PLAYCloseStream(playPort);
            INetSDK.StopRealPlayEx(realPlayId);
            if (_isRecording) {
                INetSDK.StopSaveRealData(realPlayId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        realPlayId = 0;
        _isRecording = false;
    }

    public void captureFrame() {
        try {
            sDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh_mm_ss_Sss");
            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/capture");
            if (!dir.exists()) {dir.mkdir();}
            String date = sDateFormat.format(new java.util.Date());
            File file = new File(dir, date + ".png");
            IPlaySDK.PLAYCatchPic(playPort,file.getAbsolutePath());
        }catch (Exception e){
            Log.i("TAG", "captureFrame: "+e.getMessage());
        }
    }

    public void captureVideo(){
        try {
            sDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh_mm_ss_Sss");
            _isRecording = true;
            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/capture");
            if (!dir.exists()) {dir.mkdir();}
            String date = sDateFormat.format(new java.util.Date());
            File file = new File(dir, date + ".mp4");
            IPlaySDK.PLAYStartDataRecord(playPort,file.getAbsolutePath(),0,null,0);
        }catch (Exception e){
            Log.i("TAG", "captureFrame: "+e.getMessage());
        }
    }

    public void stopCaptureVideo(){
        IPlaySDK.PLAYStopDataRecord(playPort);
    }

}
