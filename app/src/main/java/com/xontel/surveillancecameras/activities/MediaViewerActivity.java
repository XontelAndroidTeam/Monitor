package com.xontel.surveillancecameras.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.base.BaseActivity;
import com.xontel.surveillancecameras.databinding.ActivitySavedMediaBinding;

import java.io.File;

public class MediaViewerActivity extends BaseActivity {
    private ActivitySavedMediaBinding binding;
    private String mediaFilePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_video_viewer_activity);
        binding.getRoot().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LOW_PROFILE |
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        intUI();
    }

    private void intUI() {

    }

    private void showImage(){

    }

    private void showVideo(){

    }

    @Override
    protected void setUp() {
        
    }

    
}