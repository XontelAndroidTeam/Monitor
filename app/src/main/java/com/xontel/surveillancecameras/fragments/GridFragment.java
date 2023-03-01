package com.xontel.surveillancecameras.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.activities.HomeActivity;
import com.xontel.surveillancecameras.base.BaseFragment;
import com.xontel.surveillancecameras.dahua.DahuaPlayer;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.databinding.FragmentGridBinding;
import com.xontel.surveillancecameras.hikvision.HIKPlayer;
import com.xontel.surveillancecameras.hikvision.CamPlayerView;
import com.xontel.surveillancecameras.utils.CamDeviceType;
import com.xontel.surveillancecameras.utils.CamPlayer;
import com.xontel.surveillancecameras.utils.CommonUtils;
import com.xontel.surveillancecameras.viewModels.GridViewModel;
import com.xontel.surveillancecameras.viewModels.MainViewModel;
import com.xontel.surveillancecameras.viewModels.ViewModelProviderFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class GridFragment extends BaseFragment implements CamPlayerView.ClickListener {
    public static final String TAG = GridFragment.class.getSimpleName();
    public static final String INDEX = "index";
    private FragmentGridBinding binding;
    private MainViewModel viewModel;
    private GridViewModel mGridViewModel;

    private List<HIKPlayer> mHIKPlayers = new ArrayList<>();
    private List<DahuaPlayer> mDahuaPlayers = new ArrayList<>();
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
        setUpGrid();
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
        List<IpCam> ipCams = viewModel.ipCams.getValue();
        for (int i = 0; i < gridCount; i++) {
            int camIndex = (pageIndex * gridCount) + i;
            if (camIndex < ipCams.size()) {
                IpCam cam = ipCams.get(camIndex);
                if (cam.getType() == CamDeviceType.HIKVISION.getValue()) {
                    HIKPlayer hikPlayer;
                    if(i < mHIKPlayers.size()){
                        hikPlayer = mHIKPlayers.get(i);
                    }else {
                       hikPlayer = new HIKPlayer(getContext());
                       mHIKPlayers.add(hikPlayer);
                    }
                    hikPlayer.attachView((CamPlayerView) binding.grid.getChildAt(i), cam);
//                    viewModel.getHikPlayers().get(i).attachView((CamPlayerView) binding.grid.getChildAt(i), cam);
                } else {
                    DahuaPlayer dahuaPlayer;
                    if(i < mDahuaPlayers.size()){
                        dahuaPlayer = mDahuaPlayers.get(i);
                    }else {
                        dahuaPlayer = new DahuaPlayer(getContext());
                        mDahuaPlayers.add(dahuaPlayer);
                    }
                    dahuaPlayer.attachView((CamPlayerView) binding.grid.getChildAt(i), cam);
//                    viewModel.getDahPlayers().get(i).attachView((CamPlayerView) binding.grid.getChildAt(i), cam);
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume " + pageIndex);
        setupPlayers();

    }


    private void stopAll() {
        for (int i = 0; i < gridCount; i++) {
            CamPlayer camPlayer;
            List<IpCam> cams = viewModel.ipCams.getValue();
            int camIndex = (pageIndex * gridCount) + i;
            if (camIndex < cams.size()) {
                IpCam curr = cams.get(camIndex);
                if (curr.getType() == CamDeviceType.HIKVISION.getValue()) {
                       camPlayer = mHIKPlayers.get(i);
//                    camPlayer = viewModel.getHikPlayers().get(i);
                } else {
                    camPlayer = mDahuaPlayers.get(i);
//                    camPlayer = viewModel.getDahPlayers().get(i);
                }
                if (camPlayer.getIpCam() != null) {
                    camPlayer.stopLiveView();
                }
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG, "onPause " + pageIndex);
        stopAll();
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
                int oldGridCount = gridCount;
                int oldPageIndex = pageIndex;
                pageIndex = calculateNewIndex();
                gridCount = viewModel.mGridObservable.getValue();
                if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
                    rebind(oldGridCount, oldPageIndex);
                } else {
                    notifyGridChanged();
                }
            }
        });
    }

    private void notifyGridChanged() {
        int childrenCount = binding.grid.getChildCount();
        int requiredItemsCount = gridCount;
        if (requiredItemsCount > childrenCount) {
            addViews(requiredItemsCount - childrenCount);
        } else {
            removeViews(childrenCount - requiredItemsCount);
        }
    }

    private void removeViews(int count) {
        for (int i = 0; i < count; i++) {
            binding.grid.removeViewAt(i);
        }
    }

    private void rebind(int oldGrid, int oldPage) {
        int count = (int) Math.sqrt(gridCount);
        removeUnNecessaryViews(oldGrid, oldPage);
        for (int i = 0; i < binding.grid.getChildCount(); i++) {
            binding.grid.getChildAt(i).setLayoutParams(getViewLayoutParams());
        }
        addTheRestOfViews(oldGrid, oldPage);

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
        Collections.rotate(viewModel.getHikPlayers(), gap);
        for (int i = 0; i < gridCount; i++) {
            int camIndex = (pageIndex * gridCount) + i;
            int oldRangeStart = (oldGrid * oldPage);
            int oldRangeEnd = (oldGrid * oldPage) + oldGrid;
            boolean wasInOldRange = oldRangeStart <= camIndex && camIndex < oldRangeEnd;

            if (!wasInOldRange) {
                addNewView(i);
                if (camIndex < ipCams.size()) {
                    viewModel.getHikPlayers().get(i).attachView((CamPlayerView) binding.grid.getChildAt(i), ipCams.get(camIndex));
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
                int camIndex = oldGrid * oldPage + i;
                List<IpCam> cams = viewModel.ipCams.getValue();
                if (camIndex < cams.size()) {
                    IpCam curr = cams.get(camIndex);
                    if (curr.getType() == CamDeviceType.HIKVISION.getValue()) {
                        mHIKPlayers.get(i).stopLiveView();
                        mHIKPlayers.remove(i);
//                        viewModel.getHikPlayers().get(i).stopLiveView();
                    } else {
                        mDahuaPlayers.get(i).stopLiveView();
                        mDahuaPlayers.remove(i);
//                        viewModel.getDahPlayers().get(i).stopLiveView();
                    }
                }
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
        return new CamPlayerView(requireContext(), this);
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

    @Override
    public void onViewClicked(boolean isAttachedToPlayer) {
        if(!isAttachedToPlayer){
                NavHostFragment.findNavController(this).navigate(R.id.action_monitorFragment_to_deviceFragment);

        }
    }
}
