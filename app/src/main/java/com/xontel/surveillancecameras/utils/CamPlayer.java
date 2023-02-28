package com.xontel.surveillancecameras.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.SurfaceHolder;

import androidx.lifecycle.MutableLiveData;

import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.hikvision.CamPlayerView;

import java.text.SimpleDateFormat;

public abstract class CamPlayer implements CamPlayerView.SurfaceCallback {

    public final static int STREAM_BUF_SIZE = 1024 * 1024 * 2;
    public Context context;
    public SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd-hh_mm_ss_Sss");
    public MutableLiveData<Boolean> isError = new MutableLiveData(false);
    public MutableLiveData<Boolean> isLoading = new MutableLiveData(true);
    public CamPlayerView mCamPlayerView;

    private boolean isSurfaceCreated;
    public int m_iPort = -1;

    public int realPlayId = -1 ;
    public IpCam mIpCam;
    public boolean isStopped = true;
    public boolean isConfigured = false;

    public int lock = 0;

    public Handler mHandler = new Handler(Looper.getMainLooper());
    public Runnable mRunnable;



    public CamPlayer(Context context) {
        this.context = context;
    }


    public void startLiveView() {
        openStream();
    }

    public void stopLiveView() {
        if (mCamPlayerView != null)
            mCamPlayerView.onDetachedFromPlayer();
        detachView();
        new Thread(() -> {

            unConfigurePlay();
            stopStream();
            isStopped = true;
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (mCamPlayerView != null) {
                        startLiveView();
                    }
                }
            });

        }).start();

    }


    public abstract void openStream();

    public abstract void stopStream();

    public abstract String getTAG();

    public void showError(String logMessage) {
        Log.e(getTAG(), logMessage);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mCamPlayerView.showLoading(false);
                mCamPlayerView.showError(logMessage);
            }
        });

    }

    public void attachView(CamPlayerView camPlayerView, IpCam ipCam) {
        this.mIpCam = ipCam;
        this.mCamPlayerView = camPlayerView;
        mCamPlayerView.onAttachToPlayer(this);
        if (mCamPlayerView.isSurfaceCreated() && isStopped) {
            startLiveView();
        }
    }

    public abstract void configurePlayer(int iDataType, byte[] pDataBuffer, int iDataSize);

    public abstract void play(byte[] pDataBuffer, int iDataSize);

    public abstract void unConfigurePlay();



    public abstract void captureVideo();


    public abstract void takeSnapshot();


    public void detachView() {
        mCamPlayerView = null;
        mIpCam = null;
    }

    public IpCam getIpCam() {
        return mIpCam;
    }

    @Override
    public void onSurfaceCreated() {
        isSurfaceCreated = true;
        startLiveView();
    }

    @Override
    public void onSurfaceDestroyed() {
        isSurfaceCreated = false;
    }
}
