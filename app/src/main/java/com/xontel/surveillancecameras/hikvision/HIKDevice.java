package com.xontel.surveillancecameras.hikvision;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.utils.CamDeviceType;


import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "cam_devices")
public class HIKDevice implements Parcelable  {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "name")
    public String name;
    @ColumnInfo(name = "ipAddress")
    public String ipAddress;
    @ColumnInfo(name = "userName")
    public String userName;
    @ColumnInfo(name = "password")
    public String password;
    @ColumnInfo(name = "deviceType")
    public int deviceType ;
    @ColumnInfo(name = "url")
    public String url;
    @Ignore
    private int port;
    @Ignore
    private int channels;
    @Ignore
    private int logId = -1;
    @Ignore
    private List<IpCam> cams = new ArrayList<>();

    public HIKDevice(int id,String name, String userName, String password, String ipAddress, int deviceType, String url) {
        this.name = name;
        this.id = id;
        this.url = url;
        this.ipAddress = ipAddress;
        this.userName = userName;
        this.password = password;
        this.deviceType = deviceType;
    }


    protected HIKDevice(Parcel in) {
        id = in.readInt();
        name = in.readString();
        ipAddress = in.readString();
        userName = in.readString();
        password = in.readString();
        deviceType = in.readInt();
        url = in.readString();
        port = in.readInt();
        channels = in.readInt();
        logId = in.readInt();
    }

    public static final Creator<HIKDevice> CREATOR = new Creator<HIKDevice>() {
        @Override
        public HIKDevice createFromParcel(Parcel in) {
            return new HIKDevice(in);
        }

        @Override
        public HIKDevice[] newArray(int size) {
            return new HIKDevice[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<IpCam> getCams() {
        return cams;
    }

    public void setCams(List<IpCam> cams) {
        this.cams = cams;
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
        return password;
    }

    public void setPassWord(String passWord) {
        this.password = passWord;
    }

    public Integer getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(Integer deviceType) {
        this.deviceType = deviceType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(ipAddress);
        parcel.writeString(userName);
        parcel.writeString(password);
        parcel.writeInt(deviceType);
        parcel.writeString(url);
        parcel.writeInt(port);
        parcel.writeInt(channels);
        parcel.writeInt(logId);
    }

    public boolean isLoginValid(){
        if (deviceType == CamDeviceType.HIKVISION.getValue()){
            return  HikUtil.loginNormalDevice(this) >= 0;
        }else if (deviceType == CamDeviceType.DAHUA.getValue()){

        }else{

        }
        return false;
    }
}

