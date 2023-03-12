package com.xontel.surveillancecameras.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.databinding.DialogLoadingBinding;


public class LoadingDialog extends Dialog {
    private Context context;
    private DialogLoadingBinding binding;
    private String title;

    public LoadingDialog(@NonNull Context context, String title) {
        super(context);
        this.context = context;
        this.title = title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_loading, null, false);
        setContentView(binding.getRoot());
        setCancelable(false);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        initUI();
    }

    private void initUI() {
        if(title == null || title.isEmpty()) {
            binding.tvTitle.setVisibility(View.GONE);
        }else{
            binding.tvTitle.setVisibility(View.VISIBLE);
            binding.tvTitle.setText(title);
        }
    }


}
