package com.xontel.surveillancecameras.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.adapters.PagerAdapter;
import com.xontel.surveillancecameras.base.BaseActivity;
import com.xontel.surveillancecameras.databinding.ActivityCamerasBinding;
import com.xontel.surveillancecameras.dialogs.CamDetailsDialog;
import com.xontel.surveillancecameras.fragments.CameraFragment;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.presenters.MainMvpPresenter;
import com.xontel.surveillancecameras.presenters.MainMvpView;
import com.xontel.surveillancecameras.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class CamerasActivity extends BaseActivity implements MainMvpView {
    public static final String KEY_CAMERAS = "cameras";
    private static final int REQUEST_CODE_EDIT_CAM = 44;
    private List<IpCam> cams = new ArrayList<>();
    private ActivityCamerasBinding binding;
    int slideInterval = 2 ;
    int selectedPage = 0;
    private final Handler handler = new Handler();
    PagerAdapter pagerAdapter;
    @Inject
    MainMvpPresenter<MainMvpView> mPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cameras);
        setSupportActionBar(binding.toolbar);
        cams = getIntent().getParcelableArrayListExtra(KEY_CAMERAS);
        getActivityComponent().inject(this);
        mPresenter.onAttach(this);

        initUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDetach();
    }

    @Override
    protected void setUp() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_delete:
                deleteCam();
                return true;
            case R.id.action_edit:
                editCam();
                return true;
            case R.id.action_share:
                shareCam();
                return true;
            case R.id.action_details:
                showCamDetails();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showCamDetails() {
        int camPosition = binding.vpSlider.getCurrentItem();
        CamDetailsDialog camDetailsDialog = new CamDetailsDialog(this, cams.get(camPosition));
        camDetailsDialog.show();
    }

    private void shareCam() {
        int camPosition = binding.vpSlider.getCurrentItem();
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
        i.putExtra(Intent.EXTRA_TEXT, cams.get(camPosition).getUrl());
        startActivity(Intent.createChooser(i, getString(R.string.share_url)));
    }

    private void editCam() {
        int camPosition = binding.vpSlider.getCurrentItem();
        Intent intent = new Intent(this, AddCamActivity.class);
        intent.putExtra(AddCamActivity.KEY_CAMERA, cams.get(camPosition));
        startActivityForResult(intent, REQUEST_CODE_EDIT_CAM);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_EDIT_CAM:
                if(resultCode == Activity.RESULT_OK && data != null){
                    Log.e("TAG", "onActivityResult");
                    IpCam ipCam = data.getParcelableExtra(KEY_CAMERAS);
                    updateCurrentCam(ipCam);
                }
        }
    }

    private void updateCurrentCam(IpCam ipCam) {
        replaceCamById(ipCam);
        setupCamerasPager();
    }

    private void replaceCamById(IpCam ipCam) {
        for(int i =0 ; i<cams.size();i++){
            if(ipCam.getId() == cams.get(i).getId()){
                cams.set(i, ipCam);
                break;
            }
        }
    }

    private void deleteCam() {
        int camPosition = binding.vpSlider.getCurrentItem();
        new AlertDialog.Builder(this)
                .setTitle(Html.fromHtml("<font color='#fff'>" + getString(R.string.delete_camera) + "</font>"))
                .setMessage(Html.fromHtml("<font color='#fff'>" + getString(R.string.are_you_sure_delete) + "</font>"))

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(Html.fromHtml("<font color='#fff'>" + getString(android.R.string.yes) + "</font>"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        mPresenter.deleteCamera(cams.get(camPosition));
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(Html.fromHtml("<font color='#fff'>" + getString(android.R.string.no) + "</font>"), null)
                .show();
    }

    private void initUI() {
        binding.ivBack.setOnClickListener(v -> {
            onBackPressed();
        });
        setupCamerasPager();
        checkPreview();
    }

    private void setupCamerasPager() {
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        for (IpCam ipCam : cams) {
            CameraFragment cameraFragment = CameraFragment.newInstance(ipCam);
            pagerAdapter.addFragment(cameraFragment);
        }
        binding.vpSlider.setAdapter(pagerAdapter);
        binding.vpSlider.setOffscreenPageLimit(1);
        if (cams.size()>1)
            binding.dotsIndicator.setViewPager(binding.vpSlider);


    }

    void checkPreview(){
        boolean autoPreview = getSharedPreferences(CommonUtils.SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE)
                .getBoolean(CommonUtils.KEY_AUTO_PREVIEW, true);
        if (autoPreview) {
            int slideShowIntervalChoiceIndex = getSharedPreferences(CommonUtils.SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE)
                    .getInt(CommonUtils.KEY_SLIDE_INTERVAL_INDEX, 0);
            slideInterval = Integer.parseInt(getResources().getStringArray(R.array.intervals)[slideShowIntervalChoiceIndex].split(" ")[0]);
            Log.e("TAG", "checkPreview: "+slideInterval);
            handler.post(viewPagerVisibleScroll);
        }else{
            handler.removeCallbacks(viewPagerVisibleScroll);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    Runnable viewPagerVisibleScroll = new Runnable() {
        @Override
        public void run() {
            selectedPage = binding.vpSlider.getCurrentItem();
            if (selectedPage <= pagerAdapter.getCount()) {
                selectedPage = (selectedPage+1) % pagerAdapter.getCount() ;
                binding.vpSlider.setCurrentItem(selectedPage);
            }
            handler.postDelayed(viewPagerVisibleScroll, slideInterval* 1000);
        }
    };

    @Override
    public void onInsertingCamera() {

    }

    @Override
    public void onUpdatingCamera() {

    }

    @Override
    public void onDeletingCamera() {
        finish();
    }

    @Override
    public void onGettingCamera(IpCam response) {

    }

    @Override
    public void onGettingAllCameras(List<IpCam> response) {

    }

    @Override
    public void onCreatingCam() {

    }
}