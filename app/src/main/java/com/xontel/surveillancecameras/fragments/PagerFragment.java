package com.xontel.surveillancecameras.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import com.xontel.surveillancecameras.activities.HomeActivity;
import com.xontel.surveillancecameras.adapters.RecyclerViewPagerAdapter;
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
    private RecyclerViewPagerAdapter recyclerViewPagerAdapter;
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
        Log.i("TATZ", "initialization: "+recyclerViewPagerAdapter);
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
        if (recyclerViewPagerAdapter == null){recyclerViewPagerAdapter = new RecyclerViewPagerAdapter();}
        binding.pagerRecycler.setAdapter(recyclerViewPagerAdapter);
        if (!isInitialized){updateData();}
    }

    private void updateData() {
        for (int i = rangeFrom ; i <= rangeTo ; i ++){
            if (viewModel.ipCams.getValue() != null && !viewModel.ipCams.getValue().isEmpty() &&  viewModel.ipCams.getValue().size() > i ){
                data.add(viewModel.ipCams.getValue().get(i));
            }else{
                data.add(new IpCam(-1,-1,-1,-1));
            }
        }
        recyclerViewPagerAdapter.setItemList(data);
    }

    private void observers() {

    }

}
