package org.videolan.libvlc.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.videolan.R;
import org.videolan.libvlc.MediaPlayer;

public class VolumeControllerView extends LinearLayout {
    private MediaPlayer mediaPlayer ;
    private ImageView volumeIcon;
    private SeekBar volumeBar ;
    private boolean isExpanded;
    public VolumeControllerView(Context context) {
        super(context);
        init();
    }

    public VolumeControllerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VolumeControllerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public VolumeControllerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    public void expandLayout(){

    }


    public void init(){
        View view = inflate(getContext(), R.layout.sound_controller, this);
//         volumeIcon = view.findViewById(R.id.iv_sound);
//         volumeIcon.setOnClickListener(new OnClickListener() {
//             @Override
//             public void onClick(View view) {
//                 if(isExpanded){
//
//                 }else {
//                     volumeBar.setVisibility(VISIBLE);
//                     isExpanded = true;
//                 }
//             }
//         });
//         volumeBar = view.findViewById(R.id.seek_bar);
//         volumeBar.setProgress(mediaPlayer.getVolume());
//         volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//             @Override
//             public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                 mediaPlayer.setVolume(i);
//             }
//
//             @Override
//             public void onStartTrackingTouch(SeekBar seekBar) {
//
//             }
//
//             @Override
//             public void onStopTrackingTouch(SeekBar seekBar) {
//
//             }
//         });

    }
}
