package com.xontel.surveillancecameras.fragments;

import android.content.Intent;
import android.hardware.camera2.CameraDevice;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.activities.AddNewDeviceActivity;
import com.xontel.surveillancecameras.adapters.DevicesAdapter;
import com.xontel.surveillancecameras.base.BaseFragment;
import com.xontel.surveillancecameras.data.db.model.CamDevice;
import com.xontel.surveillancecameras.databinding.FragmentDevicesBinding;
import com.xontel.surveillancecameras.presenters.MainDeviceMvpPresenter;
import com.xontel.surveillancecameras.presenters.MainDeviceMvpView;
import com.xontel.surveillancecameras.utils.CamDeviceType;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class DevicesFragment extends BaseFragment implements MainDeviceMvpView, DevicesAdapter.ClickListener, AdapterView.OnItemClickListener {
    private FragmentDevicesBinding binding;
    private DevicesAdapter mDevicesAdapter ;
    private int deviceType = CamDeviceType.OTHER.getValue();
    @Inject
    MainDeviceMvpPresenter<MainDeviceMvpView> mPresenter ;

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
        getFragmentComponent().inject(this);
        mPresenter.onAttach(this);
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
    }

    private void setSelectedData(CamDevice data){
        String[] types = getResources().getStringArray(R.array.device_type);
        binding.etName.setText(data.getName());
        binding.dropDown.slideShowFilter.setText(types[data.getType()], false);
        if (data.getType() == CamDeviceType.OTHER.getValue()){
          binding.camFields.etUrl.setText(data.getUrl());
        }else{
            binding.deviceFields.etIp.setText(data.getIp());
            binding.deviceFields.etUsername.setText(data.getUserName());
            binding.deviceFields.etPassword.setText(data.getPassword());
        }
    }

    private void setupDeviceTypeDropDown(){
        String[] types = getResources().getStringArray(R.array.device_type);
        ArrayAdapter typesDropDownAdapter = new ArrayAdapter<String>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, types
        );
        binding.dropDown.slideShowFilter.setAdapter(typesDropDownAdapter);
        binding.dropDown.slideShowFilter.setText(types[CamDeviceType.OTHER.getValue()], false);
        binding.dropDown.slideShowFilter.setOnItemClickListener(this);
        typesDropDownAdapter.notifyDataSetChanged();
        binding.setLifecycleOwner(this);
    }

    private void refreshView() {
        boolean isCam = deviceType == CamDeviceType.OTHER.getValue() ;
        binding.camFields.getRoot().setVisibility(isCam ? View.VISIBLE : View.GONE );
        binding.deviceFields.getRoot().setVisibility(!isCam ? View.VISIBLE : View.GONE );
    }

    private void setupDevicesList() {
        mDevicesAdapter = new DevicesAdapter(getContext(), new ArrayList<>(), this);
        binding.rvDevices.setLayoutManager(new LinearLayoutManager(getContext()));
//        binding.rvDevices.setEmptyView(binding.noDevices.getRoot());
        binding.rvDevices.setRoot(binding.root);
        binding.rvDevices.setAdapter(mDevicesAdapter);
        mDevicesAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDevicesBinding.inflate(inflater);

        setupDevicesList();

        setupDeviceTypeDropDown();

        binding.addDevice.setOnClickListener(view -> requireActivity().startActivity(new Intent(requireContext(),AddNewDeviceActivity.class)));


        binding.btnUpdate.setOnClickListener(view -> updateCurrentData());

        mPresenter.getAllDevices();

        return binding.getRoot();
    }


    private void updateCurrentData(){
        String deviceName = binding.etName.getText().toString();
        String url = binding.camFields.etUrl.getText().toString();
        String ip = binding.deviceFields.etIp.getText().toString();
        String userName = binding.deviceFields.etUsername.getText().toString();
        String password = binding.deviceFields.etPassword.getText().toString();
        mPresenter.updateDevice(new CamDevice(deviceName,userName,password,ip,deviceType,url ,null));
    }


    @Override
    public void onInsertingDevice() {}

    @Override
    public void onUpdatingDevice() {}

    @Override
    public void onDeletingDevice() {}

    @Override
    public void onGettingDevice(CamDevice response) {}

    @Override
    public void onGettingAllDevices(List<CamDevice> response) {
        Log.i("TAGGGG", "onGettingAllDevices: ");
        if (!response.isEmpty() ){
            mDevicesAdapter.setList(response);
            setSelectedData(response.get(0));
            deviceType = response.get(0).getType() ;
            refreshView();
        }
    }

    @Override
    public void onItemClicked(CamDevice data) {
        deviceType = data.getType() ;
        refreshView();
        setSelectedData(data);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        deviceType = i;
        refreshView();
    }
}