package com.xontel.surveillancecameras.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.Observable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.xontel.surveillancecameras.activities.CamerasActivity;
import com.xontel.surveillancecameras.activities.HomeActivity;
import com.xontel.surveillancecameras.adapters.CamsAdapter;
import com.xontel.surveillancecameras.base.BaseFragment;
import com.xontel.surveillancecameras.dahua.DahuaCamView;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.databinding.FragmentGridBinding;
import com.xontel.surveillancecameras.hikvision.HikCamView;
import com.xontel.surveillancecameras.utils.CamDeviceType;
import com.xontel.surveillancecameras.utils.CamViewEmpty;
import com.xontel.surveillancecameras.viewModels.GridViewModel;
import com.xontel.surveillancecameras.viewModels.MainViewModel;
import com.xontel.surveillancecameras.viewModels.ViewModelProviderFactory;
import com.xontel.surveillancecameras.vlc.VlcCamView;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.inject.Inject;

public class GridFragment extends BaseFragment {
    public static final String INDEX = "index";
    private FragmentGridBinding binding;
    private MainViewModel viewModel;
    private GridViewModel mGridViewModel ;


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

    public int getPageIndex(){
        return mGridViewModel.getIndex();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(null);
        setupDagger();

    }

    @Override
    protected void setUp(View view) {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initialization(inflater);
        return  binding.getRoot();
    }

    private void setupDagger() {
         ((HomeActivity)requireActivity()).getActivityComponent().inject(this);
    }

    private void initialization(LayoutInflater inflater) {
        binding = FragmentGridBinding.inflate(inflater);
        viewModel = new ViewModelProvider(requireActivity(), viewModelProviderFactory).get(MainViewModel.class);
        mGridViewModel = new ViewModelProvider(requireActivity(), viewModelProviderFactory).get(GridViewModel.class);
        mGridViewModel.setMainViewModel(viewModel);
        if (getArguments() != null) {
            mGridViewModel.setIndex(getArguments().getInt(INDEX));
        }

        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
    }





    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void drawGrid(int gridCount){
        int count = (int)Math.sqrt(gridCount) ;
        int childCount = binding.grid.getChildCount();

        if(gridCount > childCount){
            binding.grid.setColumnCount(count);
            binding.grid.setRowCount(count);
            addViews();
        }else{
            binding.grid.removeViews(gridCount, binding.grid.getChildCount() - gridCount );
            for(int i = 0 ; i < binding.grid.getChildCount() ; i++){
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


    private void addViews() {
        View playerView;
        for (int i = from + binding.grid.getChildCount(); i < to; i++) {
                 playerView =  createCamView();
            GridLayout.LayoutParams param = new GridLayout.LayoutParams(GridLayout.spec(
                    GridLayout.UNDEFINED, GridLayout.FILL, 1f),
                    GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f));

            param.height = 0;
            param.width = 0;
            binding.grid.addView(playerView,param);
        }
    }

    private View createCamView() {
            return new HikCamView(requireContext());

    }



    private void navigateToCamPreview(IpCam ipCam){
//        Intent intent = new Intent(requireContext(), CamerasActivity.class);
//        intent.putExtra(CamsAdapter.KEY_CAMERAS, ipCam);
//        requireContext().startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
