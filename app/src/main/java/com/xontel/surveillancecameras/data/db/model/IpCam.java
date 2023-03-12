package com.xontel.surveillancecameras.data.db.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.xontel.surveillancecameras.dahua.DahuaUtil;
import com.xontel.surveillancecameras.hikvision.HikUtil;
import com.xontel.surveillancecameras.utils.CamDeviceType;

import io.reactivex.rxjava3.core.Single;

@Entity(tableName = "channels")
public class IpCam implements Parcelable {
    public static final String TAG = IpCam.class.getSimpleName();

    @PrimaryKey(autoGenerate = true)
    public long id;
    @ColumnInfo(name = "device_id")
    private long deviceId;
    @ColumnInfo(name = "device_name")
    private String deviceName;
    @ColumnInfo(name = "type")
    private int type;
    @ColumnInfo(name = "stream_type")
    private int streamType = 1;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "full_name")
    private String fullName;
    @ColumnInfo(name = "included")
    private boolean included = true;
    @ColumnInfo(name = "analog")
    private boolean analog;
    @ColumnInfo(name = "channel")
    private int channel;
    @Ignore
    private long logId;


    public IpCam(long id,  long deviceId, String deviceName, int type, int streamType, String name, String fullName, boolean included, boolean analog) {
        this.id = id;
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.type = type;
        this.streamType = streamType;
        this.name = name;
        this.fullName = fullName;
        this.included = included;
        this.analog = analog;
    }

    @Ignore
    public IpCam(int channel, long deviceId, String deviceName, int type, long logId, boolean analog) {
        this.channel = channel;
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.type = type;
        this.logId = logId;
        this.analog = analog;
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

    public boolean isAnalog() {
        return analog;
    }

    public void setAnalog(boolean analog) {
        this.analog = analog;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getStreamType() {
        return streamType;
    }

    public void setStreamType(int streamType) {
        this.streamType = streamType;
    }

    public long getLogId() {
        return logId;
    }

    public void setLogId(long logId) {
        this.logId = logId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        fullName = deviceName + "-" + name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public boolean isIncluded() {
        return included;
    }

    public void setIncluded(boolean included) {
        this.included = included;
    }

    public void toggleStreamType() {
        this.streamType = (streamType == 1) ? 0 : 1;
    }

    @Ignore
    protected IpCam(Parcel in) {
        id = in.readLong();
        channel = in.readInt();
        deviceId = in.readInt();
        deviceName = in.readString();
        type = in.readInt();
        streamType = in.readInt();
        logId = in.readInt();
        name = in.readString();
        fullName = in.readString();
        included = in.readByte() != 0;
        analog = in.readByte() != 0;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeInt(channel);
        parcel.writeLong(deviceId);
        parcel.writeString(deviceName);
        parcel.writeInt(type);
        parcel.writeInt(streamType);
        parcel.writeLong(logId);
        parcel.writeString(name);
        parcel.writeString(fullName);
        parcel.writeByte((byte) (included ? 1 : 0));
        parcel.writeByte((byte) (analog ? 1 : 0));
    }


    public Single<String> extractChannelName() {
        return CamDeviceType.HIKVISION.getValue() == type ? HikUtil.extractChannelName(this) : DahuaUtil.extractChannelName(this);
    }
}
