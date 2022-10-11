package com.xontel.surveillancecameras.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.adapters.SideMenuAdapter;
import com.xontel.surveillancecameras.customObservers.GridObservable;
import com.xontel.surveillancecameras.customObservers.SettingObservable;
import com.xontel.surveillancecameras.viewModels.MainViewModel;
import com.xontel.surveillancecameras.viewModels.ViewModelProviderFactory;
import com.xontel.surveillancecameras.adapters.PagerAdapter;
import com.xontel.surveillancecameras.base.BaseActivity;
import com.xontel.surveillancecameras.data.DataManager;
import com.xontel.surveillancecameras.databinding.ActivityMainBinding;

import javax.inject.Inject;

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String KEY_CURRENT_PAGE_INDEX = "current_page_index";
    public static final int DEFAULT_GRID_COUNT = 4;
    private PagerAdapter pagerAdapter;
    private ActivityMainBinding binding;
    private int currentPageIndex;


    @Inject
    GridObservable mGridObservable ;


    @Inject
    DataManager dataManager;

    @Inject
    ViewModelProviderFactory providerFactory;

    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        getActivityComponent().inject(this);
        setUp();
//        mainViewModel = new ViewModelProvider(this, providerFactory).get(MainViewModel.class);
//        mainViewModel.getAllCameras();
//        if (savedInstanceState != null) {
//            currentPageIndex = savedInstanceState.getInt(KEY_CURRENT_PAGE_INDEX, 0);
//        }
    }


    @Override
    protected void setUp() {
        setupToolbar();
        setupGridDropDown();
    }

    private void setupGridDropDown() {
        ArrayAdapter gridDropDownAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.grid_count));
        binding.toolbarLayout.gridFilter.setAdapter(gridDropDownAdapter);
        binding.toolbarLayout.setData(mGridObservable);
        binding.setLifecycleOwner(this);
    }

    private void setupSideMenu() {
        SideMenuAdapter sideMenuAdapter = new SideMenuAdapter(this, new SideMenuAdapter.ClickCallback() {
            @Override
            public void onItemClicked(int labelsId) {
                switch (labelsId){
                    case R.string.devices:
                        startActivity(new Intent(MainActivity.this, DevicesActivity.class));
                        break;
                    case R.string.saved_media:
                        startActivity(new Intent(MainActivity.this, SavedMediaActivity.class));
                        break;
                    case R.string.settings:
                        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                        break;
                }

            }
        });
        binding.sideMenu.setAdapter(sideMenuAdapter);
    }


    private void setupToolbar() {
        setSupportActionBar(binding.toolbarLayout.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setToolbarTitle(R.string.monitor);
        setupToolbarActions();

    }

    private void setupToolbarActions() {
        binding.home.pagerEmptyView.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddCamActivity.class));
            }
        });
        binding.toolbarLayout.ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddCamActivity.class));
            }
        });
        binding.toolbarLayout.ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.drawer.isOpen())
                    binding.drawer.close();
                else
                    binding.drawer.open();
            }
        });
        binding.toolbarLayout.ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddCamActivity.class));
            }
        });
    }


    public void setToolbarTitle(int titleResId) {
        binding.toolbarLayout.tvTitle.setText(titleResId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupSideMenu();
    }

//    public List<IpCam> getCams() {
//        return cams;
//    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void initUI() {
//        setupObservables();
//        binding.ivAddCam.setOnClickListener(v -> {
//            addNewCam();
//        });
//        binding.ivSettings.setOnClickListener(v -> {
//            showSettings();
//        });
//        binding.tvSlideShow.setOnClickListener(v -> {
//            if (mainViewModel.ipCams.getValue().size() > 0) {
//                Intent intent = new Intent(MainActivity.this, CamerasActivity.class);
//                intent.putParcelableArrayListExtra(CamerasActivity.KEY_CAMERAS, (ArrayList) mainViewModel.ipCams.getValue());
//                intent.putExtra(CamerasActivity.KEY_SLIDE_SHOW, true);
//                startActivity(intent);
//            } else {
//                showMessage(R.string.no_cameras_added_yet);
//            }
//        });
//
//        binding.spGridCount.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<Object>() {
//            @Override
//            public void onItemSelected(int i, @Nullable Object o, int i1, Object t1) {
//                if (i != i1) { // take decision only when the value change
//                    int gridCount = Integer.parseInt((String) t1);
//                    mainViewModel.gridCount.postValue(gridCount);
//                }
//            }
//        });
//        setupCamerasPager();
    }

    private void setupObservables() {
        mainViewModel.getLoading().observe(this, loading -> {
            if (loading) {
                showLoading();
            } else {
                hideLoading();
            }
        });
        mainViewModel.getError().observe(this, error -> {
            if (error) {
                showMessage(R.string.error_occurred_while_processing);
            }
        });
        mainViewModel.ipCams.observe(this, ipCams -> {
            updateViewPager();
        });
//        mainViewModel.gridCount.observe(this, gridCount -> {
//            binding.spGridCount.setText(String.format("%d", gridCount));
//            dataManager.setGridCount(gridCount);
//            updateViewPager();
//        });
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
//        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
//        binding.vpSlider.setAdapter(pagerAdapter);
//        binding.vpSlider.setEmptyView(binding.pagerEmptyView.getRoot());
//        binding.vpSlider.setOffscreenPageLimit(0);
//        binding.dotsIndicator.setViewPager(binding.vpSlider);
//        binding.vpSlider.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                currentPageIndex = position;
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//
//            }
//        });
    }


    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_PAGE_INDEX, currentPageIndex);
    }

    private void updateViewPager() {
//        int gridCount = mainViewModel.gridCount.getValue();
//        int camsCount = mainViewModel.ipCams.getValue().size();
//        int numOfFragments = (int) Math.ceil(camsCount * 1.0 / gridCount);
//        Log.v("TAG_", "pages : "+pagerAdapter.getFragmentList().size()+" grid count : " + gridCount + " numOfFragments : " + numOfFragments);
        // recreate the fragments in view pager
//        if (pagerAdapter.getCount() > numOfFragments) { // a change in the view pager is needed
//            for (int i = pagerAdapter.getCount() - 1; i >= numOfFragments; i--) {
//                Log.v("TAG_", i+"");
//                pagerAdapter.destroyItem(binding.vpSlider, i, pagerAdapter.getFragmentList().get(i));
//                pagerAdapter.removeFragment(i);
//            }
//        } else if(pagerAdapter.getCount() < numOfFragments){
//            numOfFragments = numOfFragments - pagerAdapter.getCount();
//            for (int i = 0 ; i < numOfFragments; i++) {
//                GridFragment gridFragment = GridFragment.newInstance(pagerAdapter.getFragmentList().size() + 1);
//                pagerAdapter.addFragment(gridFragment);
//            }
//        }
//
//        pagerAdapter.notifyDataSetChanged();
//        binding.vpSlider.setCurrentItem(currentPageIndex);
//        binding.dotsIndicator.refreshDots();
//
    }


}