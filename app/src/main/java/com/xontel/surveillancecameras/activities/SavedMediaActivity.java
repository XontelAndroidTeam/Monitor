package com.xontel.surveillancecameras.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.base.BaseActivity;
import com.xontel.surveillancecameras.databinding.ActivitySavedMediaBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SavedMediaActivity extends BaseActivity {
    private ActivitySavedMediaBinding binding ;
    public static final String imagesDirPath = "/images";
    public static final String videosDirPath = "/videos";
    private List<File> mediaFiles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_saved_media);
        loadMediaFiles();
        initUI();
    }

    private void loadMediaFiles() {
        File[] externalStorageDirs = getExternalFilesDirs(null) ;
        for(File dir : externalStorageDirs){
            mediaFiles.addAll(Arrays.asList(new File(dir.getAbsolutePath()+imagesDirPath)));
            mediaFiles.addAll(Arrays.asList(new File(dir.getAbsolutePath()+videosDirPath)));
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