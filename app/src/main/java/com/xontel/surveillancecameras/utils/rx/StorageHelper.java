package com.xontel.surveillancecameras.utils.rx;

import static android.content.Context.STORAGE_SERVICE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StorageHelper {
    public static final int INTERNAL_STORAGE = 0;
    public static final int SDCARD_STORAGE = 1;
    public static final int USB_STORAGE = 2;
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

    public static List<String> getVolumesNamesList(Context context) {
        List<String> labels = new ArrayList<>();
        List<StorageVolume> volumes = getActiveVolumes(context);
        for (StorageVolume storageVolume : volumes) {
            String volumeLabel = getVolumeLabel(context, storageVolume);
            if (volumeLabel != null)
                labels.add(volumeLabel);
        }
//        String[] labelsArr = new String[labels.size()];
       return labels /*.toArray(labelsArr)*/;
    }

    public static List<Integer> getVolumesTypesList(Context context){
        List<Integer> types = new ArrayList<>();
        List<StorageVolume> volumes = getActiveVolumes(context);
        for (StorageVolume storageVolume : volumes) {
                types.add(getVolumeType(context, storageVolume));
        }
        return types;
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
        if (isInternalStorage(storageVolume)) {
            return context.getString(R.string.internal_storage);
        } else if (isSDCard(context, storageVolume)) {
            return context.getString(R.string.sd_card);
        } else if (isUSB(context, storageVolume)) {
            return context.getString(R.string.usb);
        }
        return null;
    }
    public static int getStorageTypeFromLabel(Context context, String label){
        if(label.equals(context.getString(R.string.sd_card))){
            return SDCARD_STORAGE ;
        }
        else if(label.equals(context.getString(R.string.usb))){
            return USB_STORAGE ;
        }else{
            return INTERNAL_STORAGE;
        }
    }



    public static boolean isInternalStorage(StorageVolume storageVolume) {
        return storageVolume.isEmulated();
    }

    public static boolean isSDCard(Context context, StorageVolume storageVolume) {
        return storageVolume.isRemovable() && storageVolume.getDescription(context).toLowerCase().contains("card");
    }

    public static boolean isUSB(Context context, StorageVolume storageVolume) {
        return storageVolume.isRemovable() && storageVolume.getDescription(context).toLowerCase().contains("usb");
    }

    public static int getSavedStorageType(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CommonUtils.SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
        int storageType = sharedPreferences.getInt(KEY_CHOSEN_STORAGE, INTERNAL_STORAGE);
        return getVolumesTypesList(context).contains(storageType) ? storageType : INTERNAL_STORAGE ;
    }

    public static void saveStorageType(Context context, String label) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CommonUtils.SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(KEY_CHOSEN_STORAGE, getStorageTypeFromLabel(context, label)).commit();
    }

}
