package com.xontel.surveillancecameras.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.BaseInputConnection;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;
import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.base.BaseActivity;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.databinding.ActivityAddCamBinding;
import com.xontel.surveillancecameras.presenters.MainMvpPresenter;
import com.xontel.surveillancecameras.presenters.MainMvpView;

import java.util.List;

import javax.inject.Inject;

public class AddCamActivity extends BaseActivity implements MainMvpView {
    public static final String KEY_CAMERA = "camera";
    private IpCam editedCam ;
    private ActivityAddCamBinding binding ;
    @Inject
    MainMvpPresenter<MainMvpView> mPresenter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_cam);
        getActivityComponent().inject(this);
        mPresenter.onAttach(this);


        if(getIntent().hasExtra(KEY_CAMERA)){
            editedCam = getIntent().getParcelableExtra(KEY_CAMERA);
            fillFieldsWithData();
        }
        setUp();
    }


    public void setToolbarTitle(int titleResId){
//        binding.toolbarLayout.tvTitle.setText(titleResId);
    }

    private void fillFieldsWithData() {
        binding.etName.setText(editedCam.getName());
        binding.etName.setSelection(binding.etName.getText().length());
        binding.etUrl.setText(editedCam.getUrl());
        binding.etUrl.setSelection(binding.etUrl.getText().length());
        binding.etDescription.setText(editedCam.getDescription());
        binding.etDescription.setSelection(binding.etDescription.getText().length());
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDetach();
    }

    @Override
    protected void setUp() {
        super.setUp();
        binding.btnCancel.setOnClickListener(v-> {
            hitBack();
        });

        binding.btnSave.setOnClickListener(v->{
            if(isNotEmpty(binding.etName ) && isNotEmpty(binding.etUrl ) && isNotEmpty(binding.etDescription )){
               String url =  binding.etUrl.getText().toString();
               String name =  binding.etName.getText().toString();
                String description = binding.etDescription.getText().toString();
                if(editedCam == null) {
                    mPresenter.createCamera(new IpCam(url, name , description ));
                }else{
                    editedCam.setUrl(url);
                    editedCam.setName(name);
                    editedCam.setDescription(description);
                    mPresenter.updateCamera(editedCam);
                }
            }
        });
    }

    boolean isNotEmpty(EditText editText){
        if(editText.getText().toString().isEmpty() ){
//            editText.setErrorEnabled(false);
            editText.setError(getString(R.string.empty_field));
            return false;
        }else{
            return true;
        }
    }

    @Override
    public void onInsertingCamera() {
        onBackPressed();
    }

    @Override
    public void onUpdatingCamera() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(CamerasActivity.KEY_CAMERAS, editedCam);
        setResult(Activity.RESULT_OK, resultIntent);
        hitBack();
    }

    @Override
    public void onDeletingCamera() {

    }

    @Override
    public void onGettingCamera(IpCam response) {

    }

    @Override
    public void onGettingAllCameras(List<IpCam> response) {

    }
}