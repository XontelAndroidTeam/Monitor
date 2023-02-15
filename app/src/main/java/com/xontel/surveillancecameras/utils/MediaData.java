package com.xontel.surveillancecameras.utils;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class MediaData  implements Parcelable {
    private String name;
    private Integer size;
    private Integer duration;
    private String mediaPath;
    private Uri mediaUri ;
    private String mediaType;

    public MediaData(Uri mediaUri, String name, Integer size, String mediaType, Integer duration, String mediaPath) {
        this.mediaUri = mediaUri;
        this.name = name;
        this.size = size;
        this.duration = duration;
        this.mediaPath = mediaPath;
        this.mediaType = mediaType;
    }

    public Bitmap getMediaThumbnail(){
        return null;
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
        mediaPath = in.readString();
        mediaUri = Uri.parse(in.readString());
        mediaType = in.readString();
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


    public String getMediaPath() {
        return mediaPath;
    }

    public void setMediaPath(String mediaPath) {
        this.mediaPath = mediaPath;
    }

    public Uri getMediaUri() {
        return mediaUri;
    }

    public void setMediaUri(Uri mediaUri) {
        this.mediaUri = mediaUri;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
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
        dest.writeString(mediaPath);
        dest.writeString(mediaUri.toString());
        dest.writeString(mediaType);

    }
}
