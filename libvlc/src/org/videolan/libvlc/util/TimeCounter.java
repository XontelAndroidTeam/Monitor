package org.videolan.libvlc.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

public class TimeCounter extends Timer {
    private TextView textView ;
    private long elapsedTime;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    public TimeCounter(TextView textView) {
        super();
        this.textView = textView;
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public void count(){
        scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(dateFormat.format(elapsedTime));
                        elapsedTime += 1000;
                    }
                });

            }
        }, 0, 1000);
    }
    public void stop(){
        cancel();
        elapsedTime = 0;
    }

}
