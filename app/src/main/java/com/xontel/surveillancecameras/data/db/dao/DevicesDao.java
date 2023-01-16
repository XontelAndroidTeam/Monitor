package com.xontel.surveillancecameras.data.db.dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.xontel.surveillancecameras.data.db.model.CamDevice;

import java.util.List;


import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

import static androidx.room.OnConflictStrategy.REPLACE;
@Dao
public interface DevicesDao {
    @Query("SELECT * FROM devices")
    Flowable<List<CamDevice>> getDevicesAll();

    @Insert(onConflict = REPLACE)
    Single<Long> insertCamDevice(CamDevice mCamDevice);

    @Insert
    Single<List<Long>> insertAllCamDevice(CamDevice... mCamDevicesList);

    @Delete
    Integer deleteCamDevice(CamDevice mCamDevice);

    @Update
    void updateCamDevice(CamDevice mCamDevice);

    @Query("SELECT * FROM devices WHERE id = :id")
    Single<CamDevice> getCamDeviceById(int id);


    @Query("SELECT * FROM devices WHERE id IN (:cameraIds)")
    Single<List<CamDevice>> loadAllDevicesByIds(int[] cameraIds);

    @Query("SELECT * FROM devices WHERE name LIKE :name")
    Single<CamDevice> findDeviceByName(String name);
}