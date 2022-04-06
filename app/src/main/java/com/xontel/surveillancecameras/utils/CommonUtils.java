package com.xontel.surveillancecameras.utils;

import static android.content.Context.STORAGE_SERVICE;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.data.db.model.IpCam;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class CommonUtils {
    public static final String SHARED_PREFERENCES_FILE = "com.xontel.surveillancecameras.preferences.file";
    public static final String KEY_AUTO_PREVIEW = "auto_preview";
    public static final String KEY_SLIDE_INTERVAL_INDEX = "slide_interval_index";
    public static final String KEY_MEDIA_STORAGE = "media_storage";
    public static final String KEY_GRID_COUNT = "grid_count";
    private static final long NUM_BYTES_NEEDED_FOR_MY_APP = 1024 * 1024 * 10L;

    public static void galleryAddPic(Context context, String filePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(filePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }
    // America/New jersey => New jersey
    public static String cityNameFromZoneId(String zoneId){
        Log.e("adapter", "cityNameFromZoneId: "+zoneId );
        String [] words = zoneId.split("/");
        return words[words.length -1];
    }
    public static String getFileSize(File file) {
        double size = (double) file.length() / 1000.0; // Get size and convert bytes into KB.
        if (size >= 1024) {
            return new DecimalFormat("##.00", DecimalFormatSymbols.getInstance(new Locale("en"))).format(size / 1024) + " MB";
        } else {
            return size + " KB";
        }
    }

    public static boolean deleteFile(File file) {
        if (file.exists())
            return file.delete();
        return false;

    }




//

    public static File saveBitmap(Context context, Bitmap bitmap, String parentDirPath){
        if(parentDirPath !=null) {
            OutputStream outStream = null;
            File file = new File(parentDirPath, System.currentTimeMillis() + ".jpeg");
            try {
                outStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                outStream.flush();
                outStream.close();
                return file;
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        }
        return null;
    }

    public static boolean hasSDCard(Context context){
        StorageManager storageManager = (StorageManager) context.getSystemService(STORAGE_SERVICE);
        for(StorageVolume storageVolume : storageManager.getStorageVolumes()){
            Log.e("TAG", storageVolume.isRemovable()+"" );
        }
        File[] externalDirs = context.getExternalFilesDirs(null);
        for(File dir: externalDirs){
            if(Environment.isExternalStorageRemovable(dir)){
                return true;
            }
        }
       return   false;
    }

    // 3 => GMT+3:00
    public static String getHoursFromUTCQualified(double timeFromUTC){
        String sign = "+";
        int hours =  (int)Math.abs(timeFromUTC);
        int minutes = (int)((Math.abs(timeFromUTC) - hours)*60) ;
        String hoursText =hours +"";
        String minutesText = minutes+"" ;
        if(Math.abs(hours) <= 9) { // 1 digit
            hoursText = "0"+hours;
        }
        if(timeFromUTC <0) sign = "-";

        if(minutes <= 9 )
            minutesText = "0"+minutes ;
        return "GMT"+sign+hoursText+":"+minutesText;
    }

    public static boolean isConnectionAvailable(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public static ProgressDialog showLoadingDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        return progressDialog;
    }


//    public static boolean hasFreeSpace(Context context, File videoDirectory) {
        // App needs 10 MB within internal storage.
//
//
//        StorageManager storageManager = context.getSystemService(StorageManager.class);
//        UUID appSpecificInternalDirUuid = null;
//        try {
//            appSpecificInternalDirUuid = storageManager.getUuidForPath(videoDirectory);
//
//        long availableBytes =
//                storageManager.getAllocatableBytes(appSpecificInternalDirUuid);
//        if (availableBytes >= NUM_BYTES_NEEDED_FOR_MY_APP) {
//            return true ;
//        } else {
//            // To request that the user remove all app cache files instead, set
//            // "action" to ACTION_CLEAR_APP_CACHE.
//            Intent storageIntent = new Intent();
//            storageIntent.setAction(ACTION_MANAGE_STORAGE);
//        }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
