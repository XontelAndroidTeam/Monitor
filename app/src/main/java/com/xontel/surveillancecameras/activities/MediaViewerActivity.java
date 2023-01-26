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
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.base.BaseActivity;
import com.xontel.surveillancecameras.databinding.ActivityMediaViewerActivityBinding;
import com.xontel.surveillancecameras.databinding.DialogMediaDetailsBinding;
import com.xontel.surveillancecameras.dialogs.MediaDetailsDialog;

import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;

public class MediaViewerActivity extends BaseActivity {
    private ActivityMediaViewerActivityBinding binding;
    public static final String KEY_MEDIA_TYPE = "media_type";
    public static final String KEY_MEDIA_FILE_PATH = "media_path";
    public static final int MEDIA_VIDEO = 1;
    public static final int MEDIA_IMAGE = 0;
    private MediaPlayer mediaPlayer;
    Uri contentUri;
    Uri collection;
    String[] projection = new String[] {
            MediaStore.MediaColumns.RELATIVE_PATH,
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DURATION,
            MediaStore.Images.Media.SIZE};

    String[] projectionVideo = new String[] {
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE};
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
        getAllVideos();
        if(getIntent().hasExtra(KEY_MEDIA_TYPE)){
            if(getIntent().getIntExtra(KEY_MEDIA_TYPE, MEDIA_IMAGE) == MEDIA_IMAGE){
             //   showImage();
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


            binding.vlcLayout.setVisibility(View.VISIBLE);
            mediaPlayer = new MediaPlayer(this);
            mediaPlayer.attachViews(binding.vlcLayout);
            final Media media = new Media(mediaPlayer.getLibVLCInstance(), contentUri.toString());
            media.addCommonOptions();
            mediaPlayer.setMedia(media);
            media.release();
            mediaPlayer.play();


    }
    private void getAllVideos(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            collection = MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        } else {
            collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        }
        try(Cursor cursor = this.getContentResolver().query(
                collection,
                projectionVideo,
                null,
                null,
                null
        ))  {
            // Cache column indices.
            int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
            int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
            int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
            int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);
            int data = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);

            while (cursor.moveToNext()) {
                // Get values of columns for a given video.
                long id = cursor.getLong(idColumn);
                String name = cursor.getString(nameColumn);
                int duration = cursor.getInt(durationColumn);
                int size = cursor.getInt(sizeColumn);
                String dataPath = cursor.getString(data);
                 contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                Log.i("TATZ", "getAllVideos: "+contentUri);
                Log.i("TATZ", "getAllVideos: "+dataPath);
            }
            showVideo();
        }catch(Exception e){
            Log.e("TAG", "error: " + e.getMessage());
        }
    }


    @Override
    protected void setUp() {
        
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null){mediaPlayer.release();}
    }
}