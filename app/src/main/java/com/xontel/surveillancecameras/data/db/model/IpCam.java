package com.xontel.surveillancecameras.data.db.model;

import android.os.Parcel;
import android.os.Parcelable;


public class IpCam implements  Parcelable{
    private int realPlayId = -1;
    private int channel = 0 ;
    private int playPort = -1 ;
    private int deviceId;
    private int type ;
    private int loginId = -1;
    private String name;
    private String urlOrIpAddress;


    public IpCam(int channel, int deviceId, int type,int loginId,String name,String urlOrIpAddress) {
        this.channel = channel;
        this.deviceId = deviceId;
        this.type = type;
        this.loginId = loginId;
        this.urlOrIpAddress = urlOrIpAddress;
        this.name = name;
    }


    protected IpCam(Parcel in) {
        realPlayId = in.readInt();
        channel = in.readInt();
        playPort = in.readInt();
        deviceId = in.readInt();
        type = in.readInt();
        loginId = in.readInt();
        name = in.readString();
        urlOrIpAddress = in.readString();
    }

    public static final Creator<IpCam> CREATOR = new Creator<IpCam>() {
        @Override
        public IpCam createFromParcel(Parcel in) {
            return new IpCam(in);
        }

        @Override
        public IpCam[] newArray(int size) {
            return new IpCam[size];
        }
    };

    public int getRealPlayId() {
        return realPlayId;
    }

    public void setRealPlayId(int realPlayId) {
        this.realPlayId = realPlayId;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int getPlayPort() {
        return playPort;
    }

    public void setPlayPort(int playPort) {
        this.playPort = playPort;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getLoginId() {
        return loginId;
    }

    public void setLoginId(int loginId) {
        this.loginId = loginId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrlOrIpAddress() {
        return urlOrIpAddress;
    }

    public void setUrlOrIpAddress(String urlOrIpAddress) {
        this.urlOrIpAddress = urlOrIpAddress;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(realPlayId);
        parcel.writeInt(channel);
        parcel.writeInt(playPort);
        parcel.writeInt(deviceId);
        parcel.writeInt(type);
        parcel.writeInt(loginId);
        parcel.writeString(name);
        parcel.writeString(urlOrIpAddress);
    }

    @Override
    public String toString() {
        return "IpCam{" +
                "realPlayId=" + realPlayId +
                ", channel=" + channel +
                ", playPort=" + playPort +
                ", deviceId=" + deviceId +
                ", type=" + type +
                ", loginId=" + loginId +
                ", name='" + name + '\'' +
                ", urlOrIpAddress='" + urlOrIpAddress + '\'' +
                '}';
    }
}
