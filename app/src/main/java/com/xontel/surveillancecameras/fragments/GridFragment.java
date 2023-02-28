package com.xontel.surveillancecameras.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.Observable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.xontel.surveillancecameras.activities.HomeActivity;
import com.xontel.surveillancecameras.base.BaseFragment;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.databinding.FragmentGridBinding;
import com.xontel.surveillancecameras.hikvision.HIKPlayer;
import com.xontel.surveillancecameras.hikvision.HikCamView;
import com.xontel.surveillancecameras.utils.CommonUtils;
import com.xontel.surveillancecameras.viewModels.GridViewModel;
import com.xontel.surveillancecameras.viewModels.MainViewModel;
import com.xontel.surveillancecameras.viewModels.ViewModelProviderFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class GridFragment extends BaseFragment {
    public static final String TAG = GridFragment.class.getSimpleName();
    public static final String INDEX = "index";
    private FragmentGridBinding binding;
    private MainViewModel viewModel;
    private GridViewModel mGridViewModel;


    private int gridCount;
    private int pageIndex;


    @Inject
    ViewModelProviderFactory viewModelProviderFactory;


    public GridFragment() {
    }

    public static GridFragment newInstance(int index) {
        GridFragment fragment = new GridFragment();
        Bundle args = new Bundle();
        args.putInt(INDEX, index);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(null);
        setupDagger();
        Log.v(TAG, "onCreate " + pageIndex);


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGridBinding.inflate(inflater);
        viewModel = new ViewModelProvider(requireActivity(), viewModelProviderFactory).get(MainViewModel.class);
        mGridViewModel = new ViewModelProvider(this, viewModelProviderFactory).get(GridViewModel.class);
        pageIndex = getArguments().getInt(INDEX);
        mGridViewModel.setMainViewModel(viewModel);
        gridCount = viewModel.mGridObservable.getValue();
//        setUpGrid();
        Log.v(TAG, "onCreateView " + pageIndex);
        return binding.getRoot();
    }

    private void setUpGrid() {
        int gridCount = viewModel.mGridObservable.getValue();
        int rowCount = (int) Math.sqrt(gridCount);
        binding.grid.setColumnCount(rowCount);
        binding.grid.setRowCount(rowCount);
        addViews(gridCount);
    }


    @Override
    protected void setUp(View view) {
        setupObservables();
    }

    private void setupPlayers() {
        Log.v(TAG, "setupPlayers "+pageIndex);
        List<IpCam> ipCams = viewModel.ipCams.getValue();
        for (int i = 0; i < gridCount; i++) {
            int camIndex = (pageIndex * gridCount) + i;
            if (camIndex < ipCams.size()) {
                Log.v(TAG, "loooop");
                viewModel.getPlayers().get(i).setIpCam(ipCams.get(camIndex));
                viewModel.getPlayers().get(i).attachView((HikCamView) binding.grid.getChildAt(i));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume " + pageIndex);
//        setupPlayers();

    }


    private void stopAll() {
        for (int i = 0; i < gridCount; i++) {
            HIKPlayer hikPlayer = viewModel.getPlayers().get(i);
            if (hikPlayer.getIpCam() != null) {
                hikPlayer.stopLiveView();
                Log.v(TAG, "stooped " + i);
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "onPause " + pageIndex);
//        stopAll();
    }


    private void addViews(int count) {
        for (int i = 0; i < count; i++) {
            addNewView(null);
        }
    }

    public int calculateNewIndex() {
        return (int) Math.floor((pageIndex * 1.0 * gridCount) / viewModel.mGridObservable.getValue());
    }


    private void setupObservables() {
        mGridViewModel.gridChanged.observe(getViewLifecycleOwner(), changed -> {
            if (changed) {
//                int oldGridCount = gridCount;
//                int oldPageIndex = pageIndex;
//                pageIndex = calculateNewIndex();
//                gridCount = viewModel.mGridObservable.getValue();
//                if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
//                    rebind(oldGridCount, oldPageIndex);
//                } else {
//                    notifyGridChanged();
//                }
            }
        });
    }

    private void notifyGridChanged() {
        int childrenCount = binding.grid.getChildCount();
        int requiredItemsCount = gridCount;
        if(requiredItemsCount > childrenCount){
            addViews(requiredItemsCount - childrenCount);
        }else{
            removeViews(childrenCount - requiredItemsCount);
        }
    }

    private void removeViews(int count) {
        for (int i = 0; i < count; i++) {
            binding.grid.removeViewAt(i);
        }
    }

    private void rebind(int oldGrid, int oldPage) {
        Log.e(TAG, "rebind");
        int count = (int) Math.sqrt(gridCount);
        removeUnNecessaryViews(oldGrid, oldPage);
        addTheRestOfViews(oldGrid, oldPage);
        for (int i = 0; i < binding.grid.getChildCount(); i++) {

            binding.grid.getChildAt(i).setLayoutParams(getViewLayoutParams());

        }
        binding.grid.setColumnCount(count);
        binding.grid.setRowCount(count);

    }


    private GridLayout.LayoutParams getViewLayoutParams() {
        int marginInPixels = (int) CommonUtils.convertDpToPixel(4, getContext());
        GridLayout.LayoutParams param = new GridLayout.LayoutParams(GridLayout.spec(
                GridLayout.UNDEFINED, GridLayout.FILL, 1f),
                GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f));
        param.leftMargin = marginInPixels;
        param.rightMargin = marginInPixels;
        param.topMargin = marginInPixels;
        param.bottomMargin = marginInPixels;
        param.height = 0;
        param.width = 0;

        return param;
    }

    private void addTheRestOfViews(int oldGrid, int oldPage) {
        List<IpCam> ipCams = viewModel.ipCams.getValue();
        int firstElementCamIndexInOldRange = (oldPage * oldGrid);
        int firstElementCamIndexInNewRange = (pageIndex * gridCount);
        int gap = Math.abs(firstElementCamIndexInNewRange - firstElementCamIndexInOldRange);
        Collections.rotate(viewModel.getPlayers(), gap);
        for (int i = 0; i < gridCount; i++) {
            int camIndex = (pageIndex * gridCount) + i;
            int oldRangeStart = (oldGrid * oldPage);
            int oldRangeEnd = (oldGrid * oldPage) + oldGrid;
            boolean wasInOldRange = oldRangeStart <= camIndex && camIndex < oldRangeEnd;

            if (!wasInOldRange) {
                addNewView(i);
                if (camIndex < ipCams.size()) {
                    viewModel.getPlayers().get(i).setIpCam(ipCams.get(camIndex));
                    viewModel.getPlayers().get(i).attachView((HikCamView) binding.grid.getChildAt(i));
//                    HIKPlayer hikPlayer = new HIKPlayer(getContext(), ipCams.get(camIndex));
//                    hikPlayer.attachView((HikCamView) binding.grid.getChildAt(i));
//                    mHIKPlayers.add(hikPlayer);
                }
            }

        }
    }

    private void removeUnNecessaryViews(int oldGrid, int oldPage) {
        int childCount = binding.grid.getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            boolean isInTheNewRange = ((oldGrid * oldPage) + i) - (pageIndex * gridCount) < gridCount;
            if (!isInTheNewRange) {
                Log.v(TAG, "index : " + i);
                viewModel.getPlayers().get(i).stopLiveView();
                binding.grid.removeViewAt(i);
            }
        }


    }


    private void setupDagger() {
        ((HomeActivity) requireActivity()).getActivityComponent().inject(this);
    }


//    private void drawGrid(int gridCount) {
//        int count = (int) Math.sqrt(gridCount);
//        int childCount = binding.grid.getChildCount();
//
//        if (gridCount > childCount) {
//            binding.grid.setColumnCount(count);
//            binding.grid.setRowCount(count);
////            addViews(gridCount);
//        } else {
//            binding.grid.removeViews(gridCount, binding.grid.getChildCount() - gridCount);
//            for (int i = 0; i < binding.grid.getChildCount(); i++) {
//                binding.grid.getChildAt(i).setLayoutParams(getViewLayoutParams());
//
//            }
//            binding.grid.setColumnCount(count);
//            binding.grid.setRowCount(count);
//        }
//
//    }


    public void addNewView(Integer index) {
        View playerView = createCamView();

        if (index != null) {
            binding.grid.addView(playerView, index, getViewLayoutParams());
        } else {
            binding.grid.addView(playerView, getViewLayoutParams());
        }
    }

    //
    private View createCamView() {
        return new HikCamView(requireContext());

    }


    private void navigateToCamPreview(IpCam ipCam) {
//        Intent intent = new Intent(requireContext(), CamerasActivity.class);
//        intent.putExtra(CamsAdapter.KEY_CAMERAS, ipCam);
//        requireContext().startActivity(intent);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(TAG, "onStop " + pageIndex);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v(TAG, "onStart " + pageIndex);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy " + pageIndex);
    }
}
