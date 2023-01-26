package com.xontel.surveillancecameras.utils;

import android.os.Parcel;
import android.os.Parcelable;

public class MediaData  implements Parcelable {
    private String name;
    private Integer size;
    private Integer duration;
    private String videoPath;
    private String imagePath;

    public MediaData(String name, Integer size, Integer duration, String videoPath, String imagePath) {
        this.name = name;
        this.size = size;
        this.duration = duration;
        this.videoPath = videoPath;
        this.imagePath = imagePath;
    }

    protected MediaData(Parcel in) {
        name = in.readString();
        if (in.readByte() == 0) {
            size = null;
        } else {
            size = in.readInt();
        }
        if (in.readByte() == 0) {
            duration = null;
        } else {
            duration = in.readInt();
        }
        videoPath = in.readString();
        imagePath = in.readString();
    }

    public static final Creator<MediaData> CREATOR = new Creator<MediaData>() {
        @Override
        public MediaData createFromParcel(Parcel in) {
            return new MediaData(in);
        }

        @Override
        public MediaData[] newArray(int size) {
            return new MediaData[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        if (size == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(size);
        }
        if (duration == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(duration);
        }
        dest.writeString(videoPath);
        dest.writeString(imagePath);
    }
}
