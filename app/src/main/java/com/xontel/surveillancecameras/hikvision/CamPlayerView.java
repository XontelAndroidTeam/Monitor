package com.xontel.surveillancecameras.hikvision;

import android.content.Context;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.data.db.model.IpCam;

import org.videolan.libvlc.util.LoadingDots;

public class CamPlayerView extends CardView implements SurfaceHolder.Callback, View.OnClickListener {
    public static final String TAG = CamPlayerView.class.getSimpleName();
    private LoadingDots mLoadingDots;
    private TextView errorTextView;

    private TextView name;
    private ImageView addBtn;
    private ViewStub surfaceStub;
    private SurfaceView mSurfaceView;

    private Context context;

    private SurfaceCallback mSurfaceCallback;
    private boolean isSurfaceCreated;

    private ClickListener mClickListener;

    public CamPlayerView(@NonNull Context context, ClickListener clickListener) {
        super(context);
        this.context = context;
        this.mClickListener = clickListener;
        setCardBackgroundColor(context.getColor(R.color.grey_color));
        setRadius(10);
        init();
    }

    private void init() {
        inflate(context, R.layout.item_player_view, this);

        mLoadingDots = findViewById(R.id.loading_dots);
        name = findViewById(R.id.tv_cam_name);
        errorTextView = findViewById(R.id.error_stream);
        addBtn = findViewById(R.id.iv_add);
        surfaceStub = findViewById(R.id.stub);
        mSurfaceView = (SurfaceView) surfaceStub.inflate();
        mSurfaceView.getHolder().addCallback(this);
    }


    public void showLoading(boolean show) {
        mLoadingDots.setVisibility(show ? VISIBLE : GONE);
    }

    public SurfaceView getSurfaceView() {
        return mSurfaceView;
    }

    public void setCamName(String camName) {
        name.setVisibility(VISIBLE);
        name.setText(camName);
    }


    public void showError(String logMessage) {
        errorTextView.setVisibility(VISIBLE);
        errorTextView.setText(logMessage);
    }

    public void onAttachToPlayer(SurfaceCallback surfaceCallback) {
        this.mSurfaceCallback = surfaceCallback;
        mLoadingDots.setVisibility(View.VISIBLE);
//        name.setVisibility(View.VISIBLE);
//        name.setText(mIpCam.getName());
        addBtn.setVisibility(View.GONE);
    }

    public void onDetachedFromPlayer() {
        this.mSurfaceCallback = null;
        mLoadingDots.setVisibility(View.GONE);
        addBtn.setVisibility(View.VISIBLE);
        errorTextView.setVisibility(View.GONE);
        errorTextView.setText("");
        name.setVisibility(GONE);
        name.setText("");
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        Log.v("GridFragment", "surfaceCreated__");
        isSurfaceCreated = true;
        if (mSurfaceCallback != null)
            mSurfaceCallback.onSurfaceCreated();

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Log.v("GridFragment", "surfaceChanged__");
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        Log.v("GridFragment", "surfaceDestroyed__");
        isSurfaceCreated = false;
        if (mSurfaceCallback != null)
            mSurfaceCallback.onSurfaceDestroyed();
    }

    public boolean isSurfaceCreated() {
        return isSurfaceCreated;
    }

    @Override
    public void onClick(View view) {
        mClickListener.onViewClicked(mSurfaceCallback != null);
    }


    public interface HikClickViews {
        void onHikClick(IpCam ipCam);
    }

    public interface SurfaceCallback {
        void onSurfaceCreated();

        void onSurfaceDestroyed();
    }

    public interface ClickListener{
        void onViewClicked(boolean isAttachedToPlayer);
    }
}

