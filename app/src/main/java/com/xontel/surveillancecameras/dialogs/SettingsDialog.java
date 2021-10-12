package com.xontel.surveillancecameras.dialogs;


import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.databinding.DialogSettingsBinding;
import com.xontel.surveillancecameras.utils.CommonUtils;

public class SettingsDialog extends Dialog {
    private Context context;
    private DialogSettingsBinding binding;
    private SharedPreferences sharedPreferences;
    private boolean isAutoPreview ;
    private int slideIntervalIndex ;

    public SettingsDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_settings, null, false);
        setContentView(binding.getRoot());
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        sharedPreferences = context.getSharedPreferences(CommonUtils.SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
         isAutoPreview = sharedPreferences.getBoolean(CommonUtils.KEY_AUTO_PREVIEW, true);
         slideIntervalIndex = sharedPreferences.getInt(CommonUtils.KEY_SLIDE_INTERVAL_INDEX, 0);
        initUI();
    }

    private void initUI() {

        binding.swAutoPreview.setChecked(isAutoPreview);
        setupIntervalsSpinner();
        binding.btnOk.setOnClickListener(v -> {
            applySettings();
            dismiss();
        });
        binding.btnCancel.setOnClickListener(v -> {
            dismiss();
        });

        binding.swAutoPreview.setOnCheckedChangeListener((v, isChecked)->{
            binding.spSlideShowInterval.setEnabled(isChecked);
        });
    }

    private void setupIntervalsSpinner() {
        binding.spSlideShowInterval.selectItemByIndex(slideIntervalIndex);
        binding.spSlideShowInterval.setEnabled(binding.swAutoPreview.isChecked());

    }

    private void applySettings() {
        boolean newIsAutoPreview = binding.swAutoPreview.isChecked();
        int newSlideIntervalIndex = binding.spSlideShowInterval.getSelectedIndex();
        if(newIsAutoPreview != isAutoPreview || newSlideIntervalIndex != slideIntervalIndex) { // check if the values have changed
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(CommonUtils.KEY_AUTO_PREVIEW, newIsAutoPreview);
            editor.putInt(CommonUtils.KEY_SLIDE_INTERVAL_INDEX, newSlideIntervalIndex);
            editor.apply();
        }
    }

}

