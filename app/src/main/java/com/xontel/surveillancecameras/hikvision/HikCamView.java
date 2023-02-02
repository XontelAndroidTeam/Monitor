package com.xontel.surveillancecameras.hikvision;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.activities.HomeActivity;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.vlc.VlcSinglePlayer;

import org.videolan.libvlc.util.LoadingDots;
import org.videolan.libvlc.util.VLCVideoLayout;

public class HikCamView extends FrameLayout {
    private IpCam ipCam;
    private HIKSinglePlayer hikSinglePlayer ;
    private Context context;
    private TextView errorTextView;
    private LoadingDots loadingDots;
    private SurfaceView surfaceView ;
    private HikClickViews hikClickViews;


    public HikCamView(@NonNull Context context, IpCam ipCam,HikClickViews hikCamView) {
        super(context);
        this.context = context;
        this.hikClickViews = hikCamView;
        this.ipCam = ipCam;
        init();
    }

    public HikCamView(@NonNull Context context, IpCam ipCam, @Nullable AttributeSet attrs,HikClickViews hikCamView) {
        super(context, attrs);
        this.context = context;
        this.hikClickViews = hikCamView;
        this.ipCam = ipCam;
        init();
    }

    public HikCamView(@NonNull Context context, IpCam ipCam, @Nullable AttributeSet attrs, int defStyleAttr,HikClickViews hikCamView) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.hikClickViews = hikCamView;
        this.ipCam = ipCam;
        init();
    }

    public HikCamView(@NonNull Context context, IpCam ipCam, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes,HikClickViews hikCamView) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        this.hikClickViews = hikCamView;
        this.ipCam = ipCam;
        init();
    }

    private void init() {
        inflate(context, R.layout.item_hik_cam, this);
        bind();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.v("TAGG", "attached");
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.v("TAGG", "detached");
    }

    private void bind() {
        surfaceView =  findViewById(R.id.hik_layout) ;
        errorTextView =  findViewById(R.id.error_stream) ;
        loadingDots =  findViewById(R.id.loading_dots) ;
        hikSinglePlayer = new HIKSinglePlayer(ipCam.getChannel(), ipCam.getLoginId(), 0, context);
        hikSinglePlayer.initView(surfaceView);

        surfaceView.setOnClickListener(view -> hikClickViews.onHikClick(ipCam));

        hikSinglePlayer.isLoading.observe((HomeActivity)context, aBoolean -> {
            if (aBoolean){loadingDots.setVisibility(VISIBLE);}
            else{loadingDots.setVisibility(GONE);}
        });

        hikSinglePlayer.isError.observe((HomeActivity) context, aBoolean -> {
            if (aBoolean){errorTextView.setVisibility(VISIBLE);}
            else{errorTextView.setVisibility(GONE);}
        });
    }

    public interface HikClickViews{
        void onHikClick(IpCam ipCam);
      //  void onError(Boolean value);
      //  void onLoading(Boolean value);
    }
}

