package com.xontel.surveillancecameras.vlc;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.xontel.surveillancecameras.utils.StorageHelper;

import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.VLCVideoLayout;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class VlcSinglePlayer {
    private MediaPlayer mediaPlayer;
    private Context context;

    public VlcSinglePlayer(Context context) {
        this.context = context;
    }

    public void initVlcPlayer(String url, VLCVideoLayout vlcLayout) {
        mediaPlayer = new MediaPlayer(context);
        mediaPlayer.setRecordingDirectory(StorageHelper.getMediaDirectory(context,Environment.DIRECTORY_MOVIES).getAbsolutePath());
        mediaPlayer.attachViews(vlcLayout);
        final Media media = new Media(mediaPlayer.getLibVLCInstance(), Uri.parse(url));
        media.addCommonOptions();
        mediaPlayer.setMedia(media);
        media.release();
        mediaPlayer.stop();
    }

    public void removeVlcPlayer(){
        if (mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }

    public void vlcRecording(){
        mediaPlayer.startRecording();
    }

    public void vlcStopRecording(){
        mediaPlayer.stopRecording();
    }

    public void vlcCaptureImage(){
        saveImageWithoutResolver(mediaPlayer.takeSnapShot(context));
    }



    private void saveImageWithoutResolver(Bitmap bitmap){
        String filename = System.currentTimeMillis() + ".png";
        File file = new File(StorageHelper.getMediaDirectory(context,Environment.DIRECTORY_PICTURES),filename);
        OutputStream fos ;
        try {
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
//            MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()}, new String[]{"image/*"}, (s, uri) -> Log.i("TATZ", "onScanCompleted: "+uri));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
