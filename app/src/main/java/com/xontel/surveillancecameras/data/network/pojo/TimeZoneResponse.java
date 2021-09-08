package com.xontel.surveillancecameras.data.network.pojo;

import com.google.gson.annotations.SerializedName;

public class TimeZoneResponse {
    @SerializedName("timezone")
    private String timezoneID;

    public String getTimezoneID() {
        return timezoneID;
    }

    public void setTimezoneID(String timezoneID) {
        this.timezoneID = timezoneID;
    }
}
