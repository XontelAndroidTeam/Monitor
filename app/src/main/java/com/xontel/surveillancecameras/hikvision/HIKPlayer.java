package com.xontel.surveillancecameras.hikvision;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.INT_PTR;
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO;
import com.hikvision.netsdk.RealPlayCallBack;
import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.utils.CamPlayer;
import com.xontel.surveillancecameras.utils.StorageHelper;

import org.MediaPlayer.PlayM4.Player;
import org.MediaPlayer.PlayM4.PlayerCallBack;
import org.videolan.libvlc.util.LoadingDots;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;


public class HIKPlayer extends CamPlayer implements RealPlayCallBack, SurfaceHolder.Callback {
    public static final String TAG = HIKPlayer.class.getSimpleName();
    private LoadingDots mLoadingDots;
    private TextView errorTextView;
    private TextView name;
    private ImageView addBtn;
    private ViewStub surfaceStub ;
    private SurfaceView mSurfaceView;
    private IpCam mIpCam;

    public HIKPlayer(Context context, IpCam ipCam) {
        super(context, ipCam);
        this.mIpCam = ipCam;
    }

    @Override
    public void attachView(HikCamView hikCamView) {
        this.mHikCamView = hikCamView;
        mLoadingDots = hikCamView.findViewById(R.id.loading_dots);
        name = hikCamView.findViewById(R.id.tv_cam_name);
        errorTextView = hikCamView.findViewById(R.id.error_stream);
        addBtn = hikCamView.findViewById(R.id.iv_add);
        surfaceStub = hikCamView.findViewById(R.id.stub);
        if(surfaceStub != null) {
                mSurfaceView = (SurfaceView) surfaceStub.inflate();
        }else{
            createNewSurface();
        }
        mSurfaceView.getHolder().addCallback(this);
        mLoadingDots.setVisibility(View.VISIBLE);
        name.setVisibility(View.VISIBLE);
        name.setText(mIpCam.getName());
        addBtn.setVisibility(View.GONE);
    }

    private void createNewSurface() {
        mSurfaceView = new SurfaceView(context);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        );
        mHikCamView.addView(mSurfaceView, layoutParams);
    }

    @Override
    public void startLiveView() {
        openStream();
    }

    @Override
    public void stopLiveView() {
        unConfigurePlay();
        stopStream();
        detachView();

    }


    // OUR START IS HERE
    @Override
    public void openStream() {
        NET_DVR_PREVIEWINFO previewInfo = new NET_DVR_PREVIEWINFO();
        previewInfo.lChannel = mIpCam.getChannel();
        previewInfo.dwStreamType = 1; // mainstream
        previewInfo.bBlocked = 1;

        new Thread(() -> {
            realPlayId = HCNetSDK.getInstance().NET_DVR_RealPlay_V40((int) mIpCam.getLoginId(), previewInfo, HIKPlayer.this);
            if (realPlayId < 0L) {
                showError("NET_DVR_RealPlay is failed!Err: " + HCNetSDK.getInstance().NET_DVR_GetLastError() + " channel : " + mIpCam.getChannel());
                return;
            }
        }).start();


    }

    @Override
    public void showError(String logMessage) {
        Log.e(getTAG(), logMessage);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mLoadingDots.setVisibility(View.GONE);
                errorTextView.setText(logMessage);
                errorTextView.setVisibility(View.VISIBLE);
            }
        });

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


            if (!Player.getInstance().play(m_iPort, mSurfaceView.getHolder())) {
                showError("play failed");
                return;
            }
            if (!Player.getInstance().playSound(m_iPort)) {
                showError("playSound failed with error code:"
                        + Player.getInstance().getLastError(m_iPort));
                return;
            }
            if (!Player.getInstance().setImageCorrection(m_iPort, 0)) {
                showError("failed to setImageCorrection :"
                        + Player.getInstance().getLastError(m_iPort));
                return;
            }
            Player.getInstance().setDisplayCB(m_iPort, (i, bytes, i1, i2, i3, i4, i5, i6) -> new Handler(Looper.getMainLooper()).post(() -> mLoadingDots.setVisibility(View.GONE)));
            isConfigured = true;
            Log.v(TAG, "configured");
        }

    }

    @Override
    public void play(byte[] pDataBuffer, int iDataSize) {
        if (!Player.getInstance().inputData(m_iPort, pDataBuffer, iDataSize)) {
            Log.e(TAG, "inputData failed with: " + Player.getInstance().getLastError(m_iPort));
        }
    }


    // THE END
    @Override
    public void unConfigurePlay() {
        isConfigured = false;
        if (!Player.getInstance().stopSound()) {
            showError( "stop sound failed!");
            return;
        }


//         player stop play
        if (!Player.getInstance().stop(m_iPort)) {
            showError("stop is failed!");
            return;
        }
        Log.v(TAG, "stopping");

    }

    @Override
    public void detachView() {
        mHikCamView = null;
        mSurfaceView = null;
        mLoadingDots.setVisibility(View.GONE);
        name.setVisibility(View.GONE);
        name.setText("");
        addBtn.setVisibility(View.VISIBLE);
    }

    @Override
    public void stopStream() {

        if (realPlayId < 0L) {
            return;
        }
        // net sdk stop preview
        if (!HCNetSDK.getInstance().NET_DVR_StopRealPlay( realPlayId)) {
            showError("StopRealPlay is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return;
        }
        Log.v(TAG, "stop real play");



        if (!Player.getInstance().closeStream(m_iPort)) {
            showError( "closeStream is failed!");
            return;
        }
        Log.v(TAG, "close stream");
        if (!Player.getInstance().freePort(m_iPort)) {
            showError( "freePort is failed!" + m_iPort);
            return;
        }

        Log.v(TAG, "free port");




        m_iPort = -1;
        realPlayId = -1;
        mLoadingDots.setVisibility(View.GONE);
        name.setText("");
        addBtn.setVisibility(View.VISIBLE);
    }


    @Override
    public String getTAG() {
        return TAG;
    }


    @Override
    public void fRealDataCallBack(int iRealhandle, int iDataType, byte[] pDataBuffer, int iDataSize) {

        if (HCNetSDK.NET_DVR_SYSHEAD == iDataType) {
            configurePlayer(iDataType, pDataBuffer, iDataSize);
        } else {
            if (isConfigured) {
                play(pDataBuffer, iDataSize);
            }
        }
    }



    @Override
    public void captureVideo() {
        if (!isRecording) {
            String date = sDateFormat.format(new java.util.Date());
            File dir = new File(StorageHelper.getMediaDirectory(context, Environment.DIRECTORY_MOVIES).getAbsolutePath() + "/monitor");
            File file = new File(dir, date + ".mp4");
            if (!HCNetSDK.getInstance().NET_DVR_SaveRealData((int) realPlayId, file.getAbsolutePath())) {
                Log.e(TAG, "NET_DVR_SaveRealData on channel " + mIpCam.getChannel() + " failed! error: "
                        + HCNetSDK.getInstance().NET_DVR_GetLastError());
                return;
            } else {
                Log.v(TAG, "Record started successfully on channel " + mIpCam.getChannel());
            }
            isRecording = true;
        } else {
            if (!HCNetSDK.getInstance().NET_DVR_StopSaveRealData((int) realPlayId)) {
                Log.e(TAG, "NET_DVR_StopSaveRealData failed! error: "
                        + HCNetSDK.getInstance()
                        .NET_DVR_GetLastError());
            } else {
                System.out.println("NET_DVR_StopSaveRealData succ!");
            }
            isRecording = false;
        }
    }


    @Override
    public void takeSnapshot() {
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
            showError("error: " + err.getMessage());

        }
    }


    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        Log.v(TAG, "surfaceCreated");
        startLiveView();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Log.v(TAG, "surfaceChanged");
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        Log.v(TAG, "surfaceDestroyed");
    }
}
