package com.xontel.surveillancecameras.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
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
//
//import org.videolan.libvlc.LibVLC;
//import org.videolan.libvlc.Media;
//import org.videolan.libvlc.MediaPlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MainActivity extends BaseActivity implements MainMvpView /*, CamsAdapter.Callback*/ {
    private static final String TAG = MainActivity.class.getSimpleName();
    private PagerAdapter pagerAdapter;
    private ActivityMainBinding binding;
    private int gridCount;
    private List<IpCam> cams = new ArrayList<>();
//    private List<MediaPlayer> mediaPlayers = new ArrayList<>();
    @Inject
    MainMvpPresenter<MainMvpView> mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate: " );
//        Log.e(TAG, "filesDir: "+getFilesDir().getAbsolutePath()+"\n"+"externalFilesDir:"+getExternalFilesDir(null).getAbsolutePath());
//        File[] dirs = ContextCompat.getExternalFilesDirs(this, null);
//        for(File dir : dirs) {
//            Log.e(TAG, "parts: " +dir.getAbsolutePath()+" type: "+dir.getT);
//        }
        Log.e(TAG, "onCreate: " );

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        getActivityComponent().inject(this);
        mPresenter.onAttach(this);
        setSupportActionBar(binding.toolbar);
        initUI();
        mPresenter.getAllCameras();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: " );
        mPresenter.onDetach();

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause: " );
        binding.spGridCount.dismiss();
    }

    @Override
    protected void onResume() {
        Log.e(TAG, "onResume: " );
        updateViewPager();
        super.onResume();
    }

    public List<IpCam> getCams() {
        return cams;
    }

    @Override
    protected void setUp() {

    }

    @Override
    protected void onStop() {
        Log.e(TAG, "onStop: " );
        super.onStop();
    }

    @Override
    protected void onStart() {
        Log.e(TAG, "onStart: " );
        super.onStart();
    }

    private void initUI() {
        gridCount = getSharedPreferences(CommonUtils.SHARED_PREFERENCES_FILE, MODE_PRIVATE).getInt(CommonUtils.KEY_GRID_COUNT, GridFragment.DEFAULT_GRID_COUNT);
        binding.spGridCount.setText(String.valueOf(gridCount));
        binding.ivAddCam.setOnClickListener(v -> {
            addNewCam();

        });
        binding.ivSettings.setOnClickListener(v -> {
        showSettings();
        });
        binding.tvSlideShow.setOnClickListener(v -> {
            if(cams.size() > 0 ) {
                Intent intent = new Intent(MainActivity.this, CamerasActivity.class);
                intent.putParcelableArrayListExtra(CamerasActivity.KEY_CAMERAS, (ArrayList) cams);
                intent.putExtra(CamerasActivity.KEY_SLIDE_SHOW, true);
                startActivity(intent);
            }else{
                showMessage(R.string.no_cameras_added_yet);
            }
        });
        binding.spGridCount.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<Object>() {
            @Override
            public void onItemSelected(int i, @Nullable Object o, int i1, Object t1) {
                if(i != i1) {
                    gridCount = Integer.parseInt((String) t1);/*(int) Math.pow(((((int) Math.sqrt(gridCount)) % 4) + 1), 2);*/
                    Log.e("TAG", "gridCount: " + gridCount);
                    binding.spGridCount.setText(String.valueOf(gridCount));
                    getSharedPreferences(CommonUtils.SHARED_PREFERENCES_FILE, MODE_PRIVATE).edit().putInt(CommonUtils.KEY_GRID_COUNT, gridCount).apply();
                    updateViewPager();
                }
            }
        });
        setupCamerasPager();
//        setupCamsGrid();
    }



    private void showSettings() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    public void addNewCam() {
        if (cams.size() < 24) {
            startActivity(new Intent(MainActivity.this, AddCamActivity.class));
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
        binding.vpSlider.setOffscreenPageLimit(0);
        binding.dotsIndicator.setViewPager(binding.vpSlider);
    }

    private void updateViewPager() {

        if (cams.size() > 0) {
            pagerAdapter.getFragmentList().clear();
            for (int i = 0; i < cams.size(); i += gridCount) {
                List<IpCam> subCams = cams.subList(i, Math.min(cams.size(), i + gridCount));
                Log.e("subCams", subCams.size() + "");
                GridFragment gridFragment = GridFragment.newInstance(subCams, cams.size());
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
        updateViewPager();

    }




}