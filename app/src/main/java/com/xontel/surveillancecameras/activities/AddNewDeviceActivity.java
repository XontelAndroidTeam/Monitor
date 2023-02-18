package com.xontel.surveillancecameras.activities;

import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.base.BaseActivity;
import com.xontel.surveillancecameras.databinding.ActivityAddNewDeviceBinding;
import com.xontel.surveillancecameras.hikvision.CamDevice;
import com.xontel.surveillancecameras.presenters.MainDeviceMvpPresenter;
import com.xontel.surveillancecameras.presenters.MainDeviceMvpView;
import com.xontel.surveillancecameras.utils.CamDeviceType;

import java.util.List;

import javax.inject.Inject;

public class AddNewDeviceActivity extends BaseActivity implements MainDeviceMvpView, AdapterView.OnItemClickListener {
    public static final String KEY_DEVICE = "device";
    private ActivityAddNewDeviceBinding binding ;
    private int deviceType = 0;
    private boolean isEditMode = false;
    private CamDevice mCamDevice;
    @Inject
    MainDeviceMvpPresenter<MainDeviceMvpView> mPresenter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_new_device);
        getActivityComponent().inject(this);
        mPresenter.onAttach(this);
        if(getIntent().hasExtra(KEY_DEVICE)){
            mCamDevice = getIntent().getParcelableExtra(KEY_DEVICE);
            isEditMode = true;
            fillFieldsWithData();
        }
        setUp();
    }

    private void fillFieldsWithData() {
        binding.etName.setText(mCamDevice.getName());
        deviceType = mCamDevice.getDeviceType();
        bindDeviceFields();
        refreshView();
    }



    private void bindDeviceFields() {
        binding.deviceFields.etDomain.setText(mCamDevice.getDomain());
        binding.deviceFields.etUsername.setText(mCamDevice.getUserName());
        binding.deviceFields.etPassword.setText(mCamDevice.getPassWord());
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
            if(validateFields() ){
                String deviceName = binding.etName.getText().toString();
                String ip = binding.deviceFields.etDomain.getText().toString();
                String userName = binding.deviceFields.etUsername.getText().toString();
                String password = binding.deviceFields.etPassword.getText().toString();
//                CamDevice camDevice = new CamDevice(0,deviceName,userName,password,ip,deviceType,url );
//                if (camDevice.isLoginValid()) {
//                    if (mCamDevice == null) {
//                        mPresenter.createDevice(camDevice);
//                    } else {
//                        mPresenter.updateDevice(new CamDevice(0, deviceName, userName, password, ip, deviceType, url));
//                    }
//                }else{
//                    showMessage(this.getString(R.string.Cant_LOGIN));
//                }
            }
        });
    }


    private boolean validateFields() {
        if(deviceType == 0){
            return binding.etName.isValid() && binding.camFields.etUrl.isValid();
        }
        return binding.etName.isValid() &&
                binding.deviceFields.etDomain.isValid() &&
                binding.deviceFields.etUsername.isValid() &&
                binding.deviceFields.etPassword.isValid() ;
    }


    private void refreshView() {
        boolean isCam = deviceType == 0 ;
        binding.camFields.getRoot().setVisibility(isCam ? View.VISIBLE : View.GONE );
        binding.deviceFields.getRoot().setVisibility(!isCam ? View.VISIBLE : View.GONE );
    }




    private void setupDeviceTypeDropDown(){
        String[] types = getResources().getStringArray(R.array.device_type);
        ArrayAdapter typesDropDownAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, types
                );
        binding.dropDown.slideShowFilter.setAdapter(typesDropDownAdapter);
        binding.dropDown.slideShowFilter.setText(types[0], false);
        binding.dropDown.slideShowFilter.setOnItemClickListener(this);
        typesDropDownAdapter.notifyDataSetChanged();
        binding.setLifecycleOwner(this);
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        deviceType = position;
        refreshView();
    }

    @Override
    public void onInsertingDevice() {hitBack();}

    @Override
    public void onUpdatingDevice() {hitBack();}

    @Override
    public void onDeletingDevice() {

    }

    @Override
    public void onGettingDevice(CamDevice response) {

    }

    @Override
    public void onGettingAllDevices(List<CamDevice> response) {

    }


}