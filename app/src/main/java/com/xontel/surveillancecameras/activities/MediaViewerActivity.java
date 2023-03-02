package com.xontel.surveillancecameras.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.adapters.PagerAdapter;
import com.xontel.surveillancecameras.base.BaseActivity;
import com.xontel.surveillancecameras.databinding.ActivityMediaViewerActivityBinding;
import com.xontel.surveillancecameras.databinding.DialogMediaDetailsBinding;
import com.xontel.surveillancecameras.dialogs.MediaDetailsDialog;
import com.xontel.surveillancecameras.utils.MediaData;

import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;

public class MediaViewerActivity extends BaseActivity {
    public static final String TAG  = MediaViewerActivity.class.getSimpleName();
    private ActivityMediaViewerActivityBinding binding;
    public static final String KEY_MEDIA_DATA = "media_data";

    private MediaData mMediaData;
    private ExoPlayer simpleExoPlayer;
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
        mMediaData = getIntent().getParcelableExtra(KEY_MEDIA_DATA);
        binding.ivDetails.setOnClickListener(v -> {
            MediaDetailsDialog mediaDetailsDialog = new MediaDetailsDialog(this, new File( mMediaData.getMediaPath() == null  ? mMediaData.getMediaPath() : mMediaData.getMediaPath()));
            mediaDetailsDialog.show();
        });

        if(mMediaData.getMediaType().equals(Environment.DIRECTORY_PICTURES)){
            showImage();
        }else{
            showVideo();
        }

    }

    private void showImage(){
        Uri uri = mMediaData.getMediaUri();
        Log.v(TAG, uri.toString());
        Glide.with(this)
                .asBitmap()
                .load(mMediaData.getMediaUri())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        binding.ivImagePreview.setImageBitmap(resource);
                    }
                });
        binding.ivImagePreview.setZoom(1f);
    }

    private void showVideo(){
        simpleExoPlayer = ExoPlayerFactory.newSimpleInstance(this);
        binding.playView.setPlayer(simpleExoPlayer);
        binding.playView.setKeepScreenOn(true);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, "cameras");
        MediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.fromFile(new File(mMediaData.getMediaPath())));
        simpleExoPlayer.prepare(mediaSource);
        simpleExoPlayer.setPlayWhenReady(true);
        binding.playView.setVisibility(View.VISIBLE);
    }




    @Override
    protected void setUp() {
        
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (simpleExoPlayer != null){simpleExoPlayer.release();}
    }
}