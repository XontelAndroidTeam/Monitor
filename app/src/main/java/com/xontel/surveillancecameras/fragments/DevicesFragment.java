package com.xontel.surveillancecameras.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.activities.AddNewDeviceActivity;
import com.xontel.surveillancecameras.adapters.DevicesAdapter;
import com.xontel.surveillancecameras.base.BaseFragment;
import com.xontel.surveillancecameras.databinding.FragmentDevicesBinding;
import com.xontel.surveillancecameras.hikvision.CamDevice;
import com.xontel.surveillancecameras.presenters.MainDeviceMvpPresenter;
import com.xontel.surveillancecameras.presenters.MainDeviceMvpView;
import com.xontel.surveillancecameras.utils.CamDeviceType;
import com.xontel.surveillancecameras.utils.DataFormMode;
import com.xontel.surveillancecameras.viewModels.MainViewModel;
import com.xontel.surveillancecameras.viewModels.ViewModelProviderFactory;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class DevicesFragment extends BaseFragment implements DevicesAdapter.ClickListener {
    private FragmentDevicesBinding binding;
    private DevicesAdapter mDevicesAdapter ;
    private MainViewModel mMainViewModel;
    @Inject
    ViewModelProviderFactory providerFactory;

    public DevicesFragment() {
        // Required empty public constructor
    }

    
    public static DevicesFragment newInstance() {
        DevicesFragment fragment = new DevicesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentComponent().inject(this);
        setHasOptionsMenu(true);
        mMainViewModel = new ViewModelProvider(requireActivity(), providerFactory).get(MainViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        requireActivity().setTitle(R.string.devices);
        binding = FragmentDevicesBinding.inflate(inflater);



        binding.setLifecycleOwner(this);


        
        binding.addDevice.getRoot().setOnClickListener(view -> {
            enableCreationMode();
        });

        binding.btnUpdate.setOnClickListener(view -> updateCurrentData());

        binding.btnDelete.setOnClickListener(view -> deleteCurrentData());

        return binding.getRoot();
    }
    private void setUpObservables() {
        mMainViewModel.camDevices.observe(getViewLifecycleOwner(), camDevices -> {
            mDevicesAdapter.addItems(camDevices);
        });
    }



    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:{
                requireActivity().startActivity(new Intent(requireContext(),AddNewDeviceActivity.class));
                return true;}
            default: return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void setUp(View view) {
        setupDevicesList();
        setUpObservables();
        setupDeviceTypeDropDown();
    }

    private void setupDeviceTypeDropDown(){
        String[] types = getResources().getStringArray(R.array.device_type);
        ArrayAdapter typesDropDownAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, types
        );
        binding.dropDown.slideShowFilter.setAdapter(typesDropDownAdapter);
        binding.dropDown.slideShowFilter.setOnItemClickListener((adapterView, view, position, id) -> {
            refreshView(CamDeviceType.OTHER.getValue() != position);
        });
        typesDropDownAdapter.notifyDataSetChanged();
        binding.setLifecycleOwner(this);
    }

    private void setSelectedData(CamDevice data){
        binding.etName.setText(data.getName());
//        binding.dropDown.slideShowFilter.setText(CamDeviceType.values(), false);
        if (data.getDeviceType() == CamDeviceType.OTHER.getValue()){
            binding.camFields.etUrl.setText(data.getUrl());
        }else{
            binding.deviceFields.etIp.setText(data.getIpAddress());
            binding.deviceFields.etUsername.setText(data.getUserName());
            binding.deviceFields.etPassword.setText(data.getPassWord());
        }
    }


    private void refreshView(boolean isHikOrDah) {
        binding.camFields.getRoot().setVisibility(!isHikOrDah ? View.VISIBLE : View.GONE );
        binding.deviceFields.getRoot().setVisibility(isHikOrDah ? View.VISIBLE : View.GONE );
    }

    private void setupDevicesList() {
        mDevicesAdapter = new DevicesAdapter(getContext(), mMainViewModel.camDevices.getValue(), this);
        binding.rvDevices.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvDevices.setEmptyView(binding.noDevices.getRoot());
        binding.rvDevices.setRoot(binding.root);
        binding.rvDevices.setAdapter(mDevicesAdapter);
    }

    private void enableCreationMode() {
        mDevicesAdapter.setCurrentSelectedItem(DevicesAdapter.NO_SELECTION);
        setAddLayoutSelected(true);
        notifyDataFormModeChanged(DataFormMode.CREATE);
    }

    private void enableReadMode() {
        setAddLayoutSelected(false);
        notifyDataFormModeChanged(DataFormMode.READ);
    }
    private void enableEditMode() {
        notifyDataFormModeChanged(DataFormMode.EDIT);
    }

    private void notifyDataFormModeChanged(DataFormMode dataFormMode) {
        switch (dataFormMode) {
            case EDIT:

                break;
            case CREATE:
                flushAllFields();
                break;
                
            case READ:
                lockAllFields();
                break;
        }
    }

    private void flushAllFields() {
        setAllFieldsEnabled(true);
        setAllFieldsEmpty();
        binding.btnSave.setVisibility(View.VISIBLE);
        binding.btnDelete.setVisibility(View.GONE);
        binding.btnUpdate.setVisibility(View.GONE);
        binding.dropDown.getRoot().setEnabled(true);
    }

    private void setAllFieldsEmpty() {
        binding.camFields.etUrl.setText("");
        binding.etName.setText("");
        binding.deviceFields.etPassword.setText("");
        binding.deviceFields.etUsername.setText("");
        binding.deviceFields.etIp.setText("");
        binding.deviceFields.etDescription.setText("");
    }

    private void setAllFieldsEnabled(boolean enabled) {
        binding.nameInputLayout.setEnabled(enabled);
        binding.camFields.urlInputLayout.setEnabled(enabled);
        binding.deviceFields.passwordInputLayout.setEnabled(enabled);
        binding.deviceFields.usernameInputLayout.setEnabled(enabled);
        binding.deviceFields.ipInputLayout.setEnabled(enabled);
        binding.deviceFields.descriptionInputLayout.setEnabled(enabled);
    }

    private void lockAllFields() {
        setAllFieldsEnabled(false);
        binding.btnSave.setVisibility(View.GONE);
        binding.btnDelete.setVisibility(View.VISIBLE);
        binding.btnUpdate.setVisibility(View.VISIBLE);
        binding.dropDown.getRoot().setEnabled(false);
    }


    private void setAddLayoutSelected(boolean selected) {
        binding.addDevice.getRoot().setBackgroundColor(ContextCompat.getColor(getContext(),selected ? R.color.accent_color : R.color.white_color));
        binding.addDevice.tvTitle.setTextColor(ContextCompat.getColor(getContext(),selected ? R.color.white_color : R.color.black_color));
        binding.addDevice.ivCam.setColorFilter(ContextCompat.getColor(getContext(),selected ? R.color.white_color : R.color.grey_color));
    }


    private void updateCurrentData(){
        String deviceName = binding.etName.getText().toString();
        String url = binding.camFields.etUrl.getText().toString();
        String ip = binding.deviceFields.etIp.getText().toString();
        String userName = binding.deviceFields.etUsername.getText().toString();
        String password = binding.deviceFields.etPassword.getText().toString();
//        mPresenter.updateDevice(new CamDevice(currentSelectedData.getId(),deviceName,userName,password,ip,deviceType,url));
    }

    private void deleteCurrentData(){
        showDeleteDialog();
    }

    private void showDeleteDialog() {
        new MaterialAlertDialogBuilder(getContext(), R.style.AlertDialogTheme)
                .setTitle(R.string.delete_camera)
                .setMessage(R.string.are_you_sure_delete)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> mMainViewModel.deleteDevice(mDevicesAdapter.getDeviceList().get(mDevicesAdapter.getSelectedItemPosition())))
                .setNegativeButton(android.R.string.no, null)
                .show();
    }


    public void onInsertingDevice() {}


    public void onUpdatingDevice() {}


    public void onDeletingDevice() {
//        currentSelectedItemIndex = 0;
//        mDevicesAdapter.setCurrentSelectedItem();
    }


//    public void onGettingAllDevices(List<CamDevice> response) {
//            setSelectedData(response.get(currentSelectedItemIndex));
//            deviceType = response.get(currentSelectedItemIndex).getDeviceType() ;
//            refreshView();
//    }

    @Override
    public void onItemClicked(CamDevice camDevice) {
        if(camDevice != null) {
            enableReadMode();
            refreshView(camDevice.deviceType != CamDeviceType.OTHER.getValue());
            setSelectedData(camDevice);
        }
    }


}