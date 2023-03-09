package com.xontel.surveillancecameras.presenters;

import android.util.Log;

import com.xontel.surveillancecameras.base.BasePresenter;
import com.xontel.surveillancecameras.base.MvpView;
import com.xontel.surveillancecameras.data.DataManager;
import com.xontel.surveillancecameras.data.db.model.CamDevice;
import com.xontel.surveillancecameras.utils.rx.SchedulerProvider;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainDevicePresenter<V extends MainDeviceMvpView & MvpView> extends BasePresenter<V>
        implements MainDeviceMvpPresenter<V> {

    @Inject
    public MainDevicePresenter(DataManager manager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(manager, schedulerProvider, compositeDisposable);
    }

    @Override
    public void createDevice(CamDevice device) {
        getMvpView().showLoading();
        getCompositeDisposable().add(getDataManager()
                .insertCamDevice(device)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(response -> {
                    if (!isViewAttached()) {
                        return;
                    }
                    getMvpView().hideLoading();
                    getMvpView().onInsertingDevice();
                }, error -> {
                    if (!isViewAttached()) {
                        return;
                    }
                    Log.e("TAGGG", "error : " +error.getMessage());
                    getMvpView().hideLoading();
                    handleApiError(error);
                }));

    }

    @Override
    public void updateDevice(CamDevice device) {
        getCompositeDisposable().add(
        Single.create(sub -> getDataManager().updateCamDevice(device))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success ->{}, this::handleApiError));
    }

    @Override
    public void deleteDevice(CamDevice device) {
        getMvpView().showLoading();
        getCompositeDisposable().add(
        Single.fromCallable((Callable<Object>) () -> {
           return getDataManager().deleteCamDevice(device);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(success ->{
                    if (!isViewAttached()) {
                        return;
                    }
                    getMvpView().hideLoading();
                    getMvpView().onDeletingDevice();
                    }, error ->{
                    if (!isViewAttached()) {
                        return;
                    }
                    Log.e("error", error.getMessage() );
                    getMvpView().hideLoading();
                    handleApiError(error);
                }));

    }

    @Override
    public void getAllDevices() {
         getMvpView().showLoading();
        getCompositeDisposable().add(getDataManager()
                .getDevicesAll()
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(response -> {
                    if (!isViewAttached()) {
                        return;
                    }
                    getMvpView().hideLoading();
                    getMvpView().onGettingAllDevices(response);
                }, error -> {
                    if (!isViewAttached()) {
                        return;
                    }
                    Log.e("error", error.getMessage() );
                    getMvpView().hideLoading();
                    handleApiError(error);
                }));
    }

    @Override
    public void getDeviceById(int id) {

    }
}
