package com.xontel.surveillancecameras.utils;

import java.util.Observable;

public class SDCardObservable extends Observable {
    private static SDCardObservable instance = new SDCardObservable();

    public static SDCardObservable getInstance() {
        return instance;
    }

    private SDCardObservable() {
    }

    public void updateValue(String action) {
        synchronized (this) {
            setChanged();
            notifyObservers(action);
        }
    }
}
