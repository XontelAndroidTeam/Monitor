package com.xontel.surveillancecameras.data.db.dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.xontel.surveillancecameras.data.db.model.CamDevice;
import com.xontel.surveillancecameras.hikvision.HIKDevice;

import java.util.List;


import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

import static androidx.room.OnConflictStrategy.REPLACE;
@Dao
public interface DevicesDao {
    @Query("SELECT * FROM cam_devices")
    Flowable<List<HIKDevice>> getDevicesAll();

    @Insert(onConflict = REPLACE)
    Single<Long> insertCamDevice(HIKDevice mCamDevice);

    @Insert
    Single<List<Long>> insertAllCamDevice(HIKDevice... mCamDevicesList);

    @Delete
    Integer deleteCamDevice(HIKDevice mCamDevice);

    @Update
    void updateCamDevice(HIKDevice mCamDevice);

    @Query("SELECT * FROM cam_devices WHERE id = :id")
    Single<HIKDevice> getCamDeviceById(int id);


    @Query("SELECT * FROM cam_devices WHERE id IN (:cameraIds)")
    Single<List<HIKDevice>> loadAllDevicesByIds(int[] cameraIds);

    @Query("SELECT * FROM cam_devices WHERE name LIKE :name")
    Single<HIKDevice> findDeviceByName(String name);
}