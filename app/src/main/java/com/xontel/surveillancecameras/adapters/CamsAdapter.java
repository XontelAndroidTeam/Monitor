package com.xontel.surveillancecameras.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eyalbira.loadingdots.LoadingDots;
import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.activities.AddCamActivity;
import com.xontel.surveillancecameras.activities.CamerasActivity;
import com.xontel.surveillancecameras.activities.MainActivity;
import com.xontel.surveillancecameras.base.BaseViewHolder;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.fragments.GridFragment;
import com.xontel.surveillancecameras.utils.HikUtil;
import com.xontel.surveillancecameras.utils.VideoHelper;

import org.videolan.libvlc.MediaPlayer;

import java.util.ArrayList;
import java.util.List;


public class CamsAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private List<IpCam> cams;
    private Context context;
    private int gridCount;
    private GridFragment gridFragment;
    private LifecycleObservable lifecycleObservable;
    private List<VideoHelper> videoHelpers;
    private static final int ITEM_CAM = 0;
    private static final int ITEM_ADD_CAM = 1;


    public CamsAdapter(GridFragment gridFragment, List<IpCam> cams, List<VideoHelper> videoHelpers, Context context, int gridCount) {
        this.cams = cams;
        this.context = context;
        this.gridFragment = gridFragment;
        this.gridCount = gridCount;
        lifecycleObservable = new LifecycleObservable();
        this.videoHelpers = videoHelpers;
    }


    public List<IpCam> getCams() {
        return cams;
    }

    public void addItems(List<IpCam> cams) {
        this.cams.clear();
        this.cams.addAll(cams);
        notifyDataSetChanged();
    }

    public void addItem(IpCam cam) {
        cams.add(cam);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case ITEM_CAM:
                view = LayoutInflater.from(context).inflate(R.layout.item_cam, parent, false);
                GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();
                lp.height = (parent.getMeasuredHeight() / (int) Math.sqrt(gridCount)) - 10;
                view.setLayoutParams(lp);
                return new CamsViewHolder(view);

            case ITEM_ADD_CAM:
            default:
                view = LayoutInflater.from(context).inflate(R.layout.item_add_cam, parent, false);
                GridLayoutManager.LayoutParams lp2 = (GridLayoutManager.LayoutParams) view.getLayoutParams();
                lp2.height = parent.getMeasuredHeight() / (int) Math.sqrt(gridCount);
                view.setLayoutParams(lp2);
                return new AddCamViewHolder(view);

        }

    }

    @Override
    public int getItemViewType(int position) {
        return position < cams.size() ? ITEM_CAM : ITEM_ADD_CAM;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.onBind(position);
    }

    public void pauseAll() {
        lifecycleObservable.lifecycleStatus(LifecycleObservable.ON_PAUSE);
    }

    public void resumeAll() {
        lifecycleObservable.lifecycleStatus(LifecycleObservable.ON_RESUME);
    }


    @Override
    public int getItemCount() {
        return gridCount;
    }

    public class CamsViewHolder extends BaseViewHolder implements GridFragment.LifecycleCallbacks {
        private static final int PLAY_HIK_STREAM_CODE = 1001;
        private IpCam ipCam;
        private TextView camName;
        private TextView textError;
        private VideoHelper videoHelper;
        private FrameLayout frameLayout;
        private LoadingDots loadingBar;
        private FrameLayout mVideoSurfaceFrame;
        private ViewStub stub;
        private SurfaceView mSurfaceView;


        public CamsViewHolder(View itemView) {
            super(itemView);
            // observe onPause to pause all together
//            lifecycleObservable.addObserver(new Observer() {
//                @Override
//                public void update(Observable o, Object arg) {
//                    //                int lifecycleStatus = ((LifecycleObservable) o).status;
//                    if (((Integer) arg).intValue() == LifecycleObservable.ON_PAUSE) {
//                        if (videoHelper != null) {
//                            videoHelper.onStop();
//                            videoHelper.onDestroy();
//                        }
//                    } else {
//                        initVlcPlayer()
//                    }
//
//                }
//            });
//            vlcVideoLayout = itemView.findViewById(R.id.video_layout);
//            progressDialog = itemView.findViewById(R.id.loading_dialog);
//            mSurfaceView = itemView.findViewById(R.id.surface_view);
            mVideoSurfaceFrame = itemView.findViewById(R.id.video_surface_frame);
            stub = itemView.findViewById(R.id.surface_stub);
            frameLayout = itemView.findViewById(R.id.video_surface_frame);
            camName = itemView.findViewById(R.id.tv_cam_name);
            textError = itemView.findViewById(R.id.tv_error);
            loadingBar = itemView.findViewById(R.id.loading);
        }

        @Override
        public void onBind(int position) {
            super.onBind(position);
            IpCam ipCam = cams.get(position);
            camName.setText(ipCam.getName());
            if (gridFragment.isResumed())
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initVlcPlayer();
                    }
                }, getAdapterPosition()* 10);

//          initHikPlayer();
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, CamerasActivity.class);
                ArrayList<IpCam> cams = new ArrayList<>();
                cams.add(ipCam);
                intent.putParcelableArrayListExtra(CamerasActivity.KEY_CAMERAS, cams);
                context.startActivity(intent);
            });

        }

        @Override
        protected void clear() {

        }


        private void initVlcPlayer() {
            ipCam = cams.get(getAdapterPosition());
            videoHelper = new VideoHelper(context, mVideoSurfaceFrame, stub, itemView);
            videoHelper.setVIDEO_URL(ipCam.getUrl());
            videoHelpers.add(videoHelper);
            videoHelper.getMediaPlayer().setEventListener(new MediaPlayer.EventListener() {
                float buffered = 0.0f;

                @Override
                public void onEvent(MediaPlayer.Event event) {
                    if (event.type == MediaPlayer.Event.Buffering) {
                        buffered = event.getBuffering();
                    }
                    if (buffered == 100.0) {
                        loadingBar.setVisibility(View.GONE);
                        Log.d("EVENT", event.type + "");
                    }

                    if (event.type == MediaPlayer.Event.EncounteredError) {
                        Log.d("EVENT", event.type + "");
                        loadingBar.setVisibility(View.GONE);
                        textError.setVisibility(View.VISIBLE);
                        textError.setText(R.string.error_occurred);
                    }
                }
            });
            videoHelper.onStart();

        }

        private void initHikPlayer() {
            ipCam = cams.get(getAdapterPosition());
            HikUtil hikUtil = new HikUtil();
            Handler mHandler = new Handler(new Handler.Callback() {
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
            HikUtil.initSDK();
            hikUtil.initView(mSurfaceView);
            Uri uri = Uri.parse(ipCam.getUrl());
            hikUtil.setDeviceData(uri.getHost(), uri.getPort() == -1 ? 8000 : uri.getPort(), uri.getUserInfo().split(":")[0], uri.getUserInfo().split(":")[1]);
            hikUtil.loginDevice(mHandler, PLAY_HIK_STREAM_CODE);
        }

        private void hideProgressDialog() {

        }


        @Override
        public void onResumed() {
            initVlcPlayer();
        }
    }

    public class AddCamViewHolder extends BaseViewHolder {
        public AddCamViewHolder(@NonNull View itemView) {
            super(itemView);
            if(!(((MainActivity)context).getCams().size() < 24)){
                itemView.setVisibility(View.GONE);
            }
        }

        @Override
        public void onBind(int position) {
            super.onBind(position);
            itemView.setOnClickListener(v -> {
                ((MainActivity)context).addNewCam();
            });

        }

        @Override
        protected void clear() {

        }
    }

    class LifecycleObservable extends java.util.Observable {
        public static final int ON_PAUSE = 1;
        public static final int ON_RESUME = 0;
        public Integer status = ON_RESUME;

        void lifecycleStatus(Integer status) {
            this.status = status;
            setChanged();
            notifyObservers(status);
        }
    }


}
