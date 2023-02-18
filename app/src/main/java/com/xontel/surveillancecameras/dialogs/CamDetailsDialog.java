package com.xontel.surveillancecameras.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.databinding.DialogCamDetailsBinding;

public class CamDetailsDialog extends Dialog {
    private Context context;
    private IpCam ipCam ;
    private DialogCamDetailsBinding binding;

    public CamDetailsDialog(@NonNull Context context, IpCam ipCam) {
        super(context);
        this.context = context ;
        this.ipCam = ipCam;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_cam_details, null, false);
        setContentView(binding.getRoot());
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        initUI();
    }

    private void initUI() {
       binding.tvCamName.setText(ipCam.getName());
//        binding.tvCamUrl.setText(ipCam.getUrlOrIpAddress());
     //   binding.tvCamDesc.setText(ipCam.getDescription());
        binding.btnOk.setOnClickListener(v->{
            dismiss();
        });
    }

}
