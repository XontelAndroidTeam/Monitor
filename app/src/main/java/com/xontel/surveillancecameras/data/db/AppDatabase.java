package com.xontel.surveillancecameras.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.xontel.surveillancecameras.data.db.dao.CamDao;
import com.xontel.surveillancecameras.data.db.dao.DevicesDao;
import com.xontel.surveillancecameras.data.db.model.CamDevice;
import com.xontel.surveillancecameras.data.db.model.IpCam;
import com.xontel.surveillancecameras.hikvision.HIKDevice;


/**
 * Created on : Feb 01, 2019
 * Author     : AndroidWave
 */
@Database(entities = {CamDevice.class, HIKDevice.class}, version = 5, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "cameras-database";
    private static AppDatabase mInstance;

    public synchronized static AppDatabase getDatabaseInstance(Context context) {
        if (mInstance == null) {
            mInstance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, DATABASE_NAME)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return mInstance;
    }

    public static void destroyInstance() {
        mInstance = null;
    }

    public abstract DevicesDao mDevicesDao();
    public abstract CamDao camDao();

}