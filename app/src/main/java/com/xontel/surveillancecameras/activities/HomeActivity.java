package com.xontel.surveillancecameras.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.material.textfield.TextInputLayout;
import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.adapters.SideMenuAdapter;
import com.xontel.surveillancecameras.viewModels.MainViewModel;
import com.xontel.surveillancecameras.viewModels.ViewModelProviderFactory;
import com.xontel.surveillancecameras.adapters.PagerAdapter;
import com.xontel.surveillancecameras.base.BaseActivity;
import com.xontel.surveillancecameras.databinding.ActivityMainBinding;

import java.util.Objects;
import javax.inject.Inject;

public class HomeActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private static final String TAG = HomeActivity.class.getSimpleName();
    private static final String KEY_CURRENT_PAGE_INDEX = "current_page_index";
    private PagerAdapter pagerAdapter;
    private TextInputLayout textInputLayout;
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
//        mainViewModel.getAllCameras();
        setUp();
//        if (savedInstanceState != null) {
//            currentPageIndex = savedInstanceState.getInt(KEY_CURRENT_PAGE_INDEX, 0);
//        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       // MenuInflater menuInflater = getMenuInflater();
     //   menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
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
//        binding.home.pagerEmptyView.btnAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(HomeActivity.this, AddNewDeviceActivity.class));
//            }
//        });
        setupObservables();
//        setupCamerasPager();
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

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
                if (Objects.requireNonNull(navController.getCurrentDestination()).getId() == R.id.monitorFragment){
                    setupGridDropDown();
                }else{
                    removeGridDropDown();
                }
            }
        });

    }

    private void setupGridDropDown() {
        textInputLayout = (TextInputLayout) getLayoutInflater().inflate(R.layout.drop_down, null);
        binding.appBar.llCustomView.addView(textInputLayout);
        ArrayAdapter gridDropDownAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.grid_count));
        AutoCompleteTextView autoCompleteTextView = textInputLayout.findViewById(R.id.slide_show_filter);
        autoCompleteTextView.setText(String.valueOf(mainViewModel.gridCount.getValue()));
        autoCompleteTextView.setAdapter(gridDropDownAdapter);
        autoCompleteTextView.setOnItemClickListener(this);
        binding.setLifecycleOwner(this);
    }

    private void removeGridDropDown(){
        binding.appBar.llCustomView.removeView(textInputLayout);
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




    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_PAGE_INDEX, currentPageIndex);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        AutoCompleteTextView autoCompleteTextView = textInputLayout.findViewById(R.id.slide_show_filter);
        int gridCount = Integer.parseInt(autoCompleteTextView.getText().toString());
        if (mainViewModel.gridCount.getValue() != gridCount){
            mainViewModel.gridCount.setValue(gridCount);
            mainViewModel.refreshGridCount.setValue(true);
        }
    }

//    private void updateViewPager() {
//        int gridCount = mainViewModel.getGridObservable().getValue();
//        int camsCount = mainViewModel.ipCams.getValue().size();
//        int numOfFragments = (int) Math.ceil(camsCount * 1.0 / gridCount);
//        Log.v("TAG_", "pages : " + pagerAdapter.getFragmentList().size() + " grid count : " + gridCount + " numOfFragments : " + numOfFragments);
////         recreate the fragments in view pager
//        if (pagerAdapter.getCount() > numOfFragments) { // a change in the view pager is needed
//            for (int i = pagerAdapter.getCount() - 1; i >= numOfFragments; i--) {
//                Log.v("TAG_", i + "");
////                pagerAdapter.destroyItem(binding.home.vpSlider, i, pagerAdapter.getFragmentList().get(i));
//                pagerAdapter.removeFragment(i);
//            }
//        } else if (pagerAdapter.getCount() < numOfFragments) {
//            numOfFragments = numOfFragments - pagerAdapter.getCount();
//            for (int i = 0; i < numOfFragments; i++) {
//                MonitorFragment gridFragment = MonitorFragment.newInstance(pagerAdapter.getFragmentList().size() + 1);
//                pagerAdapter.addFragment(gridFragment);
//                pagerAdapter.notifyDataSetChanged();
////                binding.home.vpSlider.setCurrentItem(currentPageIndex);
////                binding.home.dotsIndicator.refreshDots();
//            }
//        }
//
//
//    }


}