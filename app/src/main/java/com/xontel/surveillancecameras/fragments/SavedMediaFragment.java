package com.xontel.surveillancecameras.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ActionMode;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.adapters.MediaAdapter;
import com.xontel.surveillancecameras.base.BaseFragment;
import com.xontel.surveillancecameras.databinding.FragmentSavedMediaBinding;
import com.xontel.surveillancecameras.utils.CommonUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class SavedMediaFragment extends BaseFragment implements MediaAdapter.ClickActionListener {
    private FragmentSavedMediaBinding binding ;
    private MediaAdapter mediaAdapter;
    private List<File> mediaFiles = new ArrayList<>();
    private ActionMode actionMode;
    private ProgressDialog progressDialog;


    public SavedMediaFragment() {
        // Required empty public constructor
    }



    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        endSelectionMode();
        super.onPause();
    }

    public void endSelectionMode() {
        if (actionMode != null) {
            actionMode.finish();
        }
    }

    public static SavedMediaFragment newInstance(String param1, String param2) {
        SavedMediaFragment fragment = new SavedMediaFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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

    private void loadMediaFiles() {
        mediaFiles.clear();
        String imagesDirPath = "/media/images";
        String videosDirPath = "/media/videos";
        File[] externalStorageDirs = getContext().getExternalFilesDirs(null);
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


//    public void startSelectionMode() {
//        if (actionMode == null) {
//            actionMode = requireActivity().startActionMode(new ActionMode.Callback() {
//                @Override
//                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//                    MenuInflater inflater = requireActivity().getMenuInflater();
//                    inflater.inflate(R.menu.selection_menu, menu);
//                    return true;
//                }
//
//                @Override
//                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//                    return false;
//                }
//
//                @Override
//                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//                    switch (item.getItemId()) {
//                        case R.id.action_delete:
//                            deleteSelectedItems(mediaAdapter.getSelectedItems());
//                            endSelectionMode();
//                            return true;
//                        case R.id.action_share:
//                            shareSelectedItems(mediaAdapter.getSelectedItems());
//                            endSelectionMode();
//                            return true;
//                        case R.id.action_select_all:
//                            mediaAdapter.selectAll();
//                            return true;
//                    }
//                    return false;
//                }
//
//                @Override
//                public void onDestroyActionMode(ActionMode mode) {
//                    actionMode = null;
//                    mediaAdapter.enableSelectionMode(false);
//                }
//            });
//            actionMode.setTitle(mediaAdapter.getSelectedItems().size() + "");
//            mediaAdapter.enableSelectionMode(true);
//        }
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_delete:
            case R.id.action_share:
            default:
//                startSelectionMode();
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMax(100); // Progress Dialog Max Value
        progressDialog.setTitle(R.string.deleting); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); // Progress Dialog Style Horizontal
        progressDialog.setCancelable(false);
    }

    private void deleteSelectedItems(List<File> selectedFiles) {
        if(selectedFiles.size() > 0 ) {
            for (int i = 0; i < selectedFiles.size(); i++) {
                if (!CommonUtils.deleteFile(selectedFiles.get(i))) {
                    Toast.makeText(getContext(), R.string.file_delete_error + selectedFiles.get(i).getName(), Toast.LENGTH_LONG).show();
                }
            }
            selectedFiles.clear();
            onDeleteCompleted();
        }else{
            showMessage(R.string.nothing_selected);
        }


    }

    public void onDeleteCompleted() {
        loadMediaFiles();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().setTitle(R.string.saved_media);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSavedMediaBinding.inflate(inflater);
        return binding.getRoot();
    }


    @Override
    protected void setUp(View view) {
        setupMediaList();
    }

    private void setupMediaList() {
        mediaAdapter = new MediaAdapter(getContext(), new ArrayList<>(), this);
        binding.rvMedia.setEmptyView(binding.llEmptyIndicator);
        binding.rvMedia.setAdapter(mediaAdapter);
        binding.rvMedia.setLayoutManager(new GridLayoutManager(getContext(), 4));
    }

    @Override
    public void onSelectionModeEnabled(boolean enabled) {

    }

    @Override
    public void notifySelectionMode() {

    }
}