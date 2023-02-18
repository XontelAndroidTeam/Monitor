package com.xontel.surveillancecameras.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.activities.CamerasActivity;
import com.xontel.surveillancecameras.base.BaseFragment;
import com.xontel.surveillancecameras.dahua.DahuaSinglePlayer;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.databinding.ActivityCamerasBinding;
import com.xontel.surveillancecameras.databinding.FragmentCameraBinding;
import com.xontel.surveillancecameras.dialogs.CamDetailsDialog;
import com.xontel.surveillancecameras.hikvision.HIKPlayer;
import com.xontel.surveillancecameras.utils.CamDeviceType;
import com.xontel.surveillancecameras.utils.StorageBroadcastReceiver;
import com.xontel.surveillancecameras.utils.StorageHelper;
import com.xontel.surveillancecameras.viewModels.MainViewModel;
import com.xontel.surveillancecameras.viewModels.ViewModelProviderFactory;
import com.xontel.surveillancecameras.vlc.VlcSinglePlayer;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import javax.inject.Inject;

public class CamPreviewFragment extends BaseFragment {
    public static final String TAG = CamPreviewFragment.class.getSimpleName();
    private VlcSinglePlayer vlcSinglePlayer;
    private HIKPlayer hikSinglePlayer ;
    private DahuaSinglePlayer dahuaSinglePlayer;
    private MainViewModel mainViewModel;
    private int recordTime = 0;
    private FileObserver observer;
    private SimpleDateFormat mSimpleDateFormat;
    private Timer mTimer;
    private Boolean isPicture = false;
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


    @SuppressLint("SimpleDateFormat")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCameraBinding.inflate(inflater);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        parentBinding = ((CamerasActivity)requireActivity()).getViewRoot();
        mSimpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        TimeZone tz = TimeZone.getTimeZone("UTC");
        mSimpleDateFormat.setTimeZone(tz);
        playCamStream();
//        mainViewModel.isRecording.observe(getViewLifecycleOwner(), aBoolean -> {
//            if (aBoolean){
//                parentBinding.btnRecord.setClickable(false);
//                parentBinding.btnSnapshot.setClickable(false);
//            }else {
//                parentBinding.btnRecord.setClickable(true);
//                parentBinding.btnSnapshot.setClickable(true);
//            }
//        });

        binding.recordLayout.btnStop.setOnClickListener(view -> {
            stopRecordingVideo();
        });

        StorageBroadcastReceiver.refreshRemovable.observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean){
                watchFile();
                StorageBroadcastReceiver.refreshRemovable.setValue(false);
            }

        });
        return binding.getRoot();
    }

    private void watchFile() {
        if (observer != null){ observer.stopWatching();}
        String path = StorageHelper.getMediaDirectory(requireContext(),isPicture ? Environment.DIRECTORY_PICTURES :Environment.DIRECTORY_MOVIES ).getAbsolutePath();
        observer = new FileObserver(path) {
        @Override
        public void onEvent(int event, String file) {
            if(event == FileObserver.CREATE && !file.equals(".probe") && file.toLowerCase().endsWith("mp4")){
                File pathFile = new File(path, file);
                MediaScannerConnection.scanFile(requireContext(), new String[]{pathFile.getAbsolutePath()}, new String[]{"video/*"}, (s, uri) -> Log.i("TATZ", "onScanCompleted_video: "+uri));
            }else if(event == FileObserver.CREATE && !file.equals(".probe") ){
                File pathFile = new File(path, file);
                MediaScannerConnection.scanFile(requireContext(), new String[]{pathFile.getAbsolutePath()}, new String[]{"image/*"}, (s, uri) -> Log.i("TATZ", "onScanCompleted_image: "+uri));
            }
        }
    };
        observer.startWatching();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void setUp(View view) { }


    private void playCamStream() {
        if (cam.getType() == CamDeviceType.HIKVISION.getValue()){
//            hikSinglePlayer =  new HIKSinglePlayer(cam.getChannel(),cam.getLoginId(),requireContext());
//            hikSinglePlayer.initView(binding.surfaceView);
        }else if(cam.getType() == CamDeviceType.DAHUA.getValue()){
//            dahuaSinglePlayer =  new DahuaSinglePlayer(cam.getChannel(),cam.getLoginId(),requireContext());
//            dahuaSinglePlayer.initView(binding.surfaceView);
        }else{
            binding.vlcLayout.setVisibility(View.VISIBLE);
            binding.surfaceView.setVisibility(View.GONE);
//            vlcSinglePlayer = new VlcSinglePlayer(requireContext());
//            vlcSinglePlayer.initVlcPlayer(cam.get(),binding.vlcLayout);
        }
    }

    private void showSuccessMessage() {
        Toast.makeText(getContext(), R.string.record_saved, Toast.LENGTH_LONG).show();
    }

    private void showFailedMessage() {
        Toast.makeText(getContext(), R.string.record_failed, Toast.LENGTH_LONG).show();
    }

    private void stopRecordingVideo() {
        binding.recordLayout.llRecordPanel.setVisibility(View.GONE);
//        mainViewModel.toggleVideoRecord();
        if (mTimer != null){mTimer.cancel();}
        stopCaptureVideo();
    }


    private void startRecordingVideo() {
        enableVideoRecordingView();
//        mainViewModel.toggleVideoRecord();
        captureVideo();
    }


    private void enableVideoRecordingView() {
       binding.recordLayout.llRecordPanel.setVisibility(View.VISIBLE);
        mTimer = new Timer();
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        binding.recordLayout.timer.setText(mSimpleDateFormat.format(new Date(recordTime)));
                        recordTime += 1000;
                    }
                });

            }
        }, 0, 1000);
    }



    @Override
    public void onResume() {
        parentBinding.btnDetails.setOnClickListener(view ->{ showCamDetails();});
        parentBinding.btnShare.setOnClickListener(view ->{ shareCam(); });
        parentBinding.btnSnapshot.setOnClickListener(view ->{ captureImage(); });
        parentBinding.btnRecord.setOnClickListener(view ->{ startRecordingVideo();});
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
//        i.putExtra(Intent.EXTRA_TEXT, cam.ge());
        startActivity(Intent.createChooser(i, getString(R.string.share_url)));
    }

   // private void editCam() {
     //   Intent intent = new Intent(requireActivity(), AddNewDeviceActivity.class);
   //     intent.putExtra(AddNewDeviceActivity.KEY_DEVICE, cam);
   //     startActivityForResult(intent, REQUEST_CODE_EDIT_CAM);
   // }

  //  private void deleteCam() {
   //     new MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
   //             .setTitle(R.string.delete_camera)
   //             .setMessage(R.string.are_you_sure_delete)
      //          .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
     //               public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                      //  mPresenter.deleteCamera(cams.get(camPosition));
    //                }
     //           })
      //          .setNegativeButton(android.R.string.no, null)
      //          .show();
  //  }

    private void captureVideo(){
        isPicture = false;
        watchFile();
        if (cam.getType() == CamDeviceType.HIKVISION.getValue()){}
        else if(cam.getType() == CamDeviceType.DAHUA.getValue()){dahuaSinglePlayer.captureVideo();}
        else{vlcSinglePlayer.vlcRecording();}
    }

    private void stopCaptureVideo(){
        if (cam.getType() == CamDeviceType.HIKVISION.getValue()){}
        else if(cam.getType() == CamDeviceType.DAHUA.getValue()){dahuaSinglePlayer.stopCaptureVideo();}
        else{vlcSinglePlayer.vlcStopRecording();}
    }

    private void captureImage(){
        isPicture = true;
        watchFile();
        if (cam.getType() == CamDeviceType.HIKVISION.getValue()){hikSinglePlayer.takeSnapshot();}
        else if(cam.getType() == CamDeviceType.DAHUA.getValue()){dahuaSinglePlayer.takeSnapshot();}
        else{vlcSinglePlayer.vlcCaptureImage();}
        ((CamerasActivity)requireActivity()).showMessage(getString(R.string.Take_Picture));
    }




    @Override
    public void onDestroy() {
        if (mTimer != null){mTimer.cancel();}
        if (cam.getType() == CamDeviceType.HIKVISION.getValue()){ //hikSinglePlayer.cleanUp();
        }
        else if(cam.getType() == CamDeviceType.DAHUA.getValue()){ dahuaSinglePlayer.stopStream();}
        else{vlcSinglePlayer.removeVlcPlayer();}
        if(observer != null){observer.stopWatching();}
        super.onDestroy();
    }

}