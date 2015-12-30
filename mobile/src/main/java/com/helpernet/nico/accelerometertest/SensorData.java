package com.helpernet.nico.accelerometertest;

/**
 * Created by nico on 30/12/15.
 */
public class SensorData {

    private long timestamp;

    private float acc_x;
    private float acc_y;
    private float acc_z;

    private float lin_acc;

    private float gyr_x;
    private float gyr_y;
    private float gyr_z;

    private float rot_x;
    private float rot_y;
    private float rot_z;

    private float mag_x;
    private float mag_y;
    private float mag_z;

    private float lat;
    private float lng;
    private float alt;
    private float bearing;

    public String toString() {
        return "";
    }

    public float getAcc_x() {
        return acc_x;
    }

    public void setAcc_x(float acc_x) {
        this.acc_x = acc_x;
    }

    public float getAcc_y() {
        return acc_y;
    }

    public void setAcc_y(float acc_y) {
        this.acc_y = acc_y;
    }

    public float getAcc_z() {
        return acc_z;
    }

    public void setAcc_z(float acc_z) {
        this.acc_z = acc_z;
    }

    public float getLin_acc() {
        return lin_acc;
    }

    public void setLin_acc(float lin_acc) {
        this.lin_acc = lin_acc;
    }

    public float getGyr_x() {
        return gyr_x;
    }

    public void setGyr_x(float gyr_x) {
        this.gyr_x = gyr_x;
    }

    public float getGyr_y() {
        return gyr_y;
    }

    public void setGyr_y(float gyr_y) {
        this.gyr_y = gyr_y;
    }

    public float getGyr_z() {
        return gyr_z;
    }

    public void setGyr_z(float gyr_z) {
        this.gyr_z = gyr_z;
    }

    public float getRot_x() {
        return rot_x;
    }

    public void setRot_x(float rot_x) {
        this.rot_x = rot_x;
    }

    public float getRot_y() {
        return rot_y;
    }

    public void setRot_y(float rot_y) {
        this.rot_y = rot_y;
    }

    public float getRot_z() {
        return rot_z;
    }

    public void setRot_z(float rot_z) {
        this.rot_z = rot_z;
    }

    public float getMag_x() {
        return mag_x;
    }

    public void setMag_x(float mag_x) {
        this.mag_x = mag_x;
    }

    public float getMag_y() {
        return mag_y;
    }

    public void setMag_y(float mag_y) {
        this.mag_y = mag_y;
    }

    public float getMag_z() {
        return mag_z;
    }

    public void setMag_z(float mag_z) {
        this.mag_z = mag_z;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }

    public float getAlt() {
        return alt;
    }

    public void setAlt(float alt) {
        this.alt = alt;
    }

    public float getBearing() {
        return bearing;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }

    public long getTimestamp() {

        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
