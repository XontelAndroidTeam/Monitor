package org.videolan.libvlc.util;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.videolan.R;

public class RecordFlashingCircle extends ImageView {
    ObjectAnimator objAnimator ;
    public RecordFlashingCircle(Context context) {
        super(context);
        init();
    }

    public RecordFlashingCircle(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RecordFlashingCircle(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public RecordFlashingCircle(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        setupAnimation();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(objAnimator != null){
            objAnimator.end();
        }
    }

    private void setupAnimation() {
        objAnimator = ObjectAnimator.ofFloat(this, "alpha", 0f, 1f);
        objAnimator.setDuration(200);
        objAnimator.setRepeatMode(ValueAnimator.REVERSE);
        objAnimator.setRepeatCount(Animation.INFINITE);
        objAnimator.start();
    }
}
