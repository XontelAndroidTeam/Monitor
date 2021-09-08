package com.xontel.surveillancecameras.data.utils;

import androidx.room.TypeConverter;

import java.util.Date;


/**
 * Created on : Jan 29, 2019
 * Author     : AndroidWave
 */
public class DateConverter {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}