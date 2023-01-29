package com.xontel.surveillancecameras.utils;

import androidx.lifecycle.MutableLiveData;

import java.util.Observable;

public class SDCardObservable extends Observable {
    private static SDCardObservable instance = new SDCardObservable();

    public MutableLiveData<Boolean> refreshRemovable = new MutableLiveData<>(false);

    public static SDCardObservable getInstance() {
        return instance;
    }

    private SDCardObservable() {
    }

    public void updateValue(String action) {
        refreshRemovable.setValue(true);
      //  synchronized (this) {
       //     setChanged();
       //     notifyObservers(action);
       // }
    }
}
