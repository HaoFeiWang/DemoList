package com.whf.demolist.bluetooth.basic;

/**
 * Created by @author WangHaoFei on 2018/8/18.
 */

public class BluetoothInfo {
    private String name;
    private String address;
    private String rssi;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }

    @Override
    public String toString() {
        return "BluetoothInfo{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", rssi='" + rssi + '\'' +
                '}';
    }
}
