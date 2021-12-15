package com.xontel.surveillancecameras.fragments;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.VLCVideoLayout;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.github.niqdev.mjpeg.DisplayMode;
import com.github.niqdev.mjpeg.Mjpeg;
//import com.longdo.mjpegviewer.MjpegView;
import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.activities.CamerasActivity;
import com.xontel.surveillancecameras.databinding.FragmentCameraBinding;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.utils.VideoHelper;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import rx.functions.Action1;

public class CameraFragment extends Fragment {
    public static final String TAG = CameraFragment.class.getSimpleName();
    private MediaPlayer mediaPlayer;
    private LibVLC libVLC;
    private VideoHelper videoHelper;
    private boolean isRecording = false;
    private ObjectAnimator objAnimator;
    private long recordTime = 0;
     private SimpleDateFormat mSimpleDateFormat ;
    private static final boolean USE_TEXTURE_VIEW = false;
    private static final boolean ENABLE_SUBTITLES = true;
    private static final String KEY_CAM_INFO = "cam_info";
    private Timer mTimer;

    private IpCam cam;

    private FragmentCameraBinding binding;


    public CameraFragment() {
        // Required empty public constructor
    }

    public void recordVideo() {

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
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            cam = getArguments().getParcelable(KEY_CAM_INFO);
        }
        Log.e("TAG", "onCreate" + hashCode());
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
            case R.id.action_edit:
            case R.id.action_share:
            case R.id.action_details:
                return false;
            case R.id.action_capture_photo:
                capturePhoto();
                return true;
            case R.id.action_record_video:
                startRecordingVideo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void initVlcPlayer() {

        videoHelper = new VideoHelper(getContext(), binding.videoSurfaceFrame, binding.surfaceStub.getViewStub(), binding.getRoot());
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

                if (event.type == MediaPlayer.Event.EncounteredError) {
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
         mSimpleDateFormat = new SimpleDateFormat("HH:mm:ss");
         mSimpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        binding.btnStop.setOnClickListener(v -> {
            stopRecordingVideo();
        });

        initVlcPlayer();
    }

    private void stopRecordingVideo() {
        if (isRecording) {
            isRecording = false;
            recordTime = 0;
            boolean isRecorded = videoHelper.getMediaPlayer().record(null); // check if ended successfully
            Log.v(TAG, "isRecording : " + isRecorded);
            binding.llRecordPanel.setVisibility(View.GONE);
            mTimer.cancel();
            stopIndicatorAnimation();
            if (isRecorded)
                showSuccessMessage();
            else
                showFailedMessage();
        }
    }

    private void showSuccessMessage() {
        Toast.makeText(getContext(), R.string.record_saved, Toast.LENGTH_LONG).show();
    }

    private void showFailedMessage() {
        Toast.makeText(getContext(), R.string.record_failed, Toast.LENGTH_LONG).show();
    }

    private void startRecordingVideo() {
        if(videoHelper.getMediaPlayer().isPlaying() && videoHelper.getMediaPlayer().hasMedia() ) {
            if (!isRecording) { // there is no record operation in progress
                isRecording = videoHelper.getMediaPlayer().record(getContext().getExternalFilesDir(null).getAbsolutePath());
                if (isRecording) { // if player started recording do ui things
                    binding.llRecordPanel.setVisibility(View.VISIBLE);
                    mTimer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    binding.timer.setText(mSimpleDateFormat.format(recordTime));
                                    recordTime += 1000;
                                }
                            });

                        }
                    }, 0, 1000);
                    startIndicatorAnimation();
                    Log.v(TAG, "isRecording : " + isRecording);
                } else {
                    showFailedMessage();
                }
            }
        }else{
            Toast.makeText(getContext(), R.string.cant_rec_video, Toast.LENGTH_LONG).show();
        }
    }

    private void startIndicatorAnimation() {
        objAnimator = ObjectAnimator.ofFloat(binding.ivRecordIndicator, "alpha", 0f, 1f);
        objAnimator.setDuration(200);
        objAnimator.setRepeatMode(ValueAnimator.REVERSE);
        objAnimator.setRepeatCount(Animation.INFINITE);
        objAnimator.start();
    }

    private void stopIndicatorAnimation() {
        objAnimator.end();
    }

    private void capturePhoto() {
        if(videoHelper.getMediaPlayer().isPlaying() && videoHelper.getMediaPlayer().hasMedia() ) {
            binding.videoSurfaceFrame.setDrawingCacheEnabled(true);
            binding.videoSurfaceFrame.buildDrawingCache();
            Bitmap bitmap = binding.videoSurfaceFrame.getDrawingCache();
            String extStorageDirectory = getContext().getExternalFilesDir(null).getAbsolutePath();
            OutputStream outStream = null;
            File file = new File(extStorageDirectory, System.currentTimeMillis()+".jpg");
            try {
                outStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                outStream.flush();
                outStream.close();
                Toast.makeText(getContext(), R.string.snapshot_taken, Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(getContext(), R.string.error_occurred, Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }else{
            Toast.makeText(getContext(), R.string.cant_take_photo, Toast.LENGTH_LONG).show();
        }

    }


    @Override
    public void onStop() {
        Log.e("TAG", "onStop" + hashCode());
        super.onStop();

    }

    @Override
    public void onDestroy() {
        Log.e("TAG", "onDestroy" + hashCode());
        if (videoHelper != null) {
            videoHelper.onStop();
            videoHelper.onDestroy();
        }
        super.onDestroy();

    }

    @Override
    public void onResume() {
        Log.e("TAG", "onResume" + hashCode());
        if (mTimer != null) {
            mTimer.cancel();
        }
        mTimer = new Timer();
        super.onResume();
    }


    @Override
    public void onPause() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        Log.e("TAG", "onPause" + hashCode());

        super.onPause();

    }

}