package com.xontel.surveillancecameras.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.database.DataSetObserver;
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

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MainActivity extends BaseActivity implements MainMvpView /*, CamsAdapter.Callback*/ {
    private PagerAdapter pagerAdapter;
    private ActivityMainBinding binding;
    private int gridCount;
    private List<IpCam> cams = new ArrayList<>();
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
        super.onDestroy();
        mPresenter.onDetach();
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

    private void initUI() {
        gridCount = getSharedPreferences(CommonUtils.SHARED_PREFERENCES_FILE, MODE_PRIVATE).getInt(CommonUtils.KEY_GRID_COUNT, GridFragment.DEFAULT_GRID_COUNT);
        binding.tvGridCount.setText(String.valueOf(gridCount));
        binding.ivAddCam.setOnClickListener(v -> {
           addNewCam();

        });
        binding.tvSlideShow.setOnClickListener(v -> {
            Intent intent = new Intent(this, CamerasActivity.class);
            intent.putParcelableArrayListExtra(CamerasActivity.KEY_CAMERAS, (ArrayList) cams);
            startActivity(intent);
        });
        binding.tvGridCount.setOnClickListener(v->{
            gridCount =(int) Math.pow(((((int)Math.sqrt(gridCount))% 4) + 1), 2);
            Log.e("TAG", "gridCount: "+gridCount);
            binding.tvGridCount.setText(String.valueOf(gridCount));
            getSharedPreferences(CommonUtils.SHARED_PREFERENCES_FILE, MODE_PRIVATE).edit().putInt(CommonUtils.KEY_GRID_COUNT, gridCount).apply();
            updateViewPager();

        });
        setupCamerasPager();
//        setupCamsGrid();
    }

    public void addNewCam() {
        if(cams.size() < 16){
            startActivity(new Intent(this, AddCamActivity.class));
        }else{
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
        binding.vpSlider.setOffscreenPageLimit(1);
        binding.dotsIndicator.setViewPager(binding.vpSlider);
    }

    private void updateViewPager() {
        pagerAdapter.getFragmentList().clear();
        for(int i = 0  ; i< cams.size() ; i+=gridCount){
            List<IpCam> subCams = cams.subList(i, Math.min(cams.size(), i+gridCount));
            Log.e("subCams", subCams.size()+"");
            GridFragment gridFragment =  GridFragment.newInstance(subCams);
            pagerAdapter.addFragment(gridFragment);

        }
        binding.vpSlider.setAdapter(pagerAdapter);
        binding.vpSlider.setOffscreenPageLimit(1);

        binding.dotsIndicator.refreshDots();


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
        Log.e("cams number", response.size()+"");
        cams.clear();
        cams.addAll(response);
        updateViewPager();

    }

    @Override
    public void onCreatingCam() {

    }
}