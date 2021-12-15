package com.xontel.surveillancecameras.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.util.Log;

import com.xontel.surveillancecameras.R;

import java.io.File;

public class CommonUtils {
    public static final String SHARED_PREFERENCES_FILE = "com.xontel.surveillancecameras.preferences.file";
    public static final String KEY_AUTO_PREVIEW = "auto_preview";
    public static final String KEY_SLIDE_INTERVAL_INDEX = "slide_interval_index";
    public static final String KEY_MEDIA_STORAGE = "media_storage";
    public static final String KEY_GRID_COUNT = "grid_count";

    // America/New jersey => New jersey
    public static String cityNameFromZoneId(String zoneId){
        Log.e("adapter", "cityNameFromZoneId: "+zoneId );
        String [] words = zoneId.split("/");
        return words[words.length -1];
    }

    public static boolean hasSDCard(Context context){
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


}
