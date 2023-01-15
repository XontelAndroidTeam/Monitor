package com.xontel.surveillancecameras.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.textfield.TextInputLayout;
import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.adapters.SideMenuAdapter;
import com.xontel.surveillancecameras.fragments.MonitorFragment;
import com.xontel.surveillancecameras.viewModels.MainViewModel;
import com.xontel.surveillancecameras.viewModels.ViewModelProviderFactory;
import com.xontel.surveillancecameras.adapters.PagerAdapter;
import com.xontel.surveillancecameras.base.BaseActivity;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                addNewCam();
                return false;
            case android.R.id.home:
                toggleSideMenu();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void setUp() {
        super.setUp();
        setupNavigation();
        setupGridDropDown();
//        binding.home.pagerEmptyView.btnAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(HomeActivity.this, AddNewDeviceActivity.class));
//            }
//        });
        setupObservables();
        setupCamerasPager();
    }

    private void setupNavigation() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(binding.navView, navController);
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(navHostFragment.getNavController().getGraph())
                        .setOpenableLayout(binding.drawer)
                        .build();
//        NavigationUI.setupWithNavController(
//                binding.appBar.toolbar, navController, appBarConfiguration);

    }

    private void setupGridDropDown() {
//        TextInputLayout child = (TextInputLayout) getLayoutInflater().inflate(R.layout.drop_down, null);
//        binding.appBar.llCustomView.addView(child);
//        ArrayAdapter gridDropDownAdapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_spinner_dropdown_item,
//                getResources().getStringArray(R.array.grid_count));
//        AutoCompleteTextView autoCompleteTextView = child.findViewById(R.id.slide_show_filter);
//        autoCompleteTextView.setAdapter(gridDropDownAdapter);
//        autoCompleteTextView.setText(mainViewModel.getGridObservable().getGridCount());
//        binding.setLifecycleOwner(this);
    }

    private void setupSideMenu() {
        SideMenuAdapter sideMenuAdapter = new SideMenuAdapter(this, labelsId -> {
            switch (labelsId) {
                case R.string.monitor:
                    binding.drawer.close();
                    break;
                case R.string.devices:
                    navigateToActivity(DevicesActivity.class);
                    break;
                case R.string.saved_media:
                    navigateToActivity(SavedMediaActivity.class);
                    break;
                case R.string.settings:
                    navigateToActivity(SettingsActivity.class);
                    break;
            }

        });
//        binding.sideMenu.setAdapter(sideMenuAdapter);
    }

    private void navigateToActivity(Class classZ) {
        binding.drawer.close();
        startActivity(new Intent(this, classZ));
    }


    private void toggleSideMenu() {
        if (binding.drawer.isOpen())
            binding.drawer.close();
        else
            binding.drawer.open();
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

        mainViewModel.getGridObservable().addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                updateViewPager();
            }
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
            startActivity(new Intent(HomeActivity.this, AddNewDeviceActivity.class));

        } else {
            showMessage(R.string.cameras_limit);
        }
    }


    private void setupCamerasPager() {
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
//        binding.home.vpSlider.setAdapter(pagerAdapter);
//        binding.home.vpSlider.setEmptyView(binding.home.pagerEmptyView.getRoot());
//        binding.home.vpSlider.setOffscreenPageLimit(0);
//        binding.home.dotsIndicator.setViewPager(binding.home.vpSlider);
//        binding.home.vpSlider.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
        int gridCount = mainViewModel.getGridObservable().getValue();
        int camsCount = mainViewModel.ipCams.getValue().size();
        int numOfFragments = (int) Math.ceil(camsCount * 1.0 / gridCount);
        Log.v("TAG_", "pages : " + pagerAdapter.getFragmentList().size() + " grid count : " + gridCount + " numOfFragments : " + numOfFragments);
//         recreate the fragments in view pager
        if (pagerAdapter.getCount() > numOfFragments) { // a change in the view pager is needed
            for (int i = pagerAdapter.getCount() - 1; i >= numOfFragments; i--) {
                Log.v("TAG_", i + "");
//                pagerAdapter.destroyItem(binding.home.vpSlider, i, pagerAdapter.getFragmentList().get(i));
                pagerAdapter.removeFragment(i);
            }
        } else if (pagerAdapter.getCount() < numOfFragments) {
            numOfFragments = numOfFragments - pagerAdapter.getCount();
            for (int i = 0; i < numOfFragments; i++) {
                MonitorFragment gridFragment = MonitorFragment.newInstance(pagerAdapter.getFragmentList().size() + 1);
                pagerAdapter.addFragment(gridFragment);
                pagerAdapter.notifyDataSetChanged();
//                binding.home.vpSlider.setCurrentItem(currentPageIndex);
//                binding.home.dotsIndicator.refreshDots();
            }
        }


    }


}