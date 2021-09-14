package com.xontel.surveillancecameras.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.niqdev.mjpeg.DisplayMode;
import com.github.niqdev.mjpeg.Mjpeg;
import com.longdo.mjpegviewer.MjpegView;
import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.databinding.FragmentCameraBinding;
import com.xontel.surveillancecameras.data.db.model.IpCam;

import org.jetbrains.annotations.NotNull;

import rx.functions.Action1;

public class CameraFragment extends Fragment {

    private static final String KEY_CAM_INFO = "cam_info";

    private IpCam cam;

    private FragmentCameraBinding binding ;

    public CameraFragment() {
        // Required empty public constructor
    }


    public static CameraFragment newInstance(IpCam cam) {
        CameraFragment fragment = new CameraFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_CAM_INFO, cam);
        fragment.setArguments(args);
        return fragment;
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cam = getArguments().getParcelable(KEY_CAM_INFO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_camera, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI();
        
    }

    private void initUI() {
        setupCamView();
    }
    @Override
    public void onResume() {
        binding.mjpegView.setUrl(cam.getUrl());
        binding.mjpegView.startStream();
        super.onResume();
    }

    @Override
    public void onPause() {
        binding.mjpegView.stopStream();
        super.onPause();
    }

    private void setupCamView() {
         binding.tvCamName.setText(cam.getName());
        binding.mjpegView.setAdjustHeight(true);
        binding.mjpegView.setAdjustWidth(true);
        binding.mjpegView.setMode(MjpegView.MODE_FIT_WIDTH);
        binding.mjpegView.setMsecWaitAfterReadImageError(1000);
        binding.mjpegView.setUrl(cam.getUrl());
        binding.mjpegView.setRecycleBitmap(true);
        binding.mjpegView.startStream();

    }
}