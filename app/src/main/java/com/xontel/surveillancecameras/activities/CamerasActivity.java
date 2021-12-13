package com.xontel.surveillancecameras.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.adapters.PagerAdapter;
import com.xontel.surveillancecameras.base.BaseActivity;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.databinding.ActivityCamerasBinding;
import com.xontel.surveillancecameras.dialogs.CamDetailsDialog;
import com.xontel.surveillancecameras.dialogs.SettingsDialog;
import com.xontel.surveillancecameras.fragments.CameraFragment;
import com.xontel.surveillancecameras.presenters.MainMvpPresenter;
import com.xontel.surveillancecameras.presenters.MainMvpView;
import com.xontel.surveillancecameras.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

public class CamerasActivity extends BaseActivity implements MainMvpView {
    public static final String KEY_CAMERAS = "cameras";
    public static final String KEY_SLIDE_SHOW = "slide_show";
    private static final int REQUEST_CODE_EDIT_CAM = 44;
    int slideInterval;
    int selectedPage;
    boolean isSlideShow = false;
    PagerAdapter pagerAdapter;
    @Inject
    MainMvpPresenter<MainMvpView> mPresenter;
    private List<IpCam> cams = new ArrayList<>();
    private ActivityCamerasBinding binding;
    private Timer timer;
    private SharedPreferences sharedPreferences;
    SharedPreferences.OnSharedPreferenceChangeListener listener = (sharedPreferences, key) -> {
        setupSliderSettings();
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cameras);
        sharedPreferences = getSharedPreferences(CommonUtils.SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
        setSupportActionBar(binding.toolbar);
        cams = getIntent().getParcelableArrayListExtra(KEY_CAMERAS);
        if (getIntent().hasExtra(KEY_SLIDE_SHOW)) {
            binding.tvTitle.setText(R.string.slide_show);
            isSlideShow = true;
        } else {
            try {
                binding.tvTitle.setText(cams.get(0).getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            isSlideShow = false;
        }
        getActivityComponent().inject(this);
        mPresenter.onAttach(this);
        initUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDetach();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    protected void setUp() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(!isSlideShow) {
            getMenuInflater().inflate(R.menu.toolbar_menu, menu);
            return true;
        }else{
            return false;
        }

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
//            case R.id.action_settings:
//                showSettings();
//                return true;
            case R.id.action_capture_photo:
                capturePhoto();
                return true;
            case R.id.action_record_video:
                recordVideo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void recordVideo() {
    }

    private void capturePhoto() {

    }

    private void showSettings() {
        SettingsDialog settingsDialog = new SettingsDialog(this);
        settingsDialog.show();
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
        switch (requestCode) {
            case REQUEST_CODE_EDIT_CAM:
                if (resultCode == Activity.RESULT_OK && data != null) {
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
        for (int i = 0; i < cams.size(); i++) {
            if (ipCam.getId() == cams.get(i).getId()) {
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
    }

    private void setupCamerasPager() {
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        for (IpCam ipCam : cams) {
            CameraFragment cameraFragment = CameraFragment.newInstance(ipCam);
            pagerAdapter.addFragment(cameraFragment);
        }
        binding.vpSlider.setAdapter(pagerAdapter);
        binding.vpSlider.setOffscreenPageLimit(0);
        if (cams.size() > 1)
            binding.dotsIndicator.setViewPager(binding.vpSlider);
        setupSliderSettings();
    }

    private void setupSliderSettings() {
        boolean isAutoPreview = sharedPreferences.getBoolean(CommonUtils.KEY_AUTO_PREVIEW, true);
        int slideIntervalIndex = sharedPreferences.getInt(CommonUtils.KEY_SLIDE_INTERVAL_INDEX, 0);
        disableAutoPreview();
        if (isAutoPreview) {
            slideInterval = Integer.parseInt(getResources().getStringArray(R.array.intervals)[slideIntervalIndex].split(" ")[0]);
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    Log.e("TAG", System.currentTimeMillis() + "");
                    selectedPage = binding.vpSlider.getCurrentItem();
                    if (selectedPage <= pagerAdapter.getCount()) {
                        selectedPage = (selectedPage + 1) % pagerAdapter.getCount();
                        CamerasActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.vpSlider.setCurrentItem(selectedPage);
                            }
                        });

                    }
                }
            }, slideInterval * 1000, slideInterval * 1000);
        }
    }

    private void disableAutoPreview() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupCamerasPager();
    }

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


}