package com.xontel.surveillancecameras.utils;

import static android.content.Context.STORAGE_SERVICE;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.MediaStore;
import android.util.Log;

import com.xontel.surveillancecameras.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class StorageHelper {
    public static final int INTERNAL_STORAGE = 0;
    public static final int SDCARD_STORAGE = 1;
    public static final int USB_STORAGE = 2;
    public static final String IMAGES_DIRECTORY_NAME = "images";
    public static final String VIDEOS_DIRECTORY_NAME = "videos";
    public static final String KEY_CHOSEN_STORAGE = "chosen_storage";


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

    public static File getMediaDirectory(Context context, String dirType) {
        try {
            File appMediaDir = new File(getChosenExternalStorageDir(context), "media");
            appMediaDir.mkdir();
            if (appMediaDir.exists()) {
                File dir = new File(appMediaDir, dirType);
                dir.mkdir();
                if (dir.exists()) {
                    return dir;
                }
            }
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
            File[] externalDirs = context.getExternalFilesDirs(null);
            for (File dir : externalDirs) {
                Log.v("err", storageVolume.getUuid() + " == " + dir.getAbsolutePath());
                if (storageVolume.getUuid() != null && dir.getAbsolutePath().contains(storageVolume.getUuid())) {
                    return dir;
                }
            }
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
        return new ArrayList<>(new HashSet<>(types));
    }

    private static Integer getVolumeType(Context context, StorageVolume storageVolume) {
        if (isSDCard(context, storageVolume)) {
            return SDCARD_STORAGE;
        } else if (isUSB(context, storageVolume)) {
            return USB_STORAGE;
        }
        return INTERNAL_STORAGE;
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

    public static int getStorageTypeFromLabel(Context context, String label) {
        if (label.equals(context.getString(R.string.sd_card))) {
            return SDCARD_STORAGE;
        } else if (label.equals(context.getString(R.string.usb))) {
            return USB_STORAGE;
        } else {
            return INTERNAL_STORAGE;
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
        sharedPreferences.edit().putInt(KEY_CHOSEN_STORAGE, getStorageTypeFromLabel(context, label)).commit();
    }

}
