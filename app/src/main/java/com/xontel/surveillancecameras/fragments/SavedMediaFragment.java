package com.xontel.surveillancecameras.fragments;

import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Environment;
import android.os.FileObserver;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ActionMode;
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
import com.xontel.surveillancecameras.utils.MediaData;
import com.xontel.surveillancecameras.utils.SDCardObservable;
import com.xontel.surveillancecameras.utils.StorageBroadcastReceiver;
import com.xontel.surveillancecameras.utils.StorageHelper;

import org.videolan.libvlc.Media;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class SavedMediaFragment extends BaseFragment implements MediaAdapter.ClickActionListener {
    private FragmentSavedMediaBinding binding ;
    private MediaAdapter mediaAdapter;
    private List<MediaData> mediaFiles = new ArrayList<>();
    private android.view.ActionMode actionMode;
    private ProgressDialog progressDialog;
    List<Uri> collection;
    String[] projection = new String[] {
            MediaStore.MediaColumns.RELATIVE_PATH,
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DURATION,
            MediaStore.Images.Media.SIZE};

    String[] projectionVideo = new String[] {
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE};

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


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.selection_menu, menu);
        menu.findItem(R.id.action_select_all).setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
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
/*
    private void shareSelectedItems(List<MediaData> selectedFiles) {
        if(selectedFiles.size() > 0) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND_MULTIPLE);
            intent.putExtra(Intent.EXTRA_SUBJECT, "Here are some files.");
            intent.setType("image/jpeg");
            ArrayList<Uri> files = new ArrayList<Uri>();
            for (MediaData file : selectedFiles) {
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

 */

    public void updateActionMode() {
        //  ((MainActivity)getContext()).getActionMode().getMenu().getItem(0).setVisible(itemsCount == 1);
        if (actionMode != null) {
            actionMode.setTitle(mediaAdapter.getSelectedItems().size() + "");
            actionMode.invalidate();
        }
    }


    private void getAllPics(){
        collection = StorageHelper.getContentUris(requireContext(),true);

        for (Uri uri:collection){
            try(Cursor cursor = requireContext().getContentResolver().query(
                    uri,
                    projection,
                    null,
                    null,
                    null
            ))  {
                // Cache column indices.
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DURATION);
                int relativePath = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.RELATIVE_PATH);
                int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);
                int data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                while (cursor.moveToNext()) {
                    // Get values of columns for a given video.
                    long id = cursor.getLong(idColumn);
                    String name = cursor.getString(nameColumn);
                    int duration = cursor.getInt(durationColumn);
                    int size = cursor.getInt(sizeColumn);
                    String dataPath = cursor.getString(data);
                    Uri contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                    if (dataPath.contains("/Pictures")){
                        Log.i("TATZ", "getAllPics: "+dataPath);
                        mediaFiles.add(new MediaData(name,size,duration,null,dataPath));
                    }
                }
            }catch(Exception e){
                Log.e("TAG", "error: " + e.getMessage());
            }
        }
        mediaAdapter.setAllData(mediaFiles);
        getAllVideos();
    }

    private void getAllVideos(){
        collection = StorageHelper.getContentUris(requireContext(),false);

        for (Uri uri:collection) {
            try (Cursor cursor = requireContext().getContentResolver().query(
                    uri,
                    projectionVideo,
                    null,
                    null,
                    null
            )) {
                // Cache column indices.
                int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
                int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
                int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
                int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);
                int data = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);

                while (cursor.moveToNext()) {
                    // Get values of columns for a given video.
                    long id = cursor.getLong(idColumn);
                    String name = cursor.getString(nameColumn);
                    int duration = cursor.getInt(durationColumn);
                    int size = cursor.getInt(sizeColumn);
                    String dataPath = cursor.getString(data);
                    Uri contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                    if (dataPath.contains("/Movies")){
                        mediaFiles.add(new MediaData(name, size, duration, dataPath, null));
                    }
                }
            } catch (Exception e) {
                Log.e("TAG", "error: " + e.getMessage());
            }
        }
        mediaAdapter.setAllData(mediaFiles);
    }

/*
    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(requireContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

 */

    private void deleteDataFromMediaStore(String dataPath){
        requireActivity().getContentResolver().delete(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI ,
                MediaStore.Images.Media.DATA + " = ?",
                new String[] { dataPath });
    }



    public void startSelectionMode() {
        if (actionMode == null) {
            actionMode = requireActivity().startActionMode(new ActionMode.Callback() {
                @Override
                public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                    MenuInflater inflater = requireActivity().getMenuInflater();
                    inflater.inflate(R.menu.selection_menu, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode actionMode, MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.action_delete:
                            deleteSelectedItems(mediaAdapter.getSelectedItems());
                            endSelectionMode();
                            return true;
                        case R.id.action_share:
                          //  shareSelectedItems(mediaAdapter.getSelectedItems());
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



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            default:
                startSelectionMode();
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMax(100); // Progress Dialog Max Value
        progressDialog.setTitle(R.string.deleting); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL); // Progress Dialog Style Horizontal
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void setProgressDialogValues(int value){
        progressDialog.setProgress(value);
    }
    private void hideProgressDialogValues(){
        progressDialog.dismiss();
    }

    private void deleteSelectedItems(List<MediaData> selectedFiles) {
        if(selectedFiles.size() > 0 ) {
            setupProgressDialog();
            for (int i = 0; i < selectedFiles.size(); i++) {
                setProgressDialogValues(i);
                if (!CommonUtils.deleteFile(new File(selectedFiles.get(i).getImagePath() == null || selectedFiles.get(i).getImagePath().isEmpty()  ? selectedFiles.get(i).getVideoPath() : selectedFiles.get(i).getImagePath()  ))) {
                    Toast.makeText(getContext(), R.string.file_delete_error + selectedFiles.get(i).getName(), Toast.LENGTH_LONG).show();
                }
            }
            hideProgressDialogValues();
            selectedFiles.clear();
            onDeleteCompleted();
        }else{
            showMessage(R.string.nothing_selected);
        }
    }

    public void onDeleteCompleted() {
        mediaFiles.clear();
        getAllPics();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        requireActivity().setTitle(R.string.saved_media);
        binding = FragmentSavedMediaBinding.inflate(inflater);
        return binding.getRoot();
    }



    @Override
    protected void setUp(View view) {
        setupMediaList();
        getAllPics();
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
        updateActionMode();
    }
}