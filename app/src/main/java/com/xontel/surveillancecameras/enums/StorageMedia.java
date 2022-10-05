package com.xontel.surveillancecameras.enums;

public enum StorageMedia {
    INTERNAL(1),
    SDCARD(2),
    USB(3);

    private final int value;

    StorageMedia(final int newValue) {
        value = newValue;
    }

    public int getValue() { return value; }
}
