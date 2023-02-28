package com.xontel.surveillancecameras.dahua;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.company.NetSDK.CB_fRealDataCallBackEx;
import com.company.NetSDK.INetSDK;
import com.company.NetSDK.SDK_RealPlayType;
import com.company.PlaySDK.IPlaySDK;
import com.company.PlaySDK.IPlaySDKCallBack;
import com.xontel.surveillancecameras.hikvision.CamPlayerView;
import com.xontel.surveillancecameras.utils.CamPlayer;
import com.xontel.surveillancecameras.utils.StorageHelper;

import java.io.File;
import java.text.SimpleDateFormat;

public class DahuaPlayer extends CamPlayer implements CamPlayerView.SurfaceCallback, CB_fRealDataCallBackEx {
    public static final String TAG = DahuaPlayer.class.getSimpleName();
    public static final int DEFAULT_Dahua_PORT_NUMBER = 37777;
    private final static int RAW_AUDIO_VIDEO_MIX_DATA = 0;
    private boolean _isOpenSound;
    private boolean _isRecording;
    private int _curVolume = 0;
    private boolean _isDelayPlay;


    public DahuaPlayer(Context context) {
       super(context);
    }




    @Override
    public void openStream() {
        new Thread(()->{
            Log.v(TAG, "in player logid : "+mIpCam.getLoginId()+" channel : "+mIpCam.getChannel());
            realPlayId = (int) INetSDK.RealPlayEx(mIpCam.getLoginId(), 0, SDK_RealPlayType.SDK_RType_Realplay_1);
            if (realPlayId == 0) {
                showError(" DAHUA_RealPlay is failed!Err: ");
                return;
            }

            if (realPlayId != 0) {
                INetSDK.SetRealDataCallBackEx(realPlayId, this, 1);
            }
        }).start();


    }






    @Override
    public void unConfigurePlay() {
        if (!isConfigured) {
            return;
        }

        mHandler.removeCallbacks(mRunnable);
        mRunnable = null;
        isConfigured = false;


        try {
            if (_isOpenSound) {
                if(IPlaySDK.PLAYStopSoundShare(m_iPort) !=0){
                    Log.e(TAG, "stop sound failed! ");
                }
            }
            if(IPlaySDK.PLAYStop(m_iPort) != 0){
                showError("stop is failed!");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    @Override
    public void stopStream() {
        if (realPlayId == 0L) {
            Log.e(TAG, "realPlayId = 0");
            return;
        }
        if(INetSDK.StopRealPlayEx(realPlayId)){
            showError("StopRealPlay is failed!Err:");
            return;
        }
        if (_isRecording) {
            if(INetSDK.StopSaveRealData(realPlayId)){
                Log.e(TAG, "cannot StopSaveRealData ");
            }
        }

        if(IPlaySDK.PLAYCloseStream(m_iPort) != 0){
            showError("closeStream is failed!");
            return;
        }
        if(IPlaySDK.PLAYReleasePort(m_iPort) != 0){
            showError("freePort is failed!" + m_iPort);
            return;
        }
        m_iPort = -1 ;
        realPlayId = 0;
    }

    @Override
    public String getTAG() {
        return TAG;
    }

    @Override
    public void configurePlayer(int iDataType, byte[] pDataBuffer, int iDataSize) {
        if (m_iPort >= 0) {
            return;
        }

        m_iPort = IPlaySDK.PLAYGetFreePort();
        if (m_iPort == -1) {
            showError("getPort is failed with: ");
            return;
        }


        boolean isOpened = IPlaySDK.PLAYOpenStream(m_iPort, null, 0, STREAM_BUF_SIZE) != 0;
        if (!isOpened) {
            showError( "OpenStream Failed");

        }


        boolean isPlaying = IPlaySDK.PLAYPlay(m_iPort, mCamPlayerView.getSurfaceView()) != 0;
        if (!isPlaying) {
            showError( "PLAYPlay Failed");
            IPlaySDK.PLAYCloseStream(m_iPort);
            return;
        }
        if (_isOpenSound) {
            boolean isSuccess = IPlaySDK.PLAYPlaySoundShare(m_iPort) != 0;
            if (!isSuccess) {
                showError( "SoundShare Failed");
//                IPlaySDK.PLAYStop(m_iPort);
//                IPlaySDK.PLAYCloseStream(m_iPort);
            }
            if (-1 == _curVolume) {
                _curVolume = IPlaySDK.PLAYGetVolume(m_iPort);
            } else {
                IPlaySDK.PLAYSetVolume(m_iPort, _curVolume);
            }
        }
        if (_isDelayPlay) {
            if (IPlaySDK.PLAYSetDelayTime(m_iPort, 500 /*ms*/, 1000 /*ms*/) == 0) {
                Log.d(TAG, "SetDelayTime Failed");
            }
        }

        IPlaySDK.PLAYSetDisplayCallBack(m_iPort, new IPlaySDKCallBack.fDisplayCBFun() {
            @Override
            public void invoke(int i, byte[] bytes, int i1, int i2, int i3, int i4, int i5, long l) {
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
        }, mIpCam.getChannel());

        isConfigured = true ;
    }

    @Override
    public void play(byte[] pDataBuffer, int iDataSize) {
        IPlaySDK.PLAYInputData(m_iPort, pDataBuffer, pDataBuffer.length);
    }

    public void captureVideo(){
        try {
            sDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh_mm_ss_Sss");
            _isRecording = true;
            File dir = new File(StorageHelper.getMediaDirectory(context,Environment.DIRECTORY_MOVIES).getAbsolutePath());
            String date = sDateFormat.format(new java.util.Date());
            File file = new File(dir, date + ".mp4");
            IPlaySDK.PLAYStartDataRecord(m_iPort,file.getAbsolutePath(),0,null,0);
        }catch (Exception e){
            Log.i("TAG", "captureFrame: "+e.getMessage());
        }
    }

    @Override
    public void takeSnapshot() {
        try {
            sDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh_mm_ss_Sss");
            File dir = new File(StorageHelper.getMediaDirectory(context,Environment.DIRECTORY_PICTURES).getAbsolutePath());
            String date = sDateFormat.format(new java.util.Date());
            File file = new File(dir, date + ".png");
            IPlaySDK.PLAYCatchPic(m_iPort,file.getAbsolutePath());
        }catch (Exception e){
            Log.i("TAG", "captureFrame: "+e.getMessage());
        }
    }

    public void stopCaptureVideo(){
        IPlaySDK.PLAYStopDataRecord(m_iPort);
    }


    @Override
    public void invoke(long l, int dataType, byte[] buffer, int i1, int i2) {
        if (RAW_AUDIO_VIDEO_MIX_DATA == dataType ) {
            if(!isConfigured){
                configurePlayer(dataType, buffer, i1);
            }else {
                play(buffer, buffer.length);
            }
        }
    }



}
