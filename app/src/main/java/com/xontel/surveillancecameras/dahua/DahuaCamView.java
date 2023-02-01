package com.xontel.surveillancecameras.dahua;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.data.db.model.IpCam;

public class DahuaCamView extends FrameLayout {
    private IpCam ipCam;
    private DahuaSinglePlayer dahuaSinglePlayer ;
    private Context context;
    private SurfaceView surfaceView ;

    public DahuaCamView(@NonNull Context context,IpCam ipCam) {
        super(context);
        this.context = context;
        this.ipCam = ipCam;
        init();
    }

    public DahuaCamView(@NonNull Context context, @Nullable AttributeSet attrs,IpCam ipCam) {
        super(context, attrs);
        this.context = context;
        this.ipCam = ipCam;
        init();
    }

    public DahuaCamView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr,IpCam ipCam) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.ipCam = ipCam;
        init();
    }

    public DahuaCamView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes,IpCam ipCam) {
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
        dahuaSinglePlayer = new DahuaSinglePlayer(ipCam.getChannel(), ipCam.getLoginId(), 0, context);
        dahuaSinglePlayer.initView(surfaceView);
    }

}
