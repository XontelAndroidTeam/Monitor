package com.xontel.surveillancecameras.utils;

public class HIKDevice {

    public  String ipAddress;
    private   int port;
    private  String userName;
    private  String passWord;
    private HIKDeviceType deviceType ;
    private int channels;
    private int logId = -1;

    public HIKDevice(String ipAddress, int port, String userName, String passWord, HIKDeviceType deviceType) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.userName = userName;
        this.passWord = passWord;
        this.deviceType = deviceType;
    }


    public int getLogId() {
        return logId;
    }

    public void setLogId(int logId) {
        this.logId = logId;
    }

    public int getChannels() {
        return channels;
    }

    public void setChannels(int channels) {
        this.channels = channels;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    public HIKDeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(HIKDeviceType deviceType) {
        this.deviceType = deviceType;
    }
}

