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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
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
import com.xontel.surveillancecameras.adapters.MediaAdapter;
import com.xontel.surveillancecameras.base.BaseFragment;
import com.xontel.surveillancecameras.databinding.FragmentSavedMediaBinding;
import com.xontel.surveillancecameras.utils.CommonUtils;
import com.xontel.surveillancecameras.utils.MediaData;
import com.xontel.surveillancecameras.utils.MediaUtils;
import com.xontel.surveillancecameras.utils.StorageHelper;
import com.xontel.surveillancecameras.viewModels.MediaViewModel;
import com.xontel.surveillancecameras.viewModels.ViewModelProviderFactory;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;


public class SavedMediaFragment extends BaseFragment implements MediaAdapter.ClickActionListener {
    public static final String TAG = SavedMediaFragment.class.getSimpleName();
    private FragmentSavedMediaBinding binding ;
    private MediaAdapter mediaAdapter;
    private android.view.ActionMode actionMode;


    private MediaViewModel mMediaViewModel;

    @Inject
    ViewModelProviderFactory providerFactory;


    public SavedMediaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
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
        getFragmentComponent().inject(this);
        mMediaViewModel = new ViewModelProvider(this, providerFactory).get(MediaViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        requireActivity().setTitle(R.string.saved_media);
        binding = FragmentSavedMediaBinding.inflate(inflater);
        return binding.getRoot();
    }



    @Override
    protected void setUp(View view) {
                mMediaViewModel.getAllAppMedia();
        setupMediaList();

        mMediaViewModel.getLoading().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean loading) {
                if(loading){
                    showLoading();
                }else{
                    hideLoading();
                }
            }
        });
        mMediaViewModel.media.observe(getViewLifecycleOwner(), new Observer<List<MediaData>>() {
            @Override
            public void onChanged(List<MediaData> mediaData) {
                mediaAdapter.setAllData(mediaData);
            }
        });


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



    private void deleteSelectedItems(List<MediaData> selectedFiles) {
        if(selectedFiles.size() > 0 ) {
//            setupProgressDialog();
            for (int i = 0; i < selectedFiles.size(); i++) {
//                setProgressDialogValues(i);
                if (!CommonUtils.deleteFile(new File(selectedFiles.get(i).getMediaPath() == null || selectedFiles.get(i).getMediaPath().isEmpty()  ? selectedFiles.get(i).getMediaPath() : selectedFiles.get(i).getMediaPath()  ))) {
                    Toast.makeText(getContext(), R.string.file_delete_error + selectedFiles.get(i).getName(), Toast.LENGTH_LONG).show();
                }
            }
//            hideProgressDialogValues();
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



  


    @Override
    public void onStop() {
        super.onStop();
    }
}