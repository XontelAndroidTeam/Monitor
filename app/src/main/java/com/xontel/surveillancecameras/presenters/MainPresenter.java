package com.xontel.surveillancecameras.presenters;

import android.util.Log;

import com.xontel.surveillancecameras.base.BasePresenter;
import com.xontel.surveillancecameras.data.DataManager;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.utils.rx.SchedulerProvider;

import javax.inject.Inject;

import io.reactivex.rxjava3.disposables.CompositeDisposable;


public class MainPresenter<V extends MainMvpView> extends BasePresenter<V>
        implements MainMvpPresenter<V> {
    @Inject
    public MainPresenter(DataManager manager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(manager, schedulerProvider, compositeDisposable);
    }


    @Override
    public void createCamera(IpCam ipCam) {
        getMvpView().showLoading();
        getCompositeDisposable().add(getDataManager()
                .insertIpCam(ipCam)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(response -> {
                    if (!isViewAttached()) {
                        return;
                    }
                    getMvpView().hideLoading();
                    getMvpView().onInsertingCamera();
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
    public void updateCamera(IpCam ipCam) {

        getMvpView().showLoading();
        getCompositeDisposable().add(getDataManager()
                .updateIpCam(ipCam)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(response -> {
                    if (!isViewAttached()) {
                        return;
                    }
                    getMvpView().hideLoading();
                    getMvpView().onUpdatingCamera();
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
    public void deleteCamera(IpCam ipCam) {

        getMvpView().showLoading();
        getCompositeDisposable().add(getDataManager()
                .deleteIpCam(ipCam)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(response -> {
                    if (!isViewAttached()) {
                        return;
                    }
                    getMvpView().hideLoading();
                    getMvpView().onDeletingCamera();
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
    public void getAllCameras() {
        getMvpView().showLoading();
        getCompositeDisposable().add(getDataManager()
                .getAll()
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(response -> {
                    if (!isViewAttached()) {
                        return;
                    }
                    getMvpView().hideLoading();
                    getMvpView().onGettingAllCameras(response);
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
    public void getCameraById(int id) {
        getMvpView().showLoading();
        getCompositeDisposable().add(getDataManager()
                .getIpCamById(id)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(response -> {
                    if (!isViewAttached()) {
                        return;
                    }
                    getMvpView().hideLoading();
                    getMvpView().onGettingCamera(response);
                }, error -> {
                    if (!isViewAttached()) {
                        return;
                    }
                    Log.e("error", error.getMessage() );
                    getMvpView().hideLoading();


                    handleApiError(error);
                }));
    }
}
