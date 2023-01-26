package com.xontel.surveillancecameras.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.view.ActionMode;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.adapters.MediaAdapter;
import com.xontel.surveillancecameras.base.BaseActivity;
import com.xontel.surveillancecameras.databinding.ActivitySavedMediaBinding;
import com.xontel.surveillancecameras.utils.CommonUtils;
import com.xontel.surveillancecameras.utils.SDCardObservable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class SavedMediaActivity extends BaseActivity implements Observer, MediaAdapter.ClickActionListener/*, DialogDeleteProgress.ClickAction*/ {
    private ActivitySavedMediaBinding binding;
    private List<File> mediaFiles = new ArrayList<>();
    private MediaAdapter mediaAdapter;
    private ActionMode actionMode;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_saved_media);
        SDCardObservable.getInstance().addObserver(this);
        setUp();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.selection_menu, menu);
        menu.findItem(R.id.action_select_all).setVisible(false);
        return true;
    }

    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMax(100); // Progress Dialog Max Value
        progressDialog.setTitle(R.string.deleting); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); // Progress Dialog Style Horizontal
        progressDialog.setCancelable(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            default:
                startSelectionMode();
                return super.onOptionsItemSelected(item);
        }
    }


        @Override
        protected void onResume() {
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
            actionMode =  startSupportActionMode(new ActionMode.Callback() {
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
                          //  deleteSelectedItems(mediaAdapter.getSelectedItems());
                            endSelectionMode();
                            return true;
                        case R.id.action_share:
                           // shareSelectedItems(mediaAdapter.getSelectedItems());
                            endSelectionMode();
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
            actionMode.setTitle(mediaAdapter.getSelectedItems().size() + "");
            mediaAdapter.enableSelectionMode(true);
        }
    }

    private void shareSelectedItems(List<File> selectedFiles) {
        if(selectedFiles.size() > 0) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND_MULTIPLE);
            intent.putExtra(Intent.EXTRA_SUBJECT, "Here are some files.");
            intent.setType("image/jpeg"); /* This example is sharing jpeg images. */
            ArrayList<Uri> files = new ArrayList<Uri>();
            for (File file : selectedFiles) {
                Uri uri = Uri.fromFile(file);
                files.add(uri);
            }
            selectedFiles.clear();
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, files);
            startActivity(intent);
        }else{
            showMessage(R.string.nothing_selected);
        }

    }

    public void updateActionMode() {
        //  ((MainActivity)getContext()).getActionMode().getMenu().getItem(0).setVisible(itemsCount == 1);
        if (actionMode != null) {
            actionMode.setTitle(mediaAdapter.getSelectedItems().size() + "");
            actionMode.invalidate();
        }
    }


    private void deleteSelectedItems(List<File> selectedFiles) {
        if(selectedFiles.size() > 0 ) {
            for (int i = 0; i < selectedFiles.size(); i++) {
                if (!CommonUtils.deleteFile(selectedFiles.get(i))) {
                    Toast.makeText(this, R.string.file_delete_error + selectedFiles.get(i).getName(), Toast.LENGTH_LONG).show();
                }
            }
            selectedFiles.clear();
            onDeleteCompleted();
        }else{
            showMessage(R.string.nothing_selected);
        }


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
            if (k > 0) {
                return 1;
            } else if (k == 0) {
                return 0;
            } else {
                return -1;
            }
        });

        mediaAdapter.notifyDataSetChanged();
    }

    private void initUI() {
        setupMediaList();
        loadMediaFiles();
    }

    private void setupMediaList() {
       // mediaAdapter = new MediaAdapter(this, mediaFiles, this);
        binding.rvMedia.setEmptyView(binding.llEmptyIndicator);
        binding.rvMedia.setAdapter(mediaAdapter)    ;
        binding.rvMedia.setLayoutManager(new GridLayoutManager(this, 4));
    }


    @Override
    protected void setUp() {
        super.setUp();
    }


    @Override
    public void onSelectionModeEnabled(boolean enabled) {

    }

    @Override
    public void notifySelectionMode() {
        updateActionMode();
    }


    public void onDeleteCompleted() {
        loadMediaFiles();
    }

    @Override
    public void update(Observable o, Object arg) {
        loadMediaFiles();
    }
}