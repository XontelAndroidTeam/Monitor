package com.xontel.surveillancecameras.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MediaUtils {
    public static final String TAG = MediaUtils.class.getSimpleName();

    public static List<MediaData> extractMedia(Cursor cursor, String mediaType){
        Log.v(TAG, cursor.getCount()+" count");
        List<MediaData> mediaList = new ArrayList<>();

        // Cache column indices.
        int idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
        int nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
        int durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DURATION);
        int sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);
        int data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        while (cursor.moveToNext()) {
            // Get values of columns for a given video.
            long id = cursor.getLong(idColumn);
            String name = cursor.getString(nameColumn);
            int duration = cursor.getInt(durationColumn);
            int size = cursor.getInt(sizeColumn);
            String dataPath = cursor.getString(data);
            Uri contentUri = ContentUris.withAppendedId(mediaType.equals(Environment.DIRECTORY_PICTURES) ? MediaStore.Images.Media.EXTERNAL_CONTENT_URI : MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id);
            mediaList.add(new MediaData(contentUri, name, size, mediaType, duration, dataPath));
        }
        return mediaList;
    }

  
    public static String getMediaStoreVolumesAsSelectionArgs(Context context) {
        String selectionArg = "(";
        Set<String> volumesNames = MediaStore.getExternalVolumeNames(context);
        for(String vol : volumesNames){
            selectionArg+="'"+vol+"',";
        }
       String result = selectionArg.substring(0, selectionArg.length() - 1)+")";
        Log.v(TAG, result);
        return result;
    }
}
