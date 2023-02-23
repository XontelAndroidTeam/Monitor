package com.xontel.surveillancecameras.hikvision;

import android.content.Context;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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
    private Context context;
    public HikCamView(@NonNull Context context) {
        super(context);
        this.context = context;
        setCardBackgroundColor(context.getColor(R.color.grey_color));
        setRadius(10);
        init();
    }


    private void init() {
        View view = inflate(context, R.layout.item_hik_cam, this);
    }








    public interface HikClickViews{
        void onHikClick(IpCam ipCam);
    }
}

