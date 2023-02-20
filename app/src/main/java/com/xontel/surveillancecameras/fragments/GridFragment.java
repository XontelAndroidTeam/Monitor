package com.xontel.surveillancecameras.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.Observable;
import androidx.lifecycle.ViewModelProvider;

import com.xontel.surveillancecameras.activities.HomeActivity;
import com.xontel.surveillancecameras.base.BaseFragment;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.databinding.FragmentGridBinding;
import com.xontel.surveillancecameras.hikvision.HIKPlayer;
import com.xontel.surveillancecameras.hikvision.HikCamView;
import com.xontel.surveillancecameras.viewModels.GridViewModel;
import com.xontel.surveillancecameras.viewModels.MainViewModel;
import com.xontel.surveillancecameras.viewModels.ViewModelProviderFactory;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class GridFragment extends BaseFragment {
    public static final String TAG = GridFragment.class.getSimpleName();
    public static final String INDEX = "index";
    private FragmentGridBinding binding;
    private MainViewModel viewModel;
    private GridViewModel mGridViewModel;
    private List<HIKPlayer> mHIKPlayers = new ArrayList<>();

    private int gridCount ;
    private int pageIndex ;


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


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentGridBinding.inflate(inflater);
        viewModel = new ViewModelProvider(requireActivity(), viewModelProviderFactory).get(MainViewModel.class);
        mGridViewModel = new ViewModelProvider(requireActivity(), viewModelProviderFactory).get(GridViewModel.class);
        mGridViewModel.setMainViewModel(viewModel);
        pageIndex = getArguments().getInt(INDEX);
        if (getArguments() != null) {
            mGridViewModel.setIndex(pageIndex);
        }
        gridCount = viewModel.mGridObservable.getValue();
        setUpGrid();
        createPlayers();
        return binding.getRoot();
    }
    @Override
    protected void setUp(View view) {
        setupObservables();
    }

    @Override
    public void onResume() {
        super.onResume();
        playAll();
    }

    private void playAll() {
        for(int i = 0 ; i < mHIKPlayers.size(); i++){
            mHIKPlayers.get(i).attachView((HikCamView) binding.grid.getChildAt(i));
        }
    }

    private void stopAll() {
        for(int i = 0 ; i < mHIKPlayers.size(); i++){
            mHIKPlayers.get(i).stopLiveView();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopAll();
        setUpGrid();
    }

    private void createPlayers() {
        int children = binding.grid.getChildCount();
        List<IpCam> ipCams = viewModel.ipCams.getValue();
        for(int i = 0 ; i <children  ; i ++){
            int camIndex = (pageIndex * gridCount) +i;
            if(camIndex<ipCams.size()) {
                HIKPlayer hikPlayer = new HIKPlayer(getContext(), ipCams.get(camIndex));
                mHIKPlayers.add(hikPlayer);
            }

        }

    }



    private void createPlayer() {

    }

    private void setUpGrid() {
        binding.grid.removeAllViews();
        int gridCount = viewModel.mGridObservable.getValue();
        int rowCount = (int) Math.sqrt(gridCount);
        binding.grid.setColumnCount(rowCount);
        binding.grid.setRowCount(rowCount);
        addViews(gridCount);
    }

    private void setupObservables() {
        viewModel.mGridObservable.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                Log.v(TAG, "reloaded");
               rebind(gridCount,pageIndex);
               gridCount = viewModel.mGridObservable.getValue();
               pageIndex = mGridViewModel.getIndex();

            }
        });
    }

    private void rebind(int oldGrid, int oldPage) {
        removeUnNecessaryViews(oldGrid, oldPage);
        addTheRestOfView(oldGrid, oldPage);
    }

    private void addTheRestOfView(int oldGrid, int oldPage) {
        List<IpCam> ipCams = viewModel.ipCams.getValue();
        for(int i = 0 ; i <gridCount  ; i ++){
            int camIndex = (pageIndex * gridCount) +i;
            int oldRangeStart = (oldGrid * oldPage);
            int oldRangeEnd = (oldGrid * oldPage) + oldGrid;
            boolean wasInOldRange = oldRangeStart <= camIndex && camIndex < oldRangeEnd ;
            if(!wasInOldRange) {
               addViews(1);
                if (camIndex < ipCams.size()) {
                    HIKPlayer hikPlayer = new HIKPlayer(getContext(), ipCams.get(camIndex));
                    hikPlayer.attachView((HikCamView) binding.grid.getChildAt(i));
                    mHIKPlayers.add(hikPlayer);
                }
            }

        }
    }

    private void removeUnNecessaryViews(int oldGrid, int oldPage) {
        for(int i = 0 ; i < binding.grid.getChildCount() ; i++){
            boolean isNotInTheNewRange = ((oldGrid * oldPage) + i) - (pageIndex * gridCount) < 0;
            if(isNotInTheNewRange){
                binding.grid.removeViewAt(i);
            }
                GridLayout.LayoutParams param = new GridLayout.LayoutParams(GridLayout.spec(
                        GridLayout.UNDEFINED, GridLayout.FILL, 1f),
                        GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f));
                param.height = 0;
                param.width = 0;
                binding.grid.getChildAt(i).setLayoutParams(param);

            }
        int rowCount = (int) Math.sqrt(gridCount);
            binding.grid.setColumnCount(rowCount);
            binding.grid.setRowCount(rowCount);

    }


    private void setupDagger() {
        ((HomeActivity) requireActivity()).getActivityComponent().inject(this);
    }




    private void drawGrid(int gridCount) {
        int count = (int) Math.sqrt(gridCount);
        int childCount = binding.grid.getChildCount();

        if (gridCount > childCount) {
            binding.grid.setColumnCount(count);
            binding.grid.setRowCount(count);
            addViews(gridCount);
        } else {
            binding.grid.removeViews(gridCount, binding.grid.getChildCount() - gridCount);
            for (int i = 0; i < binding.grid.getChildCount(); i++) {
                GridLayout.LayoutParams param = new GridLayout.LayoutParams(GridLayout.spec(
                        GridLayout.UNDEFINED, GridLayout.FILL, 1f),
                        GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f));
                param.height = 0;
                param.width = 0;
                binding.grid.getChildAt(i).setLayoutParams(param);

            }
            binding.grid.setColumnCount(count);
            binding.grid.setRowCount(count);
        }

    }


    private void addViews(int gridCount) {
        for (int i = 0; i < gridCount; i++) {
            View playerView = createCamView();
            GridLayout.LayoutParams param = new GridLayout.LayoutParams(GridLayout.spec(
                    GridLayout.UNDEFINED, GridLayout.FILL, 1f),
                    GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f));

            param.height = 0;
            param.width = 0;
            binding.grid.addView(playerView, param);
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
    public void onDestroy() {
        super.onDestroy();
    }
}
