package com.xontel.surveillancecameras.dahua;

import android.content.Context;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

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
import com.xontel.surveillancecameras.utils.StorageHelper;

import org.MediaPlayer.PlayM4.Player;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
    public MutableLiveData<Boolean> isError = new MutableLiveData(false);
    public MutableLiveData<Boolean> isLoading = new MutableLiveData(true);
    private long loginId = 0; // return by NET_DVR_Login_v30
    private long realPlayId = -1; // return by NET_DVR_RealPlay_V30
    private int playbackId  = -1; // return by NET_DVR_PlayBackByTime
    private int playPort = -1; // play port
    private int startChannel = 2; // start channel no
    private boolean stopPlayback = false;
    private boolean isShow = true;
    private Context context;
    private int loginType = EM_LOGIN_SPAC_CAP_TYPE.EM_LOGIN_SPEC_CAP_MOBILE;
    private boolean _isOpenSound;
    private boolean _isRecording;
    private int _curVolume = 0;
    private boolean _isDelayPlay;
    private  int channel = 0;
    private int logId;
    private int streamType ;


    public DahuaSinglePlayer(int channel, int logId, int streamType,Context context) {
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
                mSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
                if (-1 == playPort) {
                    playPort = playerInstance.getPort();
                    Surface surface = holder.getSurface();
                    if (surface.isValid()) {
                        startPlay(logId,channel,streamType,mSurfaceView);
                        isLoading.setValue(false);
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.i(TAG, "Player setVideoWindow release!" + playPort);
                stopPlay();
                isLoading.setValue(true);
                if (-1 == playPort) {
                    return;
                }
            }
        });
    }


    public void processRealData(int iDataType, byte[] pDataBuffer, int iDataSize, int iStreamMode) {
        if (playPort >= 0) {
            return;
        }
        playPort = playerInstance.getPort();
        if (playPort == -1) {
            Log.e(TAG, "getPort is failed with: " + playerInstance.getLastError(playPort));
            return;
        }
        if (iDataSize > 0) {
            // set stream mode
            if (!playerInstance.setStreamOpenMode(playPort, iStreamMode)) {
                Log.e(TAG, "setStreamOpenMode failed");
                isError.postValue(true);
                return;
            }else{
                isError.postValue(false);
            }
            // open stream
            if (!playerInstance.openStream(playPort, pDataBuffer, iDataSize, 2 * 1024 * 1024)) {
                Log.e(TAG, "openStream failed");
                isError.postValue(true);
                return;
            }else{
                isError.postValue(false);
            }

            if (!playerInstance.play(playPort, mSurfaceView.getHolder())) {
                Log.e(TAG, "play failed");
                isError.postValue(true);
                return;
            }else{
                isError.postValue(false);
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
            File dir = new File(StorageHelper.getMediaDirectory(context,Environment.DIRECTORY_PICTURES).getAbsolutePath());
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
            File dir = new File(StorageHelper.getMediaDirectory(context,Environment.DIRECTORY_MOVIES).getAbsolutePath());
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
