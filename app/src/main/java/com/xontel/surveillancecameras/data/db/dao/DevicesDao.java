package com.xontel.surveillancecameras.data.db.dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.xontel.surveillancecameras.hikvision.CamDevice;

import java.util.List;


import io.reactivex.rxjava3.core.Single;

import static androidx.room.OnConflictStrategy.REPLACE;
@Dao
public interface DevicesDao {
    @Query("SELECT * FROM cam_devices")
    Single<List<CamDevice>> getDevicesAll();

    @Insert(onConflict = REPLACE)
    Single<Long> insertCamDevice(CamDevice mCamDevice);

    @Insert
    Single<List<Long>> insertAllCamDevice(CamDevice... mCamDevicesList);

    @Delete
    Single<Integer> deleteCamDevice(CamDevice mCamDevice);

    @Update
    Single<Integer> updateCamDevice(CamDevice mCamDevice);

    @Query("SELECT * FROM cam_devices WHERE id = :id")
    Single<CamDevice> getCamDeviceById(long id);


    @Query("SELECT * FROM cam_devices WHERE id IN (:cameraIds)")
    Single<List<CamDevice>> loadAllDevicesByIds(int[] cameraIds);

    @Query("SELECT * FROM cam_devices WHERE name LIKE :name")
    Single<CamDevice> findDeviceByName(String name);
}