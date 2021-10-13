package com.xontel.surveillancecameras.fragments;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.VLCVideoLayout;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.niqdev.mjpeg.DisplayMode;
import com.github.niqdev.mjpeg.Mjpeg;
//import com.longdo.mjpegviewer.MjpegView;
import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.databinding.FragmentCameraBinding;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.utils.VideoHelper;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

public class CameraFragment extends Fragment {
    private MediaPlayer mediaPlayer;
    private LibVLC libVLC;
    private VideoHelper videoHelper ;
    private static final boolean USE_TEXTURE_VIEW = false;
    private static final boolean ENABLE_SUBTITLES = true;
    private static final String KEY_CAM_INFO = "cam_info";

    private IpCam cam;

    private FragmentCameraBinding binding;


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
        Log.e("TAG", "onCreate"+hashCode() );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_camera, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initUI();
    }
    private void initVlcPlayer() {

        videoHelper = new VideoHelper(getContext(),binding.videoSurfaceFrame, binding.surfaceStub.getViewStub(), binding.getRoot());
        videoHelper.setVIDEO_URL(cam.getUrl());



        binding.loading.setVisibility(View.VISIBLE);
        videoHelper.getMediaPlayer().setEventListener(new MediaPlayer.EventListener() {
            float buffered = 0.0f;

            @Override
            public void onEvent(MediaPlayer.Event event) {
                if (event.type == MediaPlayer.Event.Buffering) {
                    buffered = event.getBuffering();
                }
                if (buffered == 100.0) {
                    binding.loading.setVisibility(View.GONE);
                    Log.d("EVENT", event.type + "");
                }

                if( event.type == MediaPlayer.Event.EncounteredError) {
                    Log.d("EVENT", event.type + "");
                    binding.loading.setVisibility(View.GONE);
                    binding.tvError.setVisibility(View.VISIBLE);
                    binding.tvError.setText(R.string.error_occurred);
                }
            }
        });
        videoHelper.onStart();

    }



    private void initUI() {
        initVlcPlayer();
    }


    @Override
    public void onStop() {
        Log.e("TAG", "onStop"+hashCode() );
        super.onStop();

    }

    @Override
    public void onDestroy() {
        Log.e("TAG", "onDestroy"+hashCode() );
        if(videoHelper != null){
            videoHelper.onStop();
            videoHelper.onDestroy();
        }
        super.onDestroy();

    }

    @Override
    public void onResume() {
        Log.e("TAG", "onResume"+hashCode() );
        super.onResume();
    }


    @Override
    public void onPause() {
        Log.e("TAG", "onPause"+hashCode() );

        super.onPause();

    }

}