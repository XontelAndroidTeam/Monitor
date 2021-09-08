package com.xontel.surveillancecameras.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Bundle;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.adapters.CamsAdapter;
import com.xontel.surveillancecameras.adapters.GridAdapter;
import com.xontel.surveillancecameras.base.BaseActivity;
import com.xontel.surveillancecameras.databinding.ActivityMainBinding;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.presenters.MainMvpPresenter;
import com.xontel.surveillancecameras.presenters.MainMvpView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MainActivity extends BaseActivity implements MainMvpView, CamsAdapter.Callback {
    private CamsAdapter camsAdapter ;
    private GridAdapter gridAdapter;
    private ActivityMainBinding binding ;
    @Inject
    MainMvpPresenter<MainMvpView> mPresenter ;
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
    protected void setUp() {

    }

    private void initUI() {
        binding.llSettings.setOnClickListener(v->{
            startActivity(new Intent(this, SettingsActivity.class));
        });
        binding.llSlideShow.setOnClickListener(v->{
            Intent intent = new Intent(this, CamerasActivity.class);
            intent.putParcelableArrayListExtra(CamerasActivity.KEY_CAMERAS, (ArrayList)camsAdapter.getCams());
            startActivity(intent);
        });
//        setupCamsGrid();
        setupCamsList();
    }

    private void setupCamsGrid() {
//        gridAdapter = new GridAdapter(this, new ArrayList<>());
//        binding.simpleGridView.setAdapter(gridAdapter);
//        populateCamsList();
    }

    public ArrayList<IpCam> populateDummyCameras(){
        ArrayList<IpCam> cams = new ArrayList<>();
        for(int i = 1 ; i<= 1 ; i++){
            cams.add(new IpCam("http://192.168.1.1ideo.cgi", "cam_"+i, "dummy"));
        }
        return cams;
    }

    private void setupCamsList() {
        camsAdapter = new CamsAdapter(new ArrayList<>(), this, this);
        binding.rvCams.setAdapter(camsAdapter);
        binding.rvCams.setLayoutManager(new GridLayoutManager(this, 2));
//        populateCamsList();
    }

    void populateCamsList(){
        List<IpCam> cams = new ArrayList<>();
        cams.add(new IpCam("http://192.168.1.152:8080/video.cgi", "cam1", "my cam"));
        cams.add(new IpCam("http://192.168.1.152:8080/video.cgi", "cam2", "my cam"));
        cams.add(new IpCam("http://192.168.1.152:8080/video.cgi", "cam3", "my cam"));
        cams.add(new IpCam("http://192.168.1.152:8080/video.cgi", "cam4", "my cam"));
        cams.add(new IpCam("http://192.168.1.152:8080/video.cgi", "cam5", "my cam"));
        cams.add(new IpCam("http://192.168.1.152:8080/video.cgi", "cam6", "my cam"));
        cams.add(new IpCam("http://192.168.1.152:8080/video.cgi", "cam7", "my cam"));
        cams.add(new IpCam("http://192.168.1.152:8080/video.cgi", "cam8", "my cam"));
        cams.add(new IpCam("http://192.168.1.152:8080/video.cgi", "cam9", "my cam"));
        cams.add(new IpCam("http://192.168.1.152:8080/video.cgi", "cam10", "my cam"));
        cams.add(new IpCam("http://192.168.1.152:8080/video.cgi", "cam11", "my cam"));
        cams.add(new IpCam("http://192.168.1.152:8080/video.cgi", "cam12", "my cam"));
        cams.add(new IpCam("http://192.168.1.152:8080/video.cgi", "cam13", "my cam"));
        cams.add(new IpCam("http://192.168.1.152:8080/video.cgi", "cam14", "my cam"));
        cams.add(new IpCam("http://192.168.1.152:8080/video.cgi", "cam15", "my cam"));
        cams.add(new IpCam("http://192.168.1.152:8080/video.cgi", "cam16", "my cam"));
        camsAdapter.addItems(cams);

    }

    @Override
    public void onCamClicked(int position) {

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
        camsAdapter.addItems(response);
        camsAdapter.notifyDataSetChanged();
//        gridAdapter.addItems(response);
//        gridAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreatingCam() {

    }
}