package com.xontel.surveillancecameras.data.db.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.xontel.surveillancecameras.utils.CamDeviceType;
import com.xontel.surveillancecameras.utils.VideoHelper;


public class IpCam{
    private int realPlayId = -1;
    private int channel = 0 ;
    private int playPort = -1 ;
    private int deviceId;
    private int type ;


    public IpCam(int channel, int deviceId, int type) {
        this.channel = channel;
        this.deviceId = deviceId;
        this.type = type;
    }


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
}
