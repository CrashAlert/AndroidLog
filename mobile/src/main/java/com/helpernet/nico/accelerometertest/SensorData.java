package com.helpernet.nico.accelerometertest;

/**
 * Created by nico on 30/12/15.
 */
public class SensorData {

    private long timestamp;

    private Float acc_x = null;
    private Float acc_y = null;
    private Float acc_z = null;

    private Float lin_acc = null;

    private Float gyr_x = null;
    private Float gyr_y = null;
    private Float gyr_z = null;

    private Float rot_x = null;
    private Float rot_y = null;
    private Float rot_z = null;

    private Float mag_x = null;
    private Float mag_y = null;
    private Float mag_z = null;

    private Float lat = null;
    private Float lng = null;
    private Float alt = null;
    private Float speed = null;
    private Float bearing = null;
    private Float error = null;

    private Float pressure = null;

    public SensorData(long timestamp) {
        // convert nanoseconds to milliseconds for a resolution of 10Hz
        this.timestamp = timestamp/1000000;
    }

    public String toString() {
        String str = Long.toString(this.timestamp);
        str += "," + accString();
        str += "," + linAccString();
        str += "," + rotString();
        str += "," + magString();
        str += "," + gnsString();
        str += "," + presString();
        return str;
    }

    private String accString() {
        if (acc_x == null) {
            return ",,";
        }
        else {
            String acc = Float.toString(acc_x) + "," +
                         Float.toString(acc_y) + "," +
                         Float.toString(acc_z);
            return acc;
        }
    }

    public Float getAcc_x() {
        return acc_x;
    }

    public void setAcc_x(Float acc_x) {
        this.acc_x = acc_x;
    }

    public Float getAcc_y() {
        return acc_y;
    }

    public void setAcc_y(Float acc_y) {
        this.acc_y = acc_y;
    }

    public Float getAcc_z() {
        return acc_z;
    }

    public void setAcc_z(Float acc_z) {
        this.acc_z = acc_z;
    }

    private String linAccString() {
        if (lin_acc==null) return "";
        else return Float.toString(lin_acc);
    }

    public Float getLin_acc() {
        return lin_acc;
    }

    public void setLin_acc(Float lin_acc) {
        this.lin_acc = lin_acc;
    }

    private String gyrString() {
        if (gyr_x == null) {
            return ",,";
        }
        else {
            String gyr = Float.toString(gyr_x) + "," +
                    Float.toString(gyr_y) + "," +
                    Float.toString(gyr_z);
            return gyr;
        }
    }

    public Float getGyr_x() {
        return gyr_x;
    }

    public void setGyr_x(Float gyr_x) {
        this.gyr_x = gyr_x;
    }

    public Float getGyr_y() {
        return gyr_y;
    }

    public void setGyr_y(Float gyr_y) {
        this.gyr_y = gyr_y;
    }

    public Float getGyr_z() {
        return gyr_z;
    }

    public void setGyr_z(Float gyr_z) {
        this.gyr_z = gyr_z;
    }

    private String rotString() {
        if (rot_x == null) {
            return ",,";
        }
        else {
            String rot = Float.toString(rot_x) + "," +
                    Float.toString(rot_y) + "," +
                    Float.toString(rot_z);
            return rot;
        }
    }

    public Float getRot_x() {
        return rot_x;
    }

    public void setRot_x(Float rot_x) {
        this.rot_x = rot_x;
    }

    public Float getRot_y() {
        return rot_y;
    }

    public void setRot_y(Float rot_y) {
        this.rot_y = rot_y;
    }

    public Float getRot_z() {
        return rot_z;
    }

    public void setRot_z(Float rot_z) {
        this.rot_z = rot_z;
    }

    private String magString() {
        if (mag_x == null) {
            return ",,";
        }
        else {
            String rot = Float.toString(mag_x) + "," +
                    Float.toString(mag_y) + "," +
                    Float.toString(mag_z);
            return rot;
        }
    }

    public Float getMag_x() {
        return mag_x;
    }

    public void setMag_x(Float mag_x) {
        this.mag_x = mag_x;
    }

    public Float getMag_y() {
        return mag_y;
    }

    public void setMag_y(Float mag_y) {
        this.mag_y = mag_y;
    }

    public Float getMag_z() {
        return mag_z;
    }

    public void setMag_z(Float mag_z) {
        this.mag_z = mag_z;
    }

    private String gnsString() {
        if (lat == null) {
            return ",,,,";
        }
        else {
            String gnss = Float.toString(lat) + "," +
                    Float.toString(lng) + "," +
                    Float.toString(bearing) + "," +
                    Float.toString(speed) + "," +
                    Float.toString(alt) + "," +
                    Float.toString(error);
            return gnss;
        }
    }

    public Float getLat() {
        return lat;
    }

    public void setLat(Float lat) {
        this.lat = lat;
    }

    public Float getLng() {
        return lng;
    }

    public void setLng(Float lng) {
        this.lng = lng;
    }

    public Float getAlt() {
        return alt;
    }

    public void setAlt(Float alt) {
        this.alt = alt;
    }

    public Float getBearing() {
        return bearing;
    }

    public void setBearing(Float bearing) {
        this.bearing = bearing;
    }

    public Float getSpeed() {
        return speed;
    }

    public void setSpeed(Float speed) {
        this.speed = speed;
    }

    public Float getGPSError() {
        return error;
    }

    public void setGPSError(Float error) {
        this.error = error;
    }

    private String presString() {
        if (pressure == null) return "";
        else return Float.toString(pressure);
    }

    public Float getPressure() {
        return pressure;
    }

    public void setPressure(Float pressure) {
        this.pressure = pressure;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
