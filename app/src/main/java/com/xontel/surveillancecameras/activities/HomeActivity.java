package com.xontel.surveillancecameras.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.adapters.SideMenuAdapter;
import com.xontel.surveillancecameras.customObservers.GridObservable;
import com.xontel.surveillancecameras.fragments.GridFragment;
import com.xontel.surveillancecameras.viewModels.MainViewModel;
import com.xontel.surveillancecameras.viewModels.ViewModelProviderFactory;
import com.xontel.surveillancecameras.adapters.PagerAdapter;
import com.xontel.surveillancecameras.base.BaseActivity;
import com.xontel.surveillancecameras.data.DataManager;
import com.xontel.surveillancecameras.databinding.ActivityMainBinding;

import javax.inject.Inject;

public class HomeActivity extends BaseActivity {

    private static final String TAG = HomeActivity.class.getSimpleName();
    private static final String KEY_CURRENT_PAGE_INDEX = "current_page_index";
    private PagerAdapter pagerAdapter;
    private ActivityMainBinding binding;
    private int currentPageIndex;




    @Inject
    ViewModelProviderFactory providerFactory;

    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        getActivityComponent().inject(this);
        mainViewModel = new ViewModelProvider(this, providerFactory).get(MainViewModel.class);
        mainViewModel.getAllCameras();
        setUp();
//        if (savedInstanceState != null) {
//            currentPageIndex = savedInstanceState.getInt(KEY_CURRENT_PAGE_INDEX, 0);
//        }
    }


    @Override
    protected void setUp() {
        setupToolbar();
        setupGridDropDown();
        setupObservables();
        setupCamerasPager();
    }

    private void setupGridDropDown() {
        ArrayAdapter gridDropDownAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.grid_count));
        binding.toolbarLayout.gridFilter.setAdapter(gridDropDownAdapter);
        binding.toolbarLayout.setData(mainViewModel.getGridObservable());
        binding.setLifecycleOwner(this);
    }

    private void setupSideMenu() {
        SideMenuAdapter sideMenuAdapter = new SideMenuAdapter(this, new SideMenuAdapter.ClickCallback() {
            @Override
            public void onItemClicked(int labelsId) {
                switch (labelsId){
                    case R.string.devices:
                        startActivity(new Intent(HomeActivity.this, DevicesActivity.class));
                        break;
                    case R.string.saved_media:
                        startActivity(new Intent(HomeActivity.this, SavedMediaActivity.class));
                        break;
                    case R.string.settings:
                        startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
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
        binding.home.pagerEmptyView.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, AddCamActivity.class));
            }
        });
        binding.toolbarLayout.ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, AddCamActivity.class));
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
                startActivity(new Intent(HomeActivity.this, AddCamActivity.class));
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
            startActivity(new Intent(HomeActivity.this, AddCamActivity.class));
        } else {
            showMessage(R.string.cameras_limit);
        }
    }


    private void setupCamerasPager() {
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        binding.home.vpSlider.setAdapter(pagerAdapter);
        binding.home.vpSlider.setEmptyView(binding.home.pagerEmptyView.getRoot());
        binding.home.vpSlider.setOffscreenPageLimit(0);
        binding.home.dotsIndicator.setViewPager(binding.home.vpSlider);
        binding.home.vpSlider.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPageIndex = position;
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
        int gridCount = mainViewModel.getGridObservable().getValue();
        int camsCount = mainViewModel.ipCams.getValue().size();
        int numOfFragments = (int) Math.ceil(camsCount * 1.0 / gridCount);
        Log.v("TAG_", "pages : "+pagerAdapter.getFragmentList().size()+" grid count : " + gridCount + " numOfFragments : " + numOfFragments);
        // recreate the fragments in view pager
        if (pagerAdapter.getCount() > numOfFragments) { // a change in the view pager is needed
            for (int i = pagerAdapter.getCount() - 1; i >= numOfFragments; i--) {
                Log.v("TAG_", i+"");
                pagerAdapter.destroyItem(binding.home.vpSlider, i, pagerAdapter.getFragmentList().get(i));
                pagerAdapter.removeFragment(i);
            }
        } else if(pagerAdapter.getCount() < numOfFragments){
            numOfFragments = numOfFragments - pagerAdapter.getCount();
            for (int i = 0 ; i < numOfFragments; i++) {
                GridFragment gridFragment = GridFragment.newInstance(pagerAdapter.getFragmentList().size() + 1);
                pagerAdapter.addFragment(gridFragment);
            }
        }

        pagerAdapter.notifyDataSetChanged();
        binding.home.vpSlider.setCurrentItem(currentPageIndex);
        binding.home.dotsIndicator.refreshDots();

    }


}