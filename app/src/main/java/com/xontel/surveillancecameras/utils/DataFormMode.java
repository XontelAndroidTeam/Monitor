package com.xontel.surveillancecameras.utils;

public enum DataFormMode {
    EDIT(0),
    CREATE(1),
    READ(2);

    private final int value;
    private DataFormMode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
