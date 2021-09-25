package com.xontel.surveillancecameras.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.adapters.CamsAdapter;
import com.xontel.surveillancecameras.adapters.GridAdapter;
import com.xontel.surveillancecameras.base.BaseActivity;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.databinding.ActivityTestBinding;
import com.xontel.surveillancecameras.presenters.MainMvpPresenter;
import com.xontel.surveillancecameras.presenters.MainMvpView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class TestActivity extends BaseActivity implements MainMvpView {

    private ActivityTestBinding binding;
    private List<IpCam> cams = new ArrayList<>();
    private CamsAdapter gridAdapter;
    private VideoView videoView;
    private int gridCount = 4;
    @Inject
    MainMvpPresenter<MainMvpView> mPresenter;
    private SimpleExoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_test);
        getActivityComponent().inject(this);
        mPresenter.onAttach(this);
        videoView = findViewById(R.id.videoView);
        initUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDetach();
    }

    @Override
    protected void setUp() {

    }

    private void initUI() {
        setupPlayer();

    }

    private void setupPlayer() {

// Network video.
        String netVideoUrl = "rtsp://admin:Admin123@78.89.170.173:554/Streaming/Channels/102";
// Specify the URL of the video file.
        videoView.setVideoURI(Uri.parse(netVideoUrl));
// Set the video controller.
        videoView.setMediaController(new MediaController(this));
// Playback callback is complete.
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

            }
        });

        if(!videoView.isPlaying()){ // Play.
            videoView.start();
        }
    }

    @Override
    public void onCreatingCam() {

    }

    @Override
    public void onInsertingCamera() {

    }

    @Override
    public void onUpdatingCamera() {

    }

    @Override
    public void onDeletingCamera() {

    }

    @Override
    public void onGettingCamera(IpCam response) {

    }

    @Override
    public void onGettingAllCameras(List<IpCam> response) {
        cams.clear();
        cams.addAll(response);
        gridAdapter.notifyDataSetChanged();
    }
}