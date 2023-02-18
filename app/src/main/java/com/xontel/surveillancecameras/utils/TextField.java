package com.xontel.surveillancecameras.utils;

import android.annotation.SuppressLint;
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
import com.xontel.surveillancecameras.R;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;

public abstract class TextField extends TextInputEditText implements TextWatcher, ObservableOnSubscribe<Boolean> {
    public static final String TAG = TextField.class.getSimpleName();
    private Context mContext;
    private ObservableEmitter<Boolean> mEmitter ;

    public TextField(@NonNull Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    @Override
    public void subscribe(@NonNull ObservableEmitter<Boolean> emitter) throws Throwable {
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

    @SuppressLint("CheckResult")
    private void init(){
        Observable.create(this)
                .debounce(3, TimeUnit.SECONDS)
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
        boolean isEmpty =  getText().toString().isEmpty() ;
        boolean valid = isPatternMatched();
        if(isEmpty){
            showError(R.string.empty_field);
            return false;
        }
        if(!valid){
            showError();
            return false;
        }
        return true;
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
      showError(getErrorMessageStringId());
    }
    private void showError(int stringRes) {
        ViewGroup viewGroup = ((ViewGroup) this.getParent());
        if(viewGroup != null) {
            ((TextInputLayout) viewGroup.getParent()).setError(mContext.getString(stringRes));
        }
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        clearErrorMessage();
        if(s.length() !=0 && hasFocus()){
            mEmitter.onNext(isPatternMatched());
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
