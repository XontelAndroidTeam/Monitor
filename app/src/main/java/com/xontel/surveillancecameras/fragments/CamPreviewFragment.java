package com.xontel.surveillancecameras.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_IPPARACFG_V40;
import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.activities.AddCamActivity;
import com.xontel.surveillancecameras.activities.CamerasActivity;
import com.xontel.surveillancecameras.base.BaseFragment;
import com.xontel.surveillancecameras.dahua.DahuaSinglePlayer;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.databinding.ActivityCamerasBinding;
import com.xontel.surveillancecameras.databinding.FragmentCameraBinding;
import com.xontel.surveillancecameras.dialogs.CamDetailsDialog;
import com.xontel.surveillancecameras.hikvision.HIKSinglePlayer;
import com.xontel.surveillancecameras.utils.CamDeviceType;
import com.xontel.surveillancecameras.utils.StorageHelper;
import com.xontel.surveillancecameras.viewModels.MainViewModel;
import com.xontel.surveillancecameras.viewModels.ViewModelProviderFactory;

import org.jetbrains.annotations.NotNull;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import javax.inject.Inject;

public class CamPreviewFragment extends BaseFragment {
    public static final String TAG = CamPreviewFragment.class.getSimpleName();
    private MediaPlayer mediaPlayer;
    private static final int REQUEST_CODE_EDIT_CAM = 44;
    private  HIKSinglePlayer singlePlayer;
    private HIKSinglePlayer hikSinglePlayer ;
    private DahuaSinglePlayer dahuaSinglePlayer;
    private MainViewModel mainViewModel;
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
    private ActivityCamerasBinding parentBinding;
    @Inject
    ViewModelProviderFactory providerFactory;

    public CamPreviewFragment() {
        // Required empty public constructor
    }


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
        getFragmentComponent().inject(this);
        mainViewModel = new ViewModelProvider(requireActivity(), providerFactory).get(MainViewModel.class);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            cam = getArguments().getParcelable(KEY_CAM_INFO);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCameraBinding.inflate(inflater);
        parentBinding = ((CamerasActivity)requireActivity()).getViewRoot();
        playCamStream();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void setUp(View view) {

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
      //  mediaPlayer = new MediaPlayer(getContext());
        //TODO put the videos public dir
      //  mediaPlayer.setRecordingDirectory(StorageHelper.getMediaDirectory(getContext(), StorageHelper.VIDEOS_DIRECTORY_NAME).getAbsolutePath());
      //  mediaPlayer.attachViews(binding.vlcLayout);
      //  final Media media = new Media(mediaPlayer.getLibVLCInstance(), Uri.parse(cam.getUrl()));
       // media.addCommonOptions();
     //   mediaPlayer.setMedia(media);
     //   media.release();
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

    private void playCamStream() {
        if (cam.getType() == CamDeviceType.HIKVISION.getValue()){
             hikSinglePlayer =  new HIKSinglePlayer(cam.getChannel(),cam.getLoginId(),cam.getType());
            hikSinglePlayer.initView(binding.surfaceView);
        }else if(cam.getType() == CamDeviceType.DAHUA.getValue()){
            dahuaSinglePlayer =  new DahuaSinglePlayer(cam.getChannel(),cam.getLoginId(),cam.getType());
            dahuaSinglePlayer.initView(binding.surfaceView);
        }else{

        }
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
//       binding.llRecordPanel.setVisibility(View.VISIBLE);
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


    private void stopIndicatorAnimation() {}

    public void capturePhoto() {}

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

    @Override
    public void onResume() {
        parentBinding.btnDetails.setOnClickListener(view ->{ showCamDetails();});
        parentBinding.btnShare.setOnClickListener(view ->{ shareCam(); });
        parentBinding.btnEdit.setOnClickListener(view ->{ editCam(); });
        parentBinding.btnDelete.setOnClickListener(view ->{ deleteCam(); });
        parentBinding.btnSnapshot.setOnClickListener(view ->{ captureImage(); });
        parentBinding.btnRecord.setOnClickListener(view ->{ captureVideo();});
        super.onResume();
    }

    private void showCamDetails() {
        CamDetailsDialog camDetailsDialog = new CamDetailsDialog(requireContext(), cam);
        camDetailsDialog.show();
    }

    private void shareCam() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, "Sharing URL");
        //  i.putExtra(Intent.EXTRA_TEXT, cams.get(camPosition).getUrl());
        startActivity(Intent.createChooser(i, getString(R.string.share_url)));
    }

    private void editCam() {
        Intent intent = new Intent(requireActivity(), AddCamActivity.class);
        //intent.putExtra(AddCamActivity.KEY_CAMERA, cams.get(camPosition));
        startActivityForResult(intent, REQUEST_CODE_EDIT_CAM);
    }

    private void deleteCam() {
        new MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
                .setTitle(R.string.delete_camera)
                .setMessage(R.string.are_you_sure_delete)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                      //  mPresenter.deleteCamera(cams.get(camPosition));
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void captureVideo(){
        if (cam.getType() == CamDeviceType.HIKVISION.getValue()){
        }else if(cam.getType() == CamDeviceType.DAHUA.getValue()){
            dahuaSinglePlayer.captureVideo();
        }else{
        }
    }

    private void captureImage(){
        if (cam.getType() == CamDeviceType.HIKVISION.getValue()){
            hikSinglePlayer.captureFrame();
        }else if(cam.getType() == CamDeviceType.DAHUA.getValue()){
            dahuaSinglePlayer.captureFrame();
        }else{
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}