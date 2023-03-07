package com.xontel.surveillancecameras.fragments;

import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Environment;
import android.provider.ContactsContract;
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
import com.xontel.surveillancecameras.activities.HomeActivity;
import com.xontel.surveillancecameras.adapters.MediaAdapter;
import com.xontel.surveillancecameras.base.BaseFragment;
import com.xontel.surveillancecameras.databinding.FragmentSavedMediaBinding;
import com.xontel.surveillancecameras.utils.CommonUtils;
import com.xontel.surveillancecameras.utils.MediaData;
import com.xontel.surveillancecameras.utils.MediaUtils;
import com.xontel.surveillancecameras.utils.StorageHelper;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class SavedMediaFragment extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>, MediaAdapter.ClickActionListener {
    public static final String TAG = SavedMediaFragment.class.getSimpleName();
    private FragmentSavedMediaBinding binding ;
    private MediaAdapter mediaAdapter;
    private List<MediaData> images = new ArrayList<>();
    private List<MediaData> videos = new ArrayList<>();
    private android.view.ActionMode actionMode;
    private ProgressDialog progressDialog;
    public static final int IMAGES_LOADER = 0;
    public static final int VIDEOS_LOADER = 1;
    public static final String[] projection = new String[] {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DURATION,
            MediaStore.Images.Media.SIZE};


    public SavedMediaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        showLoading();
        LoaderManager.getInstance(this).initLoader(IMAGES_LOADER, null, this);
        LoaderManager.getInstance(this).initLoader(VIDEOS_LOADER, null, this);

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



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ((HomeActivity) requireActivity()).getSupportActionBar().show();
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

  
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        switch (id) {
            case IMAGES_LOADER:
                return new CursorLoader(
                        getContext(),
                       MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        projection,
                        MediaStore.Images.Media.VOLUME_NAME + " IN "+createSelectionForVolumes() +" AND "+ MediaStore.Images.Media.RELATIVE_PATH+"=?",
                        createSelectionArgs(Environment.DIRECTORY_PICTURES),
                        null
                );
            case VIDEOS_LOADER:
                return new CursorLoader(
                        getContext(),
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        projection,
                        MediaStore.Images.Media.VOLUME_NAME + " IN "+createSelectionForVolumes()+" AND " + MediaStore.Images.Media.RELATIVE_PATH+"=?",
                        createSelectionArgs(Environment.DIRECTORY_MOVIES),
                        null
                );
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()){
            case IMAGES_LOADER:
                images.clear();
                images.addAll(MediaUtils.extractMedia(data, Environment.DIRECTORY_PICTURES));
                notifyDataChanged();
                hideLoading();
                break;
            case VIDEOS_LOADER:
                videos.clear();
                videos.addAll(MediaUtils.extractMedia(data, Environment.DIRECTORY_MOVIES));
                notifyDataChanged();
                break;
        }
    }

    private void notifyDataChanged() {
       List<MediaData> allMedia = new ArrayList<>();
       allMedia.addAll(images);
       allMedia.addAll(videos);
       mediaAdapter.setAllData(allMedia);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    private void shareSelectedItems(List<MediaData> selectedFiles) {
        if(selectedFiles.size() > 0) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND_MULTIPLE);
            intent.putExtra(Intent.EXTRA_SUBJECT, "Here are some files.");
            intent.setType("image/jpeg");
            ArrayList<Uri> files = new ArrayList<Uri>();
            for (MediaData file : selectedFiles) {
                files.add(file.getMediaUri());
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

                    for(int i = 0; i < menu.size(); i++){
                        Drawable drawable = menu.getItem(i).getIcon();
                        if(drawable != null) {
                            drawable.mutate();
                            drawable.setColorFilter(getResources().getColor(R.color.white_color), PorterDuff.Mode.SRC_ATOP);
                        }
                    }

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
                            shareSelectedItems(mediaAdapter.getSelectedItems());
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
                if (!CommonUtils.deleteFile(new File(selectedFiles.get(i).getMediaPath() == null || selectedFiles.get(i).getMediaPath().isEmpty()  ? selectedFiles.get(i).getMediaPath() : selectedFiles.get(i).getMediaPath()  ))) {
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
//        mediaFiles.clear();
//        getAllPics();
    }

  
    private String createSelectionForVolumes(){
        Set<String> volumesNames = MediaStore.getExternalVolumeNames(getContext());
        String selection = "(";
        for(String name : volumesNames){
            selection+="?,";
        }

        selection = selection.substring(0, selection.length() - 1);
         selection+= ")";
         Log.v(TAG, selection);
         return selection;
    }

  
    private String[] createSelectionArgs(String mediaType){
        int i = 0;
        Set<String> volumesNames = MediaStore.getExternalVolumeNames(getContext());
        String[] selectionArgs = new String[volumesNames.size()+1];
        for(String name : volumesNames){
            selectionArgs[i++] = name;
        }
        selectionArgs[selectionArgs.length - 1] = mediaType + StorageHelper.APP_MEDIA_DIRECTORY_PATH;
        for(String s: selectionArgs) {
            Log.v(TAG, s);
        }
        return selectionArgs;
    }

    @Override
    public void onStop() {
        super.onStop();
        LoaderManager.getInstance(this).destroyLoader(IMAGES_LOADER);
        LoaderManager.getInstance(this).destroyLoader(VIDEOS_LOADER);
    }
}