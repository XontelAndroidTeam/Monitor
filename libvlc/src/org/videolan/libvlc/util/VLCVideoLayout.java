package org.videolan.libvlc.util;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import org.videolan.R;
import org.videolan.libvlc.MediaPlayer;

/**
 * VLC-ready layout which includes 2 {@link SurfaceView} (video+subtitles) and 1 {@link TextureView}
 * All these surfaces are stubs, only the relevant one(s) will be inflated
 * Use it preferably with {@link MediaPlayer}.attachViews()
 */
public class VLCVideoLayout extends FrameLayout {
    private boolean volumeControlEnabled ;
    private int errorTextSize ;
    public VLCVideoLayout(@NonNull Context context) {
        super(context);
        setupLayout(context, null);
    }

    public VLCVideoLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setupLayout(context, attrs);
    }

    public VLCVideoLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupLayout(context,attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public VLCVideoLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setupLayout(context,attrs);
    }

    private void setupLayout(@NonNull Context context, AttributeSet attributeSet) {
        View view = inflate(context, R.layout.vlc_video_layout, this);
        if(attributeSet != null) {
            TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.vlc_attributes);
            volumeControlEnabled = a.getBoolean(R.styleable.vlc_attributes_vlc_enable_volume_control, false);
            errorTextSize = a.getDimensionPixelSize(R.styleable.vlc_attributes_vlc_error_text_size, context.getResources().getDimensionPixelSize(R.dimen.vlc_error_text_size_default));
            ((TextView) view.findViewById(R.id.tv_error)).setTextSize(errorTextSize);
            view.findViewById(R.id.volume_controller).setVisibility(volumeControlEnabled ? VISIBLE : GONE);
        }
    }



    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        setBackgroundResource(R.color.black);
        final ViewGroup.LayoutParams lp = getLayoutParams();
        lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        setLayoutParams(lp);
    }
}
