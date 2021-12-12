package com.xontel.surveillancecameras.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;

import com.xontel.surveillancecameras.R;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.util.ArrayList;
import java.util.List;

public class VideoHelper implements IVLCVout.OnNewVideoLayoutListener {

    private static final boolean USE_SURFACE_VIEW = true;
    private static final boolean ENABLE_SUBTITLES = true;
    private static final String TAG = "VideoHelper";
    private static final int SURFACE_BEST_FIT = 0;
    private static final int SURFACE_FIT_SCREEN = 1;
    private static final int SURFACE_FILL = 2;
    private static final int SURFACE_16_9 = 3;
    private static final int SURFACE_4_3 = 4;
    private static final int SURFACE_ORIGINAL = 5;
    private static int CURRENT_SIZE = SURFACE_BEST_FIT;
    private static final int video_surface_frame = R.id.video_surface_frame;
    private static final int surface_stub = R.id.surface_stub;
    private static final int subtitles_surface_stub = R.id.subtitles_surface_stub;
    private static final int texture_stub = R.id.texture_stub;

    private FrameLayout mVideoSurfaceFrame = null;
    private SurfaceView mVideoSurface = null;
    private SurfaceView mSubtitlesSurface = null;
    private TextureView mVideoTexture = null;
    private View mVideoView = null;

    private String VIDEO_URL = "";

    private final Handler mHandler = new Handler();
    private View.OnLayoutChangeListener mOnLayoutChangeListener = null;

    private LibVLC mLibVLC = null;
    private MediaPlayer mMediaPlayer = null;
    private Context context;
    private int mVideoHeight = 0;
    private int mVideoWidth = 0;
    private int mVideoVisibleHeight = 0;
    private int mVideoVisibleWidth = 0;
    private int mVideoSarNum = 0;
    private int mVideoSarDen = 0;

    private View camsViewHolder = null;

    public enum PLAYER_STATE {
        LIBVLC_NOTHINGSPECIAL,
        LIBVLC_OPENING,
        LIBVLC_BUFFERING,
        LIBVLC_PLAYING,
        LIBVLC_PAUSED,
        LIBVLC_STOPPED,
        LIBVLC_ENDED,
        LIBVLC_ERROR
    }

    //vishal
    private int videoSurfaceFrameWidth = 0;
    private int videoSurfaceFrameHeight = 0;

    private LibVLC getLibVlcInstance() {
        List<String> args = new ArrayList<String>();
        args.add("-vvv");
        args.add("--vout=android-display");
        args.add("--network-caching=33");
        args.add("--file-caching=33");
        args.add("--live-caching=33");
        args.add("--clock-synchro=0");
        args.add("--clock-jitter=0");
        args.add("--h264-fps=6");
        args.add("--avcodec-fast");
        args.add("--avcodec-threads=1");
        args.add("--no-audio");
        LibVLC libVLC = new LibVLC(context, (ArrayList<String>) args);
        return libVLC;
    }


//    public Bitmap takeScreenShot(String directory){
//        mMediaPlayer.get
//    }


    public LibVLC getmLibVLC() {
        return mLibVLC;
    }

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    public VideoHelper(Context context, FrameLayout mVideoSurfaceFrame, ViewStub viewStub, View viewHolder) {
        this.context = context;
        this.camsViewHolder = viewHolder;
        this.mLibVLC = getLibVlcInstance();
        this.mMediaPlayer = new MediaPlayer(mLibVLC);
        this.mVideoSurfaceFrame = mVideoSurfaceFrame ;
//        mVideoSurfaceFrame = (FrameLayout) viewHolder.findViewById(video_surface_frame);
        if (USE_SURFACE_VIEW) {
//            ViewStub stub = (ViewStub) viewHolder.findViewById(surface_stub);
            mVideoSurface = (SurfaceView) viewStub.inflate();

//            if (ENABLE_SUBTITLES) {
//                stub = (ViewStub) viewHolder.findViewById(subtitles_surface_stub);
//                mSubtitlesSurface = (SurfaceView) stub.inflate();
//                mSubtitlesSurface.setZOrderMediaOverlay(true);
//                mSubtitlesSurface.getHolder().setFormat(PixelFormat.TRANSLUCENT);
//            }
            mVideoView = mVideoSurface;
        } else {
//            ViewStub stub = (ViewStub) viewHolder.findViewById(texture_stub);
//            mVideoTexture = (TextureView) stub.inflate();
//            mVideoView = mVideoTexture;
        }
    }

    public String getVIDEO_URL() {
        return VIDEO_URL;
    }

    public void setVIDEO_URL(String VIDEO_URL) {
        this.VIDEO_URL = VIDEO_URL;
    }

    public void onStart() {
        final IVLCVout vlcVout = mMediaPlayer.getVLCVout();
        if (mVideoSurface != null) {
            vlcVout.setVideoView(mVideoSurface);
            if (mSubtitlesSurface != null)
                vlcVout.setSubtitlesView(mSubtitlesSurface);
        } else
            vlcVout.setVideoView(mVideoTexture);
        vlcVout.attachViews(this);
        vlcVout.addCallback(new IVLCVout.Callback() {
            @Override
            public void onSurfacesCreated(IVLCVout ivlcVout) {
                vlcVout.setWindowSize(mVideoSurfaceFrame.getMeasuredWidth(), mVideoSurfaceFrame.getMeasuredHeight());
                mMediaPlayer.setAspectRatio(mVideoSurfaceFrame.getMeasuredWidth() + ":" + mVideoSurfaceFrame.getMeasuredHeight());
            }

            @Override
            public void onSurfacesDestroyed(IVLCVout ivlcVout) {

            }
        });
        Media media = new Media(mLibVLC, Uri.parse(VIDEO_URL));
        Log.e("TAG", mLibVLC.hashCode()+"" );
        mMediaPlayer.setMedia(media);
        media.release();
        mMediaPlayer.play();

        if (mOnLayoutChangeListener == null) {
            mOnLayoutChangeListener = new View.OnLayoutChangeListener() {
                private final Runnable mRunnable = new Runnable() {
                    @Override
                    public void run() {
//                        updateVideoSurfaces();
                    }
                };

                @Override
                public void onLayoutChange(View v, int left, int top, int right,
                                           int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    if (left != oldLeft || top != oldTop || right != oldRight || bottom != oldBottom) {
                        mHandler.removeCallbacks(mRunnable);
                        mHandler.post(mRunnable);
                    }
                }
            };
        }
//        mVideoSurfaceFrame.addOnLayoutChangeListener(mOnLayoutChangeListener);
    }

    public void onStop() {
//        if (mOnLayoutChangeListener != null) {
//            mVideoSurfaceFrame.removeOnLayoutChangeListener(mOnLayoutChangeListener);
//            mOnLayoutChangeListener = null;
//        }
        mMediaPlayer.stop();
        mMediaPlayer.getVLCVout().detachViews();
    }

    public void onDestroy() {
        mMediaPlayer.release();
        mLibVLC.release();


    }

    private void changeMediaPlayerLayout(int displayW, int displayH) {
        /* Change the video placement using the MediaPlayer API */
        switch (CURRENT_SIZE) {
            case SURFACE_BEST_FIT:
                mMediaPlayer.setAspectRatio(null);
                mMediaPlayer.setScale(0);
                break;
            case SURFACE_FIT_SCREEN:
            case SURFACE_FILL: {
                Media.VideoTrack vtrack = mMediaPlayer.getCurrentVideoTrack();
                if (vtrack == null)
                    return;
                final boolean videoSwapped = vtrack.orientation == Media.VideoTrack.Orientation.LeftBottom
                        || vtrack.orientation == Media.VideoTrack.Orientation.RightTop;
                if (CURRENT_SIZE == SURFACE_FIT_SCREEN) {
                    int videoW = vtrack.width;
                    int videoH = vtrack.height;

                    if (videoSwapped) {
                        int swap = videoW;
                        videoW = videoH;
                        videoH = swap;
                    }
                    if (vtrack.sarNum != vtrack.sarDen)
                        videoW = videoW * vtrack.sarNum / vtrack.sarDen;

                    float ar = videoW / (float) videoH;
                    float dar = displayW / (float) displayH;

                    float scale;
                    if (dar >= ar)
                        scale = displayW / (float) videoW; /* horizontal */
                    else
                        scale = displayH / (float) videoH; /* vertical */
                    mMediaPlayer.setScale(scale);
                    mMediaPlayer.setAspectRatio(null);
                } else {
                    mMediaPlayer.setScale(0);
                    mMediaPlayer.setAspectRatio(!videoSwapped ? "" + displayW + ":" + displayH
                            : "" + displayH + ":" + displayW);
                }
                break;
            }
            case SURFACE_16_9:
                mMediaPlayer.setAspectRatio("16:9");
                mMediaPlayer.setScale(0);
                break;
            case SURFACE_4_3:
                mMediaPlayer.setAspectRatio("4:3");
                mMediaPlayer.setScale(0);
                break;
            case SURFACE_ORIGINAL:
                mMediaPlayer.setAspectRatio(null);
                mMediaPlayer.setScale(1);
                break;
        }
    }

    private void updateVideoSurfaces() {

        videoSurfaceFrameWidth = mVideoSurfaceFrame.getWidth();
        videoSurfaceFrameHeight = mVideoSurfaceFrame.getHeight();
        //int sw = getWindow().getDecorView().getWidth();
        int sw = videoSurfaceFrameWidth;

        //int sh = getWindow().getDecorView().getHeight();
        int sh = videoSurfaceFrameHeight;

        // sanity check
        if (sw * sh == 0) {
            Log.e(TAG, "Invalid surface size");
            return;
        }

        mMediaPlayer.getVLCVout().setWindowSize(sw, sh);

        ViewGroup.LayoutParams lp = mVideoView.getLayoutParams();
        if (mVideoWidth * mVideoHeight == 0) {
            /* Case of OpenGL vouts: handles the placement of the video using MediaPlayer API */
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
            mVideoView.setLayoutParams(lp);
            lp = mVideoSurfaceFrame.getLayoutParams();
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
            mVideoSurfaceFrame.setLayoutParams(lp);
            changeMediaPlayerLayout(sw, sh);
            return;
        }

        if (lp.width == lp.height && lp.width == ViewGroup.LayoutParams.MATCH_PARENT) {
            /* We handle the placement of the video using Android View LayoutParams */
            mMediaPlayer.setAspectRatio(null);
            mMediaPlayer.setScale(0);
        }

        double dw = sw, dh = sh;
        final boolean isPortrait = camsViewHolder.getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        if (sw > sh && isPortrait || sw < sh && !isPortrait) {
            dw = sh;
            dh = sw;
        }

        // compute the aspect ratio
        double ar, vw;
        if (mVideoSarDen == mVideoSarNum) {
            /* No indication about the density, assuming 1:1 */
            vw = mVideoVisibleWidth;
            ar = (double) mVideoVisibleWidth / (double) mVideoVisibleHeight;
        } else {
            /* Use the specified aspect ratio */
            vw = mVideoVisibleWidth * (double) mVideoSarNum / mVideoSarDen;
            ar = vw / mVideoVisibleHeight;
        }

        // compute the display aspect ratio
        double dar = dw / dh;

        switch (CURRENT_SIZE) {
            case SURFACE_BEST_FIT:
                if (dar < ar)
                    dh = dw / ar;
                else
                    dw = dh * ar;
                break;
            case SURFACE_FIT_SCREEN:
                if (dar >= ar)
                    dh = dw / ar; /* horizontal */
                else
                    dw = dh * ar; /* vertical */
                break;
            case SURFACE_FILL:
                break;
            case SURFACE_16_9:
                ar = 16.0 / 9.0;
                if (dar < ar)
                    dh = dw / ar;
                else
                    dw = dh * ar;
                break;
            case SURFACE_4_3:
                ar = 4.0 / 3.0;
                if (dar < ar)
                    dh = dw / ar;
                else
                    dw = dh * ar;
                break;
            case SURFACE_ORIGINAL:
                dh = mVideoVisibleHeight;
                dw = vw;
                break;
        }

        // set display size
        lp.width = (int) Math.ceil(dw * mVideoWidth / mVideoVisibleWidth);
        lp.height = (int) Math.ceil(dh * mVideoHeight / mVideoVisibleHeight);
        mVideoView.setLayoutParams(lp);
        if (mSubtitlesSurface != null)
            mSubtitlesSurface.setLayoutParams(lp);

        // set frame size (crop if necessary)
        lp = mVideoSurfaceFrame.getLayoutParams();
        lp.width = (int) Math.floor(dw);
        lp.height = (int) Math.floor(dh);
        mVideoSurfaceFrame.setLayoutParams(lp);

        mVideoView.invalidate();
        if (mSubtitlesSurface != null)
            mSubtitlesSurface.invalidate();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onNewVideoLayout(IVLCVout vlcVout, int width, int height, int visibleWidth, int visibleHeight, int sarNum, int sarDen) {
        mVideoWidth = width;
        mVideoHeight = height;
        mVideoVisibleWidth = visibleWidth;
        mVideoVisibleHeight = visibleHeight;
        mVideoSarNum = sarNum;
        mVideoSarDen = sarDen;
//        updateVideoSurfaces();
    }
}
