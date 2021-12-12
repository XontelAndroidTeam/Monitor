package com.xontel.surveillancecameras.models;

import android.net.Uri;

public class MediaItem {
    public static final int TYPE_VIDEO = 0 ;
    public static final int TYPE_IMAGE = 1 ;
    private String path ;
    private Uri uri ;
    private int type ;

    public MediaItem() {
    }

    public MediaItem(String path, Uri uri, int type) {
        this.path = path;
        this.uri = uri;
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
