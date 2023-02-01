package com.xontel.surveillancecameras.hikvision;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.vlc.VlcSinglePlayer;

import org.videolan.libvlc.util.VLCVideoLayout;

public class HikCamView extends ConstraintLayout {
    private IpCam ipCam;
    private HIKSinglePlayer hikSinglePlayer ;
    private Context context;
    private SurfaceView surfaceView ;


    public HikCamView(@NonNull Context context, IpCam ipCam) {
        super(context);
        this.context = context;
        this.ipCam = ipCam;
        init();
    }

    public HikCamView(@NonNull Context context, IpCam ipCam, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.ipCam = ipCam;
        init();
    }

    public HikCamView(@NonNull Context context, IpCam ipCam, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.ipCam = ipCam;
        init();
    }

    public HikCamView(@NonNull Context context, IpCam ipCam, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
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
        hikSinglePlayer = new HIKSinglePlayer(ipCam.getChannel(), ipCam.getLoginId(), 0, context);
        hikSinglePlayer.initView(surfaceView);
    }
}
