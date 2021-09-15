package com.xontel.surveillancecameras.dialogs;


import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.databinding.DialogCamDetailsBinding;
import com.xontel.surveillancecameras.databinding.DialogSettingsBinding;
import com.xontel.surveillancecameras.utils.CommonUtils;

import org.jetbrains.annotations.Nullable;

public class SettingsDialog extends Dialog {
    private Context context;
    private DialogSettingsBinding binding;
    private SharedPreferences sharedPreferences ;

    public SettingsDialog(@NonNull Context context) {
        super(context);
        this.context = context ;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_settings, null, false);
        setContentView(binding.getRoot());
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        sharedPreferences = context.getSharedPreferences(CommonUtils.SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
        initUI();
    }

    private void initUI() {

        binding.swAutoPreview.setChecked(sharedPreferences.getBoolean(CommonUtils.KEY_AUTO_PREVIEW, true));
        setupIntervalsSpinner();
        binding.btnOk.setOnClickListener(v->{
            applySettings();
            dismiss();
        });
        binding.btnCancel.setOnClickListener(v->{
            dismiss();
        });
    }

    private void setupIntervalsSpinner() {
        int intervalChoiceIndex = sharedPreferences.getInt(CommonUtils.KEY_SLIDE_INTERVAL_INDEX, 0);
        binding.spSlideShowInterval.selectItemByIndex(intervalChoiceIndex);

    }

    private void applySettings() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(CommonUtils.KEY_AUTO_PREVIEW, binding.swAutoPreview.isChecked());
        editor.putInt(CommonUtils.KEY_SLIDE_INTERVAL_INDEX, binding.spSlideShowInterval.getSelectedIndex());
        editor.apply();
    }

}

