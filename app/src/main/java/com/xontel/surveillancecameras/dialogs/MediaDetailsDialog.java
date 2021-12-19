package com.xontel.surveillancecameras.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.databinding.DialogCamDetailsBinding;
import com.xontel.surveillancecameras.databinding.DialogMediaDetailsBinding;
import com.xontel.surveillancecameras.utils.CommonUtils;

import java.io.File;

public class MediaDetailsDialog extends Dialog {
    private Context context;
    private File mediaFile ;
    private DialogMediaDetailsBinding binding;

    public MediaDetailsDialog(@NonNull Context context, File file) {
        super(context);
        this.context = context ;
        this.mediaFile = file;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_media_details, null, false);
        setContentView(binding.getRoot());
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        initUI();
    }

    private void initUI() {
        binding.tvMediaName.setText(mediaFile.getName());
        binding.tvMediaUrl.setText(mediaFile.getAbsolutePath());
        binding.tvMediaSize.setText(CommonUtils.getFileSize(mediaFile));
        binding.btnOk.setOnClickListener(v->{
            dismiss();
        });
    }




}
