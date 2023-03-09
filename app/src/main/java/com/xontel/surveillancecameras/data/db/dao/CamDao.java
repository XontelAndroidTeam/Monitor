package com.xontel.surveillancecameras.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.xontel.surveillancecameras.data.db.model.IpCam;

import java.util.List;


import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

import static androidx.room.OnConflictStrategy.REPLACE;
@Dao
public interface CamDao {

    @Query("SELECT * FROM channels")
    Flowable<List<IpCam>> getAll();

    @Insert(onConflict = REPLACE)
    Single<Long> insertIpCam(IpCam mIpCam);

    @Insert
    Single<List<Long>> insertAllIpCam(IpCam... mIpCamsList);

    @Delete
    Single<Integer> deleteIpCam(IpCam mIpCam);

    @Update
    Single<Integer> updateIpCam(IpCam mIpCam);

    @Query("SELECT * FROM channels WHERE id = :id")
    Single<IpCam> getIpCamById(int id);


    @Query("SELECT * FROM channels WHERE id IN (:cameraIds)")
    Single<List<IpCam>> loadAllByIds(int[] cameraIds);

    @Query("SELECT * FROM channels WHERE name LIKE :name")
    Single<IpCam> findByName(String name);
}
