package com.xontel.surveillancecameras.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.ViewModels.MainViewModel;
import com.xontel.surveillancecameras.adapters.PagerAdapter;
import com.xontel.surveillancecameras.base.BaseActivity;
import com.xontel.surveillancecameras.ViewModels.ViewModelProviderFactory;
import com.xontel.surveillancecameras.databinding.ActivityMainBinding;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.fragments.GridFragment;
import com.xontel.surveillancecameras.presenters.MainMvpPresenter;
import com.xontel.surveillancecameras.presenters.MainMvpView;
import com.xontel.surveillancecameras.utils.CommonUtils;
//
//import org.videolan.libvlc.LibVLC;
//import org.videolan.libvlc.Media;
//import org.videolan.libvlc.MediaPlayer;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String KEY_CURRENT_PAGE_INDEX = "current_page_index";
    public static final int DEFAULT_GRID_COUNT = 4;
    private PagerAdapter pagerAdapter;
    private ActivityMainBinding binding;
    private int currentPageIndex ;
    @Inject
    MainMvpPresenter<MainMvpView> mPresenter;

    @Inject
    ViewModelProviderFactory providerFactory ;

    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        getActivityComponent().inject(this);
        mainViewModel = new ViewModelProvider(this, providerFactory).get(MainViewModel.class);
        setSupportActionBar(binding.toolbar);
        mainViewModel.getAllCameras();
        initUI();
        if(savedInstanceState != null){
            currentPageIndex = savedInstanceState.getInt(KEY_CURRENT_PAGE_INDEX, 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onPause() {
        super.onPause();
        binding.spGridCount.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

//    public List<IpCam> getCams() {
//        return cams;
//    }

    @Override
    protected void setUp() {

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void initUI() {
        setupObservables();
        // get the persisted value in shared preferences
        mainViewModel.gridCount.postValue(getSharedPreferences(CommonUtils.SHARED_PREFERENCES_FILE, MODE_PRIVATE).getInt(CommonUtils.KEY_GRID_COUNT, DEFAULT_GRID_COUNT));
        binding.ivAddCam.setOnClickListener(v -> {
            addNewCam();
        });
        binding.ivSettings.setOnClickListener(v -> {
        showSettings();
        });
        binding.tvSlideShow.setOnClickListener(v -> {
            if(mainViewModel.ipCams.getValue().size() > 0 ) {
                Intent intent = new Intent(MainActivity.this, CamerasActivity.class);
                intent.putParcelableArrayListExtra(CamerasActivity.KEY_CAMERAS, (ArrayList) mainViewModel.ipCams.getValue());
                intent.putExtra(CamerasActivity.KEY_SLIDE_SHOW, true);
                startActivity(intent);
            }else{
                showMessage(R.string.no_cameras_added_yet);
            }
        });
        binding.spGridCount.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<Object>() {
            @Override
            public void onItemSelected(int i, @Nullable Object o, int i1, Object t1) {
                if(i != i1) { // take decision only when the value change
                    int gridCount = Integer.parseInt((String) t1);
                    mainViewModel.gridCount.postValue(gridCount);
                }
            }
        });
        setupCamerasPager();
    }

    private void setupObservables() {
        mainViewModel.getLoading().observe(this, loading -> {
          if(loading){
              showLoading();
          }else{
              hideLoading();
          }
        });
        mainViewModel.getError().observe(this, error -> {
            if(error){
                showMessage(R.string.error_occurred_while_processing);
            }
        });
        mainViewModel.ipCams.observe(this, ipCams -> {
            updateViewPager();
        });
        mainViewModel.gridCount.observe(this, gridCount ->{
            binding.spGridCount.setText(String.format("%d",gridCount));
            getSharedPreferences(CommonUtils.SHARED_PREFERENCES_FILE, MODE_PRIVATE).edit().putInt(CommonUtils.KEY_GRID_COUNT, gridCount).apply();
            updateViewPager();
        });
    }


    private void showSettings() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    public void addNewCam() {
        if (mainViewModel.ipCams.getValue().size() < 32) {
            startActivity(new Intent(MainActivity.this, AddCamActivity.class));
        } else {
            showMessage(R.string.cameras_limit);
        }
    }


    private void setupCamerasPager() {
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        binding.vpSlider.setAdapter(pagerAdapter);
        binding.vpSlider.setEmptyView(binding.pagerEmptyView.getRoot());
        binding.vpSlider.setOffscreenPageLimit(0);
        binding.dotsIndicator.setViewPager(binding.vpSlider);
        binding.vpSlider.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPageIndex = position ;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_PAGE_INDEX, currentPageIndex);
    }

    private void updateViewPager() {
        int gridCount = mainViewModel.gridCount.getValue();
        int camsCount = mainViewModel.ipCams.getValue().size();
        int numOfFragments = (int)Math.ceil(camsCount * 1.0 / gridCount);

        // recreate the fragments in view pager
        if(pagerAdapter.getCount() != numOfFragments){ // a change in the view pager is neeeded
            pagerAdapter.getFragmentList().clear();
            for (int i = 1; i <= numOfFragments; i ++) {
                GridFragment gridFragment = GridFragment.newInstance(i);
                pagerAdapter.addFragment(gridFragment);
            }
            pagerAdapter.notifyDataSetChanged();
            binding.vpSlider.setCurrentItem(currentPageIndex);
            binding.dotsIndicator.refreshDots();
        }
    }





}