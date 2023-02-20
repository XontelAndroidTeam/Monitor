package com.xontel.surveillancecameras.data.db.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_PICCFG_V30;
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO;

import java.nio.charset.StandardCharsets;


public class IpCam implements  Parcelable{
    public static final String TAG  = IpCam.class.getSimpleName();
    private int channel = 0 ;
    private int deviceId;
    private int type ;

    private int streamType = 1 ;
    private int loginId = -1;
    private String name;


    public IpCam(int channel, int deviceId, int type,int loginId) {
        this.channel = channel;
        this.deviceId = deviceId;
        this.type = type;
        this.loginId = loginId;
        extractChannelName();
    }

    private void extractChannelName() {
        NET_DVR_PICCFG_V30 net_dvr_piccfg_v30 = new NET_DVR_PICCFG_V30();
        if(!HCNetSDK.getInstance().NET_DVR_GetDVRConfig(loginId,
                HCNetSDK.NET_DVR_GET_PICCFG_V30,
                channel, net_dvr_piccfg_v30)){
            Log.e(TAG, "failed to get channels state");
            name = "";
        }else{
            name = new String(net_dvr_piccfg_v30.sChanName, StandardCharsets.UTF_8).replaceAll("\0", "");
            Log.e(TAG, "name is : "+name);
        }

        NET_DVR_PREVIEWINFO net_dvr_previewinfo = new NET_DVR_PREVIEWINFO();
        net_dvr_previewinfo.lChannel = channel;
        net_dvr_previewinfo.dwStreamType = streamType;

        if(HCNetSDK.getInstance().NET_DVR_RealPlay_V40(loginId, net_dvr_previewinfo, null) < 0){
            Log.e(TAG, "failed to get channels state");
            streamType = 0;
        }
        HCNetSDK.getInstance().NET_DVR_StopRealPlay(loginId);
    }


    protected IpCam(Parcel in) {
        channel = in.readInt();
        deviceId = in.readInt();
        type = in.readInt();
        loginId = in.readInt();
        name = in.readString();
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



    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(channel);
        parcel.writeInt(deviceId);
        parcel.writeInt(type);
        parcel.writeInt(loginId);
        parcel.writeString(name);
    }

    @Override
    public String toString() {
        return "IpCam{" +
                ", channel=" + channel +
                ", deviceId=" + deviceId +
                ", type=" + type +
                ", loginId=" + loginId +
                ", name='" + name + '\'' +
                '}';
    }
}
