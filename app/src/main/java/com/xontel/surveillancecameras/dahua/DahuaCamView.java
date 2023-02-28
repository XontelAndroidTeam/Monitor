package com.xontel.surveillancecameras.dahua;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.activities.HomeActivity;
import com.xontel.surveillancecameras.data.db.model.IpCam;

import org.videolan.libvlc.util.LoadingDots;

public class DahuaCamView extends FrameLayout {
    public static final int DEFAULT_HIKVISION_PORT_NUMBER = 8000;
    public static final int DEFAULT_Dahua_PORT_NUMBER = 37777;
    private IpCam ipCam;
    private DahuaPlayer mDahuaPlayer;
    private Context context;
    private TextView errorTextView;
    private LoadingDots loadingDots;
    private SurfaceView surfaceView ;
    private DahuaClickViews dahuaClickViews;

    public DahuaCamView(@NonNull Context context,IpCam ipCam,DahuaClickViews dahuaClickViews) {
        super(context);
        this.context = context;
        this.dahuaClickViews = dahuaClickViews;
        this.ipCam = ipCam;
        init();
    }

    public DahuaCamView(@NonNull Context context, @Nullable AttributeSet attrs,IpCam ipCam,DahuaClickViews dahuaClickViews) {
        super(context, attrs);
        this.context = context;
        this.ipCam = ipCam;
        this.dahuaClickViews = dahuaClickViews;
        init();
    }

    public DahuaCamView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr,IpCam ipCam,DahuaClickViews dahuaClickViews) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.ipCam = ipCam;
        this.dahuaClickViews = dahuaClickViews;
        init();
    }

    public DahuaCamView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes,IpCam ipCam,DahuaClickViews dahuaClickViews) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        this.ipCam = ipCam;
        this.dahuaClickViews = dahuaClickViews;
        init();
    }
    private void init() {
        inflate(context, R.layout.item_player_view, this);
        bind();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private void bind() {
        surfaceView =  findViewById(R.id.player_surface) ;
        errorTextView =  findViewById(R.id.error_stream) ;
        loadingDots =  findViewById(R.id.loading_dots) ;
        mDahuaPlayer = new DahuaPlayer(context);
//        dahuaSinglePlayer.initView(surfaceView);
        surfaceView.setOnClickListener(view -> {
            dahuaClickViews.onDahuaClick(ipCam);
        });
        mDahuaPlayer.isLoading.observe((HomeActivity)context, aBoolean -> {
            if (aBoolean){loadingDots.setVisibility(VISIBLE);}
            else{loadingDots.setVisibility(GONE);}
        });

        mDahuaPlayer.isError.observe((HomeActivity) context, aBoolean -> {
            if (aBoolean){errorTextView.setVisibility(VISIBLE);}
            else{errorTextView.setVisibility(GONE);}
        });
    }


    public interface DahuaClickViews{
        void onDahuaClick(IpCam ipCam);
      //  void onError(Boolean value);
       // void onLoading(Boolean value);
    }

}
