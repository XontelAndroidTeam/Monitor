package com.xontel.surveillancecameras.data.db.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;


@Entity(tableName = "devicesCams")
public class CamDevice implements Parcelable{
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "username")
    private String userName;
    @ColumnInfo(name = "password")
    private String password;
    @ColumnInfo(name = "ip")
    private String ip;
    @ColumnInfo(name = "type")
    private int type ;
    @ColumnInfo(name = "url")
    private String url;
    @ColumnInfo(name = "description")
    private String description ;



    public CamDevice(int id,String name, String userName, String password, String ip, int type, String url, String description) {
        this.id = id;
        this.name = name;
        this.userName = userName;
        this.password = password;
        this.ip = ip;
        this.type = type;
        this.url = url;
        this.description = description;
    }

    @Ignore
    public CamDevice() {
    }

    public int getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    protected CamDevice(Parcel in) {
        id = in.readInt();
        name = in.readString();
        ip = in.readString();
        type = in.readInt();
        url = in.readString();
        description = in.readString();
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(url);
        parcel.writeString(name);
        parcel.writeString(description);
    }

    @Override
    public String toString() {
        return "CamDevice{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", ip='" + ip + '\'' +
                ", type=" + type +
                ", url='" + url + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
