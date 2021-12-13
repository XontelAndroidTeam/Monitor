package com.xontel.surveillancecameras.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.adapters.MediaAdapter;
import com.xontel.surveillancecameras.base.BaseActivity;
import com.xontel.surveillancecameras.databinding.ActivitySavedMediaBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SavedMediaActivity extends BaseActivity implements MediaAdapter.ClickActionListener{
    private ActivitySavedMediaBinding binding ;
    private List<File> mediaFiles = new ArrayList<>();
    private MediaAdapter mediaAdapter ;
    private ActionMode actionMode ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_saved_media);
        initUI();
    }

    private void initUI() {
        binding.ivBack.setOnClickListener(v -> {
            onBackPressed();
        });
        loadMediaFiles();
        setupMediaList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(actionMode != null){
            actionMode.finish();
        }
    }

    private void loadMediaFiles() {
        String videosDirPath = "/videos";
        String imagesDirPath = "/snapshots";
        File[] dirs = ContextCompat.getExternalFilesDirs(this, null);
        for(File dir : dirs){
            File videosDir = new File(dir.getAbsoluteFile()+videosDirPath);
            File imagesDir = new File(dir.getAbsoluteFile()+imagesDirPath);
            mediaFiles.addAll(Arrays.asList(videosDir.listFiles()));
            mediaFiles.addAll(Arrays.asList(imagesDir.listFiles()));
        }
    }

    private void setupMediaList() {
        mediaAdapter = new MediaAdapter(this, mediaFiles, this);
        binding.rvMedia.setAdapter(mediaAdapter);
        binding.rvMedia.setLayoutManager(new GridLayoutManager(this, 4));
    }

    @Override
    protected void setUp() {

    }


    @Override
    public void onItemLongClicked(int position) {
        showContextualMenu();
    }

    private void showContextualMenu() {
        actionMode = startSupportActionMode(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.selection_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_delete:
                        deleteSelected();
                        actionMode.finish();
                        break;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mediaAdapter.unSelectAll();
            }
        });
    }

    private void deleteSelected() {

    }

    @Override
    public void onItemClicked(int position) {
            actionMode.setTitle(mediaAdapter.getSelectedItemsCount() + " ");
            actionMode.invalidate();

    }
}