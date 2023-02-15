package com.xontel.surveillancecameras.hikvision;

public class StreamData {
    private int realHandle;
    private int dataType;
    private byte[] dataBuffer;
    private int dataSize;


    public StreamData(int realHandle, int dataType, byte[] dataBuffer, int dataSize) {
        this.realHandle = realHandle;
        this.dataType = dataType;
        this.dataBuffer = dataBuffer;
        this.dataSize = dataSize;
    }

    public StreamData() {
    }

    public int getRealHandle() {
        return realHandle;
    }

    public void setRealHandle(int realHandle) {
        this.realHandle = realHandle;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public byte[] getDataBuffer() {
        return dataBuffer;
    }

    public void setDataBuffer(byte[] dataBuffer) {
        this.dataBuffer = dataBuffer;
    }

    public int getDataSize() {
        return dataSize;
    }

    public void setDataSize(int dataSize) {
        this.dataSize = dataSize;
    }
}
