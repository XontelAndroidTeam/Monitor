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
import android.widget.FrameLayout;
import android.widget.Spinner;

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
import com.xontel.surveillancecameras.utils.StorageHelper;
import com.xontel.surveillancecameras.viewModels.MainViewModel;
import com.xontel.surveillancecameras.viewModels.ViewModelProviderFactory;
import com.xontel.surveillancecameras.adapters.PagerAdapter;
import com.xontel.surveillancecameras.base.BaseActivity;
import com.xontel.surveillancecameras.databinding.ActivityMainBinding;

import java.util.Objects;
import javax.inject.Inject;

public class HomeActivity extends BaseActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = HomeActivity.class.getSimpleName();
    private FrameLayout textInputLayout;
    private ActivityMainBinding binding;


    @Inject
    ViewModelProviderFactory providerFactory;

    private MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.homeTheme);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        getActivityComponent().inject(this);
        mainViewModel = new ViewModelProvider(this, providerFactory).get(MainViewModel.class);
        mainViewModel.getAllDevices();
        setUp();

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
    }



    private void setupNavigation() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(binding.navView, navController);
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(navHostFragment.getNavController().getGraph())
                        .setOpenableLayout(binding.drawer)
                        .build();

        navController.addOnDestinationChangedListener((navController1, navDestination, bundle) -> {
            if (Objects.requireNonNull(navController1.getCurrentDestination()).getId() == R.id.monitorFragment){
                setupGridDropDown();
            }else{
                removeGridDropDown();
            }
        });

    }

    private void setupGridDropDown() {
        textInputLayout = (FrameLayout) getLayoutInflater().inflate(R.layout.drop_down, null);
        binding.appBar.llCustomView.addView(textInputLayout);
        ArrayAdapter gridDropDownAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.grid_count));
        Spinner spinner = textInputLayout.findViewById(R.id.spinner);
//        autoCompleteTextView.setText(String.valueOf(mainViewModel.mGridObservable.getValue()), false);
        spinner.setAdapter(gridDropDownAdapter);
        spinner.setOnItemSelectedListener(this);
        binding.setLifecycleOwner(this);
    }

    private void removeGridDropDown(){
        binding.appBar.llCustomView.removeView(textInputLayout);
    }


    public void toggleSideMenu() {
        if (binding.drawer.isOpen())
            binding.drawer.close();
        else
            binding.drawer.open();
    }




    @Override
    protected void onResume() {
        super.onResume();
    }


    public void addNewCam() {
            startActivity(new Intent(HomeActivity.this, AddCamActivity.class));
    }




    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//        AutoCompleteTextView autoCompleteTextView = textInputLayout.findViewById(R.id.slide_show_filter);
//        int gridCount = Integer.parseInt(autoCompleteTextView.getText().toString());
//        if (mainViewModel.getGridObservable().getValue() != gridCount){
//            StorageHelper.saveGridCount(this,gridCount);
//            mainViewModel.mGridObservable.setGridCount(gridCount+"");
//        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}