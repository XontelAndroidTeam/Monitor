package com.xontel.surveillancecameras.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Bundle;

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
    private int gridCount  = 4;
    @Inject
    MainMvpPresenter<MainMvpView> mPresenter;
    private SimpleExoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_test);
        getActivityComponent().inject(this);
        mPresenter.onAttach(this);
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
        // Create a player instance.
        player = new SimpleExoPlayer.Builder(this).build();
        binding.player.setPlayer(player);

// Set the media item to be played.
        player.setMediaItem(MediaItem.fromUri("rtsp://admin:X0nP@ssw0rd_000@192.168.1.123/Streaming/Channels/102"));
// Prepare the player.
        player.prepare();
        player.play();
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