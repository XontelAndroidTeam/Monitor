package com.xontel.surveillancecameras.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Patterns;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;

public abstract class TextField extends TextInputEditText {
    private Context mContext;

    public TextField(@NonNull Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public TextField(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public TextField(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private void init(){
        Observable.create((ObservableOnSubscribe<String>) emitter -> addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                clearErrorMessage();
                if(charSequence.length() != 0 ){
                    emitter.onNext(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        })).debounce(3, TimeUnit.SECONDS)
                .map(text -> isPatternMatched())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        isPatternMatched-> {
                            if(!isPatternMatched){
                                showError();
                            }
                        }
                );



    }

    public boolean isValid(){
        return !getText().toString().isEmpty() ;
    }


    public void validate(){

    }


    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        if(visibility == GONE) getText().clear();
    }

    public void setText(String text){
        super.setText(text);
        setSelection(getText().length());
    }


    public abstract boolean isPatternMatched();

    public abstract int getErrorMessageStringId();

    private void clearErrorMessage() {
        ((TextInputLayout) ((ViewGroup) this.getParent()).getParent()).setErrorEnabled(false);
    }

    private void showError() {
        ((TextInputLayout) ((ViewGroup) this.getParent()).getParent()).setError(mContext.getString(getErrorMessageStringId()));
    }


}
