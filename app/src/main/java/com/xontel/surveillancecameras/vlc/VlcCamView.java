package com.xontel.surveillancecameras.vlc;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.utils.CamDeviceType;

import org.videolan.libvlc.util.VLCVideoLayout;

public class VlcCamView extends ConstraintLayout {
    private IpCam ipCam;
    private VlcSinglePlayer vlcSinglePlayer ;
    private Context context;

    public VlcCamView(@NonNull Context context, IpCam ipCam) {
        super(context);
        this.context = context;
        this.ipCam = ipCam;
        init();
    }

    public VlcCamView(@NonNull Context context, IpCam ipCam, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.ipCam = ipCam;
        init();
    }

    public VlcCamView(@NonNull Context context, IpCam ipCam, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.ipCam = ipCam;
        init();
    }

    public VlcCamView(@NonNull Context context, IpCam ipCam, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        this.ipCam = ipCam;
        init();
    }


    private void init() {
        inflate(context, R.layout.item_vlc_cam, this);
        bind();
    }


    public VlcSinglePlayer getVlcSinglePlayer() {
        return vlcSinglePlayer;
    }

    public void bind() {
        VLCVideoLayout vlcVideoLayout = findViewById(R.id.vlc_layout);
        vlcSinglePlayer = new VlcSinglePlayer(context);
        vlcSinglePlayer.initVlcPlayer(ipCam.getUrlOrIpAddress(), vlcVideoLayout);
    }


}
