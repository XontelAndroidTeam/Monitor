package com.xontel.surveillancecameras.utils;

import android.content.Context;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.hikvision.HikCamView;

import java.text.SimpleDateFormat;

public abstract class CamPlayer implements SurfaceHolder.Callback{
    public static final int DEFAULT_HIKVISION_PORT_NUMBER = 8000;
    public static final int DEFAULT_Dahua_PORT_NUMBER = 37777;
    public final static int STREAM_BUF_SIZE = 1024 * 1024 * 2;
    public Context context;
    public SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh_mm_ss_Sss");
    public MutableLiveData<Boolean> isError = new MutableLiveData(false);
    public MutableLiveData<Boolean> isLoading = new MutableLiveData(true);
    public HikCamView mHikCamView;
    public int m_iPort = -1;

    public int realPlayId = -1 ;
    private IpCam mIpCam;
    public boolean isConfigured = false;
    public boolean isRecording ;

    public CamPlayer(Context context, IpCam ipCam) {
        this.mIpCam = ipCam;
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

    public void attachView(HikCamView hikCamView) {
        this.mHikCamView = hikCamView;
    }

    public abstract void configurePlayer(int iDataType, byte[] pDataBuffer, int iDataSize);

    public abstract void play(byte[] pDataBuffer, int iDataSize);

    public abstract void unConfigurePlay();



    public abstract void captureVideo();


    public abstract void takeSnapshot();


    public void detachView() {

    }
}
