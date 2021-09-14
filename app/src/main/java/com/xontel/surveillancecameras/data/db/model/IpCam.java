package com.xontel.surveillancecameras.data.db.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "camera")
public class IpCam implements Parcelable  {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "url")
    private String url;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "description")
    private String description ;


    public IpCam(String url, String name, String description) {
        this.url = url;
        this.name = name;
        this.description = description;
    }

    @Ignore
    public IpCam() {
    }

    protected IpCam(Parcel in) {
        id = in.readInt();
        url = in.readString();
        name = in.readString();
        description = in.readString();
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

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
}
