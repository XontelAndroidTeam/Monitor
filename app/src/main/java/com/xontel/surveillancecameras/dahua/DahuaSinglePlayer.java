package com.xontel.surveillancecameras.dahua;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.company.NetSDK.CB_fRealDataCallBackEx;
import com.company.NetSDK.INetSDK;
import com.company.NetSDK.SDK_RealPlayType;
import com.company.PlaySDK.IPlaySDK;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.utils.CamPlayer;
import com.xontel.surveillancecameras.utils.StorageHelper;

import java.io.File;
import java.text.SimpleDateFormat;

public class DahuaSinglePlayer extends CamPlayer implements CB_fRealDataCallBackEx {
    public static final String TAG = DahuaSinglePlayer.class.getSimpleName();
    private final static int RAW_AUDIO_VIDEO_MIX_DATA = 0;
    private boolean _isOpenSound;
    private boolean _isRecording;
    private IpCam mIpCam;
    private int _curVolume = 0;
    private boolean _isDelayPlay;


    public DahuaSinglePlayer(Context context, IpCam ipCam) {
       super(context, ipCam);
    }

    @Override
    public void startLiveView() {

    }

    @Override
    public void stopLiveView() {

    }


    @Override
    public void openStream() {
        realPlayId = (int) INetSDK.RealPlayEx(mIpCam.getLoginId(), mIpCam.getChannel(), SDK_RealPlayType.SDK_RType_Realplay_1);
        if (realPlayId == 0L) {
            Log.e(TAG, "startPlay: RealPlayEx failed!");
            isError.postValue(true);
            return;
        }

        if (realPlayId != 0L) {
            INetSDK.SetRealDataCallBackEx(realPlayId, this, 1);
        }

    }




    public void configurePlayer(SurfaceHolder surfaceView) {
//        boolean isOpened = IPlaySDK.PLAYOpenStream(m_iPort, null, 0, STREAM_BUF_SIZE) != 0;
//        if (!isOpened) {
//            Log.d(TAG, "OpenStream Failed");
//           isError.postValue(true);
//        }
//        boolean isPlaying = IPlaySDK.PLAYPlay(m_iPort, surfaceView) != 0;
//        if (!isPlaying) {
//            Log.d(TAG, "PLAYPlay Failed");
//            IPlaySDK.PLAYCloseStream(m_iPort);
//            isError.postValue(true);
//        }
//        if (_isOpenSound) {
//            boolean isSuccess = IPlaySDK.PLAYPlaySoundShare(m_iPort) != 0;
//            if (!isSuccess) {
//                Log.d(TAG, "SoundShare Failed");
//                IPlaySDK.PLAYStop(m_iPort);
//                IPlaySDK.PLAYCloseStream(m_iPort);
//                isError.postValue(true);
//            }
//            if (-1 == _curVolume) {
//                _curVolume = IPlaySDK.PLAYGetVolume(m_iPort);
//            } else {
//                IPlaySDK.PLAYSetVolume(m_iPort, _curVolume);
//            }
//        }
//        if (_isDelayPlay) {
//            if (IPlaySDK.PLAYSetDelayTime(m_iPort, 500 /*ms*/, 1000 /*ms*/) == 0) {
//                Log.d(TAG, "SetDelayTime Failed");
//            }
//        }
//
//        isError.setValue(false);
//        isConfigured = true ;

    }

    @Override
    public void unConfigurePlay() {
        isConfigured = false;
        try {
            if (_isOpenSound) {
                IPlaySDK.PLAYStopSoundShare(m_iPort);
            }
            IPlaySDK.PLAYStop(m_iPort);
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
        INetSDK.StopRealPlayEx(realPlayId);
        if (_isRecording) {
            INetSDK.StopSaveRealData(realPlayId);
        }
        IPlaySDK.PLAYReleasePort(m_iPort);
        m_iPort = -1 ;
        realPlayId = 0;
    }

    @Override
    public String getTAG() {
        return TAG;
    }

    @Override
    public void configurePlayer(int iDataType, byte[] pDataBuffer, int iDataSize) {

    }

    @Override
    public void play(byte[] pDataBuffer, int iDataSize) {

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
        if (RAW_AUDIO_VIDEO_MIX_DATA == dataType && isConfigured) {
            isLoading.postValue(false);
            IPlaySDK.PLAYInputData(m_iPort, buffer, buffer.length);
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }
}
