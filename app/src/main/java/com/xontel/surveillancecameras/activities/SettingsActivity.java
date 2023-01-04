package com.xontel.surveillancecameras.activities;

import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;

import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.base.BaseActivity;
import com.xontel.surveillancecameras.customObservers.SettingObservable;
import com.xontel.surveillancecameras.databinding.ActivitySettingsBinding;
import com.xontel.surveillancecameras.utils.CommonUtils;
import com.xontel.surveillancecameras.utils.SDCardObservable;
import com.xontel.surveillancecameras.utils.StorageHelper;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.inject.Inject;

public class SettingsActivity extends BaseActivity implements Observer {
    public static final String TAG = SettingsActivity.class.getSimpleName();
    private ActivitySettingsBinding binding ;
    @Inject
     SettingObservable mSettingObservable ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        getActivityComponent().inject(this);
//        SDCardObservable.getInstance().addObserver(this);
        setUp();
    }

    @Override
    protected void setUp() {
        super.setUp();
        setupDropDowns();


    }

    private void initUI() {
//        binding.ivBack.setOnClickListener(v->{
//            hitBack();
//        });
//        binding.llShowMedia.setOnClickListener(v -> {
//            openDefaultMediaFolder();
//        });
//        binding.swAutoPreview.setChecked(sharedPreferences.getBoolean(CommonUtils.KEY_AUTO_PREVIEW, true));
//        binding.swAutoPreview.setOnCheckedChangeListener((v, isChecked)->{
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putBoolean(CommonUtils.KEY_AUTO_PREVIEW, isChecked);
//            editor.apply();
//        });
//
//        setupIntervalsSpinner();
//        setupGridCountSpinner();

    }

    private void enableBackBtn() {
//        binding.toolbarLayout.tvBack.setVisibility(View.VISIBLE);
//        binding.toolbarLayout.tvBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onBackPressed();
//            }
//        });
    }

    public void setToolbarTitle(int titleResId){
//        binding.toolbarLayout.tvTitle.setText(titleResId);
    }

    private void openDefaultMediaFolder() {
        startActivity(new Intent(this, SavedMediaActivity.class));
    }

    private void setupStorageSpinner() {
//        List<String> volNames = StorageHelper.getVolumesNamesList(this);
//        binding.spSaveTo.setItems(volNames);
//        binding.spSaveTo.setSpinnerPopupHeight(StorageHelper.getVolumesNamesList(this).size() * 46); // work around a bug in this library
//        int storageChoiceType =  StorageHelper.getSavedStorageType(this);
//        int storageChoiceIndex = getIndexFromStorageType(storageChoiceType, volNames);

//        if(!CommonUtils.hasSDCard(this)){
//            storageChoiceIndex = INTERNAL_STORAGE;
//            binding.spSaveTo.setEnabled(false);
//        }
//        binding.spSaveTo.selectItemByIndex(storageChoiceIndex);
//        binding.spSaveTo.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<String>() {
//            @Override
//            public void onItemSelected(int i, @Nullable String s, int i1, String t1) {
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putInt(CommonUtils.KEY_MEDIA_STORAGE, i1);
//                editor.apply();
//                StorageHelper.saveStorageType(SettingsActivity.this, t1);
//            }
//        });
    }

    private void setupDropDowns() {
        ArrayAdapter mediaDirsDropDownAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.save_to));
        ArrayAdapter intervalsDirsDropDownAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.intervals));
        binding.mediaFilter.setAdapter(mediaDirsDropDownAdapter);
        binding.slideShowIntervalDropDown.setAdapter(intervalsDirsDropDownAdapter);
        binding.setData(mSettingObservable);
        binding.setLifecycleOwner(this);

    }

    private int getIndexFromStorageType(int storageChoiceType, List<String> volNames) {
        String label = StorageHelper.getLabelFromStorageType(this, storageChoiceType);
        Log.v("helper", label);
        for(int i =0 ; i< volNames.size() ; i++){
            if(label.equals(volNames.get(i)))
                return i;
        }
        return 0 ;
    }


    private void setupIntervalsSpinner() {
//        int intervalChoiceIndex = sharedPreferences.getInt(CommonUtils.KEY_SLIDE_INTERVAL_INDEX, 0);
//        binding.spSlideShowInterval.selectItemByIndex(intervalChoiceIndex);
//        binding.spSlideShowInterval.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<String>() {
//            @Override
//            public void onItemSelected(int i, @Nullable String s, int i1, String t1) {
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putInt(CommonUtils.KEY_SLIDE_INTERVAL_INDEX, i1);
//                editor.apply();
//            }
//        });
    }

    private void setupGridCountSpinner() {
        int gridCount = 0 /*sharedPreferences.getInt(CommonUtils.KEY_GRID_COUNT, 4)*/;
        Log.d("TAG", "setupGridCountSpinner: "+gridCount);
        String [] gridValues = getResources().getStringArray(R.array.grid_count);
        List<String> gridValuesList = Arrays.asList(gridValues);
        int gridCountIndex = gridValuesList.indexOf(String.valueOf(gridCount)) ;
        Log.d("TAG", "setupGridCountSpinner: "+gridCountIndex);
//        binding.spGridCount.selectItemByIndex(gridCountIndex);
//        binding.spGridCount.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<String>() {
//            @Override
//            public void onItemSelected(int i, @Nullable String s, int i1, String t1) {
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putInt(CommonUtils.KEY_GRID_COUNT, Integer.parseInt(t1));
//                editor.apply();
//            }
//        });
    }

    @Override
    protected void onResume() {
//        setupStorageSpinner();
        super.onResume();
    }

    @Override
    public void update(Observable o, Object arg) {
//        if(((String)arg).equals(Intent.ACTION_MEDIA_MOUNTED)){
//            Log.v(TAG, "SDCard inserted"+ getExternalFilesDirs(null).length);
//        }else{
//            Log.v(TAG, "SDCard ejected"+ getExternalFilesDirs(null).length);
//        }
//        setupStorageSpinner();
    }
}