package com.xontel.surveillancecameras.hikvision;

import static com.hikvision.netsdk.SDKError.NET_DVR_CHANNEL_ERROR;
import static com.hikvision.netsdk.SDKError.NET_DVR_CHAN_NOTSUPPORT;

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
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

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
import java.util.Date;


public class HIKPlayer extends CamPlayer implements RealPlayCallBack, SurfaceHolder.Callback, DefaultLifecycleObserver {
    public static final String TAG = HIKPlayer.class.getSimpleName();
    private LoadingDots mLoadingDots;
    private TextView errorTextView;
    //    private TextView name;
    private ImageView addBtn;
    private ViewStub surfaceStub;
    private SurfaceView mSurfaceView;
    private IpCam mIpCam;

    private int lock = 0;

    private boolean playing;

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private Runnable mRunnable;

    public HIKPlayer(Context context, IpCam ipCam) {
        super(context, ipCam);
        this.mIpCam = ipCam;
    }

    public HIKPlayer(Context context) {
        super(context);
        Log.v(TAG, "Player created");
        Log.v("TAGGO", "created : " + hashCode());
    }


    @Override
    public void attachView(HikCamView hikCamView) {
        Log.v("TAGGO", "attached : " + hashCode());
        if (mIpCam == null) {
            throw new IllegalStateException("Set the IpCam Object first");
        }
        this.mHikCamView = hikCamView;
        Log.v("TAGGO", "attaching view channel :" + mIpCam.getChannel());
        mLoadingDots = hikCamView.findViewById(R.id.loading_dots);
//        name = hikCamView.findViewById(R.id.tv_cam_name);
        errorTextView = hikCamView.findViewById(R.id.error_stream);
        addBtn = hikCamView.findViewById(R.id.iv_add);
        surfaceStub = hikCamView.findViewById(R.id.stub);
        if (surfaceStub != null) {
            mSurfaceView = (SurfaceView) surfaceStub.inflate();
        } else {
            createNewSurface();
        }
        mSurfaceView.getHolder().addCallback(this);
        mLoadingDots.setVisibility(View.VISIBLE);
//        name.setVisibility(View.VISIBLE);
//        name.setText(mIpCam.getName());
        addBtn.setVisibility(View.GONE);
    }

    private void createNewSurface() {
        Log.v(TAG, "creating surface channel :" + mIpCam.getChannel());
        mSurfaceView = new SurfaceView(context);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        );
        mHikCamView.addView(mSurfaceView, layoutParams);
    }

    @Override
    public void startLiveView() {
        Log.v("TAGGO", "started : " + hashCode());
        Log.v(TAG, "Starting live channel :" + mIpCam.getChannel());
        openStream();
    }

    @Override
    public void stopLiveView() {
        Log.v("TAGGO", "stopped : " + hashCode());
        Log.v(TAG, "stopping live channel :" + mIpCam.getChannel());
        mLoadingDots.setVisibility(View.GONE);
        addBtn.setVisibility(View.VISIBLE);
        errorTextView.setVisibility(View.GONE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                unConfigurePlay();
                stopStream();
                detachView();
            }
        }).start();



    }


    // OUR START IS HERE
    @Override
    public void openStream() {
        NET_DVR_PREVIEWINFO previewInfo = new NET_DVR_PREVIEWINFO();
        previewInfo.lChannel = mIpCam.getChannel();
        previewInfo.dwStreamType = mIpCam.getStreamType(); // mainstream
        previewInfo.bBlocked = 1;

        new Thread(() -> {
            realPlayId = HCNetSDK.getInstance().NET_DVR_RealPlay_V40(mIpCam.getLoginId(), previewInfo, HIKPlayer.this);
            if (realPlayId < 0L) {
                int error = HCNetSDK.getInstance().NET_DVR_GetLastError();
                Log.v(TAG, "Last error " + error);
                if (error == NET_DVR_CHAN_NOTSUPPORT) {
                    mIpCam.toggleStreamType();
                    openStream();
                } else {
                    playing = false;
                    INT_PTR int_ptr = new INT_PTR();
                    int_ptr.iValue = error;
                    showError("NET_DVR_RealPlay is failed!Err: " + HCNetSDK.getInstance().NET_DVR_GetErrorMsg(int_ptr) + " channel : " + mIpCam.getChannel());
                }
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
        Log.v(TAG, "configuring channel :" + mIpCam.getChannel());
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
            Log.v("TAGGO", "before displayCB " + hashCode() + " channel is : " + getIpCam().getChannel());
            Log.v("TAGGO", "before displayCB loading " + mLoadingDots.hashCode() + " channel is : " + getIpCam().getChannel());
            Player.getInstance().setDisplayCB(m_iPort, new PlayerCallBack.PlayerDisplayCB() {
                @Override
                public void onDisplay(int i, byte[] bytes, int i1, int i2, int i3, int i4, int i5, int i6) {
                    if (lock == 0) {
                        mRunnable = () -> {

                            Log.v("TAGGO", "onDisplay " + HIKPlayer.this.hashCode() + " channel is : " + mIpCam.getChannel());
                            Log.v("TAGGO", "onDisplay  loading " + mLoadingDots.hashCode() + " channel is : " + mIpCam.getChannel());
                            mLoadingDots.setVisibility(View.GONE);
                            lock = 1;

                        };
                        mHandler.post(mRunnable);
                    }
                }
            });
            isConfigured = true;
        }

    }

    @Override
    public void play(byte[] pDataBuffer, int iDataSize) {
        if (!Player.getInstance().inputData(m_iPort, pDataBuffer, iDataSize)) {
//            Log.e(TAG, "inputData failed with: " + Player.getInstance().getLastError(m_iPort));
        }
    }


    // THE END
    @Override
    public void unConfigurePlay() {
        if (!isConfigured) {
            return;
        }
        if (!Player.getInstance().setDisplayCB(m_iPort, null)) {
            Log.e(TAG, "setDisplayCB is failed!" + m_iPort);
        }

        mHandler.removeCallbacks(mRunnable);
        mRunnable = null;
        Log.v(TAG, "unConfiguring channel :" + mIpCam.getChannel());
        isConfigured = false;
        if (!Player.getInstance().stopSound()) {
            Log.e(TAG, "stop sound failed! channel : " + mIpCam.getChannel());
        }
//         player stop play
        if (!Player.getInstance().stop(m_iPort)) {
            showError("stop is failed!");
        }


    }

    @Override
    public void detachView() {
        Log.v("TAGGO", "detached : " + hashCode());
        Log.v(TAG, "detachingViews channel :" + mIpCam.getChannel());
        lock = 0;
        Log.v("TAGGO", "\n=========================================\n");
//        mRunnable = () -> {

//            mHandler.removeCallbacks(mRunnable);
//        };
//        mHandler.post(mRunnable);

        mSurfaceView = null;
        errorTextView = null;
        mLoadingDots = null;
        addBtn = null;
        mHikCamView = null;
        mIpCam = null;
        m_iPort = -1;

//        name.setVisibility(View.GONE);
//        name.setText("");
    }

    @Override
    public void stopStream() {

        if (realPlayId < 0L) {
            return;
        }
        // net sdk stop preview
        if (!HCNetSDK.getInstance().NET_DVR_StopRealPlay(realPlayId)) {
            showError("StopRealPlay is failed!Err:" + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return;
        }
        Log.v(TAG, "stop real play channel : " + mIpCam.getChannel());


        if (!Player.getInstance().closeStream(m_iPort)) {
            showError("closeStream is failed!");
            return;
        }
        Log.v(TAG, "close stream channel : " + mIpCam.getChannel());
        if (!Player.getInstance().freePort(m_iPort)) {
            showError("freePort is failed!" + m_iPort);
            return;
        }


        Log.v("TAGGO", "port freed : " + m_iPort + " == " + hashCode());

        Log.v(TAG, "free port channel : " + mIpCam.getChannel());


        m_iPort = -1;
        realPlayId = -1;
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
            String date = sDateFormat.format(new Date());
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
            String date = sDateFormat.format(new Date());
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

    public void setIpCam(IpCam ipCam) {
        this.mIpCam = ipCam;
    }

    public IpCam getIpCam() {
        return mIpCam;
    }

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onResume(owner);
//        startLiveView();
    }

    @Override
    public void onPause(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onPause(owner);
//        stopLiveView();
    }
}