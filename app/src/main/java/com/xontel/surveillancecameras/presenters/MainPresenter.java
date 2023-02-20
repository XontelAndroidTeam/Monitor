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
}
