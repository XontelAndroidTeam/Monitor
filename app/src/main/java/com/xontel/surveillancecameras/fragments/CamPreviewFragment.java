package com.xontel.surveillancecameras.fragments;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_IPPARACFG_V40;
import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.databinding.FragmentCameraBinding;
import com.xontel.surveillancecameras.utils.HIKDevice;
import com.xontel.surveillancecameras.utils.HIKDeviceType;
import com.xontel.surveillancecameras.utils.HIKSinglePlayer;
import com.xontel.surveillancecameras.utils.HikUtil;
import com.xontel.surveillancecameras.utils.StorageHelper;

import org.jetbrains.annotations.NotNull;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

public class CamPreviewFragment extends Fragment {
    public static final String TAG = CamPreviewFragment.class.getSimpleName();
    private MediaPlayer mediaPlayer;
    private HIKSinglePlayer hikSinglePlayer ;
    private int logId;
    //    private LibVLC libVLC;
//    private VideoHelper videoHelper;
//    private boolean isRecording = false;
//    private ObjectAnimator objAnimator;
//    private long recordTime = 0;
//    private SimpleDateFormat mSimpleDateFormat;
//    private static final boolean USE_TEXTURE_VIEW = false;
//    private static final boolean ENABLE_SUBTITLES = true;
//    private Timer mTimer;
    private static final String KEY_CAM_INFO = "cam_info";

    private IpCam cam;

    private FragmentCameraBinding binding;


    public CamPreviewFragment() {
        // Required empty public constructor
    }

//    public void recordVideo() {
//
//    }


    public static CamPreviewFragment newInstance(IpCam cam) {
        CamPreviewFragment fragment = new CamPreviewFragment();
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
        Log.e("TAG", "onCreate" + cam);
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
                mediaPlayer.startRecording();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void initVlcPlayer() {
        mediaPlayer = new MediaPlayer(getContext());
//        mediaPlayer.setRecordingDirectory(StorageHelper.getMediaDirectory(getContext(), StorageHelper.VIDEOS_DIRECTORY_NAME).getAbsolutePath());
        mediaPlayer.attachViews(binding.vlcLayout);
        final Media media = new Media(mediaPlayer.getLibVLCInstance(), Uri.parse(cam.getUrl()));
        media.addCommonOptions();
        mediaPlayer.setMedia(media);
        media.release();
//        videoHelper = new VideoHelper(getContext(), binding.videoSurfaceFrame, binding.surfaceStub.getViewStub(), binding.getRoot());
//        videoHelper.setVIDEO_URL(cam.getUrl());
//
//
//        binding.loading.setVisibility(View.VISIBLE);
//        videoHelper.getMediaPlayer().setEventListener(new MediaPlayer.EventListener() {
//            float buffered = 0.0f;
//
//            @Override
//            public void onEvent(MediaPlayer.Event event) {
//                Log.d("EVENT", event.type + "");
//                if (event.type == MediaPlayer.Event.Buffering) {
//                    buffered = event.getBuffering();
//                }
//                if (buffered == 100.0) {
//                    binding.loading.setVisibility(View.GONE);
//                    Log.d("EVENT", event.type + "");
//                }
//
//                if (event.type == MediaPlayer.Event.EncounteredError) {
//                    Log.d("EVENT", event.type + "");
//                    binding.loading.setVisibility(View.GONE);
//                    binding.tvError.setVisibility(View.VISIBLE);
//                    binding.tvError.setText(R.string.error_occurred);
//                }
//            }
//        });
//        videoHelper.onStart();
    }


    private void initUI() {
//        mSimpleDateFormat = new SimpleDateFormat("HH:mm:ss");
//        mSimpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
//        binding.btnStop.setOnClickListener(v -> {
//            stopRecordingVideo();
//        });

//        initVlcPlayer();
        initHikPlayer();
    }

    private void initHikPlayer() {
        HikUtil.initSDK();
        HikUtil.loginDevice(new HIKDevice("192.168.1.123", 8000, "admin", "X0nPAssw0rd_000", HIKDeviceType.DVR), new HikUtil.HikInterface() {
            @Override
            public void onLogInSuccess(int id) {
                logId = id ;

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "logId : "+logId , Toast.LENGTH_SHORT).show();
                        openCams();

                    }
                });


            }

            @Override
            public void onLogInFailed() {
                Log.v("TAGGG", "login failed");

            }
        });
    }

    private void openCams() {
        NET_DVR_IPPARACFG_V40 ipParaCfg = new NET_DVR_IPPARACFG_V40();

        // UserId, Command, ChannelNo., Out
        HCNetSDK.getInstance().NET_DVR_GetDVRConfig( logId, HCNetSDK.NET_DVR_GET_IPPARACFG_V40, 0, ipParaCfg );
        hikSinglePlayer = new HIKSinglePlayer(3, logId, HIKSinglePlayer.HIK_SUB_STREAM_CODE);
        hikSinglePlayer.initView(binding.surfaceView);
        hikSinglePlayer.playOrStopStream();
    }

    private void showSuccessMessage() {
        Toast.makeText(getContext(), R.string.record_saved, Toast.LENGTH_LONG).show();
    }

    private void showFailedMessage() {
        Toast.makeText(getContext(), R.string.record_failed, Toast.LENGTH_LONG).show();
    }

    private void stopRecordingVideo() {
//        if (isRecording) {
//            isRecording = false;
//            boolean isRecorded = videoHelper.getMediaPlayer().record(null); // check if ended successfully
//            Log.v(TAG, "isRecording : " + isRecorded);
//            disableVideoRecordingView();
//            if (isRecorded)
//                showSuccessMessage();
//            else
//                showFailedMessage();
//        }
    }


    private void startRecordingVideo() {
//        try {
//            File videoDirectory = StorageHelper.getMediaDirectory(getContext(), StorageHelper.VIDEOS_DIRECTORY_NAME);
//            Log.v("err", videoDirectory.getAbsolutePath());
//            if (videoHelper.getMediaPlayer().isPlaying() && videoHelper.getMediaPlayer().hasMedia() && videoDirectory != null /*&& CommonUtils.hasFreeSpace(videoDirectory)*/) {
//                if (!isRecording) { // there is no record operation in progress
//                    isRecording = videoHelper.getMediaPlayer().record(videoDirectory.getAbsolutePath());
//                    if (isRecording) { // if player started recording do ui things
//                        enableVideoRecordingView();
//                    } else {
//                        showFailedMessage();
//                    }
//                }
//            } else {
//                Toast.makeText(getContext(), R.string.cant_rec_video, Toast.LENGTH_LONG).show();
//            }
//        } catch (Exception e) {
//            Log.e("err", e.getMessage());
//            Toast.makeText(getContext(), R.string.cant_rec_video, Toast.LENGTH_LONG).show();
//        }
    }

    private void enableVideoRecordingView() {
//        binding.llRecordPanel.setVisibility(View.VISIBLE);
//        mTimer = new Timer();
//        mTimer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                new Handler(Looper.getMainLooper()).post(new Runnable() {
//                    @Override
//                    public void run() {
//                        binding.timer.setText(mSimpleDateFormat.format(recordTime));
//                        recordTime += 1000;
//                    }
//                });
//
//            }
//        }, 0, 1000);
//        startIndicatorAnimation();
//        Log.v(TAG, "isRecording : " + isRecording);
    }


    private void stopIndicatorAnimation() {
//        objAnimator.end();
    }

    public void capturePhoto() {
        Bitmap bitmap = mediaPlayer.takeSnapShot(getContext());
        savePhoto(bitmap);
    }

    private void savePhoto(Bitmap bitmap) {
//        try {
//            File imagesDirectory = StorageHelper.getMediaDirectory(getContext(), StorageHelper.IMAGES_DIRECTORY_NAME);
//            Log.v("err", imagesDirectory.getAbsolutePath());
//            File imageFile = CommonUtils.saveBitmap(getContext(), bitmap, imagesDirectory.getAbsolutePath());
//            if (imageFile != null) {
//                CommonUtils.galleryAddPic(getContext(), imageFile.getAbsolutePath());
//                Toast.makeText(getContext(), R.string.snapshot_taken, Toast.LENGTH_SHORT).show();
//            } else {
//                Toast.makeText(getContext(), R.string.error_occurred, Toast.LENGTH_LONG).show();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(getContext(), R.string.error_occurred, Toast.LENGTH_LONG).show();
//        }

    }


//    @Override
//    public void onStop() {
//        super.onStop();
//
//    }

//    @Override
//    public void onDestroy() {
//        if (videoHelper != null) {
//            videoHelper.onStop();
//            videoHelper.onDestroy();
//        }
//        super.onDestroy();
//
//    }

    @Override
    public void onResume() {
//        if (mTimer != null) {
//            mTimer.cancel();
//        }
//        mTimer = new Timer();
        super.onResume();
//        mediaPlayer.play();

    }


    @Override
    public void onPause() {
//        stopRecordingVideo();
//        if (mTimer != null) {
//            mTimer.cancel();
//            mTimer = null;
//        }
        super.onPause();
        mediaPlayer.stop();
    }

}