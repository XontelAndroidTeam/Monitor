package com.xontel.surveillancecameras.models;

public class NetworkDevices{

    private String ipAddress;
    private String name;
    private String macAddress;
    private String type;


    public NetworkDevices(String ipAddress, String name, String macAddress, String type) {
        this.ipAddress = ipAddress;
        this.name = name;
        this.macAddress = macAddress;
        this.type = type;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
