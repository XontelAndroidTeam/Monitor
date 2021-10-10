package com.xontel.surveillancecameras.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.hikvision.netsdk.ExceptionCallBack;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_CLIENTINFO;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;
import com.hikvision.netsdk.NET_DVR_IPCHANINFO;
import com.hikvision.netsdk.NET_DVR_IPPARACFG_V40;
import com.hikvision.netsdk.RealPlayCallBack;
import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.adapters.CamsAdapter;
import com.xontel.surveillancecameras.adapters.GridAdapter;
import com.xontel.surveillancecameras.base.BaseActivity;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.databinding.ActivityTestBinding;
import com.xontel.surveillancecameras.hikSDK.HikvisionSdk;
import com.xontel.surveillancecameras.presenters.MainMvpPresenter;
import com.xontel.surveillancecameras.presenters.MainMvpView;
import com.xontel.surveillancecameras.utils.HikUtil;

import org.MediaPlayer.PlayM4.Player;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class TestActivity extends BaseActivity implements MainMvpView {

    private ActivityTestBinding binding;
    private List<IpCam> cams = new ArrayList<>();
    private CamsAdapter gridAdapter;
    private SurfaceView videoView;
    private static final int PLAY_HIK_STREAM_CODE = 1001;
    private static final String IP_ADDRESS = "192.168.1.123";
    private static final int PORT = 8000;
    private static final String USER_NAME = "admin";
    private static final String PASSWORD = "X0nPAssw0rd_000";

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case PLAY_HIK_STREAM_CODE:
                    hikUtil.playOrStopStream();
                    break;
                default:
                    break;
            }
            return false;
        }
    });
    private HikUtil hikUtil;
    @Inject
    MainMvpPresenter<MainMvpView> mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_test);
        getActivityComponent().inject(this);
        mPresenter.onAttach(this);
        videoView = findViewById(R.id.videoView);
        initUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDetach();
        hikUtil.playOrStopStream();
    }

    @Override
    protected void setUp() {

    }

    private void initUI() {
        setupPlayer();

    }

    private void setupPlayer() {
        Uri uri = Uri.parse("rtsp://admin:maaan@192.168.1.113/Streaming/Channels/1");
        Log.e("uri" , uri.getHost()+"\n"+uri.getPort()+"\n"+uri.getUserInfo());
        HikUtil.initSDK();
        hikUtil = new HikUtil();
        hikUtil.initView(videoView);
        hikUtil.setDeviceData(IP_ADDRESS, PORT, USER_NAME, PASSWORD);
        hikUtil.loginDevice(mHandler, PLAY_HIK_STREAM_CODE);
    }


    @Override
    public void onCreatingCam() {

    }

    @Override
    public void onInsertingCamera() {

    }

    @Override
    public void onUpdatingCamera() {

    }

    @Override
    public void onDeletingCamera() {

    }

    @Override
    public void onGettingCamera(IpCam response) {

    }

    @Override
    public void onGettingAllCameras(List<IpCam> response) {
        cams.clear();
        cams.addAll(response);
        gridAdapter.notifyDataSetChanged();
    }


}