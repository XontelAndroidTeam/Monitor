package com.xontel.surveillancecameras.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.databinding.ActivitySettingsBinding;
import com.xontel.surveillancecameras.utils.CommonUtils;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    private ActivitySettingsBinding binding ;
    private  SharedPreferences sharedPreferences ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        sharedPreferences = getSharedPreferences(CommonUtils.SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
        initUI();
    }

    private void initUI() {
        binding.ivBack.setOnClickListener(v->{
            onBackPressed();
        });
        binding.btnAddCamera.setOnClickListener(v->{
            startActivity(new Intent(this, AddCamActivity.class));
        });
        binding.swAutoPreview.setChecked(sharedPreferences.getBoolean(CommonUtils.KEY_AUTO_PREVIEW, true));
        binding.swAutoPreview.setOnCheckedChangeListener((v, isChecked)->{
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(CommonUtils.KEY_AUTO_PREVIEW, isChecked);
            editor.apply();
        });

        setupIntervalsSpinner();
        setupGridCountSpinner();
    }

    private void setupIntervalsSpinner() {
        int intervalChoiceIndex = sharedPreferences.getInt(CommonUtils.KEY_SLIDE_INTERVAL_INDEX, 0);
        binding.spSlideShowInterval.selectItemByIndex(intervalChoiceIndex);
        binding.spSlideShowInterval.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<String>() {
            @Override
            public void onItemSelected(int i, @Nullable String s, int i1, String t1) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(CommonUtils.KEY_SLIDE_INTERVAL_INDEX, i1);
                editor.apply();
            }
        });
    }

    private void setupGridCountSpinner() {
        int gridCount = sharedPreferences.getInt(CommonUtils.KEY_GRID_COUNT, 4);
        Log.d("TAG", "setupGridCountSpinner: "+gridCount);
        String [] gridValues = getResources().getStringArray(R.array.grid_count);
        List<String> gridValuesList = Arrays.asList(gridValues);
        int gridCountIndex = gridValuesList.indexOf(String.valueOf(gridCount)) ;
        Log.d("TAG", "setupGridCountSpinner: "+gridCountIndex);
        binding.spGridCount.selectItemByIndex(gridCountIndex);
        binding.spGridCount.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener<String>() {
            @Override
            public void onItemSelected(int i, @Nullable String s, int i1, String t1) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(CommonUtils.KEY_GRID_COUNT, Integer.parseInt(t1));
                editor.apply();
            }
        });
    }
}