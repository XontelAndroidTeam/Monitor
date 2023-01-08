package com.xontel.surveillancecameras.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Patterns;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;

public abstract class TextField extends TextInputEditText implements TextWatcher, ObservableOnSubscribe<String> {
    public static final String TAG = TextField.class.getSimpleName();
    private Context mContext;
    private ObservableEmitter<String> mEmitter ;

    public TextField(@NonNull Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    @Override
    public void subscribe(@NonNull ObservableEmitter<String> emitter) throws Throwable {
        this.mEmitter = emitter;
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
        Observable.create(this)
                .debounce(3, TimeUnit.SECONDS)
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



    public void setText(String text){
        super.setText(text);
        setSelection(getText().length());
    }


    public abstract boolean isPatternMatched();

    public abstract int getErrorMessageStringId();

    private void clearErrorMessage() {
        ViewGroup viewGroup = ((ViewGroup) this.getParent());
        if(viewGroup != null){
            ((TextInputLayout) viewGroup.getParent()).setErrorEnabled(false);
        }
    }

    private void showError() {
        ViewGroup viewGroup = ((ViewGroup) this.getParent());
        if(viewGroup != null) {
            ((TextInputLayout) viewGroup.getParent()).setError(mContext.getString(getErrorMessageStringId()));
        }
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Log.v(TAG, s.toString());
        clearErrorMessage();
        if(s.length() !=0){
            mEmitter.onNext(s.toString());
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
