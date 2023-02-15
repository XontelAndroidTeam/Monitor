package com.xontel.surveillancecameras.utils;

import static android.content.Context.STORAGE_SERVICE;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;

import com.xontel.surveillancecameras.R;

import org.videolan.libvlc.Media;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@TargetApi(30)
public class StorageHelper {
    public static final String TAG = StorageHelper.class.getSimpleName();
    public static final int INTERNAL_STORAGE = 0;
    public static final int SDCARD_STORAGE = 1;
    public static final int USB_STORAGE = 2;
    public static final String APP_MEDIA_DIRECTORY_PATH = "/monitor/";
    public static final String KEY_CHOSEN_STORAGE = "chosen_storage";
    public static final String KEY_CHOSEN_SLIDE = "chosen_slide";
    public static final String KEY_CHOSEN_GRID = "chosen_grid";
    public static final String[] projection = new String[] {
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DURATION,
            MediaStore.Images.Media.SIZE};


    public static List<StorageVolume> getActiveVolumes(Context context) {
        StorageManager storageManager = (StorageManager) context.getSystemService(STORAGE_SERVICE);
        List<StorageVolume> volumes = storageManager.getStorageVolumes();
        List<StorageVolume> activeVolumes = new ArrayList<>();
        for (StorageVolume storageVolume : volumes) {
            if (storageVolume.getState().equals(Environment.MEDIA_MOUNTED)) {
                activeVolumes.add(storageVolume);
            }
        }
        return activeVolumes;
    }


    public static String getVolumeLabel(Context context, StorageVolume storageVolume) {
        Log.e("err", storageVolume.getDescription(context));
        if (isSDCard(context, storageVolume)) {
            return context.getString(R.string.sd_card);
        } else if (isUSB(context, storageVolume)) {
            return context.getString(R.string.usb);
        } else {
            return context.getString(R.string.internal_storage);
        }
    }

    public static File getMediaDirectory(Context context, String mediaType) {
        try {
            File appMediaDir = new File(getChosenExternalStorageDir(context)+"/"+mediaType+"/monitor");
            if (!appMediaDir.exists()){
                appMediaDir.mkdirs();
            }
            return appMediaDir;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static StorageVolume getVolumeByType(Context context, int storageType) {
        List<StorageVolume> volumes = getActiveVolumes(context);
        for (StorageVolume volume : volumes) {
            if (getVolumeType(context, volume).equals(storageType))
                return volume;
        }
        return volumes.get(0); // internal storage volume
    }


    public static File getChosenExternalStorageDir(Context context) {
        StorageVolume storageVolume = getVolumeByType(context, getSavedStorageType(context));
        if (storageVolume.getState().equals(Environment.MEDIA_MOUNTED)) {
            return storageVolume.getDirectory();
        }
        return context.getExternalFilesDirs(null)[0];

    }

//    private static void insertMediaFile(Context context, File file, String mediaType) {
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, file.getName());
//        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, mediaType.equals(IMAGES_DIRECTORY_NAME) ? "image/jpeg" : "video/mp4");
//        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "IP cameras");
//
//
//        Uri uri = context.getContentResolver().insert(mediaType.equals(IMAGES_DIRECTORY_NAME) ? MediaStore.Images.Media.EXTERNAL_CONTENT_URI :
//                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
//    }

    public static List<String> getVolumesNamesList(Context context) {
        List<String> labels = new ArrayList<>();
        List<StorageVolume> volumes = getActiveVolumes(context);
        for (StorageVolume storageVolume : volumes) {
            labels.add(getVolumeLabel(context, storageVolume));
        }
//        String[] labelsArr = new String[labels.size()];
        return labels /*.toArray(labelsArr)*/;
    }


    public static List<Integer> getVolumesTypesList(Context context) {
        List<Integer> types = new ArrayList<>();
        List<StorageVolume> volumes = getActiveVolumes(context);
        for (StorageVolume storageVolume : volumes) {
            types.add(getVolumeType(context, storageVolume));
        }
        Log.v("helper", types.toString());
        return new ArrayList<>(new HashSet<>(types));
    }
/*
    public static StorageVolume getStorageVolumeFromName(Context context, String name){
        StorageManager storageManager = (StorageManager) context.getSystemService(STORAGE_SERVICE);
        return storageManager.getStorageVolume(new File("/storage/" + name.toUpperCase()));
    }
 */

    public static String getLabelFromVolume(Context context, StorageVolume volume){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if(volume.isEmulated()){
                return context.getString(R.string.internal_storage);
            }else if(volume.isRemovable()){
                if(isSDCard(context, volume))
                    return context.getString(R.string.sd_card);
            }
        }
        return context.getString(R.string.usb) ;
    }


    private static Integer getVolumeType(Context context, StorageVolume storageVolume) {
        if (isSDCard(context, storageVolume)) {
            return SDCARD_STORAGE;
        } else if (isUSB(context, storageVolume)) {
            return USB_STORAGE;
        }
        return INTERNAL_STORAGE;
    }


    public static int getStorageTypeFromLabel(Context context, String label) {
        if (label.equals(context.getString(R.string.sd_card))) {
            return SDCARD_STORAGE;
        } else if (label.equals(context.getString(R.string.usb))) {
            return USB_STORAGE;
        } else {
            return INTERNAL_STORAGE;
        }
    }

    public static String getLabelFromStorageType(Context context, int storageType) {
        if (storageType == SDCARD_STORAGE) {
            return  context.getString(R.string.sd_card);

        } else if (storageType == USB_STORAGE) {
            return  context.getString(R.string.usb);
        } else {
            return context.getString(R.string.internal_storage);
        }
    }


    public static boolean isInternalStorage(StorageVolume storageVolume) {
        return storageVolume.isEmulated();
    }

    public static boolean isSDCard(Context context, StorageVolume storageVolume) {
        return storageVolume.isRemovable() && storageVolume.getDescription(context).toLowerCase().contains(context.getString(R.string.card));
    }

    public static boolean isUSB(Context context, StorageVolume storageVolume) {
        return storageVolume.isRemovable() && !isSDCard(context, storageVolume);
    }

    public static int getSavedStorageType(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CommonUtils.SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
        int storageType = sharedPreferences.getInt(KEY_CHOSEN_STORAGE, INTERNAL_STORAGE);
        return getVolumesTypesList(context).contains(storageType) ? storageType : INTERNAL_STORAGE;
    }

    public static void saveStorageType(Context context, String label) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CommonUtils.SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(KEY_CHOSEN_STORAGE, getStorageTypeFromLabel(context, label)).apply();
    }

    public static String  getSaveStorageName(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CommonUtils.SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
        int storageType = sharedPreferences.getInt(KEY_CHOSEN_STORAGE, INTERNAL_STORAGE);
        return getLabelFromStorageType(context,storageType);
    }


    public static String getSlideInterval(Context context){
        ArrayList<String> data = new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.intervals)));
        SharedPreferences sharedPreferences = context.getSharedPreferences(CommonUtils.SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
        return data.get(sharedPreferences.getInt(KEY_CHOSEN_SLIDE, 0));
    }

    public static void saveSlideInterval(Context context,String name){
        ArrayList<String> data = new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.intervals)));
        SharedPreferences sharedPreferences = context.getSharedPreferences(CommonUtils.SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(KEY_CHOSEN_SLIDE, data.indexOf(name)).apply();
    }

    public static void saveGridCount(Context context,int gridCount){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CommonUtils.SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(KEY_CHOSEN_GRID, gridCount).apply();
    }

    public static int getGridCount(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(CommonUtils.SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_CHOSEN_GRID, 16);
    }


    public static List<Uri> getContentUris(@NonNull final Context context, String mediaType) {

        final List<String> allVolumes = new ArrayList<>();
        final List<Uri> output = new ArrayList<>();
        allVolumes.add(MediaStore.VOLUME_EXTERNAL_PRIMARY);
        final Set<String> externalVolumeNames = MediaStore.getExternalVolumeNames(context);
        for (String entry : externalVolumeNames) {
            Log.v(TAG, "Volume found " + entry);
            if (!allVolumes.contains(entry))
                allVolumes.add(0, entry);
        }


        for (final String entry : allVolumes) {
            if (mediaType.equals(Environment.DIRECTORY_PICTURES)) {
                output.add(MediaStore.Images.Media.getContentUri(entry));
            } else {
                output.add(MediaStore.Video.Media.getContentUri(entry));
            }
        }

        return output;
    }


    public static List<MediaData> getMediaItems(Context context, String mediaType){
        List<Uri> collection = StorageHelper.getContentUris(context, mediaType);
        List<MediaData> mediaList = new ArrayList<>();
        for (Uri uri:collection){
            mediaList.addAll(getMediaFromUri(context, uri, mediaType));
        }
        return mediaList;
    }

    private static List<MediaData> getMediaFromUri(Context context, Uri uri, String mediaType){
        List<MediaData> mediaList = new ArrayList<>();
       Cursor cursor = context.getContentResolver().query(
                uri,
                projection,
                null,
                null,
                null
        );
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
                Uri contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                if (dataPath.contains(mediaType + StorageHelper.APP_MEDIA_DIRECTORY_PATH)){
                    mediaList.add(new MediaData(contentUri, name, size, mediaType, duration, dataPath));
                }
            }
            return mediaList;
    }


}
