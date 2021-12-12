package com.xontel.surveillancecameras.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.base.BaseActivity;
import com.xontel.surveillancecameras.databinding.ActivitySavedMediaBinding;

public class SavedMediaActivity extends BaseActivity {
    private ActivitySavedMediaBinding binding ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_saved_media);
        initUI();
    }

    private void initUI() {
        setupMediaList();
    }

    private void setupMediaList() {

    }

    @Override
    protected void setUp() {

    }

    @Override
    public void onCreatingCam() {

    }
}