package com.xontel.surveillancecameras.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.adapters.PagerAdapter;
import com.xontel.surveillancecameras.adapters.SinglePagerAdapter;
import com.xontel.surveillancecameras.base.BaseActivity;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.databinding.ActivityCamerasBinding;
import com.xontel.surveillancecameras.dialogs.CamDetailsDialog;
import com.xontel.surveillancecameras.dialogs.SettingsDialog;
import com.xontel.surveillancecameras.fragments.CamPreviewFragment;
import com.xontel.surveillancecameras.presenters.MainMvpPresenter;
import com.xontel.surveillancecameras.presenters.MainMvpView;
import com.xontel.surveillancecameras.utils.CommonUtils;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

public class CamerasActivity extends BaseActivity implements MainMvpView {
    public static final String KEY_CAMERAS = "cameras";
    public static final String KEY_SLIDE_SHOW = "slide_show";
    private static final long TIME_TO_HIDE_BTNS = 5000;
    private static final long ANIMATION_DURATION = 300;
    private SinglePagerAdapter singlePagerAdapter;
    private Menu optionMenu ;
    int slideInterval;
    int selectedPage;
    boolean isSlideShow = false;
    @Inject
    MainMvpPresenter<MainMvpView> mPresenter;
    private List<IpCam> cams = new ArrayList<>();
    private ActivityCamerasBinding binding;
    private Timer timer;
    private SharedPreferences sharedPreferences;

    SharedPreferences.OnSharedPreferenceChangeListener listener = (sharedPreferences, key) -> {
        setupSliderSettings();
    };

    private  Runnable btnsRemovalRunnable = new Runnable() {
        @Override
        public void run() {
            hideButtons();
        }
    };
    private Handler btnsHandler = new Handler();

    private boolean isBtnsShown = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cameras);
        sharedPreferences = getSharedPreferences(CommonUtils.SHARED_PREFERENCES_FILE, MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
        if (getIntent().getExtras() != null && getIntent().getParcelableExtra(KEY_CAMERAS) != null ){
            cams.add((IpCam) getIntent().getParcelableExtra(KEY_CAMERAS));
            updateCurrentCam(cams.get(0));
        }
        if (getIntent().hasExtra(KEY_SLIDE_SHOW)) {
            isSlideShow = true;
        } else {
            isSlideShow = false;
        }
        getActivityComponent().inject(this);
        mPresenter.onAttach(this);
        setUp();
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



    private void showSettings() {
        SettingsDialog settingsDialog = new SettingsDialog(this);
        settingsDialog.show();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
      //      case REQUEST_CODE_EDIT_CAM:
       //         if (resultCode == Activity.RESULT_OK && data != null) {
         //           IpCam ipCam = data.getParcelableExtra(KEY_CAMERAS);
          //          updateCurrentCam(ipCam);
         //       }
        }
    }

    private void updateCurrentCam(IpCam ipCam) {
       // replaceCamById(ipCam);
        setupCamerasPager();
    }

    private void replaceCamById(IpCam ipCam) {
        for (int i = 0; i < cams.size(); i++) {
          //  if (ipCam.getId() == cams.get(i).getId()) {
          //      cams.set(i, ipCam);
           //     break;
          //  }
        }
    }

    public void deleteCam() {
        Log.i("TATZ", "action delete FromActi: ");
        int camPosition = binding.vpSlider.getCurrentItem();
        new MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
                .setTitle(R.string.delete_camera)
                .setMessage(R.string.are_you_sure_delete)

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        mPresenter.deleteCamera(cams.get(camPosition));
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    @Override
    public void setUp() {
        super.setUp();
        binding.btnBack.setOnClickListener(view -> onBackPressed());
     //   binding.btnDetails.setOnClickListener(view -> showCamDetails());
     //   binding.btnShare.setOnClickListener(view -> shareCam());
     //   binding.btnEdit.setOnClickListener(view -> editCam());
     //   binding.btnDelete.setOnClickListener(view ->  deleteCam() );
        //binding.tvCamName.setText(cams.get(0).getName());
        binding.vpSlider.setOnTouchListener((view, motionEvent) -> {
            showButtons();
            scheduleHidingBtns();
            return true;
        });

    }

    public ActivityCamerasBinding getViewRoot(){ return binding ;}

    private void scheduleHidingBtns() {
        btnsHandler.removeCallbacks(btnsRemovalRunnable);//add this
        btnsHandler.postDelayed(btnsRemovalRunnable, TIME_TO_HIDE_BTNS);
    }

    private void hideButtons() {
        if(isBtnsShown) {
            binding.llBtns.animate()
                    .alpha(0.0f)
                    .translationY(-100)
                    .setDuration(ANIMATION_DURATION)
                    .setListener(null);
            binding.dotsIndicator.animate()
                    .alpha(0.0f)
                    .translationY(100)
                    .setDuration(ANIMATION_DURATION)
                    .setListener(null);
            isBtnsShown = false;
        }
    }

    private void showButtons() {
        if(!isBtnsShown) {
            binding.llBtns.animate()
                    .alpha(1.0f)
                    .translationY(0)
                    .setDuration(ANIMATION_DURATION)
                    .setListener(null);
            binding.dotsIndicator.animate()
                    .alpha(1.0f)
                    .translationY(0)
                    .setDuration(ANIMATION_DURATION)
                    .setListener(null);
            isBtnsShown = true;
        }
    }

    private void setupCamerasPager() {
        singlePagerAdapter = new SinglePagerAdapter(getSupportFragmentManager(),1);
        for (IpCam ipCam : cams) {
            CamPreviewFragment camPreviewFragment = CamPreviewFragment.newInstance(ipCam);
            singlePagerAdapter.addFragment(camPreviewFragment);
        }
        binding.vpSlider.setAdapter(singlePagerAdapter);
        binding.vpSlider.setOffscreenPageLimit(0);
        if (cams.size() > 2) {binding.dotsIndicator.setViewPager(binding.vpSlider);}
        setupSliderSettings();
    }

    private void setupSliderSettings() {

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
        showButtons();
        scheduleHidingBtns();
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
        hitBack();
    }

    @Override
    public void onGettingCamera(IpCam response) {

    }

    @Override
    public void onGettingAllCameras(List<IpCam> response) {

    }


}