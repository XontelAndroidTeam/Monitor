package com.xontel.surveillancecameras.hikvision;

import android.content.Context;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.activities.HomeActivity;
import com.xontel.surveillancecameras.data.db.model.IpCam;

import org.videolan.libvlc.util.LoadingDots;

public class HikCamView extends FrameLayout implements SurfaceHolder.Callback {
    public static final String TAG = HikCamView.class.getSimpleName();
    private IpCam ipCam;
    private HIKPlayer mHIKPlayer ;
    private Context context;
    private TextView errorTextView;
    private LoadingDots loadingDots;
    private SurfaceView surfaceView ;
    private HikClickViews hikClickViews;
    private LiveData<Boolean> running;
    private LifecycleOwner mLifecycleOwner;
    public HikCamView(@NonNull Context context/*, LiveData<Boolean> running, LifecycleOwner lifecycleOwner,  IpCam ipCam,HikClickViews hikCamView*/) {
        super(context);
        this.context = context;
//        this.hikClickViews = hikCamView;
//        this.ipCam = ipCam;
//        this.running = running;
//        this.mLifecycleOwner = lifecycleOwner;
        init();
    }


    private void init() {
        inflate(context, R.layout.item_hik_cam, this);
        bind();
//        running.observe(mLifecycleOwner, resumed -> {
//            if(resumed!=null) {
//                if (resumed == true) {
//                    doOnResume();
//                } else {
//                    doOnPause();
//                }
//            }
//        });
    }

    private void doOnPause() {
        mHIKPlayer.stopLiveView();
    }

    private void doOnResume() {
        mHIKPlayer.attachView(surfaceView);
        mHIKPlayer.startLiveView();

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.v(TAG, "onAttachedToWindow");
    }

    @Override
    protected void onDetachedFromWindow() {

        super.onDetachedFromWindow();
        Log.v(TAG, "onDetachedFromWindow");
       if(mHIKPlayer != null){
           mHIKPlayer.stopLiveView();
       }
    }

    private void bind() {
        surfaceView =  findViewById(R.id.hik_layout) ;
        surfaceView.getHolder().addCallback(this);
        errorTextView =  findViewById(R.id.error_stream) ;
        loadingDots =  findViewById(R.id.loading_dots) ;
//        mHIKPlayer = new HIKPlayer(ipCam.getChannel(), ipCam.getLoginId(), context);
//        surfaceView.setOnClickListener(view -> hikClickViews.onHikClick(ipCam));
//        mHIKPlayer.isLoading.observe((HomeActivity)context, aBoolean -> {
//
//            if (aBoolean){loadingDots.setVisibility(VISIBLE);}
//            else{loadingDots.setVisibility(GONE);}
//        });
//
//        mHIKPlayer.isError.observe((HomeActivity) context, aBoolean -> {
//            if (aBoolean){errorTextView.setVisibility(VISIBLE);}
//            else{errorTextView.setVisibility(GONE);}
//        });
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
//        Log.v(TAG, "surfaceCreated "+ ipCam.getChannel());
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
//        Log.v(TAG, "surfaceChanged "+ ipCam.getChannel());
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
//        Log.v(TAG, "surfaceDestroyed"+ ipCam.getChannel());
    }

    public interface HikClickViews{
        void onHikClick(IpCam ipCam);
    }
}

