package com.xontel.surveillancecameras.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import com.xontel.surveillancecameras.activities.HomeActivity;
import com.xontel.surveillancecameras.adapters.CamsAdapter;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.databinding.FragmentPagerBinding;
import com.xontel.surveillancecameras.viewModels.MainViewModel;
import com.xontel.surveillancecameras.viewModels.ViewModelProviderFactory;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class PagerFragment extends Fragment  {
    private FragmentPagerBinding binding;
    private MainViewModel viewModel;
    private CamsAdapter camsAdapter;
    private List<IpCam> data = new ArrayList<>();
    private int index;
    private boolean isInitialized = false;
    private int rangeFrom,rangeTo;
    @Inject
    ViewModelProviderFactory viewModelProviderFactory;


    public PagerFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(null);
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
        rangeFrom = index * viewModel.gridCount.getValue();
        rangeTo = rangeFrom + (viewModel.gridCount.getValue() - 1);
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());
    }

    private void setupAdapter() {
        binding.pagerRecycler.setLayoutManager(new GridLayoutManager(getActivity(), (int) Math.sqrt(viewModel.gridCount.getValue())));
        if (camsAdapter == null){camsAdapter = new CamsAdapter(new ArrayList<>(), viewModel.gridCount.getValue(), getContext());}
        binding.pagerRecycler.setAdapter(camsAdapter);
        if (!isInitialized){updateData();}
    }

    private void updateData() {
        Log.i("TATZ", "updateData: ");
        if (data != null && !data.isEmpty() ){data.clear();}
        for (int i = rangeFrom ; i <= rangeTo ; i ++){
            if (viewModel.ipCams.getValue() != null && !viewModel.ipCams.getValue().isEmpty() &&  viewModel.ipCams.getValue().size() > i ) {
                data.add(viewModel.ipCams.getValue().get(i));
            }
        }
        camsAdapter.addItems(data);
    }

    private void whenGridChanged(){
        rangeFrom = index * viewModel.gridCount.getValue();
        rangeTo = rangeFrom + (viewModel.gridCount.getValue() - 1);
        updateData();
    }

    private void observers() {
        viewModel.refreshData.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean){updateData();viewModel.refreshData.setValue(false);}
        });
        viewModel.refreshGridCount.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean){whenGridChanged();viewModel.refreshGridCount.setValue(false);}
        });
    }

}
