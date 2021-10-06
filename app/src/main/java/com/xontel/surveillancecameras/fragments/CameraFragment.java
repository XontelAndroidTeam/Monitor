package com.xontel.surveillancecameras.fragments;

//import org.videolan.libvlc.IVLCVout;
//import org.videolan.libvlc.LibVLC;
//import org.videolan.libvlc.Media;
//import org.videolan.libvlc.MediaPlayer;
//import org.videolan.libvlc.util.VLCVideoLayout;

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
import android.widget.Toast;

import com.github.niqdev.mjpeg.DisplayMode;
import com.github.niqdev.mjpeg.Mjpeg;
//import com.longdo.mjpegviewer.MjpegView;
import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.databinding.FragmentCameraBinding;
import com.xontel.surveillancecameras.data.db.model.IpCam;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import rx.functions.Action1;

public class CameraFragment extends Fragment {
//    private MediaPlayer mediaPlayer;
//    private LibVLC libVLC;
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


    }
//    private void initVlcPlayer() {
//        showLoadingDialog();
//        List<String> args = new ArrayList<String>();
//        args.add("-vvv");
////        args.add("--vout=android-display");
//        args.add("--network-caching=33");
//        args.add("--file-caching=33");
//        args.add("--live-caching=33");
//        args.add("--clock-synchro=0");
//        args.add("--clock-jitter=0");
//        args.add("--h264-fps=60");
//        args.add("--avcodec-fast");
//        args.add("--avcodec-threads=1");args.add("--no-audio");
//
//
//        libVLC = new LibVLC(getContext(), (ArrayList<String>) args);
//
//
//        mediaPlayer = new MediaPlayer(libVLC);
//        mediaPlayer.getVLCVout().addCallback(new IVLCVout.Callback() {
//            @Override
//            public void onSurfacesCreated(IVLCVout vlcVout) {
//                vlcVout.setWindowSize(binding.parent.getMeasuredWidth(), binding.parent.getMeasuredHeight());
//                mediaPlayer.setAspectRatio(binding.surfaceView.getMeasuredWidth()+":"+binding.surfaceView.getMeasuredHeight());
//            }
//
//            @Override
//            public void onSurfacesDestroyed(IVLCVout vlcVout) {
//            }
//        });
//
//        mediaPlayer.setEventListener(new MediaPlayer.EventListener() {
//            @Override
//            public void onEvent(MediaPlayer.Event event) {
//                switch (event.type) {
//                    case MediaPlayer.Event.EncounteredError:
//                        hideProgressDialog();
//                        binding.tvError.setVisibility(View.VISIBLE);
//                        binding.tvError.setText(R.string.error_occurred);
//                        break;
//                    case MediaPlayer.Event.Playing:
//                        hideProgressDialog();
//                        break;
//                    case MediaPlayer.Event.Buffering:
//                        if(event.getBuffering()>= 100){
//                            hideProgressDialog();
//                        }
//                        break;
//                }
//            }
//        });
//        mediaPlayer.getVLCVout().setVideoView(binding.surfaceView);
//        mediaPlayer.getVLCVout().attachViews();
//        final Media media = new Media(libVLC, Uri.parse(cam.getUrl()));
//        media.addOption(":fullscreen");
//        media.addOption(":rtsp-tcp");
//        media.setHWDecoderEnabled(true, true);
//        mediaPlayer.setMedia(media);
////        mediaPlayer.setVideoScale(MediaPlayer.ScaleType.SURFACE_FIT_SCREEN);
//        media.release();
//
//        mediaPlayer.play();
//
//
//
//
//    }

    private void showLoadingDialog() {
        binding.loadingDialog.setVisibility(View.VISIBLE);
    }

    private void hideProgressDialog() {
        binding.loadingDialog.setVisibility(View.GONE);
    }


    private void initUI() {
        setupCamView();
    }

    @Override
    public void onStart() {
        initUI();
        super.onStart();


    }

    @Override
    public void onStop() {
//        if(mediaPlayer != null) {
//            mediaPlayer.stop();
//            mediaPlayer.detachViews();
//            Log.e("tagoo","pause");
//        }
        super.onStop();

    }

    @Override
    public void onDestroy() {
//        if(mediaPlayer != null){
//            mediaPlayer.stop();
//            mediaPlayer.detachViews();
//            mediaPlayer.release();
//            libVLC.release();
//        }
        super.onDestroy();

    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();

    }



    private void setupCamView() {
        binding.tvCamName.setText(cam.getName());
//        binding.mjpegView.setAdjustHeight(true);
//        binding.mjpegView.setAdjustWidth(true);
//        binding.mjpegView.setMode(MjpegView.MODE_FIT_WIDTH);
//        binding.mjpegView.setMsecWaitAfterReadImageError(1000);
//        binding.mjpegView.setUrl(cam.getUrl());
//        binding.mjpegView.setRecycleBitmap(true);
//        binding.mjpegView.startStream();


//
//        initVlcPlayer();



    }

}