package com.xontel.surveillancecameras.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.exoplayer2.C;
import com.google.android.material.textfield.TextInputLayout;
import com.xontel.surveillancecameras.R;

public class DropDown extends TextInputLayout {
    private AutoCompleteTextView mAutoCompleteTextView;
    private String defaultText = "";

    public DropDown(@NonNull Context context) {
        super(context);
        setupLayout(context, null);
    }

    public DropDown(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setupLayout(context, attrs);
    }

    public DropDown(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupLayout(context, attrs);
    }

    public void setupLayout(@NonNull Context context, AttributeSet attributeSet) {

        View view = inflate(context, R.layout.drop_down, this);
        mAutoCompleteTextView = findViewById(R.id.slide_show_filter);
        if (attributeSet != null) {
            TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.drop_down_attributes);
            defaultText = a.getString(R.styleable.drop_down_attributes_text);
            mAutoCompleteTextView.setText(defaultText);
        }

    }
    public void setText(String text){
        mAutoCompleteTextView.setText(text, false);
    }

    public String getText(){
        return mAutoCompleteTextView.getText().toString();
    }

    public AutoCompleteTextView getAutoCompleteTextView() {
        return mAutoCompleteTextView;
    }

    public void setAdapter(ArrayAdapter adapter) {
        mAutoCompleteTextView.setAdapter(adapter);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        setBackgroundResource(R.color.black);
        final ViewGroup.LayoutParams lp = getLayoutParams();
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        setLayoutParams(lp);
    }




}
