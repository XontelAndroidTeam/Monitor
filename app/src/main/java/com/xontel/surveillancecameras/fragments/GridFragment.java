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
import androidx.lifecycle.Observer;
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
import java.util.List;

import javax.inject.Inject;

public class GridFragment extends BaseFragment implements CamPlayerView.ClickListener {
    public static final String TAG = GridFragment.class.getSimpleName();
    public static final String INDEX = "index";
    private FragmentGridBinding binding;
    private MainViewModel viewModel;
    private GridViewModel mGridViewModel;

    private List<CamPlayer> mPlayers = new ArrayList<>();
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
        setupPlayers();
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
                CamPlayer camPlayer;
                if (cam.getType() == CamDeviceType.HIKVISION.getValue()) {
                    camPlayer = new HIKPlayer(getContext());
                } else {
                    camPlayer = new DahuaPlayer(getContext());
                }
                mPlayers.add(camPlayer);
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "onResume " + pageIndex);
        playAll();


    }

    private void playAll() {
        List<IpCam> ipCams = viewModel.ipCams.getValue();
        for (int i = 0; i < gridCount; i++) {
            int camIndex = (pageIndex * gridCount) + i;
            if (camIndex < ipCams.size()) {
                IpCam cam = ipCams.get(camIndex);
                mPlayers.get(i).attachView((CamPlayerView) binding.grid.getChildAt(i), cam);
            }
        }
    }


    private void stopAll() {
        for (int i = 0; i < gridCount; i++) {
            CamPlayer camPlayer;
            List<IpCam> cams = viewModel.ipCams.getValue();
            int camIndex = (pageIndex * gridCount) + i;
            if (camIndex < cams.size()) {
                IpCam curr = cams.get(camIndex);
                if (mPlayers.get(i).getIpCam() != null) {
                    mPlayers.get(i).stopLiveView();
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
        int newGridCount = viewModel.mGridObservable.getValue();
        return (int) Math.floor((pageIndex * 1.0 * gridCount) / newGridCount);
    }


    private void setupObservables() {
        mGridViewModel.gridChanged.observe(getViewLifecycleOwner(), changed -> {
            int newGrid = viewModel.mGridObservable.getValue();
            if (changed && newGrid != gridCount) {
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


        viewModel.takeSnapShot.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean takeSnapshot) {
                if (takeSnapshot && viewModel.mGridObservable.getValue() == 1) {
                    mPlayers.get(0).takeSnapshot();
                }
            }
        });

        viewModel.recordVideo.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean record) {
                if (viewModel.mGridObservable.getValue() == 1 && isResumed()) {
                    if (record) {
                        boolean r = mPlayers.get(0).recordVideo();
                        viewModel.isRecording.setValue(r);
                    }
                    else {
                        viewModel.isRecording.setValue(false);
                        boolean r = mPlayers.get(0).stopRecordingVideo();
                    }
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
        int marginInPixels = (int) CommonUtils.convertDpToPixel(2, getContext());
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
//        int firstElementCamIndexInOldRange = (oldPage * oldGrid);
//        int firstElementCamIndexInNewRange = (pageIndex * gridCount);
//        int gap = Math.abs(firstElementCamIndexInNewRange - firstElementCamIndexInOldRange);
//        Collections.rotate(viewModel.getHikPlayers(), gap);
        for (int i = 0; i < gridCount; i++) {
            int camIndex = (pageIndex * gridCount) + i;
            int oldRangeStart = (oldGrid * oldPage);
            int oldRangeEnd = (oldGrid * oldPage) + oldGrid;
            boolean wasInOldRange = oldRangeStart <= camIndex && camIndex < oldRangeEnd;
            if (!wasInOldRange) {
                addNewView(i);
                if (camIndex < ipCams.size()) {
//                    viewModel.getHikPlayers().get(i).attachView((CamPlayerView) binding.grid.getChildAt(i), ipCams.get(camIndex));
                    IpCam curr = ipCams.get(camIndex);
                    CamPlayer camPlayer;
                    if (curr.getType() == CamDeviceType.HIKVISION.getValue()) {
                        camPlayer = new HIKPlayer(getContext());
                    } else {
                        camPlayer = new DahuaPlayer(getContext());
                    }

                    camPlayer.attachView((CamPlayerView) binding.grid.getChildAt(i), curr);
                    mPlayers.add(camPlayer);
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
                    mPlayers.get(i).stopLiveView();
                    mPlayers.remove(i);
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
    public void onViewClicked(boolean isAttachedToPlayer, CamPlayerView camPlayerView) {
        if (isAttachedToPlayer) {
            int viewPosition = binding.grid.indexOfChild(camPlayerView);
            removeAllExcept(viewPosition);
            pageIndex = pageIndex * gridCount + viewPosition;
            gridCount = 1;
            for (int i = 0; i < binding.grid.getChildCount(); i++) {
                binding.grid.getChildAt(i).setLayoutParams(getViewLayoutParams());
            }
            binding.grid.setColumnCount(1);
            binding.grid.setRowCount(1);
            viewModel.mGridObservable.setGridCount("1");
        } else {
            Bundle bundle = new Bundle();
            bundle.putInt(DevicesFragment.KEY_VIEW_TYPE, -1);
            NavHostFragment.findNavController(this).navigate(R.id.action_monitorFragment_to_deviceFragment, bundle);
        }
    }

    private void removeAllExcept(int indexOfChild) {
        int childCount = binding.grid.getChildCount();
        for (int i = childCount - 1; i >= 0; i--) {
            if (i != indexOfChild) {
                Log.v(TAG, "index : " + i);
                int camIndex = gridCount * pageIndex + i;
                List<IpCam> cams = viewModel.ipCams.getValue();
                if (camIndex < cams.size()) {
                    mPlayers.get(i).stopLiveView();
                    mPlayers.remove(i);
                }
                binding.grid.removeViewAt(i);
            }

        }
    }
}
