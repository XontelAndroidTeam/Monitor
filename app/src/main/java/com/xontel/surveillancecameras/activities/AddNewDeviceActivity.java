package com.xontel.surveillancecameras.activities;

import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.base.BaseActivity;
import com.xontel.surveillancecameras.data.db.model.CamDevice;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.databinding.ActivityAddNewDeviceBinding;
import com.xontel.surveillancecameras.utils.CamDeviceType;

public class AddNewDeviceActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    public static final String KEY_DEVICE = "device";
    private ActivityAddNewDeviceBinding binding ;
    private int deviceType = CamDeviceType.OTHER.getValue();
    private boolean isEditMode = false;
    private CamDevice mCamDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_new_device);
        getActivityComponent().inject(this);
        if(getIntent().hasExtra(KEY_DEVICE)){
            mCamDevice = getIntent().getParcelableExtra(KEY_DEVICE);
            isEditMode = true;
            fillFieldsWithData();
        }
        setUp();
    }

    private void fillFieldsWithData() {
        binding.etName.setText(mCamDevice.getName());
        deviceType = mCamDevice.getType();
        if(deviceType == CamDeviceType.OTHER.getValue()){
           bindCamFields();
        }else{
           bindDeviceFields();
        }

        refreshView();
    }

    private void bindCamFields() {
        binding.camFields.etUrl.setText(mCamDevice.getUrl());
    }

    private void bindDeviceFields() {
        binding.deviceFields.etIp.setText(mCamDevice.getIp());
        binding.deviceFields.etUsername.setText(mCamDevice.getUserName());
        binding.deviceFields.etPassword.setText(mCamDevice.getPassword());
    }


    @Override
    protected void setUp() {
        super.setUp();
        initUI();
    }

    private void initUI() {
        setupDeviceTypeDropDown();
        binding.btnCancel.setOnClickListener(v-> {
            hitBack();
        });

        binding.btnSave.setOnClickListener(v->{
            if(validateFields()){
//                String url =  binding.fields.etUrl.getText().toString();
//                String name =  binding.etName.getText().toString();
//                String description = binding.fields.etDescription.getText().toString();
//                if(editedCam == null) {
//                    mPresenter.createCamera(new IpCam(url, name , description ));
//                }else{
//                    editedCam.setUrl(url);
//                    editedCam.setName(name);
//                    editedCam.setDescription(description);
//                    mPresenter.updateCamera(editedCam);
//                }
            }
        });
    }

    private boolean validateFields() {
        if(deviceType == CamDeviceType.OTHER.getValue()){
            return binding.etName.isValid() && binding.camFields.etUrl.isValid();
        }
        return binding.etName.isValid() &&
                binding.deviceFields.etIp.isValid() &&
                binding.deviceFields.etUsername.isValid() &&
                binding.deviceFields.etPassword.isValid() ;
    }


    private void refreshView() {
        boolean isCam = deviceType == CamDeviceType.OTHER.getValue() ;
        binding.camFields.getRoot().setVisibility(isCam ? View.VISIBLE : View.GONE );
        binding.deviceFields.getRoot().setVisibility(!isCam ? View.VISIBLE : View.GONE );
    }




    private void setupDeviceTypeDropDown(){
        String[] types = getResources().getStringArray(R.array.device_type);
        ArrayAdapter typesDropDownAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, types
                );
        binding.dropDown.slideShowFilter.setAdapter(typesDropDownAdapter);
        binding.dropDown.slideShowFilter.setText(types[CamDeviceType.OTHER.getValue()], false);
        binding.dropDown.slideShowFilter.setOnItemClickListener(this);
        typesDropDownAdapter.notifyDataSetChanged();
        binding.setLifecycleOwner(this);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        deviceType = position;
        refreshView();
    }
}