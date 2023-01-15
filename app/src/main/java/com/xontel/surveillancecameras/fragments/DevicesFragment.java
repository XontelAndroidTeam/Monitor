package com.xontel.surveillancecameras.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.adapters.DevicesAdapter;
import com.xontel.surveillancecameras.base.BaseFragment;
import com.xontel.surveillancecameras.databinding.FragmentDevicesBinding;

import java.util.ArrayList;


public class DevicesFragment extends BaseFragment implements DevicesAdapter.ClickListener {
    private FragmentDevicesBinding binding;
    private DevicesAdapter mDevicesAdapter ;

    public DevicesFragment() {
        // Required empty public constructor
    }

    
    public static DevicesFragment newInstance(String param1, String param2) {
        DevicesFragment fragment = new DevicesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().setTitle(R.string.devices);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
         inflater.inflate(R.menu.main_menu, menu);
    }



    @Override
    protected void setUp(View view) {
        setupDevicesList();
    }

    private void setupDevicesList() {
        mDevicesAdapter = new DevicesAdapter(getContext(), new ArrayList<>(), this);
        binding.rvDevices.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvDevices.setEmptyView(binding.noDevices.getRoot());
        binding.rvDevices.setRoot(binding.root);
        binding.rvDevices.setAdapter(mDevicesAdapter);
        mDevicesAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDevicesBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onItemClicked(int position) {
        
    }
}