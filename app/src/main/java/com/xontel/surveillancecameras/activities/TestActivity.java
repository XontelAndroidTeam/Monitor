package com.xontel.surveillancecameras.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.MediaController;
import android.widget.VideoView;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
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

import org.MediaPlayer.PlayM4.Player;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class TestActivity extends BaseActivity implements MainMvpView, SurfaceHolder.Callback {

    private ActivityTestBinding binding;
    private List<IpCam> cams = new ArrayList<>();
    private CamsAdapter gridAdapter;
    public static final int CHANNEL_TYPE_DIGIT = 0;
    public static final byte CHANNEL_ENABLED = 1;
    private SurfaceView videoView;
    private HCNetSDK hcNetSdk;
    private Player player;
    private int playPort = -1 ;
    private int gridCount = 4;
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
    }

    @Override
    protected void setUp() {

    }

    private void initUI() {
        setupPlayer();

    }

    private void setupPlayer() {
        videoView.getHolder().addCallback(this);
        new LoadingDevicesTask().execute();
    }

    private class LoadingDevicesTask extends AsyncTask{

        @Override
        protected Object doInBackground(Object... params) {

            final long begin = System.currentTimeMillis();

            player = Player.getInstance();


            hcNetSdk =  HCNetSDK.getInstance();

            hcNetSdk.NET_DVR_Init();

            hcNetSdk.NET_DVR_SetConnectTime( Integer.MAX_VALUE );

            hcNetSdk.NET_DVR_SetExceptionCallBack(exceptionCallback);


            // get play port
            playPort = player.getPort();
            catchErrorIfNecessary();

            // ----------------------------------------------------------------

            NET_DVR_DEVICEINFO_V30 dvr_deviceinfo = new NET_DVR_DEVICEINFO_V30();
            int userId = hcNetSdk.NET_DVR_Login_V30(
                    "192.168.1.123", 8000,
                    "admin", "X0nPAssw0rd_000",
                    dvr_deviceinfo );

//            DebugTools.dump( dvr_deviceinfo );
            System.out.println( "Attempting to login: userId " + userId );
            System.out.println(
                    String.format( "DeviceInfo: byChanNum=%s, byIPChanNum=%s",
                            dvr_deviceinfo.byChanNum, dvr_deviceinfo.byIPChanNum ) );

            catchErrorIfNecessary();

            NET_DVR_IPPARACFG_V40 ipParaCfg = new NET_DVR_IPPARACFG_V40();

            // UserId, Command, ChannelNo., Out
            hcNetSdk.NET_DVR_GetDVRConfig( userId, HCNetSDK.NET_DVR_GET_IPPARACFG_V40, 0, ipParaCfg );
            int counter = 0;
            System.out.println( "-------------------------------------" );

            for ( NET_DVR_IPCHANINFO entry : ipParaCfg.struIPChanInfo ) {
                if ( CHANNEL_ENABLED == entry.byEnable ) {
//                    DebugTools.dump( entry );
                }
            }


//            DebugTools.dump( ipParaCfg );
            catchErrorIfNecessary();


            // ----------------------------------------------------------------

            NET_DVR_CLIENTINFO clientInfo = new NET_DVR_CLIENTINFO();



            clientInfo.lChannel = 34;


            clientInfo.lLinkMode = 0;
            clientInfo.sMultiCastIP = null;

            // UserId, ClientInfo, RealplayCallback, Blocked
            final int returned = hcNetSdk.NET_DVR_RealPlay_V30(userId, clientInfo, realplayCallback, true);
            System.out.println( "Living: " + returned );
            catchErrorIfNecessary();

            return null;

        }
    }

    public void catchErrorIfNecessary() {
        int code = hcNetSdk.NET_DVR_GetLastError();
        if ( 0 != code ) System.out.println( "Error: " + code );
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        System.out.println( "surfaceCreated: " );
        if ( holder.getSurface().isValid() ) {
            if ( ! Player.getInstance().setVideoWindow( playPort, 0, holder.getSurface() ) ) {
                System.out.println( "player set video window failed!" );
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        System.out.println( "surfaceChanged: " );
        if ( holder.getSurface().isValid() ) {
            if ( ! Player.getInstance().setVideoWindow( playPort, 0, null ) ) {
                System.out.println( "player release video window failed!" );
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
    private ExceptionCallBack exceptionCallback = new ExceptionCallBack() {

        @Override
        public void fExceptionCallBack(int code, int userId, int handle) {
            System.out.println(
                    String.format(
                            "ExceptionCallBack::fExceptionCallBack( 0x%h, %s, %s )", code, userId, handle ) );
        }
    };

    private RealPlayCallBack realplayCallback = new RealPlayCallBack() {

        @Override
        public void fRealDataCallBack(int handle, int dataType, byte[] buffer, int bufferSize) {
            System.out.println( String.format( "fRealDataCallBack{ handle : %s, dataType : %s, bufferSize : %s }",
                    handle, dataType, bufferSize ) );

            int i = 0;

            switch ( dataType ) {
                case HCNetSDK.NET_DVR_SYSHEAD:

                    if ( -1 == (playPort = Player.getInstance().getPort() ) ) {
                        System.out.println( "Can't get play port!" );

                        return;
                    }

                    if ( 0 < bufferSize ) {
                        if ( openPlayer( buffer, bufferSize ) ) {
                            System.out.println( "Open player successfully." );
                        } else {
                            System.out.println( "Open player failed." );
                        }
                    }

                    break;

                case HCNetSDK.NET_DVR_STREAMDATA:
                case HCNetSDK.NET_DVR_STD_VIDEODATA:
                case HCNetSDK.NET_DVR_STD_AUDIODATA:

                    if ( 0 < bufferSize && -1 != playPort ) {
                        try {
                            for ( i = 0; i < 400; i++) {
                                if ( Player.getInstance().inputData( playPort, buffer,
                                        bufferSize ) ) {
                                    System.out.println( "Played successfully." );
                                    break;
                                }

                                System.out.println( "Playing failed." );

                                Thread.sleep( 10 );
                            }
                        } catch (Exception e) {

                        }

                        if ( i == 400 ) {
                            System.out.println( "inputData failed" );
                        }

                    }

            }


        }
    };

    private static final int PLAYING_BUFFER_SIZE = 1024 * 1024 * 4;

    private boolean openPlayer(byte[] buffer, int bufferSize) {


        if ( ! Player.getInstance().setStreamOpenMode(playPort, Player.STREAM_FILE ) ) {
            System.out.println( "The player set stream mode failed!" );
            return false;
        }

        if ( ! Player.getInstance().openStream( playPort, buffer, bufferSize, PLAYING_BUFFER_SIZE ) ) {
            Player.getInstance().freePort( playPort );
            playPort = -1;

            return false;
        }

        Player.getInstance().setStreamOpenMode( playPort, Player.STREAM_FILE );

        System.out.println( "We are using " + videoView.getHolder() + " as a Displayer." );

        if ( ! Player.getInstance().play( playPort, videoView.getHolder().getSurface() ) ) {
            Player.getInstance().closeStream( playPort );
            Player.getInstance().freePort( playPort );

            playPort = -1;

            return false;
        }

        return true;
    }

    class LoginTask extends AsyncTask<Void, Void, Boolean>
    {
        @Override
        protected Boolean doInBackground(Void... params)
        {
//            boolean loginSuccess = hikvisionSdk.login();

            return false;
        }

        @Override
        protected void onPostExecute(Boolean success)
        {
//            customProgressDialog.dismiss();

            if(success)
            {

            }
        }
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

    public void hideProgressView() {
    }
}