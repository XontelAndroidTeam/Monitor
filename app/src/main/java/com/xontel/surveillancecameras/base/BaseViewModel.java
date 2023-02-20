package com.xontel.surveillancecameras.base;

import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.xontel.surveillancecameras.data.DataManager;
import com.xontel.surveillancecameras.utils.rx.SchedulerProvider;

import javax.inject.Inject;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class BaseViewModel extends ViewModel {
    private Toast mToast;
    private final SchedulerProvider mSchedulerProvider;
    private final CompositeDisposable mCompositeDisposable;
    private final DataManager manager;
    private MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private MutableLiveData<Boolean> error = new MutableLiveData<>();
    private String errorMessage ;


    public BaseViewModel(SchedulerProvider mSchedulerProvider, CompositeDisposable mCompositeDisposable, DataManager manager) {
        this.mSchedulerProvider = mSchedulerProvider;
        this.mCompositeDisposable = mCompositeDisposable;
        this.manager = manager;
    }
    public SchedulerProvider getSchedulerProvider() {
        return mSchedulerProvider;
    }

    public CompositeDisposable getCompositeDisposable() {
        return mCompositeDisposable;
    }

    public DataManager getDataManager() {
        return manager;
    }

    public MutableLiveData<Boolean> getLoading() {
        return loading;
    }

    public MutableLiveData<Boolean> getError() {
        return error;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void showToastMessage(Context context, String message) {
        if(mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        mToast.show();

    }
    public void showToastMessage(Context context, int stringRes) {
        showToastMessage(context, context.getString(stringRes));

    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    protected void onCleared() {
        if (mCompositeDisposable != null) {
            mCompositeDisposable.dispose();
        }
        super.onCleared();
    }
}
