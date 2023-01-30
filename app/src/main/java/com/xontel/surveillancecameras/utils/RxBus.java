package com.xontel.surveillancecameras.utils;

import com.xontel.surveillancecameras.data.db.model.IpCam;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
public class RxBus {
    private static RxBus mInstance;
    private PublishSubject<List<IpCam>> camsPublisher = PublishSubject.create();
    private PublishSubject<Integer> gridCountPublisher = PublishSubject.create();

    public static RxBus getInstance() {
        if (mInstance == null) {
            mInstance = new RxBus();
        }
        return mInstance;
    }

    private RxBus() {
    }


    public void publishCams(List<IpCam> event) {
        camsPublisher.onNext(event);
    }

    public void publishGridCount(Integer event) {
        gridCountPublisher.onNext(event);
    }

    // Listen should return an Observable
    public Observable<List<IpCam>> listenToCams() {
        return camsPublisher;
    }

    public Observable<Integer> listenToGridCount() {
        return gridCountPublisher;
    }
}