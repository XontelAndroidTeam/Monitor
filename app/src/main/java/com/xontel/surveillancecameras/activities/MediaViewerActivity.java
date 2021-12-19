package com.xontel.surveillancecameras.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.base.BaseActivity;
import com.xontel.surveillancecameras.databinding.ActivityMediaViewerActivityBinding;
import com.xontel.surveillancecameras.databinding.DialogMediaDetailsBinding;
import com.xontel.surveillancecameras.dialogs.MediaDetailsDialog;

import java.io.File;

public class MediaViewerActivity extends BaseActivity {
    private ActivityMediaViewerActivityBinding binding;
    public static final String KEY_MEDIA_TYPE = "media_type";
    public static final String KEY_MEDIA_FILE_PATH = "media_path";
    public static final int MEDIA_VIDEO = 1;
    public static final int MEDIA_IMAGE = 0;
    private String mediaFilePath;
    private SimpleExoPlayer simpleExoPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_media_viewer_activity);
        binding.getRoot().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LOW_PROFILE |
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        intUI();
    }

    private void intUI() {
        mediaFilePath = getIntent().getStringExtra(KEY_MEDIA_FILE_PATH);
        binding.ivDetails.setOnClickListener(v -> {
            MediaDetailsDialog mediaDetailsDialog = new MediaDetailsDialog(this, new File(mediaFilePath));
            mediaDetailsDialog.show();
        });
        if(getIntent().hasExtra(KEY_MEDIA_TYPE)){
            if(getIntent().getIntExtra(KEY_MEDIA_TYPE, MEDIA_IMAGE) == MEDIA_IMAGE){
                showImage();
            }else{
                showVideo();
            }
        }

    }

    private void showImage(){
        Glide.with(this).asBitmap().load(mediaFilePath).into(new SimpleTarget<Bitmap>() {

            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    binding.ivImagePreview.setImageBitmap(resource);

            }
        });
        binding.ivImagePreview.setVisibility(View.VISIBLE);
        binding.ivImagePreview.setZoom(1f);
    }

    private void showVideo(){

        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this);
        binding.videoPlayer.setPlayer(simpleExoPlayer);
        binding.videoPlayer.setKeepScreenOn(true);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, "cameras");
        MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.fromFile(new File(mediaFilePath)));
        simpleExoPlayer.prepare(mediaSource);
        simpleExoPlayer.setPlayWhenReady(false);
        binding.videoPlayer.setVisibility(View.VISIBLE);
    }

    @Override
    protected void setUp() {
        
    }

    
}