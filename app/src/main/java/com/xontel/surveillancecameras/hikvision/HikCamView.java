package com.xontel.surveillancecameras.hikvision;

import android.content.Context;
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
import androidx.cardview.widget.CardView;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.activities.HomeActivity;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.utils.CommonUtils;

import org.videolan.libvlc.util.LoadingDots;

public class HikCamView extends CardView {
    public static final String TAG = HikCamView.class.getSimpleName();
    private LoadingDots mLoadingDots;
    private TextView errorTextView;

    private TextView name;
    private ImageView addBtn;
    private ViewStub surfaceStub;
    private SurfaceView mSurfaceView;
    private Context context;

    private SurfaceHolder.Callback mCallback;

    public HikCamView(@NonNull Context context) {
        super(context);
        this.context = context;
        setCardBackgroundColor(context.getColor(R.color.grey_color));
        setRadius(10);
        init();
    }

    private void init() {
       inflate(context, R.layout.item_hik_cam, this);

        mLoadingDots = findViewById(R.id.loading_dots);
        name = findViewById(R.id.tv_cam_name);
        errorTextView = findViewById(R.id.error_stream);
        addBtn = findViewById(R.id.iv_add);
        surfaceStub = findViewById(R.id.stub);





    }


    private void createNewSurface() {
        mSurfaceView = new SurfaceView(context);
        mSurfaceView.getHolder().addCallback(mCallback);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        );
       addView(mSurfaceView, layoutParams);
    }


    public void showLoading(boolean show){
        mLoadingDots.setVisibility(show ? VISIBLE : GONE);
    }

    public SurfaceView getSurfaceView() {
        return mSurfaceView;
    }

    public void setCamName(String camName){
        name.setVisibility(VISIBLE);
        name.setText(camName);
    }


    public void resetView(){
        mLoadingDots.setVisibility(View.GONE);
        addBtn.setVisibility(View.VISIBLE);
        errorTextView.setVisibility(View.GONE);
        name.setVisibility(GONE);
        name.setText("");
        if(mSurfaceView !=null) {
            mSurfaceView.getHolder().removeCallback(mCallback);
            removeView(mSurfaceView);
        }
    }

    public void showError(String logMessage) {
        errorTextView.setVisibility(VISIBLE);
        errorTextView.setText(logMessage);
    }

    public void onAttachToPlayer(SurfaceHolder.Callback callback) {
        mLoadingDots.setVisibility(View.VISIBLE);
//        name.setVisibility(View.VISIBLE);
//        name.setText(mIpCam.getName());
        addBtn.setVisibility(View.GONE);
        this.mCallback = callback;
        if(surfaceStub != null){
            mSurfaceView = (SurfaceView) surfaceStub.inflate();
            mSurfaceView.getHolder().addCallback(mCallback);
        }else{
            createNewSurface();
        }
    }


    public interface HikClickViews{
        void onHikClick(IpCam ipCam);
    }
}

