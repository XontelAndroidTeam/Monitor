package com.xontel.surveillancecameras.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.xontel.surveillancecameras.activities.HomeActivity;
import com.xontel.surveillancecameras.adapters.GridAdapter;
import com.xontel.surveillancecameras.dahua.DahuaCamView;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.databinding.FragmentPagerBinding;
import com.xontel.surveillancecameras.hikvision.HikCamView;
import com.xontel.surveillancecameras.utils.CamDeviceType;
import com.xontel.surveillancecameras.utils.CamViewEmpty;
import com.xontel.surveillancecameras.utils.PlayerView;
import com.xontel.surveillancecameras.viewModels.MainViewModel;
import com.xontel.surveillancecameras.viewModels.ViewModelProviderFactory;
import com.xontel.surveillancecameras.vlc.VlcCamView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

public class PagerFragment extends Fragment  {
    private FragmentPagerBinding binding;
    private List<IpCam> data = new ArrayList<>();
    private MainViewModel viewModel;
    private GridAdapter gridAdapter;
    private int index;
    private boolean isInitialized = false;
    @Inject
    ViewModelProviderFactory viewModelProviderFactory;


    public PagerFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(null);
       // Log.i("TATZ", "onCreatePager: "+index);
        setupDagger();
        Bundle args = getArguments();
        if (args != null){
            index = args.getInt("IN");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initialization(inflater);
        setupAdapter();
        observers();
        isInitialized = true;
        return  binding.getRoot();
    }

    private void setupDagger() {
         ((HomeActivity)requireActivity()).getActivityComponent().inject(this);
    }

    private void initialization(LayoutInflater inflater) {
        data = new ArrayList<>();
        binding = FragmentPagerBinding.inflate(inflater);
        viewModel = new ViewModelProvider(requireActivity(), viewModelProviderFactory).get(MainViewModel.class);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
    }

    private void setupAdapter() {
        if (!isInitialized){ drawGrid(viewModel.gridCount.getValue());}
    }




    private void observers() {
        viewModel.refreshData.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean){
                if (index == viewModel.pagerCount.getValue()-1){
                    viewModel.refreshData.setValue(false);
                }
            }
        });

        viewModel.refreshPagerGridCount.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean){
                drawGrid(viewModel.gridCount.getValue());
                if (index == viewModel.pagerCount.getValue()-1){
                    viewModel.refreshPagerGridCount.setValue(false);
                }
            }
        });
    }



    private void drawGrid(int gridCount){
        int count = (int)Math.sqrt(gridCount) ;
        int childCount = binding.grid.getChildCount();

        if(gridCount > childCount){
            binding.grid.setColumnCount(count);
            binding.grid.setRowCount(count);
            addViews(gridCount);
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


    private void addViews(int size) {
        View playerView;
        for (int i = binding.grid.getChildCount(); i < size; i++) {
            if ( i < viewModel.ipCams.getValue().size()){
                IpCam ipCam = Objects.requireNonNull(viewModel.ipCams.getValue()).get(i);
                 playerView =  createCamView(ipCam.getType(),ipCam);
            }else{
                 playerView = new CamViewEmpty(requireContext());
            }
            GridLayout.LayoutParams param = new GridLayout.LayoutParams(GridLayout.spec(
                    GridLayout.UNDEFINED, GridLayout.FILL, 1f),
                    GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f));

            param.height = 0;
            param.width = 0;
            binding.grid.addView(playerView,param);
        }
    }

    private View createCamView(int type,IpCam ipCam) {
        if (CamDeviceType.HIKVISION.getValue() == type) {
            return new HikCamView(requireContext(), ipCam);
        }

        else if (CamDeviceType.DAHUA.getValue() == type) {
            return new DahuaCamView(requireContext(),ipCam);
        }

        else if (CamDeviceType.OTHER.getValue() == type) {
            return new VlcCamView(requireContext(),ipCam);
        }

        return null;
    }

}
