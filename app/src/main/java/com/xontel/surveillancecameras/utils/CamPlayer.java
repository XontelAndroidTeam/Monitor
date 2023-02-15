package com.xontel.surveillancecameras.utils;

import android.content.Context;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import java.text.SimpleDateFormat;

public abstract class CamPlayer implements SurfaceHolder.Callback{
    public static final int DEFAULT_HIKVISION_PORT_NUMBER = 8000;
    public static final int DEFAULT_Dahua_PORT_NUMBER = 37777;
    public final static int STREAM_BUF_SIZE = 1024 * 1024 * 2;
    public Context context;
    public SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh_mm_ss_Sss");
    public MutableLiveData<Boolean> isError = new MutableLiveData(false);
    public MutableLiveData<Boolean> isLoading = new MutableLiveData(true);
    public SurfaceView mSurfaceView;
    public int m_iPort = -1;
    public long realPlayId = -1; // return by NET_DVR_RealPlay_V30
    public int channel = 0;
    public long logId;
    public boolean isConfigured = false;
    public boolean isRecording ;

    public CamPlayer(int channel, int logId, Context context) {
        this.channel = channel;
        this.logId = logId;
        this.context = context;

    }


    public CamPlayer() {
    }

    public abstract void startLiveView();
    public abstract void stopLiveView();
    public abstract void openStream();

    public abstract void stopStream();

    public abstract String getTAG();

    public void showError(String logMessage){
        Log.e(getTAG(), logMessage);
        isError.postValue(true);
        isLoading.postValue(false);
    }

    public void attachView(SurfaceView surfaceView) {
        this.mSurfaceView = surfaceView;
    }

    public abstract void configurePlayer(int iDataType, byte[] pDataBuffer, int iDataSize);

    public abstract void play(byte[] pDataBuffer, int iDataSize);

    public abstract void unConfigurePlay();



    public abstract void captureVideo();


    public abstract void takeSnapshot();

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
//        Log.v(getTAG(), "surfaceCreated");
//        if (-1 != m_iPort) {
//            Log.v(getTAG(), "configuring");
//            Surface surface = holder.getSurface();
//            if (surface.isValid() & !isConfigured) {
//                configurePlayer(holder);
//            }
//        }
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
//        Log.v(getTAG(), "surfaceChanged");
    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
//        Log.v(getTAG(), "surfaceDestroyed");
//        unConfigurePlay();

    }
}
