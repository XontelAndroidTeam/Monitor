package com.xontel.surveillancecameras.utils;

public enum CamDeviceType {
    HIKVISION(0),
    DAHUA(1),
    OTHER(2);

    private final int value;
    private CamDeviceType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
