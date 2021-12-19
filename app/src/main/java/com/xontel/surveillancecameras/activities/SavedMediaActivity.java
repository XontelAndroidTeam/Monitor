package com.xontel.surveillancecameras.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.view.ActionMode;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.adapters.MediaAdapter;
import com.xontel.surveillancecameras.base.BaseActivity;
import com.xontel.surveillancecameras.databinding.ActivitySavedMediaBinding;
import com.xontel.surveillancecameras.dialogs.DialogDeleteProgress;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SavedMediaActivity extends BaseActivity implements MediaAdapter.ClickActionListener, DialogDeleteProgress.ClickAction {
    private ActivitySavedMediaBinding binding;
    private List<File> mediaFiles = new ArrayList<>();
    private MediaAdapter mediaAdapter;
    private ActionMode actionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_saved_media);
        setSupportActionBar(binding.toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.selection_menu, menu);
        menu.findItem(R.id.action_select_all).setVisible(false);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_delete:
                startSelectionMode();
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onResume() {
        loadMediaFiles();
        initUI();
        super.onResume();
    }

    @Override
    protected void onPause() {
        endSelectionMode();
        super.onPause();
    }

    public void endSelectionMode() {
        if (actionMode != null) {
            actionMode.finish();
        }
    }


    public void startSelectionMode() {
        if (actionMode == null) {
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
                            deleteSelectedItems();
                            return true;
                        case R.id.action_select_all:
                            mediaAdapter.selectAll();
                            return true;
                    }
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    actionMode = null;
                    mediaAdapter.enableSelectionMode(false);
                }
            });
            mediaAdapter.enableSelectionMode(true);
        }
    }

    public void updateActionMode(int itemsCount) {
        //  ((MainActivity)getContext()).getActionMode().getMenu().getItem(0).setVisible(itemsCount == 1);
        if (actionMode != null) {
            actionMode.setTitle(itemsCount);
            actionMode.invalidate();
        }
    }


    private void deleteSelectedItems() {
        endSelectionMode();
        DialogDeleteProgress dialogDeleteProgress = new DialogDeleteProgress(this, mediaAdapter.getSelectedItems(), this);
        dialogDeleteProgress.show();


    }

    private void loadMediaFiles() {
        mediaFiles.clear();
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
        Collections.sort(mediaFiles, (file1, file2) -> {
            long k = file1.lastModified() - file2.lastModified();
            if(k > 0){
                return 1;
            }else if(k == 0){
                return 0;
            }else{
                return -1;
            }
        });
    }

    private void initUI() {
        setupMediaList();
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
    public void onSelectionModeEnabled(boolean enabled) {

    }

    @Override
    public void notifySelectionMode(int selectedItemsCount) {
        updateActionMode(selectedItemsCount);
    }

    @Override
    public void onDeleteCompleted() {
        loadMediaFiles();
        setupMediaList();
    }
}