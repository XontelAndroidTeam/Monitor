package com.xontel.surveillancecameras.activities;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.base.BaseActivity;
import com.xontel.surveillancecameras.databinding.ActivitySavedMediaBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SavedMediaActivity extends BaseActivity {
    private ActivitySavedMediaBinding binding;
    private List<File> mediaFiles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_saved_media);
        loadMediaFiles();
        initUI();
    }

    private void loadMediaFiles() {
        String imagesDirPath = "/media/images";
        String videosDirPath = "/media/videos";
        File[] externalStorageDirs = getExternalFilesDirs(null);
        for (File dir : externalStorageDirs) {
            File[] images = new File(dir.getAbsolutePath() + imagesDirPath).listFiles();
            File[] videos = new File(dir.getAbsolutePath() + videosDirPath).listFiles();
            if (images != null)
                mediaFiles.addAll(Arrays.asList(images));
            if (videos != null)
                mediaFiles.addAll(Arrays.asList(videos));
        }
    }

    private void initUI() {
        setupMediaList();
    }

    private void setupMediaList() {

    }

    @Override
    protected void setUp() {

    }

}