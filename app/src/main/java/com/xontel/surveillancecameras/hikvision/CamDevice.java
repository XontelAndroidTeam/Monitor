package com.xontel.surveillancecameras.hikvision;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.xontel.surveillancecameras.dahua.DahuaUtil;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.utils.CamDeviceType;


import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleEmitter;
import io.reactivex.rxjava3.core.SingleOnSubscribe;
import io.reactivex.rxjava3.core.SingleSource;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

@Entity(tableName = "cam_devices")
public class CamDevice implements Parcelable  {

    @PrimaryKey(autoGenerate = true)
    public long id;
    @ColumnInfo(name = "name")
    public String name;
    @ColumnInfo(name = "domain")
    public String domain;
    @ColumnInfo(name = "userName")
    public String userName;
    @ColumnInfo(name = "password")
    public String password;
    @ColumnInfo(name = "deviceType")
    public int deviceType ;

    @Ignore
    private int channels ;
    @Ignore
    private int logId = -1;
    @Ignore
    private List<IpCam> cams = new ArrayList<>();

    @Ignore
    private boolean scanned;

    public CamDevice(String name, String userName, String password, String domain, int deviceType) {
        this.name = name;
        this.domain = domain;
        this.userName = userName;
        this.password = password;
        this.deviceType = deviceType;
    }


    protected CamDevice(Parcel in) {
        id = in.readLong();
        name = in.readString();
        domain = in.readString();
        userName = in.readString();
        password = in.readString();
        deviceType = in.readInt();
        channels = in.readInt();
        logId = in.readInt();
    }

    public static final Creator<CamDevice> CREATOR = new Creator<CamDevice>() {
        @Override
        public CamDevice createFromParcel(Parcel in) {
            return new CamDevice(in);
        }

        @Override
        public CamDevice[] newArray(int size) {
            return new CamDevice[size];
        }
    };

    public int getChannels() {
        return channels;
    }

    public void setChannels(int channels) {
        this.channels = channels;
    }

    public long getId() {
        return id;
    }

    public void setId(long id){
        this.id = id;
    }

    public boolean isScanned() {
        return scanned;
    }

    public void setScanned(boolean scanned) {
        this.scanned = scanned;
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




    public String getDomain() {
        return domain;
    }

    public void setDomain(String ipAddress) {
        this.domain = ipAddress;
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



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(name);
        parcel.writeString(domain);
        parcel.writeString(userName);
        parcel.writeString(password);
        parcel.writeInt(deviceType);
        parcel.writeInt(channels);
        parcel.writeInt(logId);
    }

    public boolean isLoginValid(){
//        if (deviceType == CamDeviceType.HIKVISION.getValue()){
//            return  HikUtil.loginNormalDevice(this) >= 0;
//        }else if (deviceType == CamDeviceType.DAHUA.getValue()){
//            return DahuaUtil.loginNormalDevice(this) != 0;
//        }else{
            return true;
//        }
    }

    @Override
    public String toString() {
        return "CamDevice{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", domain='" + domain + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", deviceType=" + deviceType +
                ", channels=" + channels +
                ", logId=" + logId +
                ", cams=" + cams +
                '}';
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return ((CamDevice)obj).id == this.id;
    }

}

