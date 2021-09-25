package com.xontel.surveillancecameras.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.adapters.CamsAdapter;
import com.xontel.surveillancecameras.adapters.GridAdapter;
import com.xontel.surveillancecameras.adapters.PagerAdapter;
import com.xontel.surveillancecameras.base.BaseActivity;
import com.xontel.surveillancecameras.databinding.ActivityMainBinding;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.fragments.CameraFragment;
import com.xontel.surveillancecameras.fragments.GridFragment;
import com.xontel.surveillancecameras.presenters.MainMvpPresenter;
import com.xontel.surveillancecameras.presenters.MainMvpView;
import com.xontel.surveillancecameras.utils.CommonUtils;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MainActivity extends BaseActivity implements MainMvpView /*, CamsAdapter.Callback*/ {
    private PagerAdapter pagerAdapter;
    private ActivityMainBinding binding;
    private int gridCount;
    private List<IpCam> cams = new ArrayList<>();
    private List<MediaPlayer> mediaPlayers = new ArrayList<>();
    @Inject
    MainMvpPresenter<MainMvpView> mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        getActivityComponent().inject(this);
        mPresenter.onAttach(this);
        initUI();
        mPresenter.getAllCameras();
    }

    @Override
    protected void onDestroy() {
        try {
            releaseMediaPlayers();
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onDestroy();
        mPresenter.onDetach();

    }

    private void releaseMediaPlayers() {
        for(int i = 0 ;  i< cams.size() ; i++){
           MediaPlayer mediaPlayer =  cams.get(i).getMediaPlayer();
           if(mediaPlayer != null) {
               mediaPlayer.stop();
               mediaPlayer.detachViews();
               mediaPlayer.release();
           }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public List<IpCam> getCams() {
        return cams;
    }

    @Override
    protected void setUp() {

    }

    public List<MediaPlayer> getMediaPlayers() {
        return mediaPlayers;
    }

    private void initUI() {
        gridCount = getSharedPreferences(CommonUtils.SHARED_PREFERENCES_FILE, MODE_PRIVATE).getInt(CommonUtils.KEY_GRID_COUNT, GridFragment.DEFAULT_GRID_COUNT);
        binding.tvGridCount.setText(String.valueOf(gridCount));
        binding.ivAddCam.setOnClickListener(v -> {
            addNewCam();

        });
        binding.tvSlideShow.setOnClickListener(v -> {
            Intent intent = new Intent(this, CamerasActivity.class);
            intent.putParcelableArrayListExtra(CamerasActivity.KEY_CAMERAS, (ArrayList) cams);
            intent.putExtra(CamerasActivity.KEY_SLIDE_SHOW, true);
            startActivity(intent);
        });
        binding.tvGridCount.setOnClickListener(v -> {
            gridCount = (int) Math.pow(((((int) Math.sqrt(gridCount)) % 4) + 1), 2);
            Log.e("TAG", "gridCount: " + gridCount);
            binding.tvGridCount.setText(String.valueOf(gridCount));
            getSharedPreferences(CommonUtils.SHARED_PREFERENCES_FILE, MODE_PRIVATE).edit().putInt(CommonUtils.KEY_GRID_COUNT, gridCount).apply();
            updateViewPager();

        });
        setupCamerasPager();
//        setupCamsGrid();
    }

    public void addNewCam() {
        if (cams.size() < 16) {
            startActivity(new Intent(this, AddCamActivity.class));
        } else {
            showMessage(R.string.cameras_limit);
        }
    }

    private void setupCamsGrid() {
//        gridAdapter = new GridAdapter(this, new ArrayList<>());
//        binding.simpleGridView.setAdapter(gridAdapter);
//        populateCamsList();
    }


//    private void setupCamsList() {
//        camsAdapter = new CamsAdapter(new ArrayList<>(), this, this);
//        binding.rvCams.setAdapter(camsAdapter);
//        binding.rvCams.setLayoutManager(new GridLayoutManager(this, 4));
////        populateCamsList();

    //    }
    private void setupCamerasPager() {
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        GridFragment gridFragment = new GridFragment();
        pagerAdapter.addFragment(gridFragment);
        binding.vpSlider.setAdapter(pagerAdapter);
        binding.vpSlider.setOffscreenPageLimit(2);
        binding.dotsIndicator.setViewPager(binding.vpSlider);
    }

    private void updateViewPager() {
        if (cams.size() > 0) {
            pagerAdapter.getFragmentList().clear();
            for (int i = 0; i < cams.size(); i += gridCount) {
                List<IpCam> subCams = cams.subList(i, Math.min(cams.size(), i + gridCount));
                Log.e("subCams", subCams.size() + "");
                GridFragment gridFragment = GridFragment.newInstance(subCams);
                pagerAdapter.addFragment(gridFragment);

            }
            pagerAdapter.notifyDataSetChanged();
//            binding.vpSlider.setAdapter(pagerAdapter);
//            binding.vpSlider.setOffscreenPageLimit(1);

            binding.dotsIndicator.refreshDots();
        } else {
            setupCamerasPager();
        }


    }

//    @Override
//    public void onCamClicked(int position) {
//

//    }

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
        Log.e("cams number", response.size() + "");
        cams.clear();
        cams.addAll(response);
        createMediaPlayers();
        updateViewPager();

    }

    private void createMediaPlayers() {
        for (int i = 0; i < cams.size(); i++) {
            IpCam ipCam = cams.get(i);
            ipCam.setMediaPlayer(getMediaPlayerForCam(ipCam.getUrl()));
        }
    }

    private MediaPlayer getMediaPlayerForCam(String url) {
        LibVLC libVLC;
        MediaPlayer mediaPlayer;

        // libvlc initialization
        List<String> args = new ArrayList<String>();
        args.add("-vvv");
//        args.add("--vout=android-display");
        args.add("--network-caching=33");
        args.add("--file-caching=33");
        args.add("--live-caching=33");
        args.add("--clock-synchro=0");
        args.add("--clock-jitter=0");
        args.add("--h264-fps=60");
        args.add("--avcodec-fast");
        args.add("--avcodec-threads=1");
        args.add("--no-audio");

        libVLC = new LibVLC(this, (ArrayList<String>) args);

        // media player setup
        mediaPlayer = new MediaPlayer(libVLC);
        final Media media = new Media(libVLC, Uri.parse(url));
        media.setHWDecoderEnabled(true,true);
        media.addOption(":fullscreen");
        media.addOption(":rtsp-tcp");
        mediaPlayer.setMedia(media);

        media.release();
//            mediaPlayer.attachViews(vlcVideoLayout, null, ENABLE_SUBTITLES, USE_TEXTURE_VIEW);
//            mediaPlayer.play();

        return mediaPlayer;

    }

    @Override
    public void onCreatingCam() {

    }
}