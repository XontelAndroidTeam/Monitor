package com.xontel.surveillancecameras.fragments;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.adapters.DevicesAdapter;
import com.xontel.surveillancecameras.base.BaseFragment;
import com.xontel.surveillancecameras.databinding.FragmentDevicesBinding;
import com.xontel.surveillancecameras.data.db.model.CamDevice;
import com.xontel.surveillancecameras.utils.CamDeviceType;
import com.xontel.surveillancecameras.utils.DataFormMode;
import com.xontel.surveillancecameras.viewModels.MainViewModel;
import com.xontel.surveillancecameras.viewModels.ViewModelProviderFactory;

import javax.inject.Inject;


public class DevicesFragment extends BaseFragment implements DevicesAdapter.ClickListener  {
    private FragmentDevicesBinding binding;
    private DevicesAdapter mDevicesAdapter;
    private MainViewModel mMainViewModel;

    private DataFormMode mode ;
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
            mDevicesAdapter.setCurrentSelectedItem(DevicesAdapter.NO_SELECTION);
        });
        binding.btnUpdate.setOnClickListener(view -> updateCurrentData());
        binding.btnDelete.setOnClickListener(view -> deleteCurrentData());

        return binding.getRoot();
    }

    private void setUpObservables() {
        mMainViewModel.getLoading().observe(getViewLifecycleOwner(), loading -> {
            if(loading)
            showLoading();
            else hideLoading();
        });
        mMainViewModel.camDevices.observe(getViewLifecycleOwner(), camDevices -> {
            mDevicesAdapter.addItems(camDevices);
        });
        mMainViewModel.reloader.observe(getViewLifecycleOwner(), reload -> {
            if(reload){
                enableReadMode();
            }
        });
    }


    @Override
    protected void setUp(View view) {
        setupDevicesList();
        setUpObservables();
        setupDeviceTypeDropDown();
        binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enableEditMode();
            }
        });
        binding.btnSave.setOnClickListener(v -> {
            if (validateFields()) {
                int deviceType = binding.dropDown.spinner.getSelectedItemPosition();
                String deviceName = binding.etName.getText().toString();
                String ip = binding.deviceFields.etDomain.getText().toString();
                String userName = binding.deviceFields.etUsername.getText().toString();
                String password = binding.deviceFields.etPassword.getText().toString();

                CamDevice camDevice = CamDeviceType.HIKVISION.getValue() == deviceType ?
                        new HikDevice(deviceName, userName, password, ip) :
                        new DahuaDevice(deviceName, userName, password, ip);
                if(mode.equals(DataFormMode.CREATE)) {
                    mMainViewModel.createDevice(camDevice);
                    mDevicesAdapter.setCurrentSelectedItem(0);
                }else if(mode.equals(DataFormMode.EDIT)){
                    camDevice.setId(mDevicesAdapter.getSelectedDevice().getId());
                    mMainViewModel.updateDevice(camDevice);
                }
            }else {
                showMessage(R.string.one_or_more_invalid_fields);
            }
        });
    }

    private boolean validateFields() {
        boolean isDropDownChoiceValid =  /*CamDeviceType.getTypeFromString(binding.dropDown.slideShowFilter.getText().toString()) != -1 */true;
        if(!isDropDownChoiceValid){
            showMessage(R.string.wrong_device_type);
            return false;
        }
         boolean areFieldsValid =
                 binding.etName.isValid() &&
                binding.deviceFields.etDomain.isValid() &&
                binding.deviceFields.etUsername.isValid() &&
                binding.deviceFields.etPassword.isValid() ;

        return areFieldsValid;

    }

    private void setupDeviceTypeDropDown() {
        String[] types = getResources().getStringArray(R.array.device_type);
        ArrayAdapter typesDropDownAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_dropdown_item, types
        );
        binding.dropDown.spinner.setAdapter(typesDropDownAdapter);
        binding.setLifecycleOwner(this);
    }

    private void bindCamDevice(CamDevice data) {
        binding.llStatus.setVisibility(View.VISIBLE);
        binding.channels.setText(data.getChannels()+"");
        binding.status.setColorFilter(ContextCompat.getColor(requireContext(), data.isLoggedIn() ? R.color.green_color : R.color.red_color));
        binding.dropDown.spinner.setSelection(data.deviceType);
        binding.etName.setText(data.getName());
        binding.deviceFields.etDomain.setText(data.getDomain());
        binding.deviceFields.etUsername.setText(data.getUserName());
        binding.deviceFields.etPassword.setText(data.getPassWord());


    }


    private void setupDevicesList() {
        mDevicesAdapter = new DevicesAdapter(getContext(), mMainViewModel.camDevices.getValue(), this);
        binding.rvDevices.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvDevices.setEmptyView(binding.noDevices.getRoot());
        binding.rvDevices.setAdapter(mDevicesAdapter);
    }

    private void enableCreationMode() {
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
        mode = dataFormMode;
        switch (dataFormMode) {
            case EDIT:
                enableFieldsForEdit();
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
        binding.dropDown.spinner.setSelection(0);
        binding.btnSave.setVisibility(View.VISIBLE);
        binding.btnDelete.setVisibility(View.GONE);
        binding.btnUpdate.setVisibility(View.GONE);

    }


    private void enableFieldsForEdit() {
        setAllFieldsEnabled(true);
        binding.btnSave.setVisibility(View.VISIBLE);
        binding.btnDelete.setVisibility(View.GONE);
        binding.btnUpdate.setVisibility(View.GONE);
        binding.dropDown.getRoot().setEnabled(true);
    }


    private void setAllFieldsEmpty() {
        binding.llStatus.setVisibility(View.GONE);
        binding.channels.setText("0");
        binding.etName.setText(null);
        binding.deviceFields.etPassword.setText(null);
        binding.deviceFields.etUsername.setText(null);
        binding.deviceFields.etDomain.setText(null);
        binding.deviceFields.etDescription.setText(null);
    }

    private void setAllFieldsEnabled(boolean enabled) {
        binding.nameInputLayout.setEnabled(enabled);
        binding.deviceFields.passwordInputLayout.setEnabled(enabled);
        binding.deviceFields.usernameInputLayout.setEnabled(enabled);
        binding.deviceFields.domainInputLayout.setEnabled(enabled);
        binding.deviceFields.descriptionInputLayout.setEnabled(enabled);
        binding.dropDown.spinner.setEnabled(enabled);
    }

    private void lockAllFields() {
        setAllFieldsEnabled(false);
        binding.btnSave.setVisibility(View.GONE);
        binding.btnDelete.setVisibility(View.VISIBLE);
        binding.btnUpdate.setVisibility(View.VISIBLE);
    }


    private void setAddLayoutSelected(boolean selected) {
        binding.addDevice.getRoot().setBackgroundColor(ContextCompat.getColor(getContext(), selected ? R.color.accent_color : R.color.white_color));
        binding.addDevice.tvTitle.setTextColor(ContextCompat.getColor(getContext(), selected ? R.color.white_color : R.color.black_color));
        binding.addDevice.ivCam.setColorFilter(ContextCompat.getColor(getContext(), selected ? R.color.white_color : R.color.grey_color));
    }


    private void updateCurrentData() {
        String deviceName = binding.etName.getText().toString();
        String ip = binding.deviceFields.etDomain.getText().toString();
        String userName = binding.deviceFields.etUsername.getText().toString();
        String password = binding.deviceFields.etPassword.getText().toString();
    }

    private void deleteCurrentData() {
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


    @Override
    public void onItemClicked(CamDevice camDevice) {
        if (camDevice != null) {
            bindCamDevice(camDevice);
            enableReadMode();
        }else{
            enableCreationMode();
        }
    }






}